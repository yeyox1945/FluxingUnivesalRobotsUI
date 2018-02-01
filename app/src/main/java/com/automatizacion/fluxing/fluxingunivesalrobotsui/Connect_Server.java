package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Connect_Server extends Thread {

    private Socket s;
    private ServerSocket ss;
    private InputStreamReader entradaSocket;
    private DataOutputStream salida;
    private BufferedReader entrada;
    private String serverResponse = "";
    private int port = 29999;
    private boolean Busy = false;
    private static boolean Stop = false;

    public Connect_Server(int port) {
        this.port = port;
        Stop = false;
    }

    @Override
    public void run() {
        try {
            ss = new ServerSocket(port);
            s = ss.accept();
            entradaSocket = new InputStreamReader(s.getInputStream());
            entrada = new BufferedReader(entradaSocket);
            salida = new DataOutputStream(s.getOutputStream());

            serverResponse = entrada.readLine();
            if (serverResponse != null) {
                Log.i("Recibio server", serverResponse);
                Busy = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        Busy = true;
        start();
        while(Busy == true) {
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void desconectar() {
        try {
            Stop = true;
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerResponse() {
        return serverResponse;
    }
}
