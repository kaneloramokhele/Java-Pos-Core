/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

/**
 *
 * @author DELL
 */
class ClassSalesReport {
    private final String productCode,productName,currentQty,quantity,unitPrice,totalCost,cashier,reportDate;

    public ClassSalesReport(String productCode, String productName, String currentQty, String quantity, String unitPrice, String totalCost, String cashier, String reportDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.currentQty = currentQty;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalCost = totalCost;
        this.cashier = cashier;
        this.reportDate = reportDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getCurrentQty() {
        return currentQty;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public String getCashier() {
        return cashier;
    }

    public String getReportDate() {
        return reportDate;
    }
    
    
}
