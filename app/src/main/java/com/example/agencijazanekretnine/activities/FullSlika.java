package com.example.agencijazanekretnine.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.agencijazanekretnine.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FullSlika extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_full_slika );

        String slika = getIntent().getExtras().getString("slika");
        ImageView imageView = findViewById(R.id.imageView);

        Uri mUri = Uri.parse(slika);
        imageView.setImageURI(mUri);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
