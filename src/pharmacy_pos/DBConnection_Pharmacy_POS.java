/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author DELL
 */
public class DBConnection_Pharmacy_POS {

    /**
     * @param args the command line arguments
     */
    
    static Connection conn;
    PreparedStatement pst;// = conn.prepareStatement(sql);
    ResultSet rs;// = pst.executeQuery();*/
    //ResultSet rs;
    
    public static void main(String[] args) {
        // TODO code application logic here
        HomePage home = new HomePage();
        home.setVisible(true);
                
    }//end of main class
    
    
    public static Connection getConnection() throws ClassNotFoundException, SQLException{
        try{
            //1. Get Connection
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost/";
            //String dBName = "D:\\APP\\pos_health_care_services_ngakeng_pharmacy";
            String dBName = "POS_Health_Care_Services_Ngakeng_Pharmacy";
            String username = "root";
            String password = "";

            Class.forName(driver);
            conn = DriverManager.getConnection(url+dBName, username, password);
            //conn.setAutoCommit(false);
               
        }catch(ClassNotFoundException | SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        return conn;
    }//end of connection 
}
