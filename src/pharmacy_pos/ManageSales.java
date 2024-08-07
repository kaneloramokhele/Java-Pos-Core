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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
//import org.apache.poi.sl.usermodel.Sheet;
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
public final class ManageSales extends javax.swing.JFrame {

    /**
     * Creates new form AdminDashBord
     */
    
    static Connection conn;
    PreparedStatement pst;// = conn.prepareStatement(sql);
    ResultSet rs;// = pst.executeQuery();
    Statement st;
    
    
    public ManageSales() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage();
        salesMethod();
        totalSalesMethod();
        salesCountMethod();
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
            JOptionPane.showMessageDialog(null, "Failed to set icon image. \n" +error);
        }
        
    }//end of Logout
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    
    
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
    public void salesMethod(){
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
    
    
    //method to search sales
    private ArrayList<ClassSales> searchSalesList(){
         ArrayList<ClassSales> salesList = new ArrayList<>();
        //DefaultTableModel salesModel = new DefaultTableModel(); 
        String searchSales = txtSearchSales.getText();
        
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            
            String sql = ("SELECT * FROM SALES WHERE Cashier='"+searchSales+"' OR SalesDate='"+searchSales+"'");
            //String sql = ("SELECT * FROM SALES WHERE SalesDate="+searchSales+"");
            //String sql = "SELECT * FROM SALES";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

             ClassSales sales;
            while(rs.next()){
                sales = new ClassSales(rs.getString("ReceiptNo"), rs.getString("TotalQty"),
                        rs.getString("CashPaid"), rs.getString("TotalCost"),rs.getString("CustomerChange"),
                        rs.getString("Cashier"), rs.getString("SalesDate"));
                salesList.add(sales);
            }
            
            //lblCashierSales.setText("CASHIER SALES");
            //lblCashierSalesAmount.setText("CASHIER SALES M");
            //salesCountMethodReRsults();
            //totalSalesMethodResults();
            
            DefaultTableModel tblSalesModel = (DefaultTableModel) tblSearchSales.getModel();
            tblSalesModel.setRowCount(0); //if there is data in the table it is removed
            
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageSales.class.getName()).log(Level.SEVERE, null, ex);
        }
        return salesList;
    }//end of search sales
    
    
    //display data into a jtable sales
    public void searchSalesMethod(){
        ArrayList<ClassSales> list = searchSalesList();
        DefaultTableModel salesModel = (DefaultTableModel) tblSearchSales.getModel();
        
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
    
    
    //Total sales method start here
    private void salesCountMethod(){
        
        int sum = 0;
        for (int i=0; i<tblSales.getRowCount(); i++){
            sum = sum + Integer.parseInt(tblSales.getValueAt(i, 1).toString());
        }
        txtSalesCount.setText(Integer.toString(sum));
    }//end of sales count
    
    //Total Sales method
    private void totalSalesMethod(){
        
        double sum = 0.00;
        for (int i = 0; i<tblSales.getRowCount(); i++){
            sum = sum + Double.parseDouble(tblSales.getValueAt(i, 3).toString());
        }
        txtTotalSales.setText("M "+sum);
    }//end of sales count
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                            Search Sales                                                     //
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    private void salesCountMethodReRsults(){
        
        int sum = 0;
        for (int i=0; i<tblSearchSales.getRowCount(); i++){
            sum = sum + Integer.parseInt(tblSearchSales.getValueAt(i, 1).toString());
        }
        txtSalesCount.setText(Integer.toString(sum));
    }//end of sales count
    
    //Total Sales method
    private void totalSalesMethodResults(){
        
        double sum = 0.00;
        for (int i = 0; i<tblSearchSales.getRowCount(); i++){
            sum = sum + Double.parseDouble(tblSearchSales.getValueAt(i, 3).toString());
        }
        txtTotalSales.setText("M "+sum);
    }//end of sales count
    
    
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
                    Sheet sheet = wb.createSheet("SALES");
                    Row rowCol = sheet.createRow(0);
                    for(int i=0; i<jt.getColumnCount(); i++){
                        org.apache.poi.ss.usermodel.Cell cell = rowCol.createCell(i);
                        cell.setCellValue(jt.getColumnName(i));
                    }//end of forLoop
                    for(int j=0; j<jt.getRowCount(); j++){
                        Row row = sheet.createRow(j+1);
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
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////
    //                                   
    /////////////////////////////////////////////////////////////////////////////////////////////////
    //producPage Method
    private void productsPageMethod(){
        try{
            ManageStock products = new  ManageStock();
            dispose();
            products.show(); 
            products.loggedinUser(lblSessionUsername.getText());
        }catch(HeadlessException error){
            JOptionPane.showMessageDialog(null, "Failed to products. \n" +error);
        }
    }//end of products Page method
    
   
    
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
        btnLogFile = new javax.swing.JButton();
        btnEmployee = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExportSales = new javax.swing.JButton();
        Product_Details_Panel = new javax.swing.JPanel();
        Add_Product = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearchSales = new javax.swing.JTextField();
        data_Separator = new javax.swing.JSeparator();
        lblCashierSalesAmount = new javax.swing.JLabel();
        txtTotalSales = new javax.swing.JTextField();
        Manupulate_Panel = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblCashierSales = new javax.swing.JLabel();
        txtSalesCount = new javax.swing.JTextField();
        Manage_Products = new javax.swing.JPanel();
        Available_Products = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblSearchSales = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSales = new javax.swing.JTable();
        sessionManagementPanel = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        radioOnline = new javax.swing.JRadioButton();
        lblSessionUsername = new javax.swing.JLabel();
        Manage_Product_Panel = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POS (Sales page)");

        Container_Panel.setBackground(new java.awt.Color(0, 204, 102));
        Container_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "POS (Sales page)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Elephant", 1, 36))); // NOI18N

        Menu_Panel.setBackground(new java.awt.Color(0, 204, 153));
        Menu_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MENU", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dubai Light", 1, 12))); // NOI18N

        Products_Panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnProducts.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_products.jpg"))); // NOI18N
        btnProducts.setText("Products");
        btnProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProductsActionPerformed(evt);
            }
        });

        btnSales.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_sales.jpg"))); // NOI18N
        btnSales.setText("Sales");
        btnSales.setEnabled(false);

        btnReports.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_reports.jpg"))); // NOI18N
        btnReports.setText("Reports");
        btnReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReportsActionPerformed(evt);
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

        btnImport.setText("Import");
        btnImport.setEnabled(false);

        btnExportSales.setText("Export");
        btnExportSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportSalesActionPerformed(evt);
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
                    .addComponent(btnLogFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEmployee, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExportSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(btnExportSales)
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

        Product_Details_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder("Sales Details"));
        Product_Details_Panel.setToolTipText("Search product");

        Add_Product.setBackground(new java.awt.Color(0, 102, 102));
        Add_Product.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Product"));

        lblSearch.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lblSearch.setText("Search sales");

        txtSearchSales.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtSearchSales.setToolTipText("Search sales by cashier or date");
        txtSearchSales.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtSearchSalesFocusLost(evt);
            }
        });
        txtSearchSales.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchSalesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchSalesKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSearchSalesKeyTyped(evt);
            }
        });

        lblCashierSalesAmount.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblCashierSalesAmount.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCashierSalesAmount.setText("TOTAL AMOUNT");

        txtTotalSales.setEditable(false);
        txtTotalSales.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtTotalSales.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtTotalSales.setToolTipText("Total sales made");

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_save.jpg"))); // NOI18N
        jButton2.setText("Save");

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_update.jpg"))); // NOI18N
        jButton3.setText("Update");

        jButton4.setBackground(new java.awt.Color(204, 0, 0));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_delete.jpg"))); // NOI18N
        jButton4.setText("Delete");

        javax.swing.GroupLayout Manupulate_PanelLayout = new javax.swing.GroupLayout(Manupulate_Panel);
        Manupulate_Panel.setLayout(Manupulate_PanelLayout);
        Manupulate_PanelLayout.setHorizontalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addContainerGap(17, Short.MAX_VALUE))
        );
        Manupulate_PanelLayout.setVerticalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 3, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SALES");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                .addContainerGap())
        );

        lblCashierSales.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblCashierSales.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblCashierSales.setText("SALES");

        txtSalesCount.setEditable(false);
        txtSalesCount.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        txtSalesCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtSalesCount.setToolTipText("Total sales made");

        javax.swing.GroupLayout Add_ProductLayout = new javax.swing.GroupLayout(Add_Product);
        Add_Product.setLayout(Add_ProductLayout);
        Add_ProductLayout.setHorizontalGroup(
            Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(data_Separator, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(Add_ProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Manupulate_Panel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Add_ProductLayout.createSequentialGroup()
                        .addComponent(lblSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearchSales))
                    .addGroup(Add_ProductLayout.createSequentialGroup()
                        .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCashierSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblCashierSalesAmount))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTotalSales, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .addComponent(txtSalesCount))))
                .addContainerGap())
        );
        Add_ProductLayout.setVerticalGroup(
            Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_ProductLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearchSales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(data_Separator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCashierSales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSalesCount, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(Add_ProductLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCashierSalesAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtTotalSales, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Manupulate_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Manage_Products.setBackground(new java.awt.Color(0, 102, 102));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Current month", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        tblSearchSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RecieptNo", "Total Qty", "Cash Paid", "Total Bill", "Customer Change", "Cashier", "Sales Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSearchSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblSearchSalesMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblSearchSales);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Business sales", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        tblSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "RecieptNo", "Total Qty", "Cash Paid", "Total Bill", "Customer Change", "Cashier", "Sales Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblSales);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout Available_ProductsLayout = new javax.swing.GroupLayout(Available_Products);
        Available_Products.setLayout(Available_ProductsLayout);
        Available_ProductsLayout.setHorizontalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        Available_ProductsLayout.setVerticalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        javax.swing.GroupLayout sessionManagementPanelLayout = new javax.swing.GroupLayout(sessionManagementPanel);
        sessionManagementPanel.setLayout(sessionManagementPanelLayout);
        sessionManagementPanelLayout.setHorizontalGroup(
            sessionManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sessionManagementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(radioOnline)
                .addGap(18, 18, 18)
                .addComponent(lblSessionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
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
        lblHeader.setText("MANAGE SALES HERE");

        javax.swing.GroupLayout Manage_Product_PanelLayout = new javax.swing.GroupLayout(Manage_Product_Panel);
        Manage_Product_Panel.setLayout(Manage_Product_PanelLayout);
        Manage_Product_PanelLayout.setHorizontalGroup(
            Manage_Product_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_Product_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(Manage_Product_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sessionManagementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logoutMethod();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:
        productsPageMethod();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:
        reportsPageMethod();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnExportSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportSalesActionPerformed
        // TODO add your handling code here:
        //exportPageMethod();
        exportToExcelFileMethod(tblSales);
    }//GEN-LAST:event_btnExportSalesActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        // TODO add your handling code here:
        employeesPageMethod();
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdminActionPerformed
        // TODO add your handling code here:
        adminPageMethod();
    }//GEN-LAST:event_btnAdminActionPerformed

    private void btnLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogFileActionPerformed
        // TODO add your handling code here:
        logFilePageMethod();
    }//GEN-LAST:event_btnLogFileActionPerformed

    private void tblSearchSalesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblSearchSalesMouseClicked
        // TODO add your handling code here:
        
    }//GEN-LAST:event_tblSearchSalesMouseClicked

    private void txtSearchSalesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchSalesKeyTyped
        // TODO add your handling code here:
        searchSalesMethod();
        salesCountMethodReRsults();
        totalSalesMethodResults();
    }//GEN-LAST:event_txtSearchSalesKeyTyped

    private void txtSearchSalesKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchSalesKeyPressed
       // TODO add your handling code here:
       if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            searchSalesMethod();
            salesCountMethodReRsults();
            totalSalesMethodResults();
        }
    }//GEN-LAST:event_txtSearchSalesKeyPressed

    private void txtSearchSalesFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSearchSalesFocusLost
        // TODO add your handling code here:
        searchSalesMethod();
        salesCountMethodReRsults();
        totalSalesMethodResults();
    }//GEN-LAST:event_txtSearchSalesFocusLost

    private void txtSearchSalesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchSalesKeyReleased
        // TODO add your handling code here:
        searchSalesMethod();
        salesCountMethodReRsults();
        totalSalesMethodResults();
    }//GEN-LAST:event_txtSearchSalesKeyReleased

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
            java.util.logging.Logger.getLogger(ManageSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ManageSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ManageSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ManageSales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new ManageSales().setVisible(true);
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
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExportSales;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnLogFile;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSales;
    private javax.swing.JSeparator data_Separator;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCashierSales;
    private javax.swing.JLabel lblCashierSalesAmount;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSessionUsername;
    private javax.swing.JRadioButton radioOnline;
    private javax.swing.JPanel sessionManagementPanel;
    private javax.swing.JTable tblSales;
    private javax.swing.JTable tblSearchSales;
    private javax.swing.JTextField txtSalesCount;
    private javax.swing.JTextField txtSearchSales;
    private javax.swing.JTextField txtTotalSales;
    // End of variables declaration//GEN-END:variables
}
