package de.pschild.shcxcommutingapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import de.pschild.shcxcommutingapp.util.Logger;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

public class Scheduler {

  public static void schedule(final Context context) {
    final Calendar nextAlarm = Scheduler.calculateNextAlarm();
    Logger.log(context, "Set next alarm for " + DateFormat.format("yyyy-MM-dd HH:mm:ss", nextAlarm).toString());

    final Intent alarmIntent = new Intent(context, AlarmReceiver.class);
    final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 42, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.getTimeInMillis(), pendingIntent);
  }

  public static Calendar calculateNextAlarm() {
//    if (isWeekend()) {
//      // TODO: if Sat. or Sun., schedule for Mon.
//      return null;
//    }

    final String[] triggerTimes = getTriggerTimes();
    Arrays.sort(triggerTimes, Comparator.comparing(LocalTime::parse));
    final Calendar now = now();
    final int nowHour = now.get(Calendar.HOUR_OF_DAY);
    final int nowMinute = now.get(Calendar.MINUTE);

    String candidate = null;
    for (final String time : triggerTimes) {
      final String[] timeParts = time.split(":");
      final int timeHour = Integer.parseInt(timeParts[0]);
      final int timeMinute = Integer.parseInt(timeParts[1]);
      if (nowHour < timeHour || (nowHour == timeHour && nowMinute < timeMinute)) {
        candidate = time;
        break;
      }
    }

    if (candidate == null) {
      candidate = triggerTimes[0];
    }

    final String[] candidateParts = candidate.split(":");
    final Calendar nextAlarm = Calendar.getInstance();
    nextAlarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(candidateParts[0]));
    nextAlarm.set(Calendar.MINUTE, Integer.parseInt(candidateParts[1]));
    nextAlarm.set(Calendar.SECOND, 0);
    nextAlarm.set(Calendar.MILLISECOND, 0);

    long startTime = nextAlarm.getTimeInMillis();
    if (nextAlarm.before(now)) {
      startTime += AlarmManager.INTERVAL_DAY;
    }
    nextAlarm.setTimeInMillis(startTime);
    return nextAlarm;
  }

  public static String[] getTriggerTimes() {
    // format MUST be HH:mm (incl. leading zero) - order of entries does NOT matter
    return new String[]{"05:30", "06:48"};
  }

  public static Calendar now() {
    return Calendar.getInstance();
  }

  private static boolean isWeekend() {
    final Calendar now = now();
    final int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
    return (dayOfWeek == Calendar.SATURDAY) || (dayOfWeek == Calendar.SUNDAY);
  }

}
