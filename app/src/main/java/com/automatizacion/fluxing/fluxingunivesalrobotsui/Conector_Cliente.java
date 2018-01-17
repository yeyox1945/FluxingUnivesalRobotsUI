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
    final int puerto = 29999;

    public String TxtLog;
    public MainActivity Main = new MainActivity();

    public Conector_Cliente(String ip) {

        //Conexion de cliente
        try {
            this.s = new Socket(ip, this.puerto);

            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);

            this.salida = new DataOutputStream(s.getOutputStream());

            TxtLog = "\nServidor :  Cliente conectado.";//Cuando se Conecta al servidor
            Main.PrintToTextview(TxtLog);


        } catch (IOException e) {

            TxtLog = "\nError :" + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);

        } catch (NullPointerException e) {
            TxtLog = "\nError : Robot no encontrado"; // cuando da error nulo
            Main.PrintToTextview(TxtLog);
        }

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
            TxtLog = "\nError :" + e.getMessage(); // cuando da error
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

    public void desconectar() {
        try {
            s.close();
        } catch (IOException e) {
            TxtLog = "\nError :" + e.getMessage(); // cuando da error
            Main.PrintToTextview(TxtLog);

        }

    }

}
