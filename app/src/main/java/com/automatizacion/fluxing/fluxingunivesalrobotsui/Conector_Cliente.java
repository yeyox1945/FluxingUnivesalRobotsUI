package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.widget.TextView;

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

    TextView TxtLog = new MainActivity().findViewById(R.id.TxtLog);



    @Override
    public void run() {
        TextView TxtLog = new MainActivity().findViewById(R.id.TxtLog);
        //Metodo en segundo plano
        String texto;
        while (true) {
            try {
                texto = entrada.readLine();
                if (texto != null) {
                    TxtLog.setText(TxtLog.getText() + "\nServidor : " + texto); //Imprime la conversacion
                } else {

                    TxtLog.setText(TxtLog.getText() + "\nServidor :  Desconectado..");//Cuando se cierra el servidor
                    break;
                }
            } catch (IOException e) {
                TxtLog.setText(TxtLog.getText() + "\nError :" + e.getMessage()); // cuando da error
            }
        }

    }

    public Conector_Cliente(String ip) {


        //Conexion de cliente
        try {
            this.s = new Socket(ip, this.puerto);

            this.entradaSocket = new InputStreamReader(s.getInputStream());
            this.entrada = new BufferedReader(entradaSocket);

            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes("Cliente Conectado \n");
            TxtLog.setText(TxtLog.getText() + "\nServidor :  Cliente conectado.");//Cuando se Conecta al servidor
        } catch (IOException e) {
            TxtLog.setText(TxtLog.getText() + "\nError :" + e.getMessage()); // cuando da error
        }

    }

    public void enviarMSG(String msg) {
        System.out.println("enviado");
        try {
            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes(msg + "\n");
            TxtLog.setText(TxtLog.getText() + "\nCliente : " + msg + "\n");//Cuando le envio un mensaje

        } catch (IOException e) {
            TxtLog.setText(TxtLog.getText() + "\nError :" + e.getMessage()); // cuando da error

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
            TxtLog.setText(TxtLog.getText() + "\nError :" + e.getMessage()); // cuando da error

        }

    }

}
