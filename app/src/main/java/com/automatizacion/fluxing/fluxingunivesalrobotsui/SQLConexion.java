package com.automatizacion.fluxing.fluxingunivesalrobotsui;

/**
 * Created by jorge on 12/01/2018.
 */

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLConexion {

    public static Connection ConnectionHelper() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conexion = null;

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
           // ConnectionURL = "jdbc:jtds:sqlserver://192.168.15.131;port=1433;databaseName=FluxingUniversalRobots;user=sa;password=Flux1ng2017;";
            conexion = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.15.131:1433/FluxingUniversalRobots", "sa", "Flux1ng2017");


            System.out.println("Conectado ");
        } catch (SQLException se) {
            Log.e("ERROR", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROR", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        return conexion;
    }
}




