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
class ClassSales {
    private final String recieptNo,totalQty,cashPaid,totalCost,change,cashier,date;//pwrd;

    public ClassSales(String recieptNo, String totalQty, String cashPaid, String totalCost, String change, String cashier, String date) {
        this.recieptNo = recieptNo;
        this.totalQty = totalQty;
        this.cashPaid = cashPaid;
        this.totalCost = totalCost;
        this.change = change;
        this.cashier = cashier;
        this.date = date;
    }

    public String getRecieptNo() {
        return recieptNo;
    }

    public String getTotalQty() {
        return totalQty;
    }

    public String getCashPaid() {
        return cashPaid;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public String getChange() {
        return change;
    }

    public String getCashier() {
        return cashier;
    }

    public String getDate() {
        return date;
    }
    
    
}
