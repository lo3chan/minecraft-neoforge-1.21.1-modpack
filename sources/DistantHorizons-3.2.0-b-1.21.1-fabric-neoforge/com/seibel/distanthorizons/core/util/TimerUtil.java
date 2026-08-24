package com.seibel.distanthorizons.core.util;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {
   public static Timer CreateTimer(String timerName) {
      return new Timer("DH-" + timerName, true);
   }

   public static TimerTask createTimerTask(Runnable runMethod) {
      return new TimerTask() {
         @Override
         public void run() {
            runMethod.run();
         }
      };
   }
}
