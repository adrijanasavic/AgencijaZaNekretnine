package com.example.agencijazanekretnine.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agencijazanekretnine.R;
import com.example.agencijazanekretnine.db.DatabaseHelper;
import com.example.agencijazanekretnine.db.model.Nekretnine;
import com.example.agencijazanekretnine.dialog.AboutDialog;
import com.example.agencijazanekretnine.settings.SettingsActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<String> drawerItems;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;

    private Nekretnine nekretnine;
    private DatabaseHelper databaseHelper;

    private RecyclerView rec_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details );

        showDetalji();

        fillDataDrawer();
        setupToolbar();
        setupDrawer();
    }

    public void showDetalji() {
        int nekretninaId = getIntent().getExtras().getInt( MainActivity.NEKRETNINA_ID );

        try {
            nekretnine = getDatabaseHelper().getNekretnineDao().queryForId( nekretninaId );

            TextView naziv = findViewById( R.id.detalji_naziv );
            TextView adresa = findViewById( R.id.detalji_adresa );
            TextView telefon = findViewById( R.id.detalji_telefon );
            TextView kvadratura = findViewById( R.id.detalji_kvadratura );
            TextView brojSoba = findViewById( R.id.detalji_broj_soba );
            TextView cena = findViewById( R.id.detalji_cena );
            TextView opis = findViewById( R.id.detalji_opis );
            TextView poruka = findViewById( R.id.detalji_poruka ); //

            naziv.setText( nekretnine.getmNaziv() );
            adresa.setText( "Adresa: " + nekretnine.getmAdresa() );
            telefon.setText( "Telefon: " + nekretnine.getmBrojTelefona() );
            kvadratura.setText( "Kvadratura: " + nekretnine.getmKvadratura() + "m2" );
            brojSoba.setText( "Broj soba: " + nekretnine.getmBrojSoba() );
            cena.setText( "Cena: " + nekretnine.getmCena() + "eur" );
            opis.setText( nekretnine.getmOpis() );
            poruka.setText( nekretnine.getmBrojTelefona() );//

            telefon.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent intent = new Intent( Intent.ACTION_DIAL );
//                    intent.setData( Uri.parse( "tel:" + nekretnine.getmBrojTelefona() ) );
//                    startActivity( intent );

                    startActivity( new Intent( Intent.ACTION_DIAL, Uri.parse( "tel:" + nekretnine.getmBrojTelefona() ) ) );
                }
            } );

            poruka.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent( Intent.ACTION_SENDTO );
//                    intent.setData( Uri.parse( "smsto:" + nekretnine.getmBrojTelefona() ) );
//                    startActivity( intent );

                    startActivity( new Intent( Intent.ACTION_SENDTO, Uri.parse( "smsto:" + nekretnine.getmBrojTelefona() ) ) );

                }
            } );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        rec_list = findViewById( R.id.rvList );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.details_menu, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_slika:
                setTitle( "Dodavanje slike" );
                break;

            case R.id.edit:
                setTitle( "Izmena nekretnine" );
                break;

            case R.id.delete:
                setTitle( "Brisanje nekretnine" );
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
                        finish();
                        break;
                    case 1:
                        Toast.makeText( getBaseContext(), "Prikaz podesavanja", Toast.LENGTH_SHORT );
                        startActivity( new Intent( DetailsActivity.this, SettingsActivity.class ) );
                        title = "Settings";
                        break;
                    case 2:
                        Toast.makeText( getBaseContext(), "Prikaz o aplikaciji", Toast.LENGTH_SHORT );
                        AboutDialog dialog = new AboutDialog( DetailsActivity.this );
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
        toolbar.setSubtitle( "Detalji nekretnine" );
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
}
