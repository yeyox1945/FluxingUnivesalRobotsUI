package com.automatizacion.fluxing.fluxingunivesalrobotsui;

/**
 * Created by jorge on 12/01/2018.
 */

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//Fluxing.ddns.net:1433:1433  -- Remota
//192.168.15.131:1433   -- local

public class ConnectSQL {

    private String IP, DB, User, Password;
    public static String Robot = "";

    @SuppressLint("NewApi")
    public Connection ConnectSQL() {

       // IP = "Fluxing.ddns.net:1433";
        IP = "192.168.15.131:1433";// ip del servidor sql
        DB = "FluxingUniversalRobot";
        User = "sa";
        Password = "Flux1ng2017";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.sql.Connection connection = null;
            String ConnectionURL = "jdbc:jtds:sqlserver://" + IP + ";databaseName=" + DB + ";user=" + User + ";password=" + Password + ";loginTimeout=2;socketTimeout=2";
            try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
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

    public Boolean RegisterRobot(String Name, String Model, String IP, String Directorio) {

        boolean Validate = false;

        if (Name.equals("") || Model.equals("") || IP.equals("") || Directorio.equals(""))
            return Validate;

        Validate_Connection();

        if (Validate_Connection() == false) {
            System.out.println("No se pudo conectar a la BD");
        } else {

            try {
                PreparedStatement pst = ConnectSQL().prepareStatement("INSERT INTO RegistroRobot(Nombre,Modelo,IP,Directorio) VALUES(?,?,?,?);");
                pst.setString(1, Name);
                pst.setString(2, Model);
                pst.setString(3, IP);
                pst.setString(4, Directorio);

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

    public Boolean DeleteRobot(String Name, String Model, String IP, String Directorio) {

        boolean Validate = false;

        if (Name.equals("") || Model.equals("") || IP.equals("") || Directorio.equals(""))
            return Validate;

        Validate_Connection();

        if (Validate_Connection() == false) {
            System.out.println("No se pudo conectar a la BD");
        } else {

            try {
                PreparedStatement pst = ConnectSQL().prepareStatement("DELETE FROM RegistroRobot WHERE Nombre=? AND Modelo=? AND IP=? AND Directorio=?");
                pst.setString(1, Name);
                pst.setString(2, Model);
                pst.setString(3, IP);
                pst.setString(4, Directorio);

                pst.executeUpdate();

                Validate = true;

            } catch (SQLException e) {
                System.out.println("SQL  error : " + e.getMessage());
            } catch (NullPointerException e) {
                System.out.println("null error : " + e.getMessage());
            }
        }
        return Validate;
    }

    public Boolean ModifyRobot(String Name, String Model, String IP, String Directorio) {

        boolean Validate = false;

        if (Name.equals("") || Model.equals("") || IP.equals("") || Directorio.equals(""))
            return Validate;

        Validate_Connection();

        if (Validate_Connection() == false) {
            System.out.println("No se pudo conectar a la BD");
        } else {

            try {
                PreparedStatement pst = ConnectSQL().prepareStatement("UPDATE RegistroRobot SET Nombre=");
                pst.setString(1, Name);
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

    public static String dir = "/programs";

    public String Fill_Combo_IP_RobotsSQL() {

        PreparedStatement stmt;
        ResultSet rs;
        String Robot_for_Combobox = "";

        ConnectRobotFragment ConnectRobot = new ConnectRobotFragment();

        try {
            String Query = "SELECT * FROM [FluxingUniversalRobot].[dbo].[RegistroRobot] ORDER BY id;";
            stmt = ConnectSQL().prepareStatement(Query);
            rs = stmt.executeQuery();

            System.out.println("Entra a SQL");
            while (rs.next()) {

                String id = rs.getString(1).trim();
                String Nombre = rs.getString(2).trim();
                String Modelo = rs.getString(3).trim();
                String ip = rs.getString(4).trim();
                dir = rs.getString(5).trim();

                Robot_for_Combobox = id + " - " + Nombre + " - " + Modelo + " - " + ip;
                Robot = id + " - " + Nombre + " - " + Modelo + " - " + ip + " - " + dir;

                ConnectRobot.RobotsList.add(Robot_for_Combobox);
                AddRobotFragment.RobotsList.add(Robot_for_Combobox);
            }
        } catch (SQLException e) {
            System.out.println("Error al llenar Spinner :" + e.getMessage());
        } catch (NullPointerException e) {
        }
        return Robot_for_Combobox;
    }

}




