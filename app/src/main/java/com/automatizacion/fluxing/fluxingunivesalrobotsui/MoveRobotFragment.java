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



        //  Get and Setear seekbars con posicion actual del robot

             //  GetPositions();


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

                String Comando = "movej(p[0.4,0,0.5,0,-3.1416,0])";
                Connect_Client.enviarMSG(Comando);
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
                String Base = String.valueOf(res);

                String Comando = "movej([" + Base + ", -1.58, 1.16, -1.15, -1.55, 1.18], a=1.0, v=0.2)";
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
                String Shoulder = String.valueOf(res);

                String Comando = "movej([-1.95," + Shoulder + ", 1.16, -1.15, -1.55, 1.18], a=1.0, v=0.2)";
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
                String Elbow = String.valueOf(res);

                String Comando = "movej([-1.95,-1.58, " + Elbow + ", -1.15, -1.55, 1.18], a=1.0, v=0.2)";
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
                String Wrist1 = String.valueOf(res);

                String Comando = "movej([-1.95, -1.58, 1.16, " + Wrist1 + ", -1.55, 1.18], a=1.0, v=0.2)";
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
                String Wrist2 = String.valueOf(res);

                String Comando = "movej([-1.95, -1.58, 1.16, -1.15, " + Wrist2 + ", 1.18], a=1.0, v=0.2)";
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
                String Wrist3 = String.valueOf(res);

                String Comando = "movej([-1.95, -1.58, 1.16, -1.15, -1.55, " + Wrist3 + "], a=1.0, v=0.2)";
                Connect_Client.enviarMSG(Comando);
            }
        });
    }

    public void GetPositions (){

        Connect_Client.enviarMSG("var:=get_actual_joint_positions()");

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
}
