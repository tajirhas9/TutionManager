package com.ourcuet.tutionmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent intent1 = new Intent(context, PushNotification.class);
        context.startService(intent1);
    }
}
