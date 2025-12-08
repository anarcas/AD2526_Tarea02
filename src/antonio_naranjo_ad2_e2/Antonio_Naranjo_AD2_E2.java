/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package antonio_naranjo_ad2_e2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

/**
 *
 * @author anaranjo
 */
public class Antonio_Naranjo_AD2_E2 {

    private static Statement stmt;
    private static Connection con;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String connectionUrl = "jdbc:mysql://localhost/campeonato_atletismo?"
                    + "user=root&password=root";

            con = DriverManager.getConnection(connectionUrl);
            
        } catch (SQLException e) {
            System.err.println("SQL Exception" + e.toString());

        } catch (ClassNotFoundException cE) {
            System.out.println("Exception: " + cE.toString());
        }
    }

   
}
