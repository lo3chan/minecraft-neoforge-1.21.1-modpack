package net.mehvahdjukaar.moonlight.platform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.IConfigSpec.ILoadedConfig;
import org.slf4j.Logger;
import org.slf4j.Marker;
import sun.misc.Unsafe;

public class ConfigHacks {
   private static Unsafe getUnsafe() throws ReflectiveOperationException {
      Field u = Unsafe.class.getDeclaredField("theUnsafe");
      u.setAccessible(true);
      return (Unsafe)u.get(null);
   }

   public static <T> T replaceStaticField(Class<?> ownerClass, String fieldName, UnaryOperator<T> operator) throws Exception {
      Objects.requireNonNull(ownerClass, "ownerClass");
      Objects.requireNonNull(fieldName, "fieldName");
      Objects.requireNonNull(operator, "operator");
      Field field = ownerClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      if ((field.getModifiers() & 8) == 0) {
         throw new IllegalArgumentException("Field " + fieldName + " is not static");
      } else {
         T current = (T)field.get(null);
         T replacement = operator.apply(current);
         Unsafe unsafe = getUnsafe();
         Object base = unsafe.staticFieldBase(field);
         long offset = unsafe.staticFieldOffset(field);
         unsafe.putObjectVolatile(base, offset, replacement);
         return current;
      }
   }

   public static void init() {
   }

   static {
      try {
         replaceStaticField(ConfigTracker.class, "LOGGER", o -> new ConfigHacks.ShushLogger((Logger)o));
      } catch (Exception var1) {
         throw new RuntimeException(var1);
      }
   }

   private record ShushLogger(Logger base) implements Logger {
      public String getName() {
         return this.base.getName();
      }

      public boolean isTraceEnabled() {
         return this.base.isTraceEnabled();
      }

      public void trace(String msg) {
         this.base.trace(msg);
      }

      public void trace(String format, Object arg) {
         this.base.trace(format, arg);
      }

      public void trace(String format, Object arg1, Object arg2) {
         this.base.trace(format, arg1, arg2);
      }

      public void trace(String format, Object... arguments) {
         this.base.trace(format);
      }

      public void trace(String msg, Throwable t) {
         this.base.trace(msg, t);
      }

      public boolean isTraceEnabled(Marker marker) {
         return this.base.isTraceEnabled(marker);
      }

      public void trace(Marker marker, String msg) {
         this.base.trace(marker, msg);
      }

      public void trace(Marker marker, String format, Object arg) {
         this.base.trace(marker, format, arg);
      }

      public void trace(Marker marker, String format, Object arg1, Object arg2) {
         this.base.trace(marker, format, arg1, arg2);
      }

      public void trace(Marker marker, String format, Object... argArray) {
         this.base.trace(marker, format, argArray);
      }

      public void trace(Marker marker, String msg, Throwable t) {
         this.base.trace(marker, msg, t);
      }

      public boolean isDebugEnabled() {
         return this.base.isDebugEnabled();
      }

      public void debug(String msg) {
         this.base.debug(msg);
      }

      public void debug(String format, Object arg) {
         this.base.debug(format, arg);
      }

      public void debug(String format, Object arg1, Object arg2) {
         this.base.debug(format, arg1, arg2);
      }

      public void debug(String format, Object... arguments) {
         this.base.debug(format);
      }

      public void debug(String msg, Throwable t) {
         this.base.debug(msg, t);
      }

      public boolean isDebugEnabled(Marker marker) {
         return this.base.isDebugEnabled(marker);
      }

      public void debug(Marker marker, String msg) {
         this.base.debug(marker, msg);
      }

      public void debug(Marker marker, String format, Object arg) {
         this.base.debug(marker, format, arg);
      }

      public void debug(Marker marker, String format, Object arg1, Object arg2) {
         this.base.debug(marker, format, arg1, arg2);
      }

      public void debug(Marker marker, String format, Object... arguments) {
         this.base.debug(marker, format, arguments);
      }

