package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

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
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private int port = 29999;
    private String ip = "0.0.0.0";

    static boolean Stop = true;

    public String TxtLog;
    public String serverResponse = "sin respuesta";
    public MainActivity Main = new MainActivity();

    public Connect_Client(String ip, int port) {
        this.port = port;
        this.ip = ip;
    }


    @Override
    public void run() {
        //Metodo en segundo plano
        String texto;

        while (true) {
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
            s.close();
        } catch (IOException e) {
            TxtLog = "\nError : " + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);

        }
    }

    public void conectarServidor() {

         new AsyncTask<Integer, Void, Void>() {

            @Override
            protected Void doInBackground(Integer... integers) {

                try {
                    ss = new ServerSocket(port);
                    s = ss.accept();
                    entradaSocket = new InputStreamReader(s.getInputStream());
                    entrada = new BufferedReader(entradaSocket);
                    salida = new DataOutputStream(s.getOutputStream());
                    TxtLog = "\nServidor Conectado."; // cuando da error
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(1);
    }
}
