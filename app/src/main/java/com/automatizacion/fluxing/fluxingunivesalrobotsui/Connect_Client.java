package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.util.Log;
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
                    Log.i("Recibio", texto);
                } else {
                    TxtLog = "\nServidor :  Desconectado.";//Cuando se cierra el servidor
                }
            } catch (Exception e) {
                TxtLog = "\nError : No se pudo conectar al robot"; // cuando da error
            }
        }

    }

    public void enviarMSG(String msg) {
        try {
            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes(msg + "\n");
            TxtLog = "\nCliente : " + msg + "\n";//Cuando le envio un mensaje
        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
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
            TxtLog = "\nServidor Conectado.";

        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
        }
    }

    public void desconectar() {
        try {
            Stop = true;
            s.close();
        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
        }
    }
}
