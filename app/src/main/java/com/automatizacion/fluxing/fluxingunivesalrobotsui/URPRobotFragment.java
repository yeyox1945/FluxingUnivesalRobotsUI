package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import java.io.File;

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

        final TextView tV_FTP_Output = view.findViewById(R.id.tV_Output);
        tV_FTP_Output.setMovementMethod(new ScrollingMovementMethod());

        final MainActivity parent = new MainActivity();
        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    parent.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tV_FTP_Output.append(sftp.ReadLog());
                        }
                    });
                    try {
                        Thread.sleep(400);
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
                sftp.ExecuteAsyncMethod(FlxSFTP.FlxMethod.Connect, "", "");
            }
        });

        Button b_FTP_CD = view.findViewById(R.id.b_FTP_CD);
        b_FTP_CD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sftp.ExecuteAsyncMethod(FlxSFTP.FlxMethod.ChangeDirectory, eT_Directory.getText().toString(), "");
            }
        });

        Button b_FTP_LS = view.findViewById(R.id.b_FTP_LS);
        b_FTP_LS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sftp.ExecuteAsyncMethod(FlxSFTP.FlxMethod.ReadDirectoryContent, "", "");
                tV_FTP_Output.append(sftp.Result);
            }
        });

        Button b_URP_SearchFile = view.findViewById(R.id.b_URP_SearchFile);
        b_URP_SearchFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FileChooser(getActivity()).setFileListener(new FileChooser.FileSelectedListener() {
                    @Override public void fileSelected(final File file) {
                        eT_URP_FilePath.setText(file.getPath());
                    }}).showDialog();
            }
        });

        Button b_URP_Send = view.findViewById(R.id.b_URP_Send);
        b_URP_Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sftp.SendFile( eT_URP_FilePath.getText().toString(), "File.hao");
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

}