      public void debug(Marker marker, String msg, Throwable t) {
         this.base.debug(marker, msg, t);
      }

      public boolean isInfoEnabled() {
         return this.base.isInfoEnabled();
      }

      public void info(String msg) {
         this.base.info(msg);
      }

      public void info(String format, Object arg) {
         this.base.info(format, arg);
      }

      public void info(String format, Object arg1, Object arg2) {
         this.base.info(format, arg1, arg2);
      }

      public void info(String format, Object... arguments) {
         this.base.info(format);
      }

      public void info(String msg, Throwable t) {
         this.base.info(msg, t);
      }

      public boolean isInfoEnabled(Marker marker) {
         return this.base.isInfoEnabled(marker);
      }

      public void info(Marker marker, String msg) {
         this.base.info(marker, msg);
      }

      public void info(Marker marker, String format, Object arg) {
         this.base.info(marker, format, arg);
      }

      public void info(Marker marker, String format, Object arg1, Object arg2) {
         this.base.info(marker, format, arg1, arg2);
      }

      public void info(Marker marker, String format, Object... arguments) {
         this.base.info(marker, format, arguments);
      }

      public void info(Marker marker, String msg, Throwable t) {
         this.base.info(marker, msg, t);
      }

      public boolean isWarnEnabled() {
         return this.base.isWarnEnabled();
      }

      public void warn(String msg) {
         this.base.warn(msg);
      }

      public void warn(String format, Object arg) {
         this.base.warn(format, arg);
      }

      public void warn(String format, Object... arguments) {
         this.base.warn(format, arguments);
      }

      public void warn(String format, Object arg1, Object arg2) {
         if (!(arg1 instanceof ILoadedConfig lc && isMyConfig(lc))) {
            this.base.warn(format, arg1, arg2);
         }
      }

      private static boolean isMyConfig(ILoadedConfig lc) {
         try {
            Method method = lc.getClass().getDeclaredMethod("modConfig");
            method.setAccessible(true);
            ModConfig cf = (ModConfig)method.invoke(lc);
            return Moonlight.getDependents().contains(cf.getModId());
         } catch (Exception var3) {
            return false;
         }
      }

      public void warn(String msg, Throwable t) {
         this.base.warn(msg, t);
      }

      public boolean isWarnEnabled(Marker marker) {
         return this.base.isWarnEnabled(marker);
      }

      public void warn(Marker marker, String msg) {
         this.base.warn(marker, msg);
      }

      public void warn(Marker marker, String format, Object arg) {
         this.base.warn(marker, format, arg);
      }

      public void warn(Marker marker, String format, Object arg1, Object arg2) {
         this.base.warn(marker, format, arg1, arg2);
      }

      public void warn(Marker marker, String format, Object... arguments) {
         this.base.warn(marker, format, arguments);
      }

      public void warn(Marker marker, String msg, Throwable t) {
         this.base.warn(marker, msg, t);
      }

      public boolean isErrorEnabled() {
         return this.base.isErrorEnabled();
      }

      public void error(String msg) {
         this.base.error(msg);
      }

      public void error(String format, Object arg) {
         this.base.error(format, arg);
      }

      public void error(String format, Object arg1, Object arg2) {
         this.base.error(format, arg1, arg2);
      }

      public void error(String format, Object... arguments) {
         this.base.error(format, arguments);
      }

      public void error(String msg, Throwable t) {
         this.base.error(msg, t);
      }

      public boolean isErrorEnabled(Marker marker) {
         return this.base.isErrorEnabled(marker);
      }

      public void error(Marker marker, String msg) {
         this.base.error(marker, msg);
      }

      public void error(Marker marker, String format, Object arg) {
         this.base.error(marker, format, arg);
      }

      public void error(Marker marker, String format, Object arg1, Object arg2) {
         this.base.error(marker, format, arg1, arg2);
      }

      public void error(Marker marker, String format, Object... arguments) {
         this.base.error(marker, format, arguments);
      }

      public void error(Marker marker, String msg, Throwable t) {
         this.base.error(marker, msg, t);
      }
   }
}
