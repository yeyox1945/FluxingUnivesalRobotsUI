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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    public Conector_Cliente Connect_Client;
    public static TextView TxtLog;
    public EditText TxtMSG;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       final View view =inflater.inflate(R.layout.fragment_connect_robot, container, false);

        TxtLog = view.findViewById(R.id.TxtLog);
        TxtLog.setMovementMethod (new ScrollingMovementMethod());


        TxtMSG = view.findViewById(R.id.EditCommand);

        Button button_Connect = view.findViewById(R.id.button_Connect);
        Button button_send = view.findViewById(R.id.button_send);


        //Metodo se ejecuta al conectar un robot
        button_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TxtLog = view.findViewById(R.id.TxtLog);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Connect_Client = new Conector_Cliente("192.168.15.155", 29999);
                Connect_Client.conectar();
                Connect_Client.start();

            }
        });

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TxtLog = view.findViewById(R.id.TxtLog);
                TxtMSG = view.findViewById(R.id.EditCommand);

                Connect_Client.enviarMSG(TxtMSG.getText().toString());
                TxtLog.setText(TxtLog.getText() + "\nServidor : " + TxtMSG.getText());
                TxtMSG.setText("");

            }
        });

        return view;
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
