package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

/*
    Created by Diego Vargas
 */

public class MoveRobotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Button[] btnArray = new Button[12];
    private EditText[] etxtArray = new EditText[6];
    private SeekBar[] seekBarsArray = new SeekBar[6];
    private Integer[] countArray = new Integer[6];
    private Integer[] initPositions = new Integer[6];
    private Integer[] homePositions = new Integer[6];

    //Define variable para validar estado.
    private boolean pressed = false;

    private boolean activeFreeDrive = false;
    // setting a home position before manually moving the robot
    private Connect_Client socketMove = new Connect_Client(ConnectRobotFragment.ip_Robot, 30001);
    private Connect_Client socketInit = new Connect_Client(ConnectRobotFragment.ip_Robot, 29999);
    private Connect_Server socketServer = new Connect_Server(1025);
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

        homePositions[0] = 90;
        homePositions[1] = -120;
        homePositions[2] = 80;
        homePositions[3] = -48;
        homePositions[4] = -90;
        homePositions[5] = 90;

        // Inicializacion de arreglos de botones, editext y seekbars.
        // Base
        btnArray[0] = view.findViewById(R.id.btnBaseLeft);
        btnArray[1] = view.findViewById(R.id.btnBaseRight);
        // Shoulder
        btnArray[2] = view.findViewById(R.id.btnShoulderLeft);
        btnArray[3] = view.findViewById(R.id.btnShoulderRight);
        // Elbow
        btnArray[4] = view.findViewById(R.id.btnElbowLeft);
        btnArray[5] = view.findViewById(R.id.btnElbowRight);

        // Wrist1
        btnArray[6] = view.findViewById(R.id.btnWrist1Left);
        btnArray[7] = view.findViewById(R.id.btnWrist1Right);

        // Wrist2
        btnArray[8] = view.findViewById(R.id.btnWrist2Left);
        btnArray[9] = view.findViewById(R.id.btnWrist2Right);

        // Wrist3
        btnArray[10] = view.findViewById(R.id.btnWrist3Left);
        btnArray[11] = view.findViewById(R.id.btnWrist3Right);

        seekBarsArray[0] = view.findViewById(R.id.seekBar_Base);
        seekBarsArray[0].setEnabled(false);
        seekBarsArray[1] = view.findViewById(R.id.seekBar_Shoulder);
        seekBarsArray[1].setEnabled(false);
        seekBarsArray[2] = view.findViewById(R.id.seekBar_Elbow);
        seekBarsArray[2].setEnabled(false);
        seekBarsArray[3] = view.findViewById(R.id.seekBar_Wrist1);
        seekBarsArray[3].setEnabled(false);
        seekBarsArray[4] = view.findViewById(R.id.seekBar_Wrist2);
        seekBarsArray[4].setEnabled(false);
        seekBarsArray[5] = view.findViewById(R.id.seekBar_Wrist3);
        seekBarsArray[5].setEnabled(false);

        etxtArray[0] = view.findViewById(R.id.editText_Base);
        etxtArray[1] = view.findViewById(R.id.editText_Shoulder);
        etxtArray[2] = view.findViewById(R.id.editText_Elbow);
        etxtArray[3] = view.findViewById(R.id.editText_Wrist1);
        etxtArray[4] = view.findViewById(R.id.editText_Wrist2);
        etxtArray[5] = view.findViewById(R.id.editText_Wrist3);

        socketMove.conectar();

        for (int i = 0; i < initPositions.length; i++) {
            etxtArray[i].setText(String.valueOf(initPositions[i]));
            seekBarsArray[i].setProgress(Integer.parseInt(etxtArray[i].getText().toString()) + 360);
            countArray[i] = initPositions[i];
        }

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button btnPressed = (Button) v;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!pressed) {
                            pressed = true;
                            //AsyncTask que ejecuta Tarea.
                            new AsyncTaskCounter().execute(btnPressed.getTag().toString());
                            Log.d("CONTADOR", "Detiene contador");
                        } else {
                            Toast.makeText(getContext(), "Solo se puede hacer una operacion a la vez", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        pressed = false;
                        convertAndSendCommand();
                        break;
                }
                return true;
            }
        };

        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {

                    EditText etxt = (EditText) view;
                    countArray[Integer.parseInt(etxt.getTag().toString())] = Integer.parseInt(etxt.getText().toString());
                    seekBarsArray[Integer.parseInt(etxt.getTag().toString())].setProgress(Integer.parseInt(etxtArray[Integer.parseInt(etxt.getTag().toString())].getText().toString()) + 360);
                    convertAndSendCommand();

                    return true;
                }
                return false;
            }
        };

        for (Button btn : btnArray) {
            btn.setOnTouchListener(onTouchListener);
        }
        for (EditText etxt : etxtArray) {
            etxt.setOnKeyListener(onKeyListener);
        }

        Button Button_FreeDrive = view.findViewById(R.id.button_FreeDrive);
        Button_FreeDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!activeFreeDrive) {
                    activeFreeDrive = true;
                    String Comando = "teach_mode()";
                    socketMove.enviarMSG(Comando);
                } else {
                    activeFreeDrive = false;
                    socketMove.enviarMSG("end_teach_mode()");
                }
            }
        });

        Button Button_HomePosition = view.findViewById(R.id.button_HomePosition);
        Button_HomePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToHomePosition();
                for (int i = 0; i < etxtArray.length; i++) {
                    etxtArray[i].setText(Integer.toString(homePositions[i]));
                    seekBarsArray[i].setProgress(homePositions[i] + 360);
                    countArray[i] = homePositions[i];
                }
            }
        });
        return view;
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

        socketInit = new Connect_Client(ConnectRobotFragment.ip_Robot, 29999);
        socketInit.conectar();

        try {

            socketInit.enviarMSG("load "+ ConnectRobotFragment.DirRobot + "/URClient.urp");
            socketInit.enviarMSG(getResources().getString(R.string.Power_on));
            socketInit.enviarMSG(getResources().getString(R.string.Brake_release));

            socketInit.enviarMSG("stop");
            socketInit.enviarMSG("play");

        } catch (Exception e) {
            System.out.println("Hubo un error : " + e.getMessage());
        }


        ///Cierra sockets si estan abiertos
        try {
            socketServer.desconectar();
            socketMove.desconectar();
        } catch (Exception e) {
            System.out.printf("Esta apagado");
        }

        socketServer.conectarServidor();

        getJointPositions(socketServer.getServerResponse());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        socketServer.desconectar();
        socketInit.desconectar();
        socketMove.desconectar();
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

    public void getJointPositions(String positions) {

        if (!positions.equals(null)) {
            positions = positions.replace("[", "");
            positions = positions.replace("]", "");

            String[] parts = positions.split(",");

            for (int i = 0; i < initPositions.length; i++) {
                initPositions[i] = roundDouble(Double.parseDouble(parts[i]) * 180 / 3.1416, 0);
            }
        }
    }

    public void sendToHomePosition() {
        socketMove.enviarMSG("movej([" + Double.toString(homePositions[0] * 3.1416 / 180) +
                ", " + Double.toString(homePositions[1] * 3.1416 / 180) +
                ", " + Double.toString(homePositions[2] * 3.1416 / 180) +
                ", " + Double.toString(homePositions[3] * 3.1416 / 180) +
                ", " + Double.toString(homePositions[4] * 3.1416 / 180) +
                ", " + Double.toString(homePositions[5] * 3.1416 / 180) +
                "], a=1.0, v=0.3)");
    }

    public void convertAndSendCommand() {
        socketMove.enviarMSG("movej([" + Double.toString(countArray[0] * 3.1416 / 180) +
                ", " + Double.toString(countArray[1] * 3.1416 / 180) +
                ", " + Double.toString(countArray[2] * 3.1416 / 180) +
                ", " + Double.toString(countArray[3] * 3.1416 / 180) +
                ", " + Double.toString(countArray[4] * 3.1416 / 180) +
                ", " + Double.toString(countArray[5] * 3.1416 / 180) +
                "], a=1.0, v=0.5)");
    }

    private class AsyncTaskCounter extends AsyncTask<String, Void, Void> {
        int acceleration = 500;
        int motorValue;
        String directionValue;

        @Override
        protected Void doInBackground(String... tags) {

            String[] parts = tags[0].split(":");
            motorValue = Integer.parseInt(parts[0]);
            directionValue = parts[1];

            while (pressed) {
                taskIncrementaContador();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etxtArray[motorValue].setText(String.valueOf(countArray[motorValue]));
                        seekBarsArray[motorValue].setProgress(Integer.parseInt(etxtArray[motorValue].getText().toString()) + 360);
                    }
                });
                try {
                    Thread.sleep(acceleration);
                } catch (InterruptedException e) {
                    Log.d("ERROR", e.getMessage());
                }
            }
            return null;
        }

        private void taskIncrementaContador() {
            if (acceleration != 50)
                acceleration -= 50;

            if (directionValue.equals("+")) {
                countArray[motorValue] += 1;
            } else {
                countArray[motorValue] -= 1;
            }
            Log.d("CONTADOR", "Valor: " + String.valueOf(countArray[motorValue]));
        }
    }
}
