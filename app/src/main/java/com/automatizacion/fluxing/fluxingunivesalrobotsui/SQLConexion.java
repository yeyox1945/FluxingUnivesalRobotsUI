package com.automatizacion.fluxing.fluxingunivesalrobotsui;

/**
 * Created by jorge on 12/01/2018.
 */

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//Fluxing.ddns.net:1433:1433  -- Remota
//192.168.15.131:1433   -- local

public class SQLConexion {

    private String IP, DB, User, Password;


    @SuppressLint("NewApi")
    public Connection ConnectSQL() {

        //IP = "Fluxing.ddns.net:1433";
        IP = "192.168.15.131:1433";
        DB = "FluxingUniversalRobot";
        User = "sa";
        Password = "Flux1ng2017";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.sql.Connection connection = null;
        String ConnectionURL;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + IP + ";databaseName=" + DB + ";user=" + User + ";password=" + Password + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        } catch (SQLException se) {
            Log.e("error here 1 : ", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("error here 2 : ", e.getMessage());
        } catch (Exception e) {
            Log.e("error here 3 : ", e.getMessage());
        }
        return connection;
    }

    public Boolean Validate_Connection() {
        Boolean Connected;

        if (ConnectSQL() != null) {
            Connected = true;
        } else {
            Connected = false;
        }
        return Connected;
    }

    public Boolean RegisterRobot(String Name, String Model, String IP) {

        boolean Validate = false;

        Validate_Connection();

        if (Validate_Connection() == false) {
            System.out.println("No se pudo conectar a la BD");
        } else {

            try {
                PreparedStatement pst = ConnectSQL().prepareStatement("INSERT INTO dbo.FluxingUniversalRobots(Nombre,Modelo,IP) VALUES(?,?,?);");
                pst.setString(1, Name);
                pst.setString(2, Model);
                pst.setString(3, IP);

                pst.executeUpdate();

                Validate = true;

            } catch (SQLException e) {
                //   Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("SQL  error : " + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("null error : " + e.getMessage());
                // Toast.makeText(this, "Llena correctamente todos los campos", Toast.LENGTH_SHORT).show();

            }
        }

        return Validate;
    }

}




