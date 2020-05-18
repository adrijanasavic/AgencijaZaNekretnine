package com.example.agencijazanekretnine.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agencijazanekretnine.R;
import com.example.agencijazanekretnine.adapters.SlikaAdapter;
import com.example.agencijazanekretnine.db.DatabaseHelper;
import com.example.agencijazanekretnine.db.model.Nekretnine;
import com.example.agencijazanekretnine.db.model.Slike;
import com.example.agencijazanekretnine.dialog.AboutDialog;
import com.example.agencijazanekretnine.settings.SettingsActivity;
import com.example.agencijazanekretnine.tools.Tools;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements SlikaAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private ArrayList<String> drawerItems;
    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;

    private Nekretnine nekretnine;
    private DatabaseHelper databaseHelper;

    private RecyclerView rec_list;
    private SlikaAdapter adapter;

    private ImageView preview;
    private String imagePath = null;
    private static final int SELECT_PICTURE = 1;

    private SharedPreferences prefs;
    public static final String NOTIF_CHANNEL_ID = "notif_channel_007";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_details );

        showDetalji();

        fillDataDrawer();
        setupToolbar();
        setupDrawer();

        createNotificationChannel();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );

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

            TextView poruka = findViewById( R.id.detalji_poruka );

            naziv.setText( nekretnine.getmNaziv() );
            adresa.setText( "Adresa: " + nekretnine.getmAdresa() );
            telefon.setText( "Telefon: " + nekretnine.getmBrojTelefona() );
            kvadratura.setText( "Kvadratura: " + nekretnine.getmKvadratura() + "m2" );
            brojSoba.setText( "Broj soba: " + nekretnine.getmBrojSoba() );
            cena.setText( "Cena: " + nekretnine.getmCena() + "eur" );
            opis.setText( nekretnine.getmOpis() );

            poruka.setText( nekretnine.getmBrojTelefona() );

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

    private void deleteNekretninu() {
        AlertDialog dialogDelete = new AlertDialog.Builder( this )
                .setTitle( "Brisanje nekretnine" )
                .setMessage( "Da li zelite da obrisete \"" + nekretnine.getmNaziv() + "\"?" )
                .setPositiveButton( "DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            List<Slike> slike = getDatabaseHelper().getSlikeDao().queryForEq( "nekretnine", nekretnine.getmId() );

                            getDatabaseHelper().getNekretnineDao().delete( nekretnine );

                            String tekstNotifikacije = "Nekretnina je obrisana";

                            boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                            boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                            if (toast) {
                                Toast.makeText( DetailsActivity.this, tekstNotifikacije, Toast.LENGTH_LONG ).show();

                            }
                            if (notif) {
                                NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                                NotificationCompat.Builder builder = new NotificationCompat.Builder( DetailsActivity.this, NOTIF_CHANNEL_ID );
                                builder.setSmallIcon( android.R.drawable.ic_menu_delete );
                                builder.setContentTitle( "Notifikacija" );
                                builder.setContentText( tekstNotifikacije );

                                Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher );


                                builder.setLargeIcon( bitmap );
                                notificationManager.notify( 1, builder.build() );

                            }

                            for (Slike slika : slike) {
                                getDatabaseHelper().getSlikeDao().delete( slika );
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                } )
                .setNegativeButton( "NE", null )
                .show();
    }


    private void editNekretninu() {
        final Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.edit_layout );
        dialog.setCanceledOnTouchOutside( false );

        final EditText naziv = dialog.findViewById( R.id.edit_naziv );
        final EditText adresa = dialog.findViewById( R.id.edit_adresa );
        final EditText telefon = dialog.findViewById( R.id.edit_telefon );
        final EditText kvadratura = dialog.findViewById( R.id.edit_kvadratura );
        final EditText brojSoba = dialog.findViewById( R.id.edit_brojSoba );
        final EditText cena = dialog.findViewById( R.id.edit_cena );
        final EditText opis = dialog.findViewById( R.id.edit_opis );


        naziv.setText( nekretnine.getmNaziv() );
        adresa.setText( nekretnine.getmAdresa() );
        telefon.setText( nekretnine.getmBrojTelefona() );
        kvadratura.setText( nekretnine.getmKvadratura() );
        brojSoba.setText( nekretnine.getmBrojSoba() );
        cena.setText( nekretnine.getmCena() );
        opis.setText( nekretnine.getmOpis() );

        Button add = dialog.findViewById( R.id.edit_btn_save );
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Tools.validateInput( naziv )
                        && Tools.validateInput( opis )
                        && Tools.validateInput( adresa )
                        && Tools.validateInput( telefon )
                        && Tools.validateInput( kvadratura )
                        && Tools.validateInput( brojSoba )
                        && Tools.validateInput( cena )
                ) {


                    nekretnine.setmNaziv( naziv.getText().toString() );
                    nekretnine.setmAdresa( adresa.getText().toString() );
                    nekretnine.setmBrojTelefona( telefon.getText().toString() );
                    nekretnine.setmKvadratura( kvadratura.getText().toString() );
                    nekretnine.setmBrojSoba( brojSoba.getText().toString() );
                    nekretnine.setmCena( cena.getText().toString() );
                    nekretnine.setmOpis( opis.getText().toString() );


                    try {
                        getDatabaseHelper().getNekretnineDao().update( nekretnine );

                        String tekstNotifikacije = "Nekretnina je izmenjena";

                        boolean toast = prefs.getBoolean( getString( R.string.toast_key ), false );
                        boolean notif = prefs.getBoolean( getString( R.string.notif_key ), false );

                        if (toast) {
                            Toast.makeText( DetailsActivity.this, tekstNotifikacije, Toast.LENGTH_LONG ).show();

                        }
                        if (notif) {
                            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
                            NotificationCompat.Builder builder = new NotificationCompat.Builder( DetailsActivity.this, NOTIF_CHANNEL_ID );
                            builder.setSmallIcon( android.R.drawable.ic_menu_edit );
                            builder.setContentTitle( "Notifikacija" );
                            builder.setContentText( tekstNotifikacije );

                            Bitmap bitmap = BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher );


                            builder.setLargeIcon( bitmap );
                            notificationManager.notify( 1, builder.build() );

                        }

                        refreshNekretnine();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();

                }
            }

        } );

        Button cancel = dialog.findViewById( R.id.edit_btn_cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        dialog.show();
    }

    private void addSlike() {

        final Dialog dialog = new Dialog( this );
        dialog.setContentView( R.layout.add_slike );
        dialog.setTitle( "Unesite podatke" );
        dialog.setCanceledOnTouchOutside( false );

        Button chooseBtn = dialog.findViewById( R.id.btn_izaberi );
        chooseBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preview = dialog.findViewById( R.id.preview_image1 );
                selectPicture();
            }
        } );

        Button add = dialog.findViewById( R.id.add_atrakcija );
        add.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (preview == null || imagePath == null) {
                    Toast.makeText( DetailsActivity.this, "Slika mora biti izabrana", Toast.LENGTH_SHORT ).show();
                    return;
                }

                int position = getIntent().getExtras().getInt( "position" );
                try {
                    nekretnine = getDatabaseHelper().getNekretnineDao().queryForId( position );
                    Slike slike = new Slike();

                    slike.setmSlika( imagePath );
                    slike.setmNekretnine( nekretnine );
                    getDatabaseHelper().getSlikeDao().create( slike );

                    refresh();
                    reset();

                } catch (SQLException e) {
                    e.printStackTrace();
                }


                dialog.dismiss();

            }

        } );

        Button cancel = dialog.findViewById( R.id.cancel );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );

        dialog.show();
    }

    private void refreshNekretnine() {
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

            naziv.setText( nekretnine.getmNaziv() );
            adresa.setText( "Adresa: " + nekretnine.getmAdresa() );
            telefon.setText( "Telefon: " + nekretnine.getmBrojTelefona() );
            kvadratura.setText( "Kvadratura: " + nekretnine.getmKvadratura() + "m2" );
            brojSoba.setText( "Broj soba: " + nekretnine.getmBrojSoba() );
            cena.setText( "Cena: " + nekretnine.getmCena() + "eur" );
            opis.setText( nekretnine.getmOpis() );


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void refresh() {

        List<Slike> listaSlika = null;
        try {
            listaSlika = getDatabaseHelper().getSlikeDao().queryBuilder()
                    .where()
                    .eq( "nekretnine", nekretnine.getmId() ) //TODO:
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (adapter != null) {
            adapter.clear();
            adapter.addAll( listaSlika );
            adapter.notifyDataSetChanged();
        } else {
            adapter = new SlikaAdapter( DetailsActivity.this, listaSlika, DetailsActivity.this );

            rec_list.setHasFixedSize( true );


            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( DetailsActivity.this, LinearLayoutManager.HORIZONTAL, false );
            rec_list.setLayoutManager( layoutManager );

            rec_list.setAdapter( adapter );
        }

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
                addSlike();
                setTitle( "Dodavanje slike" );
                break;

            case R.id.edit:
                editNekretninu();
                setTitle( "Izmena nekretnine" );
                break;

            case R.id.delete:
                deleteNekretninu();
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

    private void reset() {
        imagePath = "";
        preview = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            if (selectedImage != null) {
                Cursor cursor = getContentResolver().query( selectedImage, filePathColumn, null, null, null );
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex( filePathColumn[0] );
                    imagePath = cursor.getString( columnIndex );
                    cursor.close();

                    if (preview != null) {
                        preview.setImageBitmap( BitmapFactory.decodeFile( imagePath ) );
                    }
                }
            }
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE )
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE )
                            == PackageManager.PERMISSION_GRANTED) {

                return true;
            } else {


                ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1 );
                return false;
            }
        } else {

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void selectPicture() {
        if (isStoragePermissionGranted()) {
            Intent i = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
            startActivityForResult( i, SELECT_PICTURE );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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

        List<Slike> listaSlika = null;
        try {
            listaSlika = getDatabaseHelper().getSlikeDao().queryBuilder()
                    .where()
                    .eq( "nekretnine", nekretnine.getmId() )
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent( DetailsActivity.this, FullSlika.class );
        intent.putExtra( "slika", listaSlika.get( position ).getmSlika() );
        startActivity( intent );
    }
}
