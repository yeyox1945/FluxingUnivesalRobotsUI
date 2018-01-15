package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.pm.ActivityInfo;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.widget.Toast;

import com.automatizacion.fluxing.fluxingunivesalrobotsui.MainActivity;

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

    public String Destinatario = "Servidor";

    @Override
    public void run() {
        //Metodo en segundo plano
        String texto;
        while (true) {
            try {
                texto = entrada.readLine();
                if (texto != null) {
               ///     Vista_Cliente.TxtLog.setText(Vista_Cliente.TxtLog.getText() + "\n" + Destinatario + " : " + texto);//Imprime la conversacion
                } else {
               //     Vista_Cliente.TxtLog.setText(Vista_Cliente.TxtLog.getText() + "\nServidor Cerrado..");//En caso de que se cierre el server
                    break;
                }

            } catch (IOException e) {
              ///  JOptionPane.showMessageDialog(null, "Error :" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
         //   Vista_Cliente.TxtLog.setText(Vista_Cliente.TxtLog.getText() + "\nCliente conectado.");

        } catch (IOException e) {
          //  JOptionPane.showMessageDialog(null, "Error :" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void enviarMSG(String msg) {
        System.out.println("enviado");
        try {
            this.salida = new DataOutputStream(s.getOutputStream());
            this.salida.writeBytes(msg + "\n");

        } catch (IOException e) {
           // Vista_Cliente.TxtLog.setText(Vista_Cliente.TxtLog.getText() + "\nError al enviar mensaje, conección cerrada.");

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
            Toast.makeText(null, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

}
