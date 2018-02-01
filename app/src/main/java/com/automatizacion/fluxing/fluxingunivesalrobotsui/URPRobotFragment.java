package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import java.io.File;
import java.util.Vector;

/**
 * Created by Chava on 1/17/2018.
 */

public class URPRobotFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private URPRobotFragment.OnFragmentInteractionListener mListener;

    public static FlxSFTP sftp = new FlxSFTP();
    private Connect_Client conn = new Connect_Client(ConnectRobotFragment.ip_Robot, 29999);
    private EditText eT_URP_FilePath;
    private String FileName = "";
    private TextView tV_FTP_Output, tV_URP_Name, tV_URP_State;
    private Spinner s_RemotePrograms;
    private final int iLoad = 0, iStart = 1, iStop = 2, iName = 3, iState = 4;
    private boolean bListenLog = true;
    private boolean bConnected = false;
    private boolean bBusy = false;

    public URPRobotFragment() {
    }

    public static URPRobotFragment newInstance(String param1, String param2) {
        URPRobotFragment fragment = new URPRobotFragment();
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

    public static EditText eT_FTP_Host, eT_FTP_Username, eT_FTP_Password, eT_Directory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urp_robot, container, false);

        eT_FTP_Host = view.findViewById(R.id.eT_FTP_Host);
        eT_FTP_Username = view.findViewById(R.id.eT_FTP_Username);
        eT_FTP_Password = view.findViewById(R.id.eT_FTP_Password);
        eT_Directory = view.findViewById(R.id.eT_Directory);
        eT_URP_FilePath = view.findViewById(R.id.eT_URP_FilePath);

        s_RemotePrograms = view.findViewById(R.id.s_RemotePrograms);

        tV_FTP_Output = view.findViewById(R.id.tV_Output);
        tV_URP_Name = view.findViewById(R.id.tV_URP_Name);
        tV_URP_State = view.findViewById(R.id.tV_URP_State);
        tV_FTP_Output.setMovementMethod(new ScrollingMovementMethod());


        //Settear valores del robot seleccionado
        eT_FTP_Host.setText(ConnectRobotFragment.ip_Robot);
        eT_Directory.setText(ConnectRobotFragment.DirRobot);


        Thread th = new Thread(new Runnable() {
            public void run() {
                while (bListenLog) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tV_FTP_Output.append(sftp.ReadLog());
                        }
                    });
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();

        Button b_FTP_Connect = view.findViewById(R.id.b_URPConnect);
        b_FTP_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sftp.IsConnected()) {
                    tV_FTP_Output.append("Ya existe una conexión con el servidor\n");
                    return;
                }
                sftp.host = eT_FTP_Host.getText().toString();
                sftp.username = eT_FTP_Username.getText().toString();
                sftp.password = eT_FTP_Password.getText().toString();
                sftp.Connect();
            }
        });

        Button b_URP_GetList = view.findViewById(R.id.b_URP_GetList);
        b_URP_GetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sftp.ChangeDirectoryAsync(eT_Directory.getText().toString())) {
                    ArrayAdapter<Object> filenames;
                    Vector FileNames = sftp.GetFilesByExtension(".urp");
                    filenames = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, FileNames.toArray());
                    filenames.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    s_RemotePrograms.setAdapter(filenames);
                } else
                    tV_FTP_Output.append("No se pudo acceder al directorio de programas\n");
            }
        });


        Button b_URP_SearchFile = view.findViewById(R.id.b_URP_SearchFile);
        b_URP_SearchFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int PermReadExt = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (PermReadExt != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(final File file) {
                            eT_URP_FilePath.setText(file.getPath());
                            FileName = file.getName();
                        }
                    }).showDialog();
                }
            }
        });

        Button b_URP_Send = view.findViewById(R.id.b_URP_Send);
        b_URP_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sftp.SendFileAsync(eT_URP_FilePath.getText().toString(), FileName);
            }
        });

        Button b_URP_Load = view.findViewById(R.id.b_URP_LoadProgram);
        b_URP_Load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteOnRobot(iLoad);
            }
        });

        Button b_URP_Start = view.findViewById(R.id.b_URP_StartProgram);
        b_URP_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteOnRobot(iStart);
            }
        });

        Button b_URP_Stop = view.findViewById(R.id.b_URP_StopProgram);
        b_URP_Stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteOnRobot(iStop);
            }
        });

        Button b_URP_CheckProgram = view.findViewById(R.id.b_URP_CheckProgram);
        b_URP_CheckProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecuteOnRobot(iName);
                ExecuteOnRobot(iState);
            }
        });

        return view;
    }

    private void ConnectToRobot() {
        conn.conectar();
        System.out.println("Conexión: " + conn.TxtLog);
        tV_FTP_Output.append(conn.TxtLog + "\n");
        bConnected = true;
    }

    private void ExecuteOnRobot(int Code) {
        try {
            if(!bConnected) ConnectToRobot();
            switch(Code) {
                case iLoad:
                    if(s_RemotePrograms.getSelectedItem() == null) {
                        tV_FTP_Output.append("No hay ningún programa seleccionado\n");
                        return;
                    }
                    if(s_RemotePrograms.getSelectedItem().toString().length() == 0) {
                        tV_FTP_Output.append("No hay ningún programa seleccionado\n");
                        return;
                    }
                    tV_FTP_Output.append("Cargando programa: " +
                                s_RemotePrograms.getSelectedItem().toString() + "\n");
                    conn.enviarMSG("load " + eT_Directory.getText() + "/" +
                            s_RemotePrograms.getSelectedItem().toString());
                    break;
                case iStart:
                    conn.enviarMSG("play");
                    tV_FTP_Output.append("Enviado comando: play" + "\n");
                    break;
                case iStop:
                    conn.enviarMSG("stop");
                    tV_FTP_Output.append("Enviado comando: stop" + "\n");
                    break;
                case iName:
                    conn.enviarMSG("get loaded program");
                    tV_FTP_Output.append("Leyendo nombre de programa cargado..." + "\n");
                    String sName;
                    do {
                        sName = conn.leerMSG();
                    } while(!sName.startsWith("Loaded"));
                    tV_URP_Name.setText("Nombre: " + sName.split(":")[1]);
                    tV_FTP_Output.append("Nombre leido" + "\n");
                    break;
                case iState:
                    conn.enviarMSG("programState");
                    tV_FTP_Output.append("Solicitando el estado del programa..." + "\n");
                    String sState;
                    do {
                        sState = conn.leerMSG();
                    } while(!sState.startsWith("STOPPED") && !sState.startsWith("PLAYING") &&
                        !sState.startsWith("PAUSED"));
                    tV_URP_State.setText("Estado: " + sState.split(" ")[0]);
                    tV_FTP_Output.append("Estado leido" + "\n");
                    break;
            }
        } catch (Exception e) {
            tV_FTP_Output.append(e.getMessage() + "\n");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(final File file) {
                            eT_URP_FilePath.setText(file.getPath());
                            FileName = file.getName();
                        }
                    }).showDialog();
                }
                return;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        bListenLog = true;
        if (context instanceof URPRobotFragment.OnFragmentInteractionListener) {
            mListener = (URPRobotFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        bListenLog = false;
        sftp.DisconnectAsync();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
