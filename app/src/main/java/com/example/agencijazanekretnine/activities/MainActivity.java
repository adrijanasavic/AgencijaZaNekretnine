package com.example.agencijazanekretnine.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.agencijazanekretnine.R;
import com.example.agencijazanekretnine.adapters.MainAdapter;
import com.example.agencijazanekretnine.db.DatabaseHelper;
import com.example.agencijazanekretnine.db.model.Nekretnine;
import com.example.agencijazanekretnine.dialog.AboutDialog;
import com.example.agencijazanekretnine.settings.SettingsActivity;
import com.example.agencijazanekretnine.tools.Tools;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private ArrayList<String> drawerItems;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;

    private EditText naziv;
    private EditText opis;
    private EditText adresa;
    private EditText telefon;
    private EditText kvadratura;
    private EditText brojSoba;
    private EditText cena;

    DatabaseHelper databaseHelper;

    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";
    private SharedPreferences prefs;
    public static String NEKRETNINA_ID = "NEKRETNINA_ID";

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        fillDataDrawer();
        setupToolbar();
        setupDrawer();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

    }

    public void showNekretnine() {

        final RecyclerView recyclerView = this.findViewById( R.id.rvList );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
        recyclerView.setLayoutManager( layoutManager );

        MainAdapter adapter = new MainAdapter( getDatabaseHelper(), MainActivity.this );
        recyclerView.setAdapter( adapter );

    }

    private void addNekretnina() {

        final Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.add_layout );
        dialog.setTitle( "Unesite podatke" );
        dialog.setCanceledOnTouchOutside( false );

        Button add = dialog.findViewById( R.id.btn_add );
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                naziv = dialog.findViewById( R.id.add_naziv );
                opis = dialog.findViewById( R.id.add_opis );
                adresa = dialog.findViewById( R.id.add_adresa );
                telefon = dialog.findViewById( R.id.add_telefon );
                kvadratura = dialog.findViewById( R.id.add_kvadratura );
                brojSoba = dialog.findViewById( R.id.add_brojSoba );
                cena = dialog.findViewById( R.id.add_cena );


                if (Tools.validateInput( naziv )// dobro je
                        && Tools.validateInput( opis )
                        && Tools.validateInput( adresa )
                        && Tools.validateInput( telefon )
                        && Tools.validateInput( kvadratura )
                        && Tools.validateInput( brojSoba )
                        && Tools.validateInput( cena )
                ) {

                    Nekretnine nekretnine = new Nekretnine();

                    nekretnine.setmNaziv( naziv.getText().toString() );
                    nekretnine.setmOpis( opis.getText().toString() );
                    nekretnine.setmAdresa( adresa.getText().toString() );
                    nekretnine.setmBrojTelefona( telefon.getText().toString() );
                    nekretnine.setmKvadratura( kvadratura.getText().toString() );
                    nekretnine.setmBrojSoba( brojSoba.getText().toString() );
                    nekretnine.setmCena( cena.getText().toString() );

                    try {
                        getDatabaseHelper().getNekretnineDao().create( nekretnine );

                        String tekstNotifikacije = "Uneta nova nekretnina";

                        boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                        boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                        if (toast) {
                            Toast.makeText( MainActivity.this, tekstNotifikacije, Toast.LENGTH_LONG ).show();

                        }
                        if (notif) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                            NotificationCompat.Builder builder = new NotificationCompat.Builder( MainActivity.this, NOTIF_CHANNEL_ID );
                            builder.setSmallIcon( R.drawable.heart );
                            builder.setContentTitle( "Notifikacija" );
                            builder.setContentText( tekstNotifikacije );

                            Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher_foreground );


                            builder.setLargeIcon( bitmap );
                            notificationManager.notify( 1, builder.build() );

                        }

                        refresh();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();


                }
            }

        } );

        Button cancel = dialog.findViewById( R.id.btn_cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        dialog.show();
    }

    private void refresh() {

        RecyclerView recyclerView = findViewById( R.id.rvList );
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( this );
            recyclerView.setLayoutManager( layoutManager );


            MainAdapter adapter = new MainAdapter( getDatabaseHelper(), MainActivity.this );
            recyclerView.setAdapter( adapter );

        }
    }

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addNekretnina();
                setTitle( "Dodavanje nekretnine" );
                break;

            case R.id.settings:
                startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
                setTitle( "Settings" );
                break;

            case R.id.action_about:
                AboutDialog dialog = new AboutDialog( MainActivity.this );
                dialog.show();
                setTitle( "O aplikaciji" );
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    private void setupDrawer() {
        drawerList = findViewById( R.id.left_drawer );
        drawerLayout = findViewById( R.id.drawer_layout );
        drawerPane = findViewById( R.id.drawerPane );
        drawerList.setAdapter( new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, drawerItems ) );

        drawerList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = "Unknown";
                switch (i) {
                    case 0:
                        title = "Lista nekretnina";
                        showNekretnine();
                        break;
                    case 1:
                        Toast.makeText( getBaseContext(), "Prikaz podesavanja", Toast.LENGTH_SHORT );
                        startActivity( new Intent( MainActivity.this, SettingsActivity.class ) );
                        title = "Settings";
                        break;
                    case 2:
                        Toast.makeText( getBaseContext(), "Prikaz o aplikaciji", Toast.LENGTH_SHORT );
                        AboutDialog dialog = new AboutDialog( MainActivity.this );
                        dialog.show();
                        title = "O aplikaciji";
                        break;
                    default:
                        break;
                }
                setTitle( title );
                drawerLayout.closeDrawer( Gravity.LEFT );
            }
        } );

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle( "" );
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle( "" );
                invalidateOptionsMenu();
            }
        };
    }

    private void fillDataDrawer() {
        drawerItems = new ArrayList<>();
        drawerItems.add( "Lista nekretnina" );
        drawerItems.add( "Settings" );
        drawerItems.add( "O aplikaciji" );
    }

    public void setupToolbar() {
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        toolbar.setTitleTextColor( Color.WHITE );
        toolbar.setSubtitle( "Lista nekretnina" );
        toolbar.setLogo( R.drawable.heart );

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled( true );
            actionBar.setHomeAsUpIndicator( R.drawable.drawer );
            actionBar.setHomeButtonEnabled( true );
            actionBar.show();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

    }

    public DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
        }
        return databaseHelper;
    }

    private void showDialog() {
        if (dialog == null) {
            dialog = new AboutDialog( MainActivity.this ).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
        dialog.show();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "Description of My Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel( NOTIF_CHANNEL_ID, name, importance );
            channel.setDescription( description );

            NotificationManager notificationManager = getSystemService( NotificationManager.class );
            notificationManager.createNotificationChannel( channel );
        }
    }

    @Override
    public void onItemClick(int position) {

        Intent i = new Intent( MainActivity.this, DetailsActivity.class );
        try {
            i.putExtra( NEKRETNINA_ID, getDatabaseHelper().getNekretnineDao().queryForAll().get( position ).getmId() );
            i.putExtra( "position", getDatabaseHelper().getNekretnineDao().queryForAll().get( position ).getmId() );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startActivity( i );
    }
}
