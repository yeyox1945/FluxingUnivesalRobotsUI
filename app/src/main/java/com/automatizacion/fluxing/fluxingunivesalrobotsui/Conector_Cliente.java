package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 * @author Jorge Manzano
 */
public class Conector_Cliente extends Thread {

    private Socket s;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private int port = 29999;
    private String ip = "0.0.0.0";

    static boolean Stop = true;

    public String TxtLog;
    public MainActivity Main = new MainActivity();

    public Conector_Cliente(String ip, int port) {
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

                                TxtLog = "\nServidor : " + finalTexto; //Imprime la conversacion
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
            TxtLog = "\nServidor Conectado."; // cuando da error
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
}
