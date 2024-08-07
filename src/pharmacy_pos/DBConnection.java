/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

/**
 *
 * @author Mafia Cartel
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {
	
    static Connection conn;

    protected static Connection getConnection() throws ClassNotFoundException, SQLException{

            try {
                //1. Get Connection
                String driver = "com.mysql.cj.jdbc.Driver";
                String url = "jdbc:mysql://localhost/";
                String dBName = "W_POS";
                String username = "root";
                String password = "";

                Class.forName(driver);
                conn = DriverManager.getConnection(url+dBName, username, password);

            }catch(ClassNotFoundException | SQLException ex){
            JOptionPane.showMessageDialog(null, ex);
        }
        return conn;
    }
}
