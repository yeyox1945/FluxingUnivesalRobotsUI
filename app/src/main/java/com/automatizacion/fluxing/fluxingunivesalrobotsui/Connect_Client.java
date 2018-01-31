package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
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

    public String TxtLog;
    public String serverResponse = "sin respuesta";
    public MainActivity Main = new MainActivity();

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
                synchronized (this) {
                    final String finalTexto = texto;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (finalTexto != null) {
                                serverResponse = finalTexto;
                                TxtLog = "\nServidor : " + serverResponse; //Imprime la conversacion
                                Main.PrintToTextview(TxtLog);

                                Log.i("Recibio:", finalTexto);

                            } else {
                                TxtLog = "\nServidor :  Desconectado.";//Cuando se cierra el servidor
                                Main.PrintToTextview(TxtLog);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                TxtLog = "\nError :" + e.getMessage(); // cuando da error
                Main.PrintToTextview(TxtLog);
            } catch (Exception e) {
                TxtLog = "\nError :" + e.getMessage(); // cuando da error
                Main.PrintToTextview(TxtLog);
            }
        }

    }


    public void enviarMSG(String msg) {
        try {
            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes(msg + "\n");
            TxtLog = "\nCliente : " + msg + "\n";//Cuando le envio un mensaje
            Main.PrintToTextview(TxtLog);

        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);
        }
    }

    public String leerMSG() {
        try {
            return entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void conectar() {

        //Conexion de cliente
        try {
            s = new Socket(ip, port);
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());
            TxtLog = "\nServidor Conectado.";
            Main.PrintToTextview(TxtLog);

        } catch (IOException e) {

            TxtLog = "\nError : " + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);
        }
    }

    public void desconectar() {
        try {
            Stop = true;
            s.close();
        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);

        }
    }
}
