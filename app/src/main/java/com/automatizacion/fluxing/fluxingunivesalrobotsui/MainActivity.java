package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddRobotFragment.OnFragmentInteractionListener,
        ConnectRobotFragment.OnFragmentInteractionListener,
        MoveRobotFragment.OnFragmentInteractionListener,
        URPRobotFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        //Carga fragment de conexión com  opcion predeterminada
        Fragment fragment = new ConnectRobotFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        } else if (id == R.id.URP_Robot) {

            FragmentSelect = true;
            fragment = new URPRobotFragment();
            URPRobotFragment.sftp.Disconnect();

        } else if (id == R.id.nav_manage) {

        }

        if (FragmentSelect) {
            getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }



    public static void PrintToTextview(String s) {
        ConnectRobotFragment.TxtLog.setText(ConnectRobotFragment.TxtLog.getText() + " " + s);
    }
}
