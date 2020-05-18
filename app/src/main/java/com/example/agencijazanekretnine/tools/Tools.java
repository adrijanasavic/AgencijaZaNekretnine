package com.example.agencijazanekretnine.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;


public class Tools {


    public static boolean validateInput(EditText editText) {

        String titleInput = editText.getText().toString().trim();

        if (titleInput.isEmpty()) {
            editText.setError( "Polje ne moze da bude prazno!!!" );
            return false;
        } else {
            editText.setError( null );
            return true;
        }
    }

    public static void dialPhoneNumber(String phoneNumber, Context context) {
        Intent intent = new Intent( Intent.ACTION_DIAL );
        intent.setData( Uri.parse( "tel:" + phoneNumber ) );
        if (intent.resolveActivity( context.getPackageManager() ) != null) {
            context.startActivity( intent );
        }
    }

    public static void sendMessege(String phoneNumber, Context context) {
        Intent intent = new Intent( Intent.ACTION_SENDTO );
        intent.setData( Uri.parse( "smsto:" + phoneNumber ) );
        if (intent.resolveActivity( context.getPackageManager() ) != null) {
            context.startActivity( intent );
        }
    }

    public void showMap(Uri geoLocation, Context context) {
        Intent intent = new Intent( Intent.ACTION_VIEW );
        intent.setData( geoLocation );
        if (intent.resolveActivity( context.getPackageManager() ) != null) {
            context.startActivity( intent );
        }
    }

    public static void openWebPage(String url, Context context) {
        Uri webpage = Uri.parse( url );
        Intent intent = new Intent( Intent.ACTION_VIEW, webpage );
        if (intent.resolveActivity( context.getPackageManager() ) != null) {
            context.startActivity( intent );
        }
    }
}
