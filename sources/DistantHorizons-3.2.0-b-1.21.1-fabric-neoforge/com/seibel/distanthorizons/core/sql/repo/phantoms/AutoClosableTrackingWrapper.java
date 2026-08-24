package com.seibel.distanthorizons.core.sql.repo.phantoms;

import com.seibel.distanthorizons.coreapi.ModInfo;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoClosableTrackingWrapper implements InvocationHandler {
   public static final boolean TRACK_WRAPPERS = ModInfo.IS_DEV_BUILD;
   @NotNull
   public final AutoCloseable wrappedClosable;
   @NotNull
   public final Set<AutoClosableTrackingWrapper> parentTrackingSet;

   @Nullable
   public static <TStatic extends AutoCloseable> TStatic wrap(
      @NotNull Class<?> clazz, @Nullable TStatic wrappedClosable, @NotNull Set<AutoClosableTrackingWrapper> trackingSet
   ) {
      if (!TRACK_WRAPPERS) {
         return wrappedClosable;
      } else {
         return (TStatic)(wrappedClosable == null
            ? null
            : Proxy.newProxyInstance(
               wrappedClosable.getClass().getClassLoader(), new Class[]{clazz}, new AutoClosableTrackingWrapper(wrappedClosable, trackingSet)
            ));
      }
   }

   private AutoClosableTrackingWrapper(@NotNull AutoCloseable wrappedClosable, @NotNull Set<AutoClosableTrackingWrapper> trackingSet) {
      this.wrappedClosable = wrappedClosable;
      this.parentTrackingSet = trackingSet;
      this.parentTrackingSet.add(this);
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      if ("close".equals(method.getName())) {
         this.wrappedClosable.close();
         this.parentTrackingSet.remove(this);
         return null;
      } else {
         try {
            return method.invoke(this.wrappedClosable, args);
         } catch (InvocationTargetException var5) {
            throw var5.getTargetException();
         }
      }
   }
}
