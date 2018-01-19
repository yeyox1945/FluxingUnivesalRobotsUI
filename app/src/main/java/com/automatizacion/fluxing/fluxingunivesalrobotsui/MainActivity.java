package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.ClipData;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddRobotFragment.OnFragmentInteractionListener,
        ConnectRobotFragment.OnFragmentInteractionListener,
        MoveRobotFragment.OnFragmentInteractionListener {


    public static Conector_Cliente Connect_Client;
    public static TextView TxtLog;
    public EditText TxtMSG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TxtLog = findViewById(R.id.TxtLog);
        TxtMSG = findViewById(R.id.EditCommand);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Menu de la derecha de 3 puntos
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Menu hamburguesa

        int id = item.getItemId();

        Fragment fragment = null;
        Boolean FragmentSelect = false;

        if (id == R.id.Add_Robot) {

            FragmentSelect = true;
            fragment = new AddRobotFragment();

        } else if (id == R.id.Connect_Robot) {


            FragmentSelect = true;
            fragment = new ConnectRobotFragment();

        } else if (id == R.id.Move_Robot) {

            FragmentSelect = true;
            fragment = new MoveRobotFragment();

            Connect_Client = new Conector_Cliente("192.168.15.155", 30001);
            Connect_Client.conectar();

        } else if (id == R.id.nav_manage) {

        }

        if (FragmentSelect) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    //Metodo se ejecuta cuando se oprime el boton registrar
    public void ButtonRegistrationRobot(View v) {


        EditText IP = findViewById(R.id.TxtIPnueva);
        String IPnueva = IP.getText().toString();

        AddRobotFragment FragmentAgregar = new AddRobotFragment();

        FragmentAgregar.RegistroIPRobot(IPnueva);
    }

    //Metodo se ejecuta al conectar un robot
    public void OnClickConectarRobot(View view) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TxtLog = findViewById(R.id.TxtLog);

        Connect_Client = new Conector_Cliente("192.168.15.155", 29999);
        Connect_Client.conectar();
        Connect_Client.start();
    }


    public void OnClicksendOrder(View view) {

        TxtLog = findViewById(R.id.TxtLog);
        TxtMSG = findViewById(R.id.EditCommand);

        Connect_Client.enviarMSG(TxtMSG.getText().toString());
        TxtLog.setText(TxtLog.getText() + "\nServidor : " + TxtMSG.getText());
        TxtMSG.setText("");

    }

    public static void PrintToTextview(String s) {
        TxtLog.setText(TxtLog.getText() + " " + s);
    }


}
