package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
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
public class Connect_Server extends Thread {

    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private int port = 29999;

    private static boolean Stop = false;

    public String serverResponse = "";

    public Connect_Server(int port) {
        this.port = port;
        Stop = false;
    }

    @Override
    public void run() {
        //Metodo en segundo plano

        try {
            ss = new ServerSocket(port);
            s = ss.accept();
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                                Log.i("Recibio server", finalTexto);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendProgram() {

        try {
            FlxSFTP ftp = new FlxSFTP();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            ftp.host = ConnectRobotFragment.ip_Robot;
            ftp.username = URPRobotFragment.eT_FTP_Username.getText().toString();
            ftp.password = URPRobotFragment.eT_FTP_Password.getText().toString();
            ftp.Connect();
            ftp.WaitTask();
            ftp.ChangeDirectoryAsync("/programs");

            String archivo = "android.resource://com.automatizacion.fluxing.fluxingunivesalrobotsui/raw/urclient.urp";
            Uri ruta = Uri.parse(archivo);

            ftp.SendFileAsync(ruta.toString(), "urclient.urp");
            System.out.println(ftp.ReadLog());

            System.out.println("Enviado");
        } catch (Exception e) {
            System.out.println("Hubo un error : " + e.getMessage());
        }
   /* Connect_Client = new Connect_Client(ConnectRobotFragment.ip_Robot, 29999);
        Connect_Client.conectar();
        Connect_Client.start();
        Connect_Client.enviarMSG("load /programs/urclient.urp ");
        Connect_Client.enviarMSG("play");
        Connect_Client.enviarMSG("stop");*/

    }

    public void conectarServidor() {
        start();
    }

    public void desconectar() {
        try {
            Stop = true;
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMSG(String msg) {
        try {
            salida = new DataOutputStream(s.getOutputStream());
            salida.writeBytes(msg + "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
