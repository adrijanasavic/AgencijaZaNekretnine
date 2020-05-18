package com.example.agencijazanekretnine.db.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "nekretnine")
public class Nekretnine {

    @DatabaseField(generatedId = true)
    private int mId;

    @DatabaseField(columnName = "mNaziv")
    private String mNaziv;

    @DatabaseField(columnName = "mOpis")
    private String mOpis;

    @DatabaseField(columnName = "mAdresa")
    private String mAdresa;

    @DatabaseField(columnName = "mBrojTelefona")
    private String mBrojTelefona;

    @DatabaseField(columnName = "mKvadratura")
    private String mKvadratura;

    @DatabaseField(columnName = "mBrojSoba")
    private String mBrojSoba;

    @DatabaseField(columnName = "mCena")
    private String mCena;

    @ForeignCollectionField(columnName = "mSlike", eager = true)
    private ForeignCollection<Slike> mSlike;

    //ORMLite zahteva prazan konstuktur u klasama koje opisuju tabele u bazi!
    public Nekretnine() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNaziv() {
        return mNaziv;
    }

    public void setmNaziv(String mNaziv) {
        this.mNaziv = mNaziv;
    }

    public String getmOpis() {
        return mOpis;
    }

    public void setmOpis(String mOpis) {
        this.mOpis = mOpis;
    }

    public String getmAdresa() {
        return mAdresa;
    }

    public void setmAdresa(String mAdresa) {
        this.mAdresa = mAdresa;
    }

    public String getmBrojTelefona() {
        return mBrojTelefona;
    }

    public void setmBrojTelefona(String mBrojTelefona) {
        this.mBrojTelefona = mBrojTelefona;
    }

    public String getmKvadratura() {
        return mKvadratura;
    }

    public void setmKvadratura(String mKvadratura) {
        this.mKvadratura = mKvadratura;
    }

    public String getmBrojSoba() {
        return mBrojSoba;
    }

    public void setmBrojSoba(String mBrojSoba) {
        this.mBrojSoba = mBrojSoba;
    }

    public String getmCena() {
        return mCena;
    }

    public void setmCena(String mCena) {
        this.mCena = mCena;
    }

    public ForeignCollection<Slike> getmSlike() {
        return mSlike;
    }

    public void setmSlike(ForeignCollection<Slike> mSlike) {
        this.mSlike = mSlike;
    }
}
