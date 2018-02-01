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
import java.util.ArrayList;

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

    public static Connect_Client socketInitRobot;
    public static Connect_Server SocketServer;
    public static TextView TxtLog;
    public EditText TxtMSG;
    public Spinner SpinnerRobot;
    public static ArrayList<String> RobotsList = new ArrayList<>();

    public static String id_Robot = "";
    public static String nombre_Robot = "";
    public static String modelo_Robot = "";
    public static String ip_Robot = "0.0.0.0";
    public static String DirRobot = ConnectSQL.dir;
    ///la direccion se consigue desde la clase connect SQL

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_connect_robot, container, false);

        TxtLog = view.findViewById(R.id.TxtLog);
        TxtLog.setMovementMethod(new ScrollingMovementMethod());
        SpinnerRobot = view.findViewById(R.id.spinner_Robots);

        Fill_Spinner_Robots(); // llena el sipiner con las ip registradas

        // Metodo se ejecuta al conectar un robot
        final Button button_Connect = view.findViewById(R.id.button_Connect);
        button_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Robot = SpinnerRobot.getSelectedItem().toString();

                if (!Robot.equals("Seleccióna..")) {

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    socketInitRobot = new Connect_Client(ip_Robot, 29999);
                    socketInitRobot.conectar();
                    socketInitRobot.start();
                    socketInitRobot.enviarMSG(getResources().getString(R.string.Power_on));
                    socketInitRobot.enviarMSG(getResources().getString(R.string.Brake_release));

                    SocketServer = new Connect_Server(1025);
                    SocketServer.sendProgram();

                    MainActivity Main = new MainActivity();
                    Main.BlockItem(false);

                } else {
                    Toast.makeText(getContext(),"Seleccióna un robot", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            SpinnerRobot.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_style_items, RobotsList));
            SpinnerRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                    ///Agregar codigo de selecionde ip aqui
                    String Robot = SpinnerRobot.getSelectedItem().toString();
                    String[] parts = Robot.split(" - ");

                    if (!Robot.equals("Seleccióna..")) {
                        id_Robot = parts[0];
                        nombre_Robot = parts[1];
                        modelo_Robot = parts[2];
                        ip_Robot = parts[3];
                    }
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
