package com.seibel.distanthorizons.core.wrapperInterfaces.modAccessor;

import com.seibel.distanthorizons.core.dependencyInjection.SingletonInjector;
import com.seibel.distanthorizons.core.logging.DhLogger;
import com.seibel.distanthorizons.core.logging.DhLoggerBuilder;
import com.seibel.distanthorizons.core.wrapperInterfaces.minecraft.IMinecraftClientWrapper;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class AbstractOptifineAccessor implements IOptifineAccessor {
   private static final DhLogger LOGGER = new DhLoggerBuilder().build();
   public Field ofFogField = null;
   public Object mcOptionsObject = null;

   @Override
   public void finishDelayedSetup() {
      this.mcOptionsObject = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class).getOptionsObject();
      this.ofFogField = getOptifineFogField();
   }

   public static Field getOptifineFogField() {
      Object mcOptions = SingletonInjector.INSTANCE.get(IMinecraftClientWrapper.class).getOptionsObject();

      for (Field field : mcOptions.getClass().getDeclaredFields()) {
         if (field.getName().equals("ofFogType")) {
            return field;
         }
      }

      return null;
   }

   public boolean getIsShaderActive() {
      try {
         String activeShaderName = (String)Class.forName("net.optifine.shaders.Shaders").getDeclaredMethod("getShaderPackName").invoke(null);
         return activeShaderName != null;
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var2) {
         return false;
      }
   }
}
