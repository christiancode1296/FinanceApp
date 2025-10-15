<template>
  <div>
    <h2>{{ symbol }} Chart</h2>
    <canvas ref="chartCanvas"></canvas>

    <h3>Letzte Preise:</h3>
    <ul>
      <li v-for="(price, index) in lastPrices" :key="index">
        {{ index + 1 }}. {{ price.toFixed(2) }} USD
      </li>
    </ul>
  </div>
</template>

<script>
import { onMounted, ref } from "vue";
import { Chart } from "chart.js/auto"; // wenn du Chart.js nutzt

export default {
  name: "StockChart",
  setup() {
    const symbol = ref("AAPL");
    const lastPrices = ref([]);
    const chartCanvas = ref(null);
    let chart;

    onMounted(async () => {
      try {
        // ⚠️ Demo-Endpoint – checke dein FMP-Paket/Endpoint
        const res = await fetch(
            `https://financialmodelingprep.com/api/v3/historical-price-full/${symbol.value}?serietype=line&apikey=iRmuSr8KXYvl9whZWdXKzsdaSjaAWwqb`
        );
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const data = await res.json();

        // FMP liefert {symbol, historical:[{date, close}, ...]}
        const series = (data.historical || []).slice(0, 30).reverse(); // letzte 30, chronologisch
        lastPrices.value = series.map(p => p.close).slice(-5).reverse(); // "Letzte Preise" Liste

        // Chart rendern
        const labels = series.map(p => p.date);
        const values = series.map(p => p.close);

        chart = new Chart(chartCanvas.value.getContext("2d"), {
          type: "line",
          data: {
            labels,
            datasets: [{ label: symbol.value, data: values }]
          },
          options: { responsive: true, maintainAspectRatio: false }
        });
      } catch (e) {
        console.error(e);
      }
    });

    return { symbol, lastPrices, chartCanvas };
  },
};
</script>

<style scoped>
/* Damit der Canvas etwas Höhe hat */
canvas { width: 100%; height: 320px; }
</style>
