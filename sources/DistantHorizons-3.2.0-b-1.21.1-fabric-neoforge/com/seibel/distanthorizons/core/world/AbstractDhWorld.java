package com.seibel.distanthorizons.core.world;

import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.logging.f3.F3Screen;
import java.io.Closeable;
import java.util.List;

public abstract class AbstractDhWorld implements IDhWorld, Closeable {
   protected static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public final EWorldEnvironment environment;

   protected AbstractDhWorld(EWorldEnvironment environment) {
      this.environment = environment;
   }

   @Override
   public abstract void close();

   public void addDebugMenuStringsToList(List<String> messageList) {
      EWorldEnvironment environment = this.environment;
      String levelCountStr = F3Screen.NUMBER_FORMAT.format((long)this.getLoadedLevelCount());
      String readOnlyStr = "";
      if (DhApiWorldProxy.INSTANCE.tryGetReadOnly()) {
         readOnlyStr = readOnlyStr + " - ReadOnly";
      }

      String message = environment + " World with " + levelCountStr + " levels" + readOnlyStr;
      messageList.add(message);
   }
}
