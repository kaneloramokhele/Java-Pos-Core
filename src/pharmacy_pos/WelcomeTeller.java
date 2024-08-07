/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author DELL
 */
public final class WelcomeTeller extends javax.swing.JFrame {

    /**
     * Creates new form WelcomeTeller
     */
    
    static Connection conn;
    PreparedStatement pst;// = conn.prepareStatement(sql);
    ResultSet rs;// = pst.executeQuery();
    Statement st;
    
    Date date;
    Timer timer;
    SimpleDateFormat sdf, stf, sdfReal, stfReal;
    
    public WelcomeTeller() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setIconImage();
        productsMethod();
        displaySalesMethod();
        resetFieldMethod();
    }
    
    //method to setIconImage
    private void setIconImage() {
        try{
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pharmacy_pos/Images/pos.jpg")));
            //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pharmacy_pos/Images/launcher_icon.jpg")));
        }catch(Exception error){
            JOptionPane.showMessageDialog(null, "Failed to set icon image. \n" +error);
        }
    }//end of setIconImage
    
    
    void loggedinUser(String username){
        lblSessionUsername.setText(username);
        /*if("".equals(lblSessionUsername.getText())){
            HomePage home = new HomePage();
            home.setVisible(true);
            dispose();
        }else{
            lblSessionUsername.setText(username);
        }*/
    }//end of session
    
    public void logoutMethod(){
        
        try{
            HomePage obj = new HomePage();
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure to logout?", "LOGOUT FROM SYSTEM",
                    JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
            switch(conf){
                case 0:
                    dispose();
                    obj.show();
                    break;
                case 1:
                    break;
            }  
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to perform task. \n" +error);
        }
        
    }//end of Logout
    //////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**public class IntergerSpinner<T> extends Spinner<T>{
        private void init(){
            getEditor().textProperty().addListener((observable, oldValue, newValue) ->{
                if(!newValue.matches("\\d*")){
                    getEditor().setText(oldValue); 
                } 
            });
        }
    }*/
    
    //Array of type products 
    private ArrayList<ClassProduct> productList(){
        ArrayList<ClassProduct> productList = new ArrayList<>();
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            String sql = "SELECT * FROM PRODUCTS";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            ClassProduct product;
            while(rs.next()){
                product = new ClassProduct(rs.getString("ItemCode"), rs.getString("ItemName"),
                        rs.getString("Quantity"), rs.getString("BatchNo"),rs.getString("ItemVolume"),
                        rs.getString("UnitPrice"), rs.getString("Fomulation"), rs.getString("ItemStatus"),
                        rs.getString("ExpiryDate"));
                productList.add(product);
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return productList;
    }
    
    //display data into a jtable
    public void productsMethod(){
        ArrayList<ClassProduct> list = productList();
        DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
        
        Object[] row = new Object[9];
        for(int i=0; i<list.size(); i++){
            row[0] = list.get(i).getProductCode();
            row[1] = list.get(i).getProductName();
            row[2] = list.get(i).getQuantity();
            row[3] = list.get(i).getBatchNo();
            row[4] = list.get(i).getProductVolume();
            row[5] = list.get(i).getUnitPrice();
            row[6] = list.get(i).getFomulation();
            row[7] = list.get(i).getStatus();
            row[8] = list.get(i).getExpiryDate();
            
            model.addRow(row);
        }
    }//endof
    
    
    //tableMouseClickMenthod
    private void tableProductsMouseClickMenthod(){
        try{
            int i = tblProduct.getSelectedRow();
            TableModel model = tblProduct.getModel();
            txtProductCode.setText(model.getValueAt(i,0).toString());
            txtProductName.setText(model.getValueAt(i,1).toString());
            lblAvailableQuantity.setText(model.getValueAt(i,2).toString());
            txtUnitPrice.setText(model.getValueAt(i,5).toString());
            
            spinnerQuantity.setValue(1);
            txtProductCode.setEnabled(false);
            btnAddToCart.setEnabled(true);
            spinnerQuantity.setEnabled(true);
            
            txtTotalCost.setText(model.getValueAt(i,5).toString());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Sorry! something went wrong. \n" +error);
        }
    }//end of tableMouseClickMethod
    
    
    //itemCode
    private void itemCodeSearchMethod(){
        String code = txtProductCode.getText();
        try {
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            pst = conn.prepareCall("SELECT * FROM PRODUCTS WHERE ItemCode = ?");
            
            pst.setString(1, code);
            rs = pst.executeQuery();
            
            if(rs.next()){
                String pname = rs.getString("ItemName");
                String price = rs.getString("UnitPrice");
                
                txtProductName.setText(pname.trim());
                txtUnitPrice.setText(price.trim());
                spinnerQuantity.setValue(1);
                txtProductCode.setEnabled(false);
                btnAddToCart.setEnabled(true);
            }
            else if(rs.next() == false){
                JOptionPane.showMessageDialog(this,"Product code does not exist.");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of item Code Search Method
    
    
    private void totalCostMethod(){
        try{
            int quantityAvailable = Integer.parseInt(lblAvailableQuantity.getText());
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());
            int quantityBought = Integer.parseInt(spinnerQuantity.getValue().toString());
            //double totalCost = unitPrice * quantityBought;
            //txtTotalCost.setText(String.valueOf(totalCost));
            if(quantityBought > quantityAvailable){
                JOptionPane.showMessageDialog(null, "Sorry! Quantiity bought must less or equal to available quantity.\n");
                spinnerQuantity.setValue(1);
            }
            if(quantityBought <= 0){
                JOptionPane.showMessageDialog(null, "Sorry! Quantiity bought must greater or equals to 1.\n");
                spinnerQuantity.setValue(1);
            }else{
                double totalCost = unitPrice * quantityBought;
                txtTotalCost.setText(String.valueOf(totalCost));
            }
        }catch(NumberFormatException error){
            JOptionPane.showMessageDialog(null, "Sorry! please use only whole numbers in quantity. \n" +error);
        }
    }//end of quantity Bought Method
    
    
    public void balanceChangeMethod(){
        try{
            double totalCost = Double.parseDouble(txtTotalCost.getText());
            double cashPaid = Double.parseDouble(txtCashPaid.getText());
            double balance = cashPaid - totalCost;

            if(cashPaid >= totalCost || balance >= 0){
                txtChangeBalance.setText(String.valueOf(balance));
                btnPrintBillSlip.enable();
                btnPrintBillSlip.enable(true);
                btnPrintBillSlip.setVisible(true);
                btnPrintBillSlip.setEnabled(true);
            }else if(cashPaid == 0.00){
                btnPrintBillSlip.disable();
                btnPrintBillSlip.enable(false);
                //btnPrintBillSlip.setVisible(false);
                btnPrintBillSlip.setEnabled(false);
            }
            else{
                btnPrintBillSlip.disable();
                btnPrintBillSlip.enable(false);
                //btnPrintBillSlip.setVisible(false);
                btnPrintBillSlip.setEnabled(false);
            }
        }catch(NumberFormatException error){
            //JOptionPane.showMessageDialog(null, "Sorry! please write in for of money like (100.00). \n" +error);
            //JOptionPane.showMessageDialog(null, "Sorry! please write in for of money like (100.00). \n");
            txtCashPaid.setText("");
            btnPrintBillSlip.setEnabled(false);
        }
    }//end of customer change method
    
    
    
    
    //method to calculate chenge
    public void balanceCheckMethod(){
        try{
            double totalCost, cashPaid, balance;
            totalCost = Double.parseDouble(txtTotalCost.getText());
            cashPaid = Double.parseDouble(txtCashPaid.getText());
            balance = cashPaid - totalCost;

            if(cashPaid >= totalCost || balance >= 0.00 || txtCashPaid.getText().equals("") || (balance + totalCost != cashPaid) ){
                /*btnPrintBillSlip.enable(true);
                btnPrintBillSlip.enableInputMethods(false);*/
                txtChangeBalance.setText(String.valueOf(balance));
                btnPrintBillSlip.enable();
                btnPrintBillSlip.enable(true);
                btnPrintBillSlip.setVisible(true);
                btnPrintBillSlip.setEnabled(true);
            }else if(txtCashPaid.getText().equals("0.00")){
               /*btnPrintBillSlip.enable(false);
                btnPrintBillSlip.enableInputMethods(false);*/
                btnPrintBillSlip.disable();
                btnPrintBillSlip.enable(false);
                //btnPrintBillSlip.setVisible(false);
                btnPrintBillSlip.setEnabled(false); 
            }else{
                /*btnPrintBillSlip.enable(false);
                btnPrintBillSlip.enableInputMethods(false);*/
                btnPrintBillSlip.disable();
                btnPrintBillSlip.enable(false);
                //btnPrintBillSlip.setVisible(false);
                btnPrintBillSlip.setEnabled(false);
            }
        }catch(NumberFormatException error){
            //JOptionPane.showMessageDialog(null, "Sorry! something went wrong. \n" +error);
            //JOptionPane.showMessageDialog(null, "Sorry! please write in for of money like (100.00). \n");
            txtCashPaid.setText("");
            btnPrintBillSlip.setEnabled(false);
        }
    }//end of customer change method
    
    
    
    //Update Status
    private void updateStatus(){
    
        try{
            
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class

            int row, quantityAvailable, quantitySold, remainingQuantity;
            String status = "Sold";
            
            //String iId =  this.Item_ID.getText();
            row = tblProduct.getSelectedRow();
            quantityAvailable = Integer.parseInt(lblAvailableQuantity.getText());
            quantitySold = Integer.parseInt(spinnerQuantity.getValue().toString());
            String itemCode = (tblProduct.getModel().getValueAt(row, 0).toString());
            
            remainingQuantity = quantityAvailable - quantitySold;
            
            try{
                
                if(remainingQuantity == 0){
                    String sqlUpdateStatus = "UPDATE PRODUCTS SET ItemStatus='"+status+"' WHERE ItemCode ='"+itemCode+"'";
                    pst = conn.prepareStatement(sqlUpdateStatus);
                    st = conn.createStatement();
                    pst.executeUpdate();
                    
                    
                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod();//then recalled by this method.
                    
                    lblAvailableQuantity.setText(String.valueOf(remainingQuantity));
                }
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            }
            //Reset();
            //ResetSeles();
            
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE,null, ex);
        }

    }//end of Update Status
    
    
    //Update the Product Quantity
    private void updateQuantity(){
        try{
            //getConnection Method Called
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            int row, quantityAvailable, quantitySold, remainingQuantity;
            
            row = tblProduct.getSelectedRow();
            quantityAvailable = Integer.parseInt(lblAvailableQuantity.getText());
            quantitySold = Integer.parseInt(spinnerQuantity.getValue().toString());
            String itemCode = (tblProduct.getModel().getValueAt(row, 0).toString());
            
            remainingQuantity = quantityAvailable - quantitySold;
            
            try{
                if(quantitySold == quantityAvailable){
                    String sqlUpdateQty = "UPDATE PRODUCTS SET Quantity='"+remainingQuantity+"' WHERE ItemCode ='"+itemCode+"'";
                    pst = conn.prepareStatement(sqlUpdateQty);
                    st = conn.createStatement();
                    pst.executeUpdate();
                    updateStatus();
                    //addItemToCartMethod();
                    
                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod();//then recalled by this method.
                    
                    lblAvailableQuantity.setText(String.valueOf(remainingQuantity));
                    
                }else if(remainingQuantity > 0){
                    String sqlUpdateQty = "UPDATE PRODUCTS SET Quantity='"+remainingQuantity+"' WHERE ItemCode ='"+itemCode+"'";
                    pst = conn.prepareStatement(sqlUpdateQty);
                    st = conn.createStatement();
                    pst.executeUpdate();
                    
                    //addItemToCartMethod();
                    
                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod();//then recalled by this method.
                    //AvaStockTable();
                    lblAvailableQuantity.setText(String.valueOf(remainingQuantity));
                }//end of else if()
                
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            }
            //Reset();
            //ResetSeles();
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE,null, ex);
        }
    }//end of update Quantity method
    
    
    
    //method to add to cart
    private void addItemToCartMethod(){
        
        DefaultTableModel tblCartItemModel = (DefaultTableModel) tblCartItems.getModel();
        double totalCostSum = 0.00;
        int availableQty,quantityBought,currentQty,index;
        
        index = tblCartItems.getSelectedRow();
        availableQty = Integer.parseInt(lblAvailableQuantity.getText());
        quantityBought = Integer.parseInt(spinnerQuantity.getValue().toString());
        
        
        currentQty = availableQty - quantityBought;
        
        
        
        if(txtProductCode.getText().equals("") || txtProductName.getText().equals("") ||
                spinnerQuantity.getValue().equals(0) || txtUnitPrice.getText().equals("0..00")){
            JOptionPane.showMessageDialog(this,"Product code, name quantity and price are required.");
        }else if(lblAvailableQuantity.getText().equals(0)){
            JOptionPane.showMessageDialog(this,"Sorry you can not sell zero(0) items.");
        }/*else if(txtProductCode.getText().equals(tblCartItemModel.getValueAt(index,0).toString())){
            int totalQty = (Integer)tblCartItems.getValueAt(index, 2) + ;
        }*/else{

            /*if(txtProductCode.getText().equals(tblCartItemModel.getValueAt(index,1).toString())){
                JOptionPane.showMessageDialog(null, "Sorry! you need to remove the SAME item from the 'Cart Items' table first. \n");
            }else{*/
                tblCartItemModel.addRow(new Object[]{
                txtProductCode.getText(),
                txtProductName.getText(),
                currentQty,
                spinnerQuantity.getValue(),
                txtUnitPrice.getText(),
                txtTotalCost.getText(),
                lblSessionUsername.getText()
                });
                
                for (int i=0; i<tblCartItems.getRowCount(); i++){
                    //sum = sum + Integer.parseInt(jTable1.getValueAt(i, sum).toString());
                    totalCostSum = totalCostSum + Double.parseDouble(tblCartItems.getValueAt(i, 4).toString());
                }

                updateQuantity();//jujjhhghhjhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh*********************
                recordSaleReport();
                txtTotalCost.setText(Double.toString(totalCostSum));
                
                txtProductCode.setText("");
                txtProductName.setText("");
                lblAvailableQuantity.setText("1");
                txtProductCode.setEnabled(true);
                btnAddToCart.setEnabled(false);
                txtCashPaid.setEnabled(true);
                txtCashPaid.setText("");
                txtCashPaid.setEnabled(true);
                txtCashPaid.setBackground(Color.WHITE);
                txtChangeBalance.setText("0.00");
            //}
        }
    }//end of AddToCart
    
    
    //tableMouseClickMenthod
    private void tableCartItemsMouseClickMenthod(){
        try{
            int i = tblCartItems.getSelectedRow();
            TableModel tblCartItemModel = tblCartItems.getModel();
            txtProductCode.setText(tblCartItemModel.getValueAt(i,0).toString());
            txtProductName.setText(tblCartItemModel.getValueAt(i,1).toString());
            lblAvailableQuantity.setText(tblCartItemModel.getValueAt(i,2).toString());
            
            //lblAvailableQuantity.setText(tblCartItemModel.getValueAt(i,2).toString());
            spinnerQuantity.setValue(Integer.parseInt(tblCartItemModel.getValueAt(i,3).toString()));
            //txtUnitPrice.setText(tblCartItemModel.getValueAt(i,4).toString());
            btnRemoveItem.setEnabled(true);
            spinnerQuantity.setEnabled(false);
            txtProductCode.setEnabled(false);
            //btnCancelBill.setEnabled(true);
            
            //txtTotalCost.setText(tblCartItemModel.getValueAt(i,5).toString());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Sorry! something went wrong. \n" +error);
        }
    }//end of tableMouseClickMethod
    
    
    
    //Update Status
    private void updateStatusPositive(){
    
        try{
            
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class

            int row, quantityReturned;
            TableModel tblCartItemModel = tblCartItems.getModel();
            String status = "Available";
            row = tblCartItems.getSelectedRow();
            
            //spinnerQuantity.setValue(Integer.parseInt(tblCartItemModel.getValueAt(row,3).toString()));
            quantityReturned = Integer.parseInt(tblCartItemModel.getValueAt(row,3).toString());
            String itemCode = (tblProduct.getModel().getValueAt(row, 0).toString());
            
            //currentQuantity = quantityAvailable + quantityReturned;
            
            try{
                
                if(quantityReturned > 0){
                    //String sqlUpdateStatus = "UPDATE PRODUCTS SET ItemStatus='"+status+"' WHERE ItemCode ='"+itemCode+"'";
                    String sqlUpdateStatus = "UPDATE PRODUCTS SET ItemStatus='"+status+"' WHERE ItemCode ='"+itemCode+"'";
                    pst = conn.prepareStatement(sqlUpdateStatus);
                    st = conn.createStatement();
                    pst.executeUpdate();
                    
                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod();//then recalled by this method.
                    
                    //lblAvailableQuantity.setText(String.valueOf(quantityReturned));
                }//end of if()
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            }
            //Reset();
            //ResetSeles();
            
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE,null, ex);
        }

    }//end of Update Status
    
    
    //Update the Product Quantity
    private void updateQuantityPositive(){
        try{
            //getConnection Method Called
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            int row,quantityAvailable,quantityReturned,currentQuantity;
            TableModel tblCartItemModel = tblCartItems.getModel();
            row = tblCartItems.getSelectedRow();
            
            //lblAvailableQuantity.setText(tblCartItemModel.getValueAt(row,2).toString());
            quantityReturned = Integer.parseInt(tblCartItemModel.getValueAt(row,3).toString());
            quantityAvailable = Integer.parseInt(lblAvailableQuantity.getText());
            currentQuantity = quantityAvailable + quantityReturned;
            String itemCode = (tblCartItems.getModel().getValueAt(row, 0).toString());
            
            
            try{
                if(currentQuantity > 0){
                    
                    String sqlUpdateQty = "UPDATE PRODUCTS SET Quantity = '"+currentQuantity+"' WHERE ItemCode ='"+itemCode+"'";
                    pst = conn.prepareStatement(sqlUpdateQty);
                    st = conn.createStatement();
                    pst.executeUpdate();
                    
                    updateStatusPositive();
                    
                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod();//then recalled by this method.
                    
                    //lblAvailableQuantity.setText(String.valueOf(quantityReturned));
                }
                
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            }
            //Reset();
            //ResetSeles();
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE,null, ex);
        }
    }//end of update Quantity method
    
    
    //Delete from salesReport
    private void deleteFromSalesReportMethod(){
        try{
            //getConnection Method Called
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            String itemCode = txtProductCode.getText();
            
            String sql = "DELETE FROM SalesReports WHERE ItemCode='"+itemCode+"'";
            pst = conn.prepareStatement(sql);
            pst.executeUpdate();
            
            DefaultTableModel modelProducts = (DefaultTableModel) tblProduct.getModel();
            modelProducts.setRowCount(0); //if there is data in the table it is removed
            productsMethod(); //then recalled by this method. 

            JOptionPane.showMessageDialog(null, "Successfully removed item from cart!");
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE,null, ex);
        }
    }//end of delete from salesReprot
    
    
    //method to remove item 
    private void removeItemFromCartMethod(){
        
        try{
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure to remove item from bill?", "REMOVE ITEM FROM BILL",
                    JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
            switch(conf){
                case 0:
                    DefaultTableModel tblModel = (DefaultTableModel) tblCartItems.getModel();
            switch (tblCartItems.getSelectedRowCount()) {
                case 1:
                    double currentTotalCost = 0.00;
                    double totalCostSum = Double.parseDouble(txtTotalCost.getText());
                    for (int i=0; i<tblCartItems.getRowCount(); i++){
                        currentTotalCost = totalCostSum - Double.parseDouble(tblCartItems.getValueAt(i, 5).toString());
                    }
                    updateQuantityPositive(); //**********************************************************
                    deleteFromSalesReportMethod(); //NEW METHOD
                    tblModel.removeRow(tblCartItems.getSelectedRow());
                    txtTotalCost.setText(Double.toString(currentTotalCost));
                    
                    //JOptionPane.showMessageDialog(null, "Item removed successfully from bill. \n");
                    resetFieldMethod();
                    break;
                case 0:
                    //JOptionPane.showMessageDialog(null, "Sorry you can not delete from an empty table. \n");
                    JOptionPane.showMessageDialog(null, "Sorry! you need to select at least one row from the 'Cart Items' table. \n");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Sorry! you need to select only one row. \n");
                    //JOptionPane.showMessageDialog(null, "Sorry! you need to select at least one row from the 'Cart Items' table. \n");
                    break;
            }
                    break;
                case 1:
                    break;
            }  
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed remove item from cart / bill / wishlist. \n" +error);
        }
        
    }//end of reove item from cart
    
    //method to record salesReport
    private void recordSaleReport() {
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            //conn.setAutoCommit(false);
            
            date = new Date();
            
            sdfReal = new SimpleDateFormat("yyyyMMdd");
            String realSalesDate = sdfReal.format(date);

            timer = new Timer(0, (ActionEvent ae) -> {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            });

            int rows = tblCartItems.getRowCount();
            
            String querySalesReport = "INSERT INTO SalesReports(ItemCode,ItemName,CurrentQty,Quantity,UnitPrice,TotalCost,Cashier,ReportDate) VALUES(?,?,?,?,?,?,?,?)";
            pst = conn.prepareStatement(querySalesReport);
            for(int row = 0; row<rows; row++){
                
                String itemCode = (String)tblCartItems.getValueAt(row, 0);
                String itemName = (String)tblCartItems.getValueAt(row, 1);
                int currentQty = (Integer)tblCartItems.getValueAt(row, 2);
                int totalQty = (Integer)tblCartItems.getValueAt(row, 3);
                String unitPrice = (String)tblCartItems.getValueAt(row, 4);
                String totalCost = (String)tblCartItems.getValueAt(row, 5);
                String cahier = (String)tblCartItems.getValueAt(row, 6);
                
                pst.setString(1, itemCode);
                pst.setString(2, itemName);
                pst.setInt(3, currentQty);
                pst.setInt(4, totalQty);
                pst.setString(5, unitPrice);
                pst.setString(6, totalCost);
                pst.setString(7, cahier);
                pst.setString(8, realSalesDate);
                
                
                /*String itemCode = (String)tblCartItems.getValueAt(row, 0);
                String itemName = (String)tblCartItems.getValueAt(row, 1);
                int currentQty = (Integer)tblCartItems.getValueAt(row, 2);
                String totalQty = (String)tblCartItems.getValueAt(row, 3);
                String unitPrice = (String)tblCartItems.getValueAt(row, 4);
                String totalCost = (String)tblCartItems.getValueAt(row, 5);
                String cahier = (String)tblCartItems.getValueAt(row, 6);
                
                pst.setString(1, itemCode);
                pst.setString(2, itemName);
                pst.setInt(3, currentQty);
                pst.setString(4, totalQty);
                pst.setString(5, unitPrice);
                pst.setString(6, totalCost);
                pst.setString(7, cahier);
                pst.setString(8, realSalesDate);*/

                pst.addBatch();
            }
                pst.executeBatch();
                //updateQuantity();
                //conn.commit();
        }catch(HeadlessException | SQLException ex){
            JOptionPane.showMessageDialog(this,ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WelcomeTeller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end of record salesReport
 
   
    
    //method to record sales
    private void recordSalesMethod(){
        try{
            date = new Date();
            //timer = new Timer();
            sdf = new SimpleDateFormat("yyyyMMdd");
            stf = new SimpleDateFormat("hhmmssmsSSSa");
            String salesDate = sdf.format(date);
            String salesTime = stf.format(date);

            sdfReal = new SimpleDateFormat("dd-MMM-YYYY");
            stfReal = new SimpleDateFormat("HH:mm:ss");
            String realSalesDate = sdfReal.format(date);
            String realSalesTime = stfReal.format(date);        

            String receiptNo = "ID-"+salesDate + salesTime;


            timer = new Timer(0, (ActionEvent ae) -> {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            });

            double totalCost = Double.parseDouble(txtTotalCost.getText());
            double cashPaid = Double.parseDouble(txtCashPaid.getText());
            double change = Double.parseDouble(txtChangeBalance.getText());
            int totalQty = 0;

            //DefaultTableModel tblCartItemModel = (DefaultTableModel) tblCartItems.getModel();

                for (int i=0; i<tblCartItems.getRowCount(); i++){
                    totalQty = totalQty + Integer.parseInt(tblCartItems.getValueAt(i, 2).toString());
                }

            if(receiptNo.equals("") || txtCashPaid.getText().equals("") || 
                    txtTotalCost.getText().equals("0.00") || txtChangeBalance.getText().equals("0.00")){
                JOptionPane.showMessageDialog(rootPane, "Sorry! cash fied is required.");
            }else if(lblSessionUsername.getText().equals("")){
               JOptionPane.showMessageDialog(rootPane, "Sorry! you must be authenticated to proceed selling."); 
            }
            else if(cashPaid < totalCost || change < 0 ){
                JOptionPane.showMessageDialog(rootPane, "Sorry! Customer change cannot be negative.");
            }else{
                try{
                    //getConnection(); //same class
                    conn = DBConnection_Pharmacy_POS.getConnection(); //different class
                    //conn = DBConnection.getConnection(); //different class
                    String sql = "INSERT INTO SALES(ReceiptNo,TotalQty,CashPaid,TotalCost,CustomerChange,Cashier,SalesDate)VALUES(?,?,?,?,?,?,?)"; //(Sales)
                    pst = conn.prepareStatement(sql);
                    //rs = pst.executeQuery();

                    st = conn.createStatement();
                    pst.setString(1, receiptNo.trim());
                    pst.setInt(2, totalQty);
                    pst.setDouble(3, cashPaid);
                    pst.setDouble(4, totalCost);
                    pst.setDouble(5, change);
                    pst.setString(6, lblSessionUsername.getText().trim());
                    pst.setString(7, salesDate.trim());


                    pst.executeUpdate();
                    //updateQuantity(); NOT WORKING
                    JOptionPane.showMessageDialog(null, "Successfully recorded sales of :("+receiptNo+") ON : ("+realSalesDate.trim()+" :"+realSalesTime.trim()+")\n");
                    recordSaleReport();
                    printSlipMethod();
                    //updateQuantity(); SETS QUANTITY TO ZERO

                    DefaultTableModel tblProductModel = (DefaultTableModel) tblProduct.getModel();
                    tblProductModel.setRowCount(0); //if there is data in the table it is removed
                    productsMethod(); //then recalled by this method.

                    DefaultTableModel tblSalesModel = (DefaultTableModel) tblSales.getModel();
                    tblSalesModel.setRowCount(0); //if there is data in the table it is removed
                    displaySalesMethod(); //then recalled by this method.

                }catch(HeadlessException | SQLException ex){
                    JOptionPane.showMessageDialog(null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }//end of else
        }catch(NumberFormatException error){
            txtCashPaid.setText("");
        }
    }//end of recordSales method
    
 
    
    private void printSlipMethod(){

        date = new Date();
        //timer = new Timer();
        sdf = new SimpleDateFormat("yyyyMMdd");
        stf = new SimpleDateFormat("hhmmssmsSSSa");
        String dat = sdf.format(date);
        String tim = stf.format(date);
        
        sdfReal = new SimpleDateFormat("dd-MMM-YYYY");
        stfReal = new SimpleDateFormat("HH:mm:ss");
        String realDate = sdfReal.format(date);
        String realTime = stfReal.format(date);
        

        timer = new Timer(0, (ActionEvent ae) -> {
            // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        });
        
        
        String billTotal = txtTotalCost.getText();
        String cashPaid = txtCashPaid.getText();
        String balance = txtChangeBalance.getText();
        DefaultTableModel model = (DefaultTableModel) tblCartItems.getModel();
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " +++++++++++++++++++++++++++++++++++++++++++++++++\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " |\t                POS SLIP \t     |\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " +++++++++++++++++++++++++++++++++++++++++++++++++\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " INVOICE NO : ID-"+dat+""+tim+"\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " DATE : "+realDate+"\tTIME: "+realTime+"\n");
        //Heading
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " Product\t" + "Unit-Price\t" + "Total-Cost" + "\n" );
        
        for(int i=0; i<model.getRowCount(); i++){
            String pname = (String)model.getValueAt(i, 1);
            int qty = (Integer)model.getValueAt(i, 3);
            String price = (String)model.getValueAt(i, 4);
            String amount = (String)model.getValueAt(i, 5);

            txtBillSlipArea.setText(txtBillSlipArea.getText()+" "+pname+  "\t"+qty+" * M" +price+ "\tM" +amount+ "\n" );
        }//end of forLoop
        
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " -------------------------------------------------\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + "\t" + "\t" +"Paid\t:M" + cashPaid + "\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + "\t" + "\t" +"Subtotal\t:M" + billTotal + "\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + "\t" + "\t" +"Change\t:M" + balance + "\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " +++++++++++++++++++++++++++++++++++++++++++++++++\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " |            THANK YOU COME AGAIN                |\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " +++++++++++++++++++++++++++++++++++++++++++++++++\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " CASHIER : "+lblSessionUsername.getText()+"\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + "  ________________________________________________\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " | Powered by: Power Force Tech-Solutions Lesotho |\n");
        txtBillSlipArea.setText(txtBillSlipArea.getText() + " +------------------------------------------------+\n");
        
        //updateQuantity(); NOT WORKINF
        printPDFSlip();
        clearCartItemsTableMethod();
        fullResetMethod();
        
    }//end of print slip Method
    
    //print pDF slip as INVOICE
    private void printPDFSlip(){
        try{
            txtBillSlipArea.print();
            
        }catch(java.awt.print.PrinterException ex){
            //PrintStream format;
            JOptionPane.showMessageDialog(rootPane, "Sorry! No Printer Found.\n"+ ex);
        }
    }//end of printPDFSlip
    
    
    //Array of type sales
    private ArrayList<ClassSales> salesList(){
        ArrayList<ClassSales> salesList = new ArrayList<>();
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            String sql = "SELECT * FROM SALES";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            ClassSales sales;
            while(rs.next()){
                sales = new ClassSales(rs.getString("ReceiptNo"), rs.getString("TotalQty"),
                        rs.getString("CashPaid"), rs.getString("TotalCost"),rs.getString("CustomerChange"),
                        rs.getString("Cashier"), rs.getString("SalesDate"));
                salesList.add(sales);
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return salesList;
    }
    
    //display data into a jtable sales
    public void displaySalesMethod(){
        ArrayList<ClassSales> list = salesList();
        DefaultTableModel salesModel = (DefaultTableModel) tblSales.getModel();
        
        Object[] row = new Object[9];
        for(int i=0; i<list.size(); i++){
            row[0] = list.get(i).getRecieptNo();
            row[1] = list.get(i).getTotalQty();
            row[2] = list.get(i).getCashPaid();
            row[3] = list.get(i).getTotalCost();
            row[4] = list.get(i).getChange();
            row[5] = list.get(i).getCashier();
            row[6] = list.get(i).getDate();
            
            salesModel.addRow(row);
        }
    }//endof sales display

    
    
    //fields reset Method
    private void resetFieldMethod(){
        txtProductCode.setText("");
        txtProductName.setText("");
        lblAvailableQuantity.setText("1");
        spinnerQuantity.setValue(1);
        txtUnitPrice.setText("0.00");
        txtCashPaid.setText("0.00");
        txtChangeBalance.setText("0.00");
        txtProductCode.requestFocus();
        btnRemoveItem.setEnabled(false);
        btnAddToCart.setEnabled(false);
        txtCashPaid.setEnabled(false);

        btnCancelBill.setEnabled(false);
        btnPrintBillSlip.disable();
        btnPrintBillSlip.enable(false);
        btnPrintBillSlip.setEnabled(false);
    }//end of resetFieldsMethod
    
    //full reset Method
    private void fullResetMethod(){
        txtProductCode.setText("");
        txtProductName.setText("");
        lblAvailableQuantity.setText("1");
        spinnerQuantity.setValue(1);
        txtUnitPrice.setText("0.00");
        txtTotalCost.setText("0.00");
        txtChangeBalance.setText("0.00");
        btnRemoveItem.setEnabled(false);
        btnAddToCart.setEnabled(false);
        
        txtCashPaid.setText("0.00");
        txtCashPaid.setEnabled(false);
        
        btnCancelBill.setEnabled(false);
        btnPrintBillSlip.disable();
        btnPrintBillSlip.enable(false);
        //btnPrintBillSlip.setVisible(false);
        btnPrintBillSlip.setEnabled(false);
        
    }//end of full resetMethod
    
    
    //method to refresh page
    private void refreshMethod(){
        txtProductCode.setText("");
        txtProductName.setText("");
        lblAvailableQuantity.setText("1");
        spinnerQuantity.setValue(1);
        txtUnitPrice.setText("0.00");
        txtCashPaid.setText("0.00");
        txtCashPaid.setEnabled(false);
        txtTotalCost.setText("0.00");
        txtChangeBalance.setText("0.00");
        btnAddToCart.setEnabled(false);
        
        btnPrintBillSlip.disable();
        btnPrintBillSlip.enable(false);
        btnPrintBillSlip.setEnabled(false);
        txtBillSlipArea.setText("");
    }//end method to refresh page
    
    //method to reset cartItems Table
    private void clearCartItemsTableMethod(){
        try{
            DefaultTableModel tblSalesModel = (DefaultTableModel) tblCartItems.getModel();
            tblSalesModel.setRowCount(0); //if there is data in the table it is removed
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed cancel bill. \n" +error);
        }
    }//end of reset cartItems

    
    //method to cancel bill
    public void cancelBillMethod(){
        
        try{
            /*HomePage obj = new HomePage();*/
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure to cancel bill?", "CANCEL BILL",
                    JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
            
            switch(conf){
                case 0:
                    DefaultTableModel tblModel = (DefaultTableModel) tblCartItems.getModel();
            switch (tblCartItems.getSelectedRowCount()) {
                case 1:
                    double currentTotalCost = 0.00;
                    double totalCostSum = Double.parseDouble(txtTotalCost.getText());
                    for (int i=0; i<tblCartItems.getRowCount(); i++){
                        currentTotalCost = totalCostSum - Double.parseDouble(tblCartItems.getValueAt(i, 4).toString());
                    }
                    updateQuantityPositive();
                    tblModel.removeRow(tblCartItems.getSelectedRow());
                    txtTotalCost.setText(Double.toString(currentTotalCost));
                    
                    clearCartItemsTableMethod();
                    JOptionPane.showMessageDialog(null, "Bill canceled successfully. \n");
                    resetFieldMethod();
                    break;
                case 0:
                    //JOptionPane.showMessageDialog(null, "Sorry you can not delete from an empty table. \n");
                    JOptionPane.showMessageDialog(null, "Sorry! you need to select at least one row from the 'Cart Items' table. \n");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Sorry you can not delete from an empty table. \n");
                    //JOptionPane.showMessageDialog(null, "Sorry! you need to select at least one row from the 'Cart Items' table. \n");
                    break;
            }
                    break;
                case 1:
                    break;
            }
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed cancel bill. \n" +error);
        }
        
    }//end of cancel bill
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tellerPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        salePanel = new javax.swing.JPanel();
        sellItemPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        txtProductCode = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtCashPaid = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtTotalCost = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtChangeBalance = new javax.swing.JTextField();
        btnPrintBillSlip = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        spinnerQuantity = new javax.swing.JSpinner();
        btnCancelBill = new javax.swing.JButton();
        btnRemoveItem = new javax.swing.JButton();
        btnAddToCart = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lblAvailableQuantity = new javax.swing.JLabel();
        productsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblCartItems = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSales = new javax.swing.JTable();
        slipPanel = new javax.swing.JPanel();
        recieptPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtBillSlipArea = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        radioOnlineUser = new javax.swing.JRadioButton();
        lblSessionUsername = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POS (Welcome page)");

        tellerPanel.setBackground(new java.awt.Color(51, 102, 255));
        tellerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "POS (WELCOME)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 36))); // NOI18N

        salePanel.setBackground(new java.awt.Color(153, 255, 153));
        salePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sell Items"));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel14.setText("Product code");

        txtProductCode.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtProductCode.setToolTipText("Enter product code or batch");
        txtProductCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProductCodeKeyPressed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel15.setText("Product name");
        jLabel15.setToolTipText("");

        txtProductName.setEditable(false);
        txtProductName.setBackground(new java.awt.Color(204, 204, 255));
        txtProductName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtProductName.setToolTipText("Product name");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel16.setText("Quantity");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel17.setText("Unit price");

        txtUnitPrice.setEditable(false);
        txtUnitPrice.setBackground(new java.awt.Color(204, 204, 255));
        txtUnitPrice.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtUnitPrice.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtUnitPrice.setText("0.00");
        txtUnitPrice.setToolTipText("Unit price per product");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel18.setText("Cash paid");

        txtCashPaid.setBackground(new java.awt.Color(240, 240, 240));
        txtCashPaid.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtCashPaid.setForeground(new java.awt.Color(0, 153, 51));
        txtCashPaid.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCashPaid.setText("0.00");
        txtCashPaid.setToolTipText("Enter cash payed by customer");
        txtCashPaid.setEnabled(false);
        txtCashPaid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCashPaidKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCashPaidKeyTyped(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel19.setText("Total cost");

        txtTotalCost.setEditable(false);
        txtTotalCost.setBackground(new java.awt.Color(204, 204, 255));
        txtTotalCost.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTotalCost.setForeground(new java.awt.Color(0, 102, 102));
        txtTotalCost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotalCost.setText("0.00");
        txtTotalCost.setToolTipText("Total cost of bought items");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel20.setText("Change");

        txtChangeBalance.setEditable(false);
        txtChangeBalance.setBackground(new java.awt.Color(204, 204, 255));
        txtChangeBalance.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtChangeBalance.setForeground(new java.awt.Color(0, 102, 51));
        txtChangeBalance.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtChangeBalance.setText("0.00");
        txtChangeBalance.setToolTipText("Customer change");

        btnPrintBillSlip.setBackground(new java.awt.Color(0, 153, 0));
        btnPrintBillSlip.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnPrintBillSlip.setText("Print bill");
        btnPrintBillSlip.setToolTipText("Confirm parchase or print slip");
        btnPrintBillSlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintBillSlipActionPerformed(evt);
            }
        });

        jLabel21.setBackground(new java.awt.Color(204, 204, 204));
        jLabel21.setFont(new java.awt.Font("Tahoma", 2, 11)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 102, 102));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Remove item from bill by clicking the Remove");
        jLabel21.setToolTipText("To remove item from the bill, please click on the item from the table under 'Cart items' then click the 'Remove' button above.");

        spinnerQuantity.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        spinnerQuantity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerQuantityStateChanged(evt);
            }
        });
        spinnerQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spinnerQuantityKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                spinnerQuantityKeyTyped(evt);
            }
        });

        btnCancelBill.setBackground(new java.awt.Color(102, 0, 0));
        btnCancelBill.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancelBill.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelBill.setText("Cancel");
        btnCancelBill.setToolTipText("Cancel the bill completely");
        btnCancelBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelBillActionPerformed(evt);
            }
        });

        btnRemoveItem.setBackground(new java.awt.Color(153, 0, 0));
        btnRemoveItem.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnRemoveItem.setForeground(new java.awt.Color(255, 255, 255));
        btnRemoveItem.setText("Remove");
        btnRemoveItem.setToolTipText("Remove item from the bill / cart / wish-list ");
        btnRemoveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveItemActionPerformed(evt);
            }
        });

        btnAddToCart.setBackground(new java.awt.Color(0, 153, 0));
        btnAddToCart.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnAddToCart.setText("Add");
        btnAddToCart.setToolTipText("Add item to bill / cart or wish-list");
        btnAddToCart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToCartActionPerformed(evt);
            }
        });

        lblAvailableQuantity.setBackground(new java.awt.Color(51, 0, 153));
        lblAvailableQuantity.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblAvailableQuantity.setForeground(new java.awt.Color(0, 153, 51));
        lblAvailableQuantity.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvailableQuantity.setText("0");
        lblAvailableQuantity.setToolTipText("Available quantity of selected item");

        javax.swing.GroupLayout sellItemPanelLayout = new javax.swing.GroupLayout(sellItemPanel);
        sellItemPanel.setLayout(sellItemPanelLayout);
        sellItemPanelLayout.setHorizontalGroup(
            sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(sellItemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addContainerGap(14, Short.MAX_VALUE))
            .addComponent(jSeparator4)
            .addGroup(sellItemPanelLayout.createSequentialGroup()
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sellItemPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCancelBill, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPrintBillSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sellItemPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sellItemPanelLayout.createSequentialGroup()
                                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtProductName)
                                    .addComponent(txtProductCode)
                                    .addGroup(sellItemPanelLayout.createSequentialGroup()
                                        .addComponent(lblAvailableQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(spinnerQuantity))
                                    .addComponent(txtUnitPrice, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(sellItemPanelLayout.createSequentialGroup()
                                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(4, 4, 4)
                                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCashPaid)
                                    .addComponent(txtChangeBalance)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sellItemPanelLayout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(txtTotalCost))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sellItemPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnRemoveItem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnAddToCart, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addComponent(jSeparator2)
        );
        sellItemPanelLayout.setVerticalGroup(
            sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sellItemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtProductCode, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sellItemPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(sellItemPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spinnerQuantity)
                    .addComponent(lblAvailableQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtUnitPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtTotalCost)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddToCart, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRemoveItem))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCashPaid)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtChangeBalance)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(sellItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPrintBillSlip, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelBill))
                .addGap(18, 18, 18)
                .addComponent(jLabel21)
                .addContainerGap())
        );

        javax.swing.GroupLayout salePanelLayout = new javax.swing.GroupLayout(salePanel);
        salePanel.setLayout(salePanelLayout);
        salePanelLayout.setHorizontalGroup(
            salePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sellItemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        salePanelLayout.setVerticalGroup(
            salePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(salePanelLayout.createSequentialGroup()
                .addComponent(sellItemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        productsPanel.setBackground(new java.awt.Color(102, 255, 102));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Products"));

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Name", "Qty", "Batch No.", "Volume", "Price", "Fomulation", "Status", "Expiry Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblProduct.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblProduct);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Cart items"));

        tblCartItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Code", "Item Name", "Rem. Qty", "Bought Qty", "Unit Price", "Total Cost", "Cashier"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCartItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCartItemsMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblCartItems);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Sales"));

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Receipt No.", "Total Qty Bought", "Cash Paid", "Total Cost", "Change", "Cashier", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSalesMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblSales);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout productsPanelLayout = new javax.swing.GroupLayout(productsPanel);
        productsPanel.setLayout(productsPanelLayout);
        productsPanelLayout.setHorizontalGroup(
            productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        productsPanelLayout.setVerticalGroup(
            productsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(productsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        slipPanel.setBackground(new java.awt.Color(153, 153, 255));
        slipPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reciept"));

        recieptPanel.setBackground(new java.awt.Color(255, 255, 255));

        txtBillSlipArea.setEditable(false);
        txtBillSlipArea.setColumns(20);
        txtBillSlipArea.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        txtBillSlipArea.setForeground(new java.awt.Color(0, 51, 255));
        txtBillSlipArea.setRows(5);
        jScrollPane2.setViewportView(txtBillSlipArea);

        javax.swing.GroupLayout recieptPanelLayout = new javax.swing.GroupLayout(recieptPanel);
        recieptPanel.setLayout(recieptPanelLayout);
        recieptPanelLayout.setHorizontalGroup(
            recieptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        recieptPanelLayout.setVerticalGroup(
            recieptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        javax.swing.GroupLayout slipPanelLayout = new javax.swing.GroupLayout(slipPanel);
        slipPanel.setLayout(slipPanelLayout);
        slipPanelLayout.setHorizontalGroup(
            slipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(recieptPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        slipPanelLayout.setVerticalGroup(
            slipPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(recieptPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        radioOnlineUser.setBackground(new java.awt.Color(255, 255, 255));
        radioOnlineUser.setForeground(new java.awt.Color(0, 153, 0));
        radioOnlineUser.setSelected(true);
        radioOnlineUser.setText("Online");
        radioOnlineUser.setEnabled(false);

        lblSessionUsername.setBackground(new java.awt.Color(255, 255, 255));
        lblSessionUsername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/login_icon.png"))); // NOI18N
        lblSessionUsername.setText("maluke");
        lblSessionUsername.setToolTipText("Logged in user");

        btnLogout.setBackground(new java.awt.Color(255, 255, 255));
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_logout.PNG"))); // NOI18N
        btnLogout.setToolTipText("Logout from the system");
        btnLogout.setActionCommand("Logout");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnRefresh)
                .addGap(27, 27, 27)
                .addComponent(radioOnlineUser)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSessionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblSessionUsername)
                            .addComponent(radioOnlineUser)
                            .addComponent(btnRefresh)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(salePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(slipPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(slipPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(salePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout tellerPanelLayout = new javax.swing.GroupLayout(tellerPanel);
        tellerPanel.setLayout(tellerPanelLayout);
        tellerPanelLayout.setHorizontalGroup(
            tellerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tellerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        tellerPanelLayout.setVerticalGroup(
            tellerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tellerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tellerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tellerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logoutMethod();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void tblProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseClicked
        // TODO add your handling code here:
        tableProductsMouseClickMenthod();
    }//GEN-LAST:event_tblProductMouseClicked

    private void btnAddToCartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToCartActionPerformed
        // TODO add your handling code here:
        addItemToCartMethod();
        //updateQuantity();
    }//GEN-LAST:event_btnAddToCartActionPerformed

    private void tblCartItemsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCartItemsMouseClicked
        // TODO add your handling code here:
        tableCartItemsMouseClickMenthod();
    }//GEN-LAST:event_tblCartItemsMouseClicked

    private void tblSalesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSalesMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblSalesMouseClicked

    private void txtProductCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProductCodeKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            itemCodeSearchMethod();
        }else{
            evt.consume();
        }
    }//GEN-LAST:event_txtProductCodeKeyPressed

    private void btnPrintBillSlipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintBillSlipActionPerformed
        // TODO add your handling code here:
        balanceChangeMethod();
        //updateQuantity();
        recordSalesMethod();
        //salesBillMethod();
    }//GEN-LAST:event_btnPrintBillSlipActionPerformed

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveItemActionPerformed
        // TODO add your handling code here:
        removeItemFromCartMethod();
        //updateQuantityPositive();
    }//GEN-LAST:event_btnRemoveItemActionPerformed

    private void btnCancelBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelBillActionPerformed
        // TODO add your handling code here:
        cancelBillMethod();
    }//GEN-LAST:event_btnCancelBillActionPerformed

    private void txtCashPaidKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCashPaidKeyPressed
        // TODO add your handling code here:
        //balanceCheckMethod();
        if(Character.isLetter(evt.getKeyChar())){
            evt.consume();
        }else{
            try{
                balanceCheckMethod();
                balanceChangeMethod();
            }catch(NumberFormatException e){
                evt.consume();
            }
        }
    }//GEN-LAST:event_txtCashPaidKeyPressed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        refreshMethod();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void txtCashPaidKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCashPaidKeyTyped
        // TODO add your handling code here:
        if(Character.isLetter(evt.getKeyChar())){
            evt.consume();
        }else{
            try{
                balanceCheckMethod();
                balanceChangeMethod();
            }catch(NumberFormatException e){
                evt.consume();
            }
        }
    }//GEN-LAST:event_txtCashPaidKeyTyped

    private void spinnerQuantityKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spinnerQuantityKeyTyped
        // TODO add your handling code here:
        char character = evt.getKeyChar();
        if(!Character.isDigit(character) || Character.isLetter(character)){
            evt.consume();
            JOptionPane.showMessageDialog(null, "Sorry! only (Whole)numbers / integer values allowed. \n");
            spinnerQuantity.setValue(1);
        }else{
            totalCostMethod();
        }
    }//GEN-LAST:event_spinnerQuantityKeyTyped

    private void spinnerQuantityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spinnerQuantityKeyPressed
        // TODO add your handling code here:
        char character = evt.getKeyChar();
        if(!Character.isDigit(character)){
            evt.consume();
            JOptionPane.showMessageDialog(null, "Sorry! only (Whole)numbers / integer values allowed. \n");
            spinnerQuantity.setValue(1);
        }else{
            totalCostMethod();
        }
    }//GEN-LAST:event_spinnerQuantityKeyPressed

    private void spinnerQuantityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerQuantityStateChanged
        // TODO add your handling code here:
        totalCostMethod();
        //updateQuantity();
    }//GEN-LAST:event_spinnerQuantityStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WelcomeTeller.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new WelcomeTeller().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToCart;
    private javax.swing.JButton btnCancelBill;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnPrintBillSlip;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblAvailableQuantity;
    private javax.swing.JLabel lblSessionUsername;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel productsPanel;
    private javax.swing.JRadioButton radioOnlineUser;
    private javax.swing.JPanel recieptPanel;
    private javax.swing.JPanel salePanel;
    private javax.swing.JPanel sellItemPanel;
    private javax.swing.JPanel slipPanel;
    private javax.swing.JSpinner spinnerQuantity;
    private javax.swing.JTable tblCartItems;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTable tblSales;
    private javax.swing.JPanel tellerPanel;
    private javax.swing.JTextArea txtBillSlipArea;
    private javax.swing.JTextField txtCashPaid;
    private javax.swing.JTextField txtChangeBalance;
    private javax.swing.JTextField txtProductCode;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtTotalCost;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
