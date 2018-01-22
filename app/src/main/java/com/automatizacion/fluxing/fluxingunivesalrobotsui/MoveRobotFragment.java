package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class MoveRobotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public SeekBar seekBar_Base;
    public SeekBar seekBar_Shoulder;
    public SeekBar seekBar_Elbow;
    public SeekBar seekBar_Wrist1;
    public SeekBar seekBar_Wrist2;
    public SeekBar seekBar_Wrist3;

    public EditText editText_Base;
    public EditText editText_Shoulder;
    public EditText editText_Elbow;
    public EditText editText_Wrist1;
    public EditText editText_Wrist2;
    public EditText editText_Wrist3;

    public boolean activeFreeDrive = false;
    public static Conector_Cliente Connect_Client;
    // setting a home position before manually moving the robot

    double base;
    double shoulder;
    double elbow;
    double wrist1;
    double wrist2;
    double wrist3;


    private OnFragmentInteractionListener mListener;

    public MoveRobotFragment() {
        // Required empty public constructor
    }


    public static MoveRobotFragment newInstance(String param1, String param2) {
        MoveRobotFragment fragment = new MoveRobotFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_move_robot, container, false);

        View view = inflater.inflate(R.layout.fragment_move_robot, container, false);

        seekBar_Base = view.findViewById(R.id.seekBar_Base);
        seekBar_Shoulder = view.findViewById(R.id.seekBar_Shoulder);
        seekBar_Elbow = view.findViewById(R.id.seekBar_Elbow);
        seekBar_Wrist1 = view.findViewById(R.id.seekBar_Wrist1);
        seekBar_Wrist2 = view.findViewById(R.id.seekBar_Wrist2);
        seekBar_Wrist3 = view.findViewById(R.id.seekBar_Wrist3);

        editText_Base = view.findViewById(R.id.editText_Base);
        editText_Shoulder = view.findViewById(R.id.editText_Shoulder);
        editText_Elbow = view.findViewById(R.id.editText_Elbow);
        editText_Wrist1 = view.findViewById(R.id.editText_Wrist1);
        editText_Wrist2 = view.findViewById(R.id.editText_Wrist2);
        editText_Wrist3 = view.findViewById(R.id.editText_Wrist3);


        //Hace cambio de puerto
        Connect_Client = new Conector_Cliente("192.168.15.155", 30001);
        Connect_Client.conectar();

        InitRobot();

        Button Button_FreeDrive = view.findViewById(R.id.button_FreeDrive);
        Button_FreeDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!activeFreeDrive) {
                    activeFreeDrive = true;
                    String Comando = "teach_mode()";
                    Connect_Client.enviarMSG(Comando);
                } else {
                    activeFreeDrive = false;

                    Connect_Client.enviarMSG("end_teach_mode()");
                }
            }
        });

        Button Button_HomePosition = view.findViewById(R.id.button_HomePosition);
        Button_HomePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InitRobot();
            }
        });

        ListenersSeekBars();

        return view;
    }


    public void ListenersSeekBars() {

        //Base
        seekBar_Base.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {

                editText_Base.setText(String.valueOf(i - 360));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                double res = Integer.parseInt(editText_Base.getText().toString()) * 3.1416 / 180;
                base = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);

            }
        });

        //Shoulder
        seekBar_Shoulder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                editText_Shoulder.setText(String.valueOf(i - 360));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double res = Integer.parseInt(editText_Shoulder.getText().toString()) * 3.1416 / 180;
                shoulder = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });

        //Elbow
        seekBar_Elbow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                editText_Elbow.setText(String.valueOf(i - 360));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double res = Integer.parseInt(editText_Elbow.getText().toString()) * 3.1416 / 180;
                elbow = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });

        //seekBar_Wrist1
        seekBar_Wrist1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                editText_Wrist1.setText(String.valueOf(i - 360));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double res = Integer.parseInt(editText_Wrist1.getText().toString()) * 3.1416 / 180;
                wrist1 = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });

        //seekBar_Wrist2
        seekBar_Wrist2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                editText_Wrist2.setText(String.valueOf(i - 360));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double res = Integer.parseInt(editText_Wrist2.getText().toString()) * 3.1416 / 180;
                wrist2 = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });

        //seekBar_Wrist3
        seekBar_Wrist3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                editText_Wrist3.setText(String.valueOf(i - 360));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                double res = Integer.parseInt(editText_Wrist3.getText().toString()) * 3.1416 / 180;
                wrist3 = res;
                String Comando = "movej(["+Double.toString(base)+
                        ", "+Double.toString(shoulder)+
                        ", "+Double.toString(elbow)+
                        ", "+Double.toString(wrist1)+
                        ", "+Double.toString(wrist2)+
                        ", "+Double.toString(wrist3)+
                        "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });
    }




    // TODO: Rename method, update argument and hook method into UI event
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

    public static int roundDouble(double value, int decimals) {
        if (decimals < 0)
            throw new IllegalArgumentException();

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(decimals, RoundingMode.HALF_UP);

        return bigDecimal.intValue();
    }

    public void GetPositions (){

        /*Conector_Cliente socket = new Conector_Cliente("192.168.15.21", 1025);
        socket.conectarServidor();*/

        /*Connect_Client.enviarMSG("Socket_Closed=True\n" +
                "  while (True):\n" +
                "    if (Socket_Closed ==  True  ):\n" +
                "      socket_open(“192.168.15.21″, 1025)\n" +
                "      global Socket_Closed =   False \n" +
                "      varmsg(“Socket_Closed”,Socket_Closed)\n" +
                "    end\n" +
                "    socket_send_string(“Asking_Waypoint_1″)\n" +
                "    sleep(3.0)\n" +
                "  end");*/

        Log.i("Respuesta", Connect_Client.serverResponse);

        String cadena = Connect_Client.serverResponse;

        String Base, Shoulder, Elbow, Wrist1, Wrist2, Wrist3;

        cadena = cadena.replace("(", "");
        cadena = cadena.replace(")", "");

        String parte[] = cadena.split(",");

        Base = parte[0];
        Shoulder = parte[1];
        Elbow = parte[2];
        Wrist1 = parte[3];
        Wrist2 = parte[4];
        Wrist3 = parte[5];

        seekBar_Base.setProgress(Integer.valueOf(Base));
        seekBar_Shoulder.setProgress(Integer.valueOf(Shoulder));
        seekBar_Elbow.setProgress(Integer.valueOf(Elbow));
        seekBar_Wrist1.setProgress(Integer.valueOf(Wrist1));
        seekBar_Wrist2.setProgress(Integer.valueOf(Wrist2));
        seekBar_Wrist3.setProgress(Integer.valueOf(Wrist3));
    }

    public void InitRobot() {
        base = 1.57;
        shoulder = -2.12;
        elbow = 1.39;
        wrist1 = -0.84;
        wrist2 = -1.57;
        wrist3 = 1.57;

        seekBar_Base.setProgress(roundDouble(base * 180 / 3.1416, 0) + 360);
        seekBar_Shoulder.setProgress(roundDouble(shoulder * 180 / 3.1416, 0) + 360);
        seekBar_Elbow.setProgress(roundDouble(elbow * 180 / 3.1416, 0) + 360);
        seekBar_Wrist1.setProgress(roundDouble(wrist1 * 180 / 3.1416, 0) + 360);
        seekBar_Wrist2.setProgress(roundDouble(wrist2 * 180 / 3.1416, 0) + 360);
        seekBar_Wrist3.setProgress(roundDouble(wrist3 * 180 / 3.1416, 0) + 360);

        editText_Base.setText(Integer.toString(seekBar_Base.getProgress()-360));
        editText_Shoulder.setText(Integer.toString(seekBar_Shoulder.getProgress()-360));
        editText_Elbow.setText(Integer.toString(seekBar_Elbow.getProgress()-360));
        editText_Wrist1.setText(Integer.toString(seekBar_Wrist1.getProgress()-360));
        editText_Wrist2.setText(Integer.toString(seekBar_Wrist2.getProgress()-360));
        editText_Wrist3.setText(Integer.toString(seekBar_Wrist3.getProgress()-360));

        Connect_Client.enviarMSG("movej(["+Double.toString(base)+
                ", "+Double.toString(shoulder)+
                ", "+Double.toString(elbow)+
                ", "+Double.toString(wrist1)+
                ", "+Double.toString(wrist2)+
                ", "+Double.toString(wrist3)+
                "], a=1.0, v=0.2)");
    }
}
