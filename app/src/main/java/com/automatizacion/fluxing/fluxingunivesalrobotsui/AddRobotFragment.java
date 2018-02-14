package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public class AddRobotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static ArrayList<String> RobotsList = new ArrayList<String>();
    public static Spinner spnRobotsDB;
    EditText Edit_Registro_Nombre;
    EditText Edit_Registro_Modelo;
    EditText Edit_Registro_IP;
    EditText Edit_Registro_Dir;
    ConnectSQL SQL = new ConnectSQL();

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

        Edit_Registro_Nombre = view.findViewById(R.id.Edit_Registro_Nombre);
        Edit_Registro_Modelo = view.findViewById(R.id.Edit_Registro_Modelo);
        Edit_Registro_IP = view.findViewById(R.id.Edit_Registro_IP);
        Edit_Registro_Dir = view.findViewById(R.id.Edit_Direccion_Programs);

        spnRobotsDB = view.findViewById(R.id.spnRobotsDB);
        Fill_Spinner_Robots();

        Button BtnRegister = view.findViewById(R.id.buttonRegistrar);
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean Validate = SQL.RegisterRobot(Edit_Registro_Nombre.getText().toString(),
                        Edit_Registro_Modelo.getText().toString(),
                        Edit_Registro_IP.getText().toString(),
                        Edit_Registro_Dir.getText().toString());


                if (Validate) {
                    Toast.makeText(getContext(), "Registro Exitoso",
                            Toast.LENGTH_LONG).show();

                    Edit_Registro_Nombre.setText("");
                    Edit_Registro_Modelo.setText("");
                    Edit_Registro_IP.setText("");
                    Edit_Registro_Dir.setText("");

                    Fill_Spinner_Robots();
                } else {
                    Toast.makeText(getContext(), "Registro Erroneo comprueba los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button BtnModify = view.findViewById(R.id.buttonModificar);
        BtnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String Robot = spnRobotsDB.getSelectedItem().toString();
                String[] parts = Robot.split(" - ");


                if (!Robot.equals("Modificar/Eliminar")) {

                    System.out.println(parts[0]);
                    SQL.GetDataByID(Integer.valueOf(parts[0]));

                    Boolean Validate = SQL.ModifyRobot(Integer.valueOf(parts[0]),Edit_Registro_Nombre.getText().toString(),
                            Edit_Registro_Modelo.getText().toString(),
                            Edit_Registro_IP.getText().toString(),
                            Edit_Registro_Dir.getText().toString());



                if (Validate) {
                    Toast.makeText(getContext(), "Modificacion Exitosa",
                            Toast.LENGTH_LONG).show();

                    Edit_Registro_Nombre.setText("");
                    Edit_Registro_Modelo.setText("");
                    Edit_Registro_IP.setText("");
                    Edit_Registro_Dir.setText("");

                    Fill_Spinner_Robots();

                }else{
                    Toast.makeText(getContext(), "Lo sentimos hubo un error, intentalo mas tarde de nuevo", Toast.LENGTH_SHORT).show();
                }
                } else {
                    Toast.makeText(getContext(), "Modificacion Erronea comprueba los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button BtnDelete = view.findViewById(R.id.buttonEliminar);
        BtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean Validate = SQL.DeleteRobot(Edit_Registro_Nombre.getText().toString(),
                        Edit_Registro_Modelo.getText().toString(),
                        Edit_Registro_IP.getText().toString(),
                        Edit_Registro_Dir.getText().toString());

                if (Validate) {
                    Toast.makeText(getContext(), "Eliminacion Exitosa", Toast.LENGTH_SHORT).show();

                    Edit_Registro_Nombre.setText("");
                    Edit_Registro_Modelo.setText("");
                    Edit_Registro_IP.setText("");
                    Edit_Registro_Dir.setText("");

                    Fill_Spinner_Robots();
                } else {
                    Toast.makeText(getContext(), "Eliminacion Erronea comprueba los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spnRobotsDB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                ///Agregar codigo de seleccion de ip aqui
                String Robot = spnRobotsDB.getSelectedItem().toString();
                String[] parts = Robot.split(" - ");


                if (!Robot.equals("Modificar/Eliminar")) {

                    System.out.println(parts[0]);
                    SQL.GetDataByID(Integer.valueOf(parts[0]));

                    Edit_Registro_Nombre.setText(SQL.ConsultaNombre);
                    Edit_Registro_Modelo.setText(SQL.ConsultaModelo);
                    Edit_Registro_IP.setText(SQL.Consultaip);
                    Edit_Registro_Dir.setText(SQL.Consultadir);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    public void Fill_Spinner_Robots() {

        RobotsList.clear();

        //SpinnerRobot
        spnRobotsDB.setAdapter(null);
        RobotsList.add("Modificar/Eliminar");

        //LLenar Con datos de SQL
        if (!SQL.Validate_Connection()) {
            Toast.makeText(getContext(), "No se encontro una conexión a internet.", Toast.LENGTH_SHORT).show();
        } else {
            SQL.Fill_Combo_IP_RobotsSQL();

            assert spnRobotsDB != null;
            spnRobotsDB.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_style_items, RobotsList));
        }
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
