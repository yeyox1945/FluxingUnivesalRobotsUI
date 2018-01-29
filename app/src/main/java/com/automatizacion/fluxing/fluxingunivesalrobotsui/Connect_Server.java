package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.app.Activity;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * @author Jorge Manzano
 */
public class Connect_Server extends Thread {

    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private int port = 29999;
    private String ip = ConnectRobotFragment.ip_Robot;

    private static boolean Stop = false;

    public String TxtLog;
    public String serverResponse = "sin respuesta";
    public MainActivity Main = new MainActivity();

    public Connect_Server(String ip, int port) {
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
            }
        }

    }

    public void sendProgram() {

        Connect_Client Connect_Client;
        FlxSFTP ftp = new FlxSFTP();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ftp.host = ConnectRobotFragment.ip_Robot;
        ftp.username = URPRobotFragment.eT_FTP_Username.getText().toString();
        ftp.password = URPRobotFragment.eT_FTP_Password.getText().toString();
        ftp.Connect();
        ftp.WaitTask();
        ftp.ChangeDirectoryAsync("/home/ur/ursim-current/programs.UR10/Program");

        String archivo = "android.resource://com.automatizacion.fluxing.fluxingunivesalrobotsui/raw/prueba.urp";
        Uri ruta = Uri.parse(archivo);

        ftp.SendFileAsync(ruta.toString(), "prueba.urp");
        System.out.println(ftp.ReadLog());
        Connect_Client = new Connect_Client(ConnectRobotFragment.ip_Robot, 29999);
        Connect_Client.conectar();
        Connect_Client.start();
        Connect_Client.enviarMSG("load prueba.urp ");
        Connect_Client.enviarMSG("play");


        Connect_Client.enviarMSG("stop");

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
                    start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(1);
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
}
