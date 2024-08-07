/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

import java.awt.Desktop;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author DELL
 */
public final class ManageStock extends javax.swing.JFrame {

    /**
     * Creates new form WelcomeAdminPage
     */
    
    
    static Connection conn;
    PreparedStatement pst;// = conn.prepareStatement(sql);
    ResultSet rs;// = pst.executeQuery();
    Statement st;
    
    DefaultTableModel productModel = new DefaultTableModel();
    
    public ManageStock() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage();
        productTable();
    }
    
    void loggedinUser(String username){
        lblSessionUsername.setText(username);
    }//end of session
    
    //method to setIconImage
    private void setIconImage() {
        try{
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pharmacy_pos/Images/pos.jpg")));
        }catch(Exception error){
            JOptionPane.showMessageDialog(null, "Failed to set icon image. \n" +error);
        }
    }//end of setIconImage
    
    public void logoutMethod(){
        
        try{
            HomePage home = new HomePage();
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure to logout?", "LOGOUT FROM SYSTEM",
                    JOptionPane.YES_NO_OPTION,JOptionPane.ERROR_MESSAGE);
            switch(conf){
                case 0:
                    dispose();
                    home.show();
                    break;
                case 1:
                    break;
            }  
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to set icon image. \n" +error);
        }
        
    }//end of Logout
    
    
    
    //method to store product into the database
    public void recordProduct(){
        String productCode = txtProductCode.getText();
        String productName = txtProductName.getText();
        String quantity = txtQuantity.getText();
        String batchNo = txtBatchNo.getText();
        String productVolume = txtProductVolume.getText();
        String unitPrice = txtUnitPrice.getText();
        String fomulation = comboBoxFomulation.getSelectedItem().toString();
        String status = comboBoxStatus.getSelectedItem().toString();
       
        
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        String expiryDate = sdf.format(txtExpiryDate.getDate());
        
        
        if(productCode.equals("") || productName.equals("") || quantity.equals("") || batchNo.equals("") || expiryDate.equals("")){
            JOptionPane.showMessageDialog(rootPane, "Sorry! you need to fill-in all fields on the form");
        }else{
            try{
                //getConnection(); //same class
                conn = DBConnection_Pharmacy_POS.getConnection(); //different class
                //conn = DBConnection.getConnection(); //different class
                String sql = "INSERT INTO PRODUCTS(ItemCode,ItemName,Quantity,BatchNo,ItemVolume,UnitPrice,Fomulation,ItemStatus,ExpiryDate) VALUES(?,?,?,?,?,?,?,?,?)"; //(Product)
                pst = conn.prepareStatement(sql);
                //rs = pst.executeQuery();
                
                st = conn.createStatement();
                pst.setString(1, productCode);
                pst.setString(2, productName);
                pst.setString(3,quantity);
                pst.setString(4, batchNo);
                pst.setString(5, productVolume);
                pst.setString(6, unitPrice);
                pst.setString(7, fomulation);
                pst.setString(8, status);
                pst.setString(9, expiryDate);
                

                pst.executeUpdate();
                DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
                model.setRowCount(0); //if there is data in the table it is removed
                productTable(); //then recalled by this method. 
                
                resetMethod();
                JOptionPane.showMessageDialog(null, "Product successfully recorded!");

            }catch(HeadlessException | SQLException ex){
                //JOptionPane.showMessageDialog(null, ex);
                JOptionPane.showMessageDialog(null, "Sorry! Product with the same code is already recorded!\n");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }//end of else     
    }//end of Register Method
    
    
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
    public void productTable(){
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
    
    
    //This method deletes the admin
    private void deleteProductMethod(){
        String productCode = txtProductCode.getText();
        if(productCode.equals("")){
            JOptionPane.showMessageDialog(null, "Please fill the text field for item code.");
        }
        else{
            int del = JOptionPane.showConfirmDialog(null, "Do you really want to delete?","DELETE PRODUCT",JOptionPane.YES_NO_OPTION);
            if(del == 0){
            try{
                //getConnection(); //same class
                conn = DBConnection_Pharmacy_POS.getConnection(); //different class
                //conn = DBConnection.getConnection(); //different class

                String sql = "DELETE FROM PRODUCTS WHERE ItemCode = ?";
                pst = conn.prepareStatement(sql);

                pst.setString(1, txtProductCode.getText());
                pst.executeUpdate();

                DefaultTableModel model = (DefaultTableModel) tblProduct.getModel();
                model.setRowCount(0); //if there is data in the table it is removed
                productTable(); //then recalled by this method.
                
                resetMethod();

                JOptionPane.showMessageDialog(null, "Product deleted Successfully!!");

            }catch(HeadlessException | SQLException ex){
                JOptionPane.showMessageDialog(null,ex);
            }   catch (ClassNotFoundException ex) {
                    Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }//end of else
    }//end of delete Admin
    
    //method to Search product
    private void searchProductMethod(){
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            try{
                String sql = "SELECT * FROM PRODUCTS WHERE ItemCode = ?";
                pst = conn.prepareStatement(sql);
                
                pst.setString(1, txtSearchProduct.getText());
                rs = pst.executeQuery();
                
                if(rs.next()){
                    String code = rs.getString("ItemCode");
                    txtProductCode.setText(code);
                    String name = rs.getString("ItemName");
                    txtProductName.setText(name);
                    String quantity = rs.getString("Quantity");
                    txtQuantity.setText(quantity);
                    String batchNo = rs.getString("BatchNO");
                    txtBatchNo.setText(batchNo);
                    String volume = rs.getString("ItemVolume");
                    txtProductVolume.setText(volume);
                    String unitPrice = rs.getString("UnitPrice");
                    txtUnitPrice.setText(unitPrice);
                    
                    txtSearchProduct.setText("");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Sorry! product not found, please check product code is correct!");
                }
                
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            }
        }catch(ClassNotFoundException | SQLException ex){
            Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE,null, ex);
        }
    }//end of Search Method
    
    
    
    
    private void tableMouseClickMenthod(){
        try{
            int i = tblProduct.getSelectedRow();
            TableModel model = tblProduct.getModel();
            txtProductCode.setText(model.getValueAt(i,0).toString());
            txtProductName.setText(model.getValueAt(i,1).toString());
            txtQuantity.setText(model.getValueAt(i,2).toString());
            txtBatchNo.setText(model.getValueAt(i,3).toString());
            txtProductVolume.setText(model.getValueAt(i,4).toString());
            txtUnitPrice.setText(model.getValueAt(i,5).toString());
            //txtExpiryDate.setText(model.getValueAt(i,8).toString());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Sorry! something went wrong. \n" +error);
        }
    }//end of tableMouseClickMethod
    
    
    //import method
    private void importFromExcelFileMethod(){
        File excelFile; 
        FileInputStream excelFIS;
        BufferedInputStream excelBIS;
        XSSFWorkbook excelJTableImport;
        String defaultCurrentDirectoryPath = "C:";
        JFileChooser excelFileChooser = new JFileChooser(defaultCurrentDirectoryPath); 
        int excelChooser = excelFileChooser.showOpenDialog(null);
        
        //If Open button is clicked
        if(excelChooser == JFileChooser.APPROVE_OPTION){
            try {
                excelFile = excelFileChooser.getSelectedFile();
                excelFIS = new FileInputStream(excelFile);
                excelBIS = new BufferedInputStream(excelFIS);
                
                excelJTableImport = new XSSFWorkbook(excelBIS);
                XSSFSheet excelSheet = excelJTableImport.getSheetAt(0);
                 
                
                //Looping through excel columns and rows
                for (int row=1; row<excelSheet.getLastRowNum(); row++){
                    XSSFRow excelRow = excelSheet.getRow(row);
                    
                    XSSFCell excelItemCode = excelRow.getCell(0);
                    XSSFCell excelItemName = excelRow.getCell(1);
                    XSSFCell excelQuantity = excelRow.getCell(2);
                    XSSFCell excelBatchNo = excelRow.getCell(3);
                    XSSFCell excelItemVolume = excelRow.getCell(4);
                    XSSFCell excelUnitPrice = excelRow.getCell(5);
                    XSSFCell excelFomulation = excelRow.getCell(6);
                    XSSFCell excelItemStatus = excelRow.getCell(7);
                    XSSFCell excelExpiryDate = excelRow.getCell(8);
                    //XSSFCell excelImage = excelRow.getCell(9);
                    
                    System.out.println(excelItemCode);
                    System.out.println(excelItemName);
                    System.out.println(excelQuantity);
                    System.out.println(excelBatchNo);
                    System.out.println(excelItemVolume);
                    System.out.println(excelUnitPrice);
                    System.out.println(excelFomulation);
                    System.out.println(excelItemStatus);
                    System.out.println(excelExpiryDate);
                    
                    //JTable excelJTable = new JTable();
                    //JLabel jLabel = new JLabel(new ImageIcon(new ImageIcon(excelImage.toString()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
                    
                    productModel.addRow(new Object[] {
                        excelItemCode,
                        excelItemName,
                        excelQuantity,
                        excelBatchNo,
                        excelItemVolume,
                        excelUnitPrice,
                        excelFomulation,
                        excelItemStatus,
                        excelExpiryDate,
                        //excelJTable
                    });
                    
                    /*for (int column=0; column<excelRow.getLastCellNum(); column++){
                        XSSFCell excelCell = excelRow.getCell(column);

                        System.out.println(excelCell.getStringCellValue());
                    }*/
                    //JOptionPane.showMessageDialog(null, "Successfully imported data 1. \n");
                    
                }//end of forLoop
                
                JOptionPane.showMessageDialog(null, "Successfully imported data. \n");
                
            } catch (FileNotFoundException error) {
                //Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Sorry! failed to import data from excel file. \n" +error);
            } catch (IOException ex) {
                //Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Sorry! failed to import data from excel file. \n"+ex);
            }
        }else{
            JOptionPane.showMessageDialog(null, "Sorry! failed to import data from excel file. \n");
        }
        
    }//end of import
    
    
    
    //import Data
    private void importDataFromExcelMethod(){
        /**************************************************************************************/
 
        int batchSize = 20;
 
        File excelFile; 
        FileInputStream excelFIS;
        BufferedInputStream excelBIS;
        XSSFWorkbook excelJTableImport;
        String defaultCurrentDirectoryPath = "C:";
        JFileChooser excelFileChooser = new JFileChooser(defaultCurrentDirectoryPath); 
        int excelChooser = excelFileChooser.showOpenDialog(null);
        
        
        if(excelChooser == JFileChooser.APPROVE_OPTION){
        
        try {
            
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            long start = System.currentTimeMillis();
             
            //FileInputStream inputStream = new FileInputStream(excelFilePath);
            excelFile = excelFileChooser.getSelectedFile();
            excelFIS = new FileInputStream(excelFile);
            excelBIS = new BufferedInputStream(excelFIS);
 
            Workbook workbook = new XSSFWorkbook(excelFIS);
 
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();
            
            String sql = "INSERT INTO PRODUCTS(ItemCode,ItemName,Quantity,BatchNo,ItemVolume,UnitPrice,Fomulation,ItemStatus,ExpiryDate) VALUES(?,?,?,?,?,?,?,?,?)"; //(Product)
            PreparedStatement statement = conn.prepareStatement(sql);    
             
            int count = 0;
             
            rowIterator.next(); // skip the header row
            
            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
 
                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();
 
                    int columnIndex = nextCell.getColumnIndex();
 
                    switch (columnIndex) {
                    case 0:
                        String itemCode = nextCell.getStringCellValue();
                        statement.setString(1, itemCode);
                        break;
                    case 1:
                        String itemName = nextCell.getStringCellValue();
                        statement.setString(2, itemName);
                    case 2:
                        /*String itemQty = nextCell.getStringCellValue();
                        statement.setString(3, itemQty);*/
                        int itemQty = (int) nextCell.getNumericCellValue();
                        statement.setInt(3, itemQty);
                        
                    case 3:
                        /*int progress = (int) nextCell.getNumericCellValue();
                        statement.setInt(4, progress);*/
                        String batchNo = nextCell.getStringCellValue();
                        statement.setString(4, batchNo);
                        
                    case 4:
                        /*Date enrollDate = nextCell.getDateCellValue();
                        statement.setTimestamp(5, new Timestamp(enrollDate.getTime()));*/
                        String volume = nextCell.getStringCellValue();
                        statement.setString(5, volume);
                    case 5:
                        /*String unitPrice = nextCell.getStringCellValue();
                        statement.setString(6, unitPrice);*/
                        double unitPrice = (Double) nextCell.getNumericCellValue();
                        statement.setDouble(6, unitPrice);
                        
                    case 6:
                        /*int progress = (int) nextCell.getNumericCellValue();
                        statement.setInt(7, progress);*/
                        String fomulation = nextCell.getStringCellValue();
                        statement.setString(7, fomulation);
                        
                    case 7:
                        /*int progress = (int) nextCell.getNumericCellValue();
                        statement.setInt(8, progress);*/
                        String status = nextCell.getStringCellValue();
                        statement.setString(8, status);
                        
                    case 8:
                        /*String expDate = nextCell.getStringCellValue();
                        statement.setString(9, expDate);*/
                        Date expDate = nextCell.getDateCellValue();
                        statement.setTimestamp(9, new Timestamp(expDate.getTime()));
                    }
                }
                
                statement.addBatch();
                 
                if (count % batchSize == 0) {
                    statement.executeBatch();
                } 
            }
 
            workbook.close();
             
            //execute the remaining queries
            statement.executeBatch();
             
            long end = System.currentTimeMillis();
            //System.out.printf("Import done in %d ms\n", (end - start));
            JOptionPane.showMessageDialog(null, "Import done in %d ms\n"+ (end - start));
             
        } catch (IOException ex1) {
            JOptionPane.showMessageDialog(null, "Sorry! Error reading file. \n"+ex1);
        } catch (SQLException ex2) {
            JOptionPane.showMessageDialog(null, "Sorry! Database error. \n"+ex2);
        }   catch (ClassNotFoundException ex) {
                Logger.getLogger(ManageStock.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        }
        
        /**************************************************************************************/
    }//end of import
    
    
    
    //method to open file
    private void openFile(String file){
        try{
            File path = new File(file);
            Desktop.getDesktop().open(path);
        }catch(IOException ioe){
            //System.out.println(ioe);
            JOptionPane.showConfirmDialog(null, "Error: can not save file.\n"+ioe);
        }
    }//end of open file method
    
    //method to export data
    private void exportToExcelFileMethod(JTable jt){
        try{
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.showSaveDialog(jt);
            File saveFile = jFileChooser.getSelectedFile();

            if(saveFile != null){
                saveFile = new File(saveFile.toString()+".xlsx");
                FileOutputStream out;
                //XSSFWorkbook wb = new XSSFWorkbook();
                try (Workbook wb = new XSSFWorkbook()) {
                    //XSSFWorkbook wb = new XSSFWorkbook();
                    Sheet sheet = wb.createSheet("PRODUCTS");
                    org.apache.poi.ss.usermodel.Row rowCol = sheet.createRow(0);
                    for(int i=0; i<jt.getColumnCount(); i++){
                        org.apache.poi.ss.usermodel.Cell cell = rowCol.createCell(i);
                        cell.setCellValue(jt.getColumnName(i));
                    }//end of forLoop
                    for(int j=0; j<jt.getRowCount(); j++){
                        org.apache.poi.ss.usermodel.Row row = sheet.createRow(j+1);
                        for(int k=0; k<jt.getColumnCount(); k++){
                            org.apache.poi.ss.usermodel.Cell cell = row.createCell(k);
                            if(jt.getValueAt(j, k) != null){
                                cell.setCellValue(jt.getValueAt(j, k).toString());
                            }//end of if
                        }//end of nested forLoop
                    }//end of forLoop
                    out = new FileOutputStream(new File(saveFile.toString()));
                    wb.write(out);
                }
                out.close();
                openFile(saveFile.toString());
                
            }else{
                JOptionPane.showConfirmDialog(null, "Error: can not save file");
            }
        }catch(FileNotFoundException e){
            //System.out.println(e);
            JOptionPane.showConfirmDialog(null, "Error: can not save file.\n"+e);
        }catch(IOException ioe){
            //System.out.println(ioe);
            JOptionPane.showConfirmDialog(null, "Error: can not save file.\n"+ioe);
        }
    }//end of export method
    
    
    //resetMethod
    private void resetMethod(){
        try{
            
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Sorry failed to reset form fields. \n" +error);
        }
    }//end of reset method
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    
    //salePage Method
    private void salesPageMethod(){
        try{
            ManageSales sales = new  ManageSales();
            dispose();
            sales.show(); 
            sales.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to sales. \n" +error);
        }
    }//end of sales Page method
    
    
    
    
    
    //salePage Method
    private void reportsPageMethod(){
        try{
            ManageReports reports = new  ManageReports();
            dispose();
            reports.show(); 
            reports.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to load reports. \n" +error);
        }
    }//end of sales Page method
    
    
    //employeePage Method
    private void employeesPageMethod(){
        try{
            ManageEmployee employee = new  ManageEmployee();
            dispose();
            employee.show();
            employee.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to load employee page. \n" +error);
        }
    }//end of sales Page method
    
    
    //salePage Method
    private void adminPageMethod(){
        try{
            ManageAdmin admin = new  ManageAdmin();
            dispose();
            admin.show();
            admin.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to load admin. \n" +error);
        }
    }//end of sales Page method
    
    
    //salePage Method
    private void logFilePageMethod(){
        try{
            ManageLogFile logFile = new  ManageLogFile();
            dispose();
            logFile.show();
            logFile.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to load logFile. \n" +error);
        }
    }//end of sales Page method
    
    //////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Container_Panel = new javax.swing.JPanel();
        Menu_Panel = new javax.swing.JPanel();
        Products_Panel = new javax.swing.JPanel();
        btnProducts = new javax.swing.JButton();
        btnSales = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnLogFile = new javax.swing.JButton();
        btnEmployee = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        Product_Details_Panel = new javax.swing.JPanel();
        Add_Product = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearchProduct = new javax.swing.JTextField();
        data_Separator = new javax.swing.JSeparator();
        lblProductCode = new javax.swing.JLabel();
        txtProductCode = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        txtProductName = new javax.swing.JTextField();
        Manupulate_Panel = new javax.swing.JPanel();
        btnSaveProduct = new javax.swing.JButton();
        btnUpdateProduct = new javax.swing.JButton();
        btnDeleteProduct = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUnitPrice = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        comboBoxStatus = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtBatchNo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtProductVolume = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        comboBoxFomulation = new javax.swing.JComboBox<>();
        txtExpiryDate = new com.toedter.calendar.JDateChooser();
        Manage_Products = new javax.swing.JPanel();
        Available_Products = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProduct = new javax.swing.JTable();
        sessionManagementPanel = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        radioOnline = new javax.swing.JRadioButton();
        lblSessionUsername = new javax.swing.JLabel();
        Manage_Product_Panel = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POS (Products page)");

        Container_Panel.setBackground(new java.awt.Color(0, 204, 102));
        Container_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "POS (Products page)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Elephant", 1, 36))); // NOI18N

        Menu_Panel.setBackground(new java.awt.Color(0, 204, 153));
        Menu_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MENU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dubai Light", 1, 12))); // NOI18N

        Products_Panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_products.jpg"))); // NOI18N
        btnProducts.setText("Products");
        btnProducts.setEnabled(false);

        btnSales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_sales.jpg"))); // NOI18N
        btnSales.setText("Sales");
        btnSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalesActionPerformed(evt);
            }
        });

        btnReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_reports.jpg"))); // NOI18N
        btnReports.setText("Reports");
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
            }
        });

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnLogFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_logfile.jpg"))); // NOI18N
        btnLogFile.setText("Log files");
        btnLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogFileActionPerformed(evt);
            }
        });

        btnEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_employee.png"))); // NOI18N
        btnEmployee.setText("Employee");
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });

        btnAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_admin.jpg"))); // NOI18N
        btnAdmin.setText("Admin");
        btnAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdminActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Products_PanelLayout = new javax.swing.GroupLayout(Products_Panel);
        Products_Panel.setLayout(Products_PanelLayout);
        Products_PanelLayout.setHorizontalGroup(
            Products_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Products_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Products_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnProducts, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReports, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        Products_PanelLayout.setVerticalGroup(
            Products_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Products_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnProducts)
                .addGap(18, 18, 18)
                .addComponent(btnSales)
                .addGap(18, 18, 18)
                .addComponent(btnReports)
                .addGap(18, 18, 18)
                .addComponent(btnLogFile)
                .addGap(18, 18, 18)
                .addComponent(btnEmployee)
                .addGap(18, 18, 18)
                .addComponent(btnAdmin)
                .addGap(18, 18, 18)
                .addComponent(btnImport)
                .addGap(18, 18, 18)
                .addComponent(btnExport)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Menu_PanelLayout = new javax.swing.GroupLayout(Menu_Panel);
        Menu_Panel.setLayout(Menu_PanelLayout);
        Menu_PanelLayout.setHorizontalGroup(
            Menu_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Products_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Menu_PanelLayout.setVerticalGroup(
            Menu_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Menu_PanelLayout.createSequentialGroup()
                .addComponent(Products_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        Product_Details_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Product Details"));
        Product_Details_Panel.setToolTipText("Search product");

        Add_Product.setBackground(new java.awt.Color(0, 102, 102));
        Add_Product.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Product"));

        lblSearch.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblSearch.setText("Search Product");

        txtSearchProduct.setToolTipText("Search product");
        txtSearchProduct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchProductKeyPressed(evt);
            }
        });

        lblProductCode.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductCode.setText("Item-Code");

        txtProductCode.setToolTipText("Enter product code");

        lblProductName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblProductName.setText("Item-Name");

        txtProductName.setToolTipText("Enter product name");

        btnSaveProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_save.jpg"))); // NOI18N
        btnSaveProduct.setText("Save");
        btnSaveProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProductActionPerformed(evt);
            }
        });

        btnUpdateProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_update.jpg"))); // NOI18N
        btnUpdateProduct.setText("Update");

        btnDeleteProduct.setBackground(new java.awt.Color(204, 0, 0));
        btnDeleteProduct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_delete.jpg"))); // NOI18N
        btnDeleteProduct.setText("Delete");
        btnDeleteProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteProductActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Manupulate_PanelLayout = new javax.swing.GroupLayout(Manupulate_Panel);
        Manupulate_Panel.setLayout(Manupulate_PanelLayout);
        Manupulate_PanelLayout.setHorizontalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSaveProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdateProduct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDeleteProduct)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Manupulate_PanelLayout.setVerticalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveProduct)
                    .addComponent(btnUpdateProduct)
                    .addComponent(btnDeleteProduct))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Quantity");

        txtQuantity.setToolTipText("Enter quantity must be a number");
        txtQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtQuantityKeyTyped(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setText("Unit Price");

        txtUnitPrice.setToolTipText("Enter unit cost (money)");
        txtUnitPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUnitPriceKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Status");

        comboBoxStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available" }));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setText("Expiry Date");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel5.setText("Batch No.");

        txtBatchNo.setToolTipText("Enter batch number");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel6.setText("Volume");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setText("Fomulation");

        comboBoxFomulation.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "...select...", "Suspension", "Syrup", "Tablets", "Others" }));

        txtExpiryDate.setToolTipText("Enter expiry date");

        javax.swing.GroupLayout Add_ProductLayout = new javax.swing.GroupLayout(Add_Product);
        Add_Product.setLayout(Add_ProductLayout);
        Add_ProductLayout.setHorizontalGroup(
            Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_ProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(data_Separator)
                    .addGroup(Add_ProductLayout.createSequentialGroup()
                        .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Manupulate_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(Add_ProductLayout.createSequentialGroup()
                                .addComponent(lblSearch)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtSearchProduct))
                            .addGroup(Add_ProductLayout.createSequentialGroup()
                                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(21, 21, 21)
                                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(comboBoxFomulation, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtUnitPrice, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtProductVolume)
                                    .addComponent(comboBoxStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(Add_ProductLayout.createSequentialGroup()
                                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblProductCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblProductName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtQuantity)
                                    .addComponent(txtBatchNo)
                                    .addComponent(txtProductName, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtProductCode, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtExpiryDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap())))
        );
        Add_ProductLayout.setVerticalGroup(
            Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_ProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(data_Separator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode)
                    .addComponent(txtProductCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtBatchNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtProductVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(comboBoxFomulation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(comboBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtExpiryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(Manupulate_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Manage_Products.setBackground(new java.awt.Color(0, 102, 102));

        tblProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item Code", "Item Name", "Quantity", "Batch No.", "Volume", "Unit Price", "Fomulation", "Status", "Expiry Date"
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

        javax.swing.GroupLayout Available_ProductsLayout = new javax.swing.GroupLayout(Available_Products);
        Available_Products.setLayout(Available_ProductsLayout);
        Available_ProductsLayout.setHorizontalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        Available_ProductsLayout.setVerticalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout Manage_ProductsLayout = new javax.swing.GroupLayout(Manage_Products);
        Manage_Products.setLayout(Manage_ProductsLayout);
        Manage_ProductsLayout.setHorizontalGroup(
            Manage_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Available_Products, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Manage_ProductsLayout.setVerticalGroup(
            Manage_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Available_Products, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        sessionManagementPanel.setBackground(new java.awt.Color(255, 255, 255));

        btnLogout.setBackground(new java.awt.Color(255, 255, 255));
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_logout.PNG"))); // NOI18N
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        radioOnline.setBackground(new java.awt.Color(0, 153, 51));
        radioOnline.setSelected(true);
        radioOnline.setText("Online");
        radioOnline.setEnabled(false);

        lblSessionUsername.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/login_icon.png"))); // NOI18N
        lblSessionUsername.setToolTipText("Logged in user");

        javax.swing.GroupLayout sessionManagementPanelLayout = new javax.swing.GroupLayout(sessionManagementPanel);
        sessionManagementPanel.setLayout(sessionManagementPanelLayout);
        sessionManagementPanelLayout.setHorizontalGroup(
            sessionManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sessionManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioOnline)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSessionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        sessionManagementPanelLayout.setVerticalGroup(
            sessionManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sessionManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sessionManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogout)
                    .addComponent(radioOnline)
                    .addComponent(lblSessionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Manage_Product_Panel.setBackground(new java.awt.Color(0, 204, 102));
        Manage_Product_Panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("MANAGE PRODUCTS HERE");

        javax.swing.GroupLayout Manage_Product_PanelLayout = new javax.swing.GroupLayout(Manage_Product_Panel);
        Manage_Product_Panel.setLayout(Manage_Product_PanelLayout);
        Manage_Product_PanelLayout.setHorizontalGroup(
            Manage_Product_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_Product_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 382, Short.MAX_VALUE)
                .addContainerGap())
        );
        Manage_Product_PanelLayout.setVerticalGroup(
            Manage_Product_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_Product_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout Product_Details_PanelLayout = new javax.swing.GroupLayout(Product_Details_Panel);
        Product_Details_Panel.setLayout(Product_Details_PanelLayout);
        Product_Details_PanelLayout.setHorizontalGroup(
            Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Product_Details_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Add_Product, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Manage_Products, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Product_Details_PanelLayout.createSequentialGroup()
                        .addComponent(Manage_Product_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sessionManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        Product_Details_PanelLayout.setVerticalGroup(
            Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Product_Details_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Add_Product, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Product_Details_PanelLayout.createSequentialGroup()
                        .addGroup(Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sessionManagementPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Manage_Product_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Manage_Products, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout Container_PanelLayout = new javax.swing.GroupLayout(Container_Panel);
        Container_Panel.setLayout(Container_PanelLayout);
        Container_PanelLayout.setHorizontalGroup(
            Container_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Container_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Menu_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Product_Details_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        Container_PanelLayout.setVerticalGroup(
            Container_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Container_PanelLayout.createSequentialGroup()
                .addGroup(Container_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Menu_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Product_Details_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Container_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Container_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tblProductMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductMouseClicked
        // TODO add your handling code here:
        tableMouseClickMenthod();
    }//GEN-LAST:event_tblProductMouseClicked

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logoutMethod();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesActionPerformed
        // TODO add your handling code here:
        salesPageMethod();
    }//GEN-LAST:event_btnSalesActionPerformed

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
        // TODO add your handling code here:
        adminPageMethod();
    }//GEN-LAST:event_btnAdminActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        // TODO add your handling code here:
        employeesPageMethod();
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnSaveProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveProductActionPerformed
        // TODO add your handling code here:
        recordProduct();
    }//GEN-LAST:event_btnSaveProductActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:
        reportsPageMethod();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        //importFromExcelFileMethod();
        importDataFromExcelMethod();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        exportToExcelFileMethod(tblProduct);
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogFileActionPerformed
        // TODO add your handling code here:
        logFilePageMethod();
    }//GEN-LAST:event_btnLogFileActionPerformed

    private void txtQuantityKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQuantityKeyTyped
        // TODO add your handling code here:
        char character = evt.getKeyChar();
        if(!Character.isDigit(character)){
            evt.consume();
            JOptionPane.showMessageDialog(null, "Sorry! only (Whole)numbers / integer values allowed. \n");
        }
    }//GEN-LAST:event_txtQuantityKeyTyped

    private void txtSearchProductKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchProductKeyPressed
        // TODO add your handling code here:
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            searchProductMethod();
        }
    }//GEN-LAST:event_txtSearchProductKeyPressed

    private void txtUnitPriceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUnitPriceKeyTyped
        // TODO add your handling code here:
        if(Character.isLetter(evt.getKeyChar())){
            evt.consume();
        }
    }//GEN-LAST:event_txtUnitPriceKeyTyped

    private void btnDeleteProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteProductActionPerformed
        // TODO add your handling code here:
        deleteProductMethod();
    }//GEN-LAST:event_btnDeleteProductActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ManageStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageStock.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ManageStock().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Add_Product;
    private javax.swing.JPanel Available_Products;
    private javax.swing.JPanel Container_Panel;
    private javax.swing.JPanel Manage_Product_Panel;
    private javax.swing.JPanel Manage_Products;
    private javax.swing.JPanel Manupulate_Panel;
    private javax.swing.JPanel Menu_Panel;
    private javax.swing.JPanel Product_Details_Panel;
    private javax.swing.JPanel Products_Panel;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnDeleteProduct;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnLogFile;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSales;
    private javax.swing.JButton btnSaveProduct;
    private javax.swing.JButton btnUpdateProduct;
    private javax.swing.JComboBox<String> comboBoxFomulation;
    private javax.swing.JComboBox<String> comboBoxStatus;
    private javax.swing.JSeparator data_Separator;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblProductCode;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSessionUsername;
    private javax.swing.JRadioButton radioOnline;
    private javax.swing.JPanel sessionManagementPanel;
    private javax.swing.JTable tblProduct;
    private javax.swing.JTextField txtBatchNo;
    private com.toedter.calendar.JDateChooser txtExpiryDate;
    private javax.swing.JTextField txtProductCode;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtProductVolume;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSearchProduct;
    private javax.swing.JTextField txtUnitPrice;
    // End of variables declaration//GEN-END:variables
}
