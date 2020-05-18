package com.example.agencijazanekretnine.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.agencijazanekretnine.db.model.Nekretnine;
import com.example.agencijazanekretnine.db.model.Slike;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "nekretnine.db";

    private static final int DATABASE_VERSION = 1;

    private Dao<Nekretnine, Integer> nekretnineDao = null;
    private Dao<Slike, Integer> slikeDao = null;


    public DatabaseHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable( connectionSource, Nekretnine.class );
            TableUtils.createTable( connectionSource, Slike.class );
        } catch (SQLException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {


        try {
            TableUtils.dropTable( connectionSource, Nekretnine.class, true );
            TableUtils.dropTable( connectionSource, Slike.class, true );
        } catch (SQLException e) {
            throw new RuntimeException( e );
        }
    }

    public Dao<Nekretnine, Integer> getNekretnineDao() throws SQLException {
        if (nekretnineDao == null) {
            nekretnineDao = getDao( Nekretnine.class );
        }

        return nekretnineDao;
    }

    public Dao<Slike, Integer> getSlikeDao() throws SQLException {
        if (slikeDao == null) {
            slikeDao = getDao( Slike.class );
        }

        return slikeDao;
    }

    @Override
    public void close() {
        nekretnineDao = null;
        slikeDao = null;

        super.close();
    }
}
