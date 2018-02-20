package com.automatizacion.fluxing.fluxingunivesalrobotsui;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AddRobotFragment.OnFragmentInteractionListener,
        ConnectRobotFragment.OnFragmentInteractionListener,
        MoveRobotFragment.OnFragmentInteractionListener,
        FTPRobotFragment.OnFragmentInteractionListener,
        MonitorRobotFragment.OnFragmentInteractionListener {

    public static NavigationView navigationView;
    public static Menu nav_Menu;
    Fragment fragment = null;
    Boolean FragmentSelect = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Pone pantalla horizontal
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

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        nav_Menu = navigationView.getMenu();

        BlockItems(true);// Verdadero para bloquear
    }

    public void BlockItems(boolean v) {
        if (v) {
            nav_Menu.findItem(R.id.Move_Robot).setEnabled(false);
            nav_Menu.findItem(R.id.FTP_Robot).setEnabled(false);
            nav_Menu.findItem(R.id.Monitor_Robot).setEnabled(false);
        } else {
            nav_Menu.findItem(R.id.Move_Robot).setEnabled(true);
            nav_Menu.findItem(R.id.FTP_Robot).setEnabled(true);
            nav_Menu.findItem(R.id.Monitor_Robot).setEnabled(true);
        }
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

        if (id == R.id.Add_Robot) {

            FragmentSelect = true;
            fragment = new AddRobotFragment();

        } else if (id == R.id.Connect_Robot) {

            FragmentSelect = true;
            fragment = new ConnectRobotFragment();

        } else if (id == R.id.Move_Robot) {

            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Para mover el robot se cargará otro programa y quitará el actual.\n ¿Está seguro de querer hacer esto?");
            dlgAlert.setTitle("¡Precaución!");
            dlgAlert.setIcon(R.drawable.warning);
            dlgAlert.setPositiveButton("ACEPTO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    fragment = new MoveRobotFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.Contenedor, fragment).commit();
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                }
            });

            dlgAlert.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            dlgAlert.create().show();

        } else if (id == R.id.FTP_Robot) {

            FragmentSelect = true;
            fragment = new FTPRobotFragment();

        } else if (id == R.id.Monitor_Robot) {

            FragmentSelect = true;
            fragment = new MonitorRobotFragment();
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
}
