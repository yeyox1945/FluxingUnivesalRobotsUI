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
    private EditText eT_URP_FilePath;
    private String FileName = "";
    private boolean bListenLog = true;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urp_robot, container, false);

        sftp = new FlxSFTP();

        final EditText eT_FTP_Host = view.findViewById(R.id.eT_FTP_Host),
                eT_FTP_Username = view.findViewById(R.id.eT_FTP_Username),
                eT_FTP_Password = view.findViewById(R.id.eT_FTP_Password),
                eT_Directory = view.findViewById(R.id.eT_Directory);
        eT_URP_FilePath = view.findViewById(R.id.eT_URP_FilePath);

        final Spinner s_RemotePrograms = view.findViewById(R.id.s_RemotePrograms);

        final TextView tV_FTP_Output = view.findViewById(R.id.tV_Output);
        tV_FTP_Output.setMovementMethod(new ScrollingMovementMethod());

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
                    }
                    catch (InterruptedException e) {
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
                if(sftp.ChangeDirectoryAsync(eT_Directory.getText().toString()))
                {
                    ArrayAdapter<Object> filenames;

                    Vector FileNames = sftp.GetFilesByExtension(".urp");
                    filenames = new ArrayAdapter<>(getContext(),
                            R.layout.support_simple_spinner_dropdown_item, FileNames.toArray());
                    filenames.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    s_RemotePrograms.setAdapter(filenames);
                }
                else
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
                if(sftp.IsConnected())
                    sftp.SendFileAsync( eT_URP_FilePath.getText().toString(), FileName);
            }
        });

        return view;
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
