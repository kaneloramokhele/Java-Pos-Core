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
class ClassProduct {
    private final String productCode,productName,quantity,batchNo,productVolume,unitPrice,fomulation,status,expiryDate;//pwrd;

    public ClassProduct(String productCode, String productName, String quantity, String batchNo, String productVolume, String unitPrice, String fomulation, String status, String expiryDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.batchNo = batchNo;
        this.productVolume = productVolume;
        this.unitPrice = unitPrice;
        this.fomulation = fomulation;
        this.status = status;
        this.expiryDate = expiryDate;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public String getProductVolume() {
        return productVolume;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getFomulation() {
        return fomulation;
    }

    public String getStatus() {
        return status;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

}//end of classProduct
