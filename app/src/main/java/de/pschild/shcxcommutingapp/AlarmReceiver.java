package de.pschild.shcxcommutingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.pschild.shcxcommutingapp.util.Logger;

public class AlarmReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    Logger.log(context, "AlarmReceiver.onReceive");
    this.scheduleNextAlarm(context);
    this.startLocationService(context);
  }

  private void scheduleNextAlarm(Context context) {
    Logger.log(context, "AlarmReceiver.scheduleNextAlarm");
    Scheduler.schedule(context);
  }

  private void startLocationService(Context context) {
    Logger.log(context, "AlarmReceiver.startLocationService");
    final Intent serviceIntent = new Intent(context, LocationUpdatesService.class);
    context.startForegroundService(serviceIntent);
  }
}
