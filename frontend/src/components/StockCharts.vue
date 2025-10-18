<template>
  <div>
    <h2>Stocks Chart</h2>

    <div style="margin: 12px 0; display:flex; gap:8px; align-items:center; flex-wrap: wrap;">
      <label>
        Symbol:
        <input v-model.trim="symbol" @keyup.enter="reload()" placeholder="AAPL" />
      </label>
      <button @click="reload()" :disabled="loading">Laden</button>
      <span v-if="loading">lädt…</span>
      <span v-if="errorMsg" style="color:#b00;">{{ errorMsg }}</span>
    </div>

    <div style="position: relative; width: 100%; height: 360px;color: #bb0000">
      <canvas ref="chartCanvas"></canvas>
    </div>

    <h3 style="margin-top:12px;">Letzten Preise:</h3>
    <ul>
      <li v-for="(price, index) in lastPrices" :key="index">
        {{ index + 1 }}. {{ price.toFixed(2) }} USD
      </li>
    </ul>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from "vue";

export default {
  name: "StockChart",
  setup() {
    const symbol = ref("AAPL");
    const chartCanvas = ref(null);
    const errorMsg = ref("");
    const loading = ref(false);
    const lastPrices = ref([]);

    let chart;            // Chart.js-Instanz
    let abortController;  // für laufende Fetch-Requests

    // Chart.js nur bei Bedarf laden und nur nötige Komponenten registrieren
    const loadChartJs = async () => {
      const ChartJS = await import("chart.js");
      const {
        Chart,
        LineController, LineElement, PointElement,
        LinearScale, CategoryScale,
        Tooltip, Legend,
      } = ChartJS;
      Chart.register(
          LineController, LineElement, PointElement,
          LinearScale, CategoryScale, Tooltip, Legend
      );
      return Chart;
    };

    // Einzelne Serie laden → Array [{date, close}, ...] (chronologisch aufsteigend)
    const fetchSeries = async (sym) => {
      if (abortController) abortController.abort();
      abortController = new AbortController();
      loading.value = true;
      errorMsg.value = "";
      try {
        // Frontend ruft dein Backend an (Proxy). KEIN direkter FMP-Call
        const url = `/api/stocks/${encodeURIComponent(sym)}`;
        const res = await fetch(url, { signal: abortController.signal });
        if (!res.ok) throw new Error(`${sym}: HTTP ${res.status}`);
        const data = await res.json();

        // Mögliche Response-Formate tolerant behandeln
        let rows = [];
        if (Array.isArray(data?.historical)) rows = data.historical;
        else if (Array.isArray(data?.data)) rows = data.data;
        else if (Array.isArray(data?.results)) rows = data.results;
        else if (
          Array.isArray(data?.historicalStockList) &&
          data.historicalStockList.length > 0 &&
          Array.isArray(data.historicalStockList[0]?.historical)
        ) {
          rows = data.historicalStockList[0].historical;
        } else if (Array.isArray(data)) rows = data;

        // Normalisieren auf {date, close}
        const norm = rows
          .map((p) => {
            const date = p.date || p.formattedDate || p.datetime || p.label || p.timestamp || null;
            const raw = p.close ?? p.adjClose ?? p.price ?? p.value ?? null;
            const close = typeof raw === 'string' ? parseFloat(raw.replace(',', '.')) : raw;
            return { date, close };
          })
          .filter((p) => p.date && typeof p.close === 'number' && !Number.isNaN(p.close));

        if (!norm.length) {
          throw new Error(`Keine Kursdaten erhalten (Symbol: ${sym}).`);
        }

        // Aufsteigend sortieren und auf 180 Tage begrenzen
        norm.sort((a, b) => (a.date < b.date ? -1 : a.date > b.date ? 1 : 0));
        const series = norm.slice(-180);

        // Labels/Werte erzeugen
        const labels = series.map((p) => p.date);
        const values = series.map((p) => p.close);

        // Letzte Preise aktualisieren (5 letzte Schlusskurse)
        lastPrices.value = values.slice(-5).reverse();

        return { labels, values };
      } catch (e) {
        if (e.name !== 'AbortError') {
          errorMsg.value = e.message || String(e);
        }
        return { labels: [], values: [] };
      } finally {
        loading.value = false;
      }
    };

    const formatDate = (yyyyMmDd) => {
      const d = new Date(yyyyMmDd + "T00:00:00Z");
      return new Intl.DateTimeFormat(undefined, { year: "2-digit", month: "2-digit", day: "2-digit" }).format(d);
    };

    const reload = async () => {
      if (abortController) abortController.abort();
      abortController = new AbortController();
      loading.value = true;
      errorMsg.value = "";
      try {
        const { labels, values } = await fetchSeries(symbol.value.toUpperCase());
        lastPrices.value = values.slice(-5).reverse();
        await renderChart(labels, values);
      } catch (e) {
        errorMsg.value = e.message || String(e);
        await renderChart([], []);
      } finally {
        loading.value = false;
      }
    };

    const renderChart = async (labels, values) => {
      const Chart = await loadChartJs();
      const ctx = chartCanvas.value.getContext("2d");
      if (chart) chart.destroy();
      chart = new Chart(ctx, {
        type: "line",
        data: {
          labels,
          datasets: [
            {
              label: symbol.value,
              data: values,
              tension: 0.2,
            },
          ],
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          interaction: { mode: "nearest", intersect: false },
          plugins: {
            legend: { display: true },
            tooltip: {
              enabled: true,
              callbacks: {
                title: (items) => (items.length ? formatDate(items[0].label) : ""),
                label: (ctx) => `${ctx.dataset.label}: ${ctx.formattedValue} USD`,
              },
            },
          },
          scales: {
            x: {
              display: true,
              ticks: {
                callback: (_, idx) => formatDate(labels[idx]),
                maxRotation: 0,
                autoSkip: true,
              },
            },
            y: { display: true },
          },
        },
      });
    };

    onMounted(async () => { await reload(); });
    onUnmounted(() => {
      if (abortController) abortController.abort();
      if (chart) chart.destroy();
    });

    return { symbol, chartCanvas, errorMsg, loading, lastPrices, reload };
  },
};
</script>

<style scoped>
canvas { width: 100%; height: 100%; }
</style>