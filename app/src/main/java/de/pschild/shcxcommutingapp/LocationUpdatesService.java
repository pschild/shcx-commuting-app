package de.pschild.shcxcommutingapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import de.pschild.shcxcommutingapp.util.Logger;

public class LocationUpdatesService extends Service {

  @Override
  public void onCreate() {
    Logger.log(getApplicationContext(), "LocationUpdatesService.onCreate");
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Logger.log(getApplicationContext(), "LocationUpdatesService.onStartCommand");
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
