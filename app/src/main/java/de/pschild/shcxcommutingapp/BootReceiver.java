package de.pschild.shcxcommutingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.pschild.shcxcommutingapp.util.Logger;

public class BootReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    final String action = intent.getAction();
    if (action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      Logger.log(context, "BootReceiver.onReceive");
      Scheduler.schedule(context);
    }
  }
}
