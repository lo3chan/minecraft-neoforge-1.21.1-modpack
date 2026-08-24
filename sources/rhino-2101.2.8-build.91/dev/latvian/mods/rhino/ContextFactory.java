package dev.latvian.mods.rhino;

import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.IdentityHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ContextFactory {
   private final ThreadLocal<Context> currentContext = ThreadLocal.withInitial(this::createContext);
   private final TypeWrappers typeWrappers = new TypeWrappers();
   private final Map<Class<?>, Object[]> defaultRecordProperties = new IdentityHashMap<>();
   private final Lookup methodHandlesLookup = MethodHandles.publicLookup();
   private final Map<Class<?>, MethodHandle> recordConstructors = new IdentityHashMap<>();
   private boolean instanceStaticFallback = true;

   protected Context createContext() {
      return new Context(this);
   }

   public Context enter() {
      return this.currentContext.get();
   }

   public synchronized TypeWrappers getTypeWrappers() {
      return this.typeWrappers;
   }

   public synchronized void registerDefaultRecordProperties(Record record) {
      try {
         RecordComponent[] components = record.getClass().getRecordComponents();
         Object[] properties = new Object[components.length];

         for (int i = 0; i < components.length; i++) {
            properties[i] = components[i].getAccessor().invoke(record);
         }

         this.defaultRecordProperties.put(record.getClass(), properties);
      } catch (InvocationTargetException | IllegalAccessException var5) {
         throw new RuntimeException(var5);
      }
   }

   @Nullable
   public synchronized Object[] getDefaultRecordProperties(Class<?> type) {
      return this.defaultRecordProperties.get(type);
   }

   public Lookup getMethodHandlesLookup() {
      return this.methodHandlesLookup;
   }

   @Nullable
   public synchronized MethodHandle getRecordConstructor(Class<?> type) {
      if (!type.isRecord()) {
         return null;
      } else {
         MethodHandle constructor = this.recordConstructors.get(type);
         if (constructor == null) {
            try {
               RecordComponent[] components = type.getRecordComponents();
               Class<?>[] args = new Class[components.length];

               for (int i = 0; i < components.length; i++) {
                  args[i] = components[i].getType();
               }

               constructor = this.getMethodHandlesLookup().findConstructor(type, MethodType.methodType(void.class, args));
            } catch (Exception var6) {
               return null;
            }

            this.recordConstructors.put(type, constructor);
         }

         return constructor;
      }
   }

   public void setInstanceStaticFallback(boolean value) {
      this.instanceStaticFallback = value;
   }

   public boolean getInstanceStaticFallback() {
      return this.instanceStaticFallback;
   }

   public CachedClassStorage getCachedClassStorage() {
      return CachedClassStorage.GLOBAL_PUBLIC;
   }
}
