package com.example.agencijazanekretnine.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "slike")
public class Slike {

    @DatabaseField(generatedId = true)
    private int mId;

    @DatabaseField(columnName = "mSlika")
    private String mSlika;


    @DatabaseField(columnName = "nekretnine", foreign = true, foreignAutoRefresh = true)
    private Nekretnine mNekretnine;


    public Slike() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmSlika() {
        return mSlika;
    }

    public void setmSlika(String mSlika) {
        this.mSlika = mSlika;
    }

    public Nekretnine getmNekretnine() {
        return mNekretnine;
    }

    public void setmNekretnine(Nekretnine mNekretnine) {
        this.mNekretnine = mNekretnine;
    }
}
