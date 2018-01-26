package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConnectRobotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ConnectRobotFragment() {
        // Required empty public constructor


    }

    // TODO: Rename and change types and number of parameters
    public static ConnectRobotFragment newInstance(String param1, String param2) {
        ConnectRobotFragment fragment = new ConnectRobotFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //  public com.automatizacion.fluxing.fluxingunivesalrobotsui.Connect_Client Connect_Client;
    public Connect_Client Connect_Client;
    public static TextView TxtLog;
    public EditText TxtMSG;
    public Spinner SpinnerRobot;
    public static ArrayList<String> RobotsList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_connect_robot, container, false);

        TxtLog = view.findViewById(R.id.TxtLog);
        TxtLog.setMovementMethod(new ScrollingMovementMethod());
        SpinnerRobot = view.findViewById(R.id.spinner_Robots);

        Button button_Connect = view.findViewById(R.id.button_Connect);

        Fill_Spinner_Robots();//llena el sipiner con las ip registradas

        // Metodo se ejecuta al conectar un robot
        button_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connect_Client = new Connect_Client("192.168.15.155", 29999);
                Connect_Client.conectar();
                Connect_Client.start();
                Connect_Client.enviarMSG(getResources().getString(R.string.Power_on));
                Connect_Client.enviarMSG(getResources().getString(R.string.Brake_release));
            }
        });

        Button button_Pause = view.findViewById(R.id.button_Pause);
        button_Pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Connect_Client.enviarMSG("Power Off");

            }
        });


      /*  //Metodo para enviar un comando
        Button button_send = view.findViewById(R.id.button_send);
        TxtMSG = view.findViewById(R.id.EditCommand);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TxtLog = view.findViewById(R.id.TxtLog);
                TxtMSG = view.findViewById(R.id.EditCommand);

                Connect_Client.enviarMSG(TxtMSG.getText().toString());
                TxtLog.setText(TxtLog.getText() + "\nServidor : " + TxtMSG.getText());
                TxtMSG.setText("");
            }
        });*/

        return view;
    }

    public void Fill_Spinner_Robots() {
        //SpinnerRobot


        RobotsList.clear();
        SpinnerRobot.setAdapter(null);
        RobotsList.add("Seleccióna..");
        //LLenar Con datos de SQL

        ConnectSQL SQL = new ConnectSQL();
        if (SQL.Validate_Connection() == false) {
            Toast.makeText(getContext(), "No se encontro una conexión a internet.", Toast.LENGTH_SHORT).show();

        } else {
            SQL.Fill_Combo_IP_RobotsSQL();

            assert SpinnerRobot != null;
            SpinnerRobot.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, RobotsList));
            SpinnerRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    ///Agregar codigo de selecionde ip aqui

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
