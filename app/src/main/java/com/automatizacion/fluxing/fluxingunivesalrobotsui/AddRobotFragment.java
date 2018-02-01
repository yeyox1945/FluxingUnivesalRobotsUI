package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class AddRobotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public AddRobotFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddRobotFragment newInstance(String param1, String param2) {
        AddRobotFragment fragment = new AddRobotFragment();
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


        View view = inflater.inflate(R.layout.fragment_add_robot, container, false);

        final EditText Edit_Registro_Nombre = view.findViewById(R.id.Edit_Registro_Nombre);
        final EditText Edit_Registro_Modelo = view.findViewById(R.id.Edit_Registro_Modelo);
        final EditText Edit_Registro_IP = view.findViewById(R.id.Edit_Registro_IP);
        final EditText Edit_Registro_Dir = view.findViewById(R.id.Edit_Direccion_Programs);


        Button BtnRegister = view.findViewById(R.id.buttonRegistrar);

        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectSQL SQL = new ConnectSQL();
                Boolean Validate = SQL.RegisterRobot(Edit_Registro_Nombre.getText().toString(), Edit_Registro_Modelo.getText().toString(), Edit_Registro_IP.getText().toString(),Edit_Registro_Dir.getText().toString());

                Edit_Registro_Nombre.setText("");
                Edit_Registro_Modelo.setText("");
                Edit_Registro_IP.setText("");
                Edit_Registro_Dir.setText("");

                if(Validate){
                    Toast.makeText(getContext(), "Registro Exitoso",
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getContext(), "Registro Erroneo",
                            Toast.LENGTH_LONG).show();
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
