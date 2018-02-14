package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * @author Jorge Manzano
 */
public class Connect_Client extends Thread {

    private Socket s;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private int port = 29999;
    private String ip = ConnectRobotFragment.ip_Robot;

    private static boolean Stop = false;

    public static String TxtLog;

    public Connect_Client(String ip, int port) {
        this.port = port;
        this.ip = ip;
        Stop = false;
    }


    @Override
    public void run() {
        //Metodo en segundo plano
        String texto;

        while (!Stop) {
            try {
                texto = entrada.readLine();
                if (texto != null) {
                    TxtLog = "Servidor : " + texto;
                    ConnectRobotFragment.PrintTxtLog();
                } else {
                    TxtLog = "Servidor :  Desconectado.";//Cuando se cierra el servidor
                    ConnectRobotFragment.PrintTxtLog();
                }
            } catch (Exception e) {
                try{
                    TxtLog = "Error : No se pudo conectar al robot"; // cuando da error
                    ConnectRobotFragment.PrintTxtLog();
                }catch (Exception x){
                    x.printStackTrace();
                }

            }
        }

    }

    public void enviarMSG(String msg) {
        try {
            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes(msg + "\n");
            TxtLog = "Cliente : " + msg ;//Cuando le envio un mensaje
            ConnectRobotFragment.PrintTxtLog();
        } catch (IOException e) {
            TxtLog = "Error : " + e.getMessage(); // cuando da error
            ConnectRobotFragment.PrintTxtLog();
        }
    }

    public String leerMSG() {
        try {
            return entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void conectar() {

        //Conexion de cliente
        try {
            s = new Socket(ip, port);
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());
            TxtLog = "Servidor Conectado.";
            ConnectRobotFragment.PrintTxtLog();

        } catch (IOException e) {
            TxtLog = "Error : " + e.getMessage(); // cuando da error
            ConnectRobotFragment.PrintTxtLog();
        }
    }

    public void desconectar() {
        try {
            Stop = true;
            s.close();
        } catch (IOException e) {
            TxtLog = "Error : " + e.getMessage(); // cuando da error
            ConnectRobotFragment.PrintTxtLog();
        }
    }


}
