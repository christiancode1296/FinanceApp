package FinanceApp.FinanceApp;

public class Stock {

    int stockID;
    String stockName;
    String stockPrice;
    String stockQty;

    public Stock(int stockID, String stockName, String stockPrice, String stockQty) {
        this.stockID = stockID;
        this.stockName = stockName;
        this.stockPrice = stockPrice;
        this.stockQty = stockQty;
    }
    public int getStockID() {
        return stockID;
    }
    public String getStockName() {
        return stockName;
    }
    public String getStockPrice() {
        return stockPrice;
    }
    public String getStockQty() {
        return stockQty;
    }
}
