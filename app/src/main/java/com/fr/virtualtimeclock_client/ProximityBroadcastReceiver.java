package com.fr.virtualtimeclock_client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class ProximityBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entered = intent.getBooleanExtra(key, false);

        Toast.makeText(context, (entered ? "Vous êtes entré dans la zone" : "Vous êtes sorti de la zone"), Toast.LENGTH_SHORT).show();
    }

}