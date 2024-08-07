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
import javax.swing.table.TableModel;
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
public final class ManageAdmin extends javax.swing.JFrame {

    /**
     * Creates new form AdminDashBord
     */
    
    static Connection conn;
    PreparedStatement pst;// = conn.prepareStatement(sql);
    ResultSet rs;// = pst.executeQuery();
    Statement st;
    String userRole = "Admin";
    
    DefaultTableModel adminModel = new DefaultTableModel();
    
    
    
    public ManageAdmin() {
        initComponents();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage();
        adminTable();
    }
    
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
    
    //method to setIconImage
    private void setIconImage() {
        try{
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/pharmacy_pos/Images/pos.jpg")));
            //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("launcher_image.png")));
            //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pharmacy_pos/Images/launcher_icon.jpg")));
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
    
    
    //method to store Admin registered into the database
    public void registerAdmin(){
        String name = txtName.getText();
        String surname = txtSurname.getText();
        String contact = txtContactPhone.getText();
        String idPassport = txtIDPassport.getText();
        String nationality = comboBoxNationality.getSelectedItem().toString();
        String district = comboBoxDistrict.getSelectedItem().toString();
        String resident = txtResidentPlace.getText();
        String gender = radioGroupGender.getSelection().toString();
        String nextOfKin = txtNextOfKin.getText();
        userRole = comboBoxUserRole.getSelectedItem().toString();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        //String username = hash.generateHash( txtUsername.getText(), "sha-512");
        String confirmPass = txtConfirmPassword.getText();
        
        if(name.equals("") || surname.equals("") || username.equals("") || password.equals("")){
            JOptionPane.showMessageDialog(rootPane, "Sorry you need to Fillin all fields on the form");
        }
        else if(!confirmPass.equals(password)){
            JOptionPane.showMessageDialog(rootPane, "Sorry passwords do not match");
        }else{
            try{
                //getConnection(); //same class
                conn = DBConnection_Pharmacy_POS.getConnection(); //different class
                //conn = DBConnection.getConnection(); //different class
                String sql = "INSERT INTO USERS(FirstName,Surname,ContactPhone,ID_Passport,Nationality,District,Resident,Gender,Next_of_Kin,UserRole,Username,UserPassword)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"; //(Admin)
                pst = conn.prepareStatement(sql);
                //rs = pst.executeQuery();
                
                st = conn.createStatement();
                pst.setString(1, name);
                pst.setString(2, surname);
                pst.setString(3,contact);
                pst.setString(4, idPassport);
                pst.setString(5, nationality);
                pst.setString(6, district);
                pst.setString(7, resident);
                pst.setString(8, "Female");
                pst.setString(9, nextOfKin);
                pst.setString(10, userRole);
                pst.setString(11, name+"."+surname);
                pst.setString(12, password);
                

                pst.executeUpdate();
                DefaultTableModel model = (DefaultTableModel) tblAdmin.getModel();
                model.setRowCount(0); //if there is data in the table it is removed
                adminTable(); //then recalled by this method.
                
                resetMethod();
                JOptionPane.showMessageDialog(null, "Successfully registered :"+name+" "+surname);

            }catch(HeadlessException | SQLException ex){
                JOptionPane.showMessageDialog(null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*private String md5(char[] c){
            try{
                MessageDigest digs = MessageDigest.getInstance("MD5");
                digs.update((new String(c)).getBytes("UTF8"));
                digs.digest();
                return "";
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AdminRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
                return "";
            }*/
        }//end of else     
    }//end of Register Method
    
    
    private void setUsernameMethod(){
        String username;
        if(txtName.getText().equals("") && txtSurname.getText().equals("")){
            username = txtName.getSelectedText()+"."+txtSurname.getText();
            txtUsername.setText(username);
        }else{
            JOptionPane.showMessageDialog(null, "Sorry! username is required ");
        }
    }//end of setUsermane
    
    
    //Array of type User 
    private ArrayList<ClassUser> userList(){
        ArrayList<ClassUser> userList = new ArrayList<>(); 
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            
            //String sql = "SELECT * FROM USERS WHERE UserRole = "+userRole;
            String sql = "SELECT * FROM USERS";
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            ClassUser user;
            while(rs.next()){
                user = new ClassUser(rs.getString("FirstName"), rs.getString("Surname"),
                        rs.getString("ContactPhone"), rs.getString("ID_Passport"),rs.getString("Nationality"),
                        rs.getString("District"), rs.getString("Resident"), rs.getString("Gender"),
                        rs.getString("Next_of_Kin"), rs.getString("UserRole"), rs.getString("Username"));
                userList.add(user);
            }
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userList;
    }
    
    //display data into a jtable
    public void adminTable(){
        ArrayList<ClassUser> list = userList();
        DefaultTableModel model = (DefaultTableModel) tblAdmin.getModel();
        
        Object[] row = new Object[11];
        for(int i=0; i<list.size(); i++){
            row[0] = list.get(i).getName();
            row[1] = list.get(i).getSurname();
            row[2] = list.get(i).getPhone();
            row[3] = list.get(i).getIdPassport();
            row[4] = list.get(i).getNationality();
            row[5] = list.get(i).getDistrict();
            row[6] = list.get(i).getResident();
            row[7] = list.get(i).getGender();
            row[8] = list.get(i).getNextOfKin();
            row[9] = list.get(i).getUserRole();
            row[10] = list.get(i).getUsername();
            
            model.addRow(row);
        }
    }//endof 
    
    
    //This method deletes the admin
    private void deleteAdminMethod(){
        String user = txtUsername.getText();
        if(user.equals("")){
            JOptionPane.showMessageDialog(null, "Please fill the text field for username");
        }
        else{
            int del = JOptionPane.showConfirmDialog(null, "Do you really want to delete?","Delete",JOptionPane.YES_NO_OPTION);
            if(del == 0){
            try{
                //getConnection(); //same class
                conn = DBConnection_Pharmacy_POS.getConnection(); //different class
                //conn = DBConnection.getConnection(); //different class

                String sql = "DELETE FROM USERS WHERE Username = ?";
                pst = conn.prepareStatement(sql);

                pst.setString(1, txtUsername.getText());
                pst.executeUpdate();

                DefaultTableModel model = (DefaultTableModel) tblAdmin.getModel();
                model.setRowCount(0); //if there is data in the table it is removed
                adminTable(); //then recalled by this method.
                
                resetMethod();

                JOptionPane.showMessageDialog(null, "Admin Deleted Successfully!!");

            }catch(HeadlessException | SQLException ex){
                JOptionPane.showMessageDialog(null,ex);
            }   catch (ClassNotFoundException ex) {
                    Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }//end of else
    }//end of delete Admin
    
    //method to Search Admin
    private void searchAdminMethod(){
        try{
            //getConnection(); //same class
            conn = DBConnection_Pharmacy_POS.getConnection(); //different class
            //conn = DBConnection.getConnection(); //different class
            try{
                String sql = "SELECT * FROM USERS WHERE Username = ?";
                pst = conn.prepareStatement(sql);
                
                pst.setString(1, txtSearchUser.getText());
                rs = pst.executeQuery();
                
                if(rs.next()){
                    String name = rs.getString("FirstName");
                    txtName.setText(name);
                    String surname = rs.getString("Surname");
                    txtSurname.setText(surname);
                    String phone = rs.getString("ContactPhone");
                    txtContactPhone.setText(phone);
                    String idPassport = rs.getString("ID_Passport");
                    txtIDPassport.setText(idPassport);
                    String resident = rs.getString("Resident");
                    txtResidentPlace.setText(resident);
                    String nextOfKin = rs.getString("Next_of_Kin");
                    txtNextOfKin.setText(nextOfKin);
                    String username = rs.getString("Username");
                    txtUsername.setText(username);
                    
                    txtSearchUser.setText("");
                }
                else{
                    JOptionPane.showMessageDialog(null, "Sorry! please ensure that you spelt the username correct!");
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
            int i = tblAdmin.getSelectedRow();
            TableModel model = tblAdmin.getModel();
            txtName.setText(model.getValueAt(i,0).toString());
            txtSurname.setText(model.getValueAt(i,1).toString());
            txtContactPhone.setText(model.getValueAt(i,2).toString());
            txtIDPassport.setText(model.getValueAt(i,3).toString());
            txtResidentPlace.setText(model.getValueAt(i,6).toString());
            txtNextOfKin.setText(model.getValueAt(i,8).toString());
            //txtUsername.setText(model.getValueAt(i,9).toString());
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
        String defaultCurrentDirectoryPath = "D:";
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
                for (int row=0; row<excelSheet.getLastRowNum(); row++){
                    XSSFRow excelRow = excelSheet.getRow(row);
                    for (int column=0; column<excelRow.getLastCellNum(); column++){
                        XSSFCell excelCell = excelRow.getCell(column);
                        
                        System.out.println(excelCell.getStringCellValue());
                    } 
                }
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ManageAdmin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
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
                    Sheet sheet = wb.createSheet("USERS");
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
            JOptionPane.showMessageDialog(null, "Failed to load invoice. \n" +error);
        }
    }//end of sales Page method
    
    
    
    //employeePage Method
    private void employeePageMethod(){
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

        radioGroupGender = new javax.swing.ButtonGroup();
        Container_Panel = new javax.swing.JPanel();
        Menu_Panel = new javax.swing.JPanel();
        Products_Panel = new javax.swing.JPanel();
        btnProducts = new javax.swing.JButton();
        btnSales = new javax.swing.JButton();
        btnReports = new javax.swing.JButton();
        btnEmployee = new javax.swing.JButton();
        btnAdmin = new javax.swing.JButton();
        btnLogFile = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        Product_Details_Panel = new javax.swing.JPanel();
        Add_Admin = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearchUser = new javax.swing.JTextField();
        data_Separator = new javax.swing.JSeparator();
        lblProductCode = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        txtSurname = new javax.swing.JTextField();
        Manupulate_Panel = new javax.swing.JPanel();
        btnSaveRegister = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        lblPhone = new javax.swing.JLabel();
        txtContactPhone = new javax.swing.JTextField();
        lblIDPassport = new javax.swing.JLabel();
        txtIDPassport = new javax.swing.JTextField();
        lblNationality = new javax.swing.JLabel();
        comboBoxNationality = new javax.swing.JComboBox<>();
        lblDistrictCiry = new javax.swing.JLabel();
        lblGender = new javax.swing.JLabel();
        radioMale = new javax.swing.JRadioButton();
        radioFemale = new javax.swing.JRadioButton();
        lblUsername = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        comboBoxDistrict = new javax.swing.JComboBox<>();
        lblResident = new javax.swing.JLabel();
        txtResidentPlace = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblNextOfKin = new javax.swing.JLabel();
        txtNextOfKin = new javax.swing.JTextField();
        lblUserRole = new javax.swing.JLabel();
        comboBoxUserRole = new javax.swing.JComboBox<>();
        lblConfirmPass = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        Manage_Products = new javax.swing.JPanel();
        Available_Products = new javax.swing.JPanel();
        tableScrollPane = new javax.swing.JScrollPane();
        tblAdmin = new javax.swing.JTable();
        sessionManagementPanel = new javax.swing.JPanel();
        btnLogout = new javax.swing.JButton();
        radioOnline = new javax.swing.JRadioButton();
        lblSessionUsername = new javax.swing.JLabel();
        Manage_Product_Panel = new javax.swing.JPanel();
        lblHeader = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("POS (Sales page)");

        Container_Panel.setBackground(new java.awt.Color(0, 204, 102));
        Container_Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "POS (Admin details)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Elephant", 1, 36))); // NOI18N

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

        btnEmployee.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_employee.png"))); // NOI18N
        btnEmployee.setText("Employee");
        btnEmployee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEmployeeActionPerformed(evt);
            }
        });

        btnAdmin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_admin.jpg"))); // NOI18N
        btnAdmin.setText("Admin");
        btnAdmin.setEnabled(false);

        btnLogFile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_logfile.jpg"))); // NOI18N
        btnLogFile.setText("Log files");
        btnLogFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogFileActionPerformed(evt);
            }
        });

        btnImport.setText("Import");
        btnImport.setEnabled(false);
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
                    .addComponent(btnEmployee, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdmin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLogFile, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnImport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        Product_Details_Panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Product_Details_Panel.setToolTipText("Search product");

        Add_Admin.setBackground(new java.awt.Color(0, 102, 102));
        Add_Admin.setBorder(javax.swing.BorderFactory.createTitledBorder("Add Admin"));

        lblSearch.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblSearch.setText("Search admin");

        txtSearchUser.setToolTipText("Search admin");
        txtSearchUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSearchUserKeyPressed(evt);
            }
        });

        lblProductCode.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblProductCode.setText("Name");

        txtName.setToolTipText("Enter admin name");

        lblProductName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblProductName.setText("Surname");

        txtSurname.setToolTipText("Enter admin surname");

        btnSaveRegister.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_save.jpg"))); // NOI18N
        btnSaveRegister.setText("Save");
        btnSaveRegister.setToolTipText("Save admin details");
        btnSaveRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveRegisterActionPerformed(evt);
            }
        });

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_update.jpg"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setToolTipText("Update admin details");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(204, 0, 0));
        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pharmacy_pos/Images/btn_delete.jpg"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setToolTipText("Delete admin details");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Manupulate_PanelLayout = new javax.swing.GroupLayout(Manupulate_Panel);
        Manupulate_Panel.setLayout(Manupulate_PanelLayout);
        Manupulate_PanelLayout.setHorizontalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSaveRegister)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdate)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Manupulate_PanelLayout.setVerticalGroup(
            Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manupulate_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Manupulate_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSaveRegister)
                    .addComponent(btnUpdate)
                    .addComponent(btnDelete))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblPhone.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPhone.setText("Phone");

        txtContactPhone.setToolTipText("Enter contact no. must be a phone number");
        txtContactPhone.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtContactPhoneKeyTyped(evt);
            }
        });

        lblIDPassport.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblIDPassport.setText("ID/Passport");

        txtIDPassport.setToolTipText("Enter ID or passort no.");

        lblNationality.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNationality.setText("Nationality");

        comboBoxNationality.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "...select...", "Mophuthi", "Xhosa", "Mosotho", "Lekhooa", "Others" }));
        comboBoxNationality.setToolTipText("Select nationality");

        lblDistrictCiry.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblDistrictCiry.setText("District/City");

        lblGender.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblGender.setText("Gender");

        radioGroupGender.add(radioMale);
        radioMale.setText("Male");
        radioMale.setToolTipText("Select this for male");
        radioMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioMaleActionPerformed(evt);
            }
        });

        radioGroupGender.add(radioFemale);
        radioFemale.setText("Female");
        radioFemale.setToolTipText("Select this for female");

        lblUsername.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUsername.setText("Username");

        txtUsername.setToolTipText("Eneter admin date username");

        comboBoxDistrict.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "...select...", "Quthing", "Mohale's Hoek", "Mafeteng", "Maseru", "Leribe", "Berea", "Mokhotlong", "Thaba-Tseka", "Butha-Buthe", "Qacha's Neck" }));
        comboBoxDistrict.setToolTipText("Select admin district");

        lblResident.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblResident.setText("Resident Place");

        txtResidentPlace.setToolTipText("Enter current place of resident");

        lblPassword.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblPassword.setText("Password");

        txtPassword.setToolTipText("Enter admin password");

        lblNextOfKin.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblNextOfKin.setText("Next of Kin");

        txtNextOfKin.setToolTipText("Enter the closed person name to this admin");

        lblUserRole.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblUserRole.setText("User Role");

        comboBoxUserRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "...select...", "Admin", "Assistant" }));
        comboBoxUserRole.setToolTipText("Select user role");

        lblConfirmPass.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lblConfirmPass.setText("Confirm pass");

        txtConfirmPassword.setToolTipText("Enter confirm password");

        javax.swing.GroupLayout Add_AdminLayout = new javax.swing.GroupLayout(Add_Admin);
        Add_Admin.setLayout(Add_AdminLayout);
        Add_AdminLayout.setHorizontalGroup(
            Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(data_Separator)
            .addGroup(Add_AdminLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Manupulate_Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Add_AdminLayout.createSequentialGroup()
                        .addComponent(lblSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearchUser))
                    .addGroup(Add_AdminLayout.createSequentialGroup()
                        .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblConfirmPass))
                        .addGap(18, 18, 18)
                        .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPassword)
                            .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(Add_AdminLayout.createSequentialGroup()
                        .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Add_AdminLayout.createSequentialGroup()
                                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblDistrictCiry, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblProductCode, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblProductName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblPhone, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblIDPassport, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblNationality, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(Add_AdminLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtIDPassport)
                                            .addComponent(txtSurname)
                                            .addComponent(txtContactPhone)
                                            .addComponent(comboBoxNationality, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtName, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(Add_AdminLayout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(comboBoxDistrict, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(Add_AdminLayout.createSequentialGroup()
                                .addComponent(lblResident, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtResidentPlace))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Add_AdminLayout.createSequentialGroup()
                                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblNextOfKin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblUserRole, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtUsername)
                                    .addComponent(txtNextOfKin)
                                    .addComponent(comboBoxUserRole, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(Add_AdminLayout.createSequentialGroup()
                                .addComponent(lblGender, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(radioMale, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(radioFemale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addContainerGap())
        );
        Add_AdminLayout.setVerticalGroup(
            Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Add_AdminLayout.createSequentialGroup()
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearch)
                    .addComponent(txtSearchUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(data_Separator, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(txtSurname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtContactPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPhone))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtIDPassport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblIDPassport))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comboBoxNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNationality))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDistrictCiry)
                    .addComponent(comboBoxDistrict, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblResident)
                    .addComponent(txtResidentPlace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGender)
                    .addComponent(radioMale)
                    .addComponent(radioFemale))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNextOfKin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNextOfKin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserRole)
                    .addComponent(comboBoxUserRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Add_AdminLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConfirmPass)
                    .addComponent(txtConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Manupulate_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Manage_Products.setBackground(new java.awt.Color(0, 102, 102));

        tblAdmin.setBackground(new java.awt.Color(204, 204, 204));
        tblAdmin.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Surname", "Contacts", "ID/Passport", "Nationality", "District/City", "Resident", "Gender", "Next Of Kin", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAdmin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAdminMouseClicked(evt);
            }
        });
        tableScrollPane.setViewportView(tblAdmin);

        javax.swing.GroupLayout Available_ProductsLayout = new javax.swing.GroupLayout(Available_Products);
        Available_Products.setLayout(Available_ProductsLayout);
        Available_ProductsLayout.setHorizontalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        Available_ProductsLayout.setVerticalGroup(
            Available_ProductsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Available_ProductsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
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
                .addContainerGap(12, Short.MAX_VALUE))
        );

        Manage_Product_Panel.setBackground(new java.awt.Color(0, 204, 102));
        Manage_Product_Panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblHeader.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHeader.setText("MANAGE ADMIN HERE");

        javax.swing.GroupLayout Manage_Product_PanelLayout = new javax.swing.GroupLayout(Manage_Product_Panel);
        Manage_Product_Panel.setLayout(Manage_Product_PanelLayout);
        Manage_Product_PanelLayout.setHorizontalGroup(
            Manage_Product_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Manage_Product_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHeader, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
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
                .addComponent(Add_Admin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(Add_Admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Product_Details_PanelLayout.createSequentialGroup()
                        .addGroup(Product_Details_PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(sessionManagementPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Manage_Product_Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void tblAdminMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAdminMouseClicked
        // TODO add your handling code here:
        tableMouseClickMenthod();
    }//GEN-LAST:event_tblAdminMouseClicked

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        logoutMethod();
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProductsActionPerformed
        // TODO add your handling code here:
        productsPageMethod();
    }//GEN-LAST:event_btnProductsActionPerformed

    private void radioMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radioMaleActionPerformed

    private void btnSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesActionPerformed
        // TODO add your handling code here:
        salesPageMethod();
    }//GEN-LAST:event_btnSalesActionPerformed

    private void btnReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReportsActionPerformed
        // TODO add your handling code here:
        reportsPageMethod();
    }//GEN-LAST:event_btnReportsActionPerformed

    private void btnEmployeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEmployeeActionPerformed
        // TODO add your handling code here:
        employeePageMethod();
    }//GEN-LAST:event_btnEmployeeActionPerformed

    private void btnLogFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogFileActionPerformed
        // TODO add your handling code here:
        logFilePageMethod();
    }//GEN-LAST:event_btnLogFileActionPerformed

    private void btnSaveRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveRegisterActionPerformed
        // TODO add your handling code here:
        registerAdmin();
    }//GEN-LAST:event_btnSaveRegisterActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        //updateAdmin();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        deleteAdminMethod();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        // TODO add your handling code here:
        //importPageMethod();
        importFromExcelFileMethod();
    }//GEN-LAST:event_btnImportActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        exportToExcelFileMethod(tblAdmin);
    }//GEN-LAST:event_btnExportActionPerformed

    private void txtContactPhoneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtContactPhoneKeyTyped
        // TODO add your handling code here:
        char character = evt.getKeyChar();
        if(!Character.isDigit(character)){
            evt.consume();
            JOptionPane.showMessageDialog(null, "Sorry! only (Whole)numbers / integer values allowed. \n");
        }
    }//GEN-LAST:event_txtContactPhoneKeyTyped

    private void txtSearchUserKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchUserKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            searchAdminMethod();
        }
    }//GEN-LAST:event_txtSearchUserKeyPressed

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
            java.util.logging.Logger.getLogger(ManageAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
            new ManageAdmin().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Add_Admin;
    private javax.swing.JPanel Available_Products;
    private javax.swing.JPanel Container_Panel;
    private javax.swing.JPanel Manage_Product_Panel;
    private javax.swing.JPanel Manage_Products;
    private javax.swing.JPanel Manupulate_Panel;
    private javax.swing.JPanel Menu_Panel;
    private javax.swing.JPanel Product_Details_Panel;
    private javax.swing.JPanel Products_Panel;
    private javax.swing.JButton btnAdmin;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEmployee;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnLogFile;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnProducts;
    private javax.swing.JButton btnReports;
    private javax.swing.JButton btnSales;
    private javax.swing.JButton btnSaveRegister;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> comboBoxDistrict;
    private javax.swing.JComboBox<String> comboBoxNationality;
    private javax.swing.JComboBox<String> comboBoxUserRole;
    private javax.swing.JSeparator data_Separator;
    private javax.swing.JLabel lblConfirmPass;
    private javax.swing.JLabel lblDistrictCiry;
    private javax.swing.JLabel lblGender;
    private javax.swing.JLabel lblHeader;
    private javax.swing.JLabel lblIDPassport;
    private javax.swing.JLabel lblNationality;
    private javax.swing.JLabel lblNextOfKin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblProductCode;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblResident;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSessionUsername;
    private javax.swing.JLabel lblUserRole;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JRadioButton radioFemale;
    private javax.swing.ButtonGroup radioGroupGender;
    private javax.swing.JRadioButton radioMale;
    private javax.swing.JRadioButton radioOnline;
    private javax.swing.JPanel sessionManagementPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JTable tblAdmin;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtContactPhone;
    private javax.swing.JTextField txtIDPassport;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtNextOfKin;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtResidentPlace;
    private javax.swing.JTextField txtSearchUser;
    private javax.swing.JTextField txtSurname;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
