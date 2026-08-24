package dev.latvian.mods.rhino.type;

import java.lang.reflect.Method;

public class InterfaceTypeInfo extends ClassTypeInfo {
   private Boolean functional;

   InterfaceTypeInfo(Class<?> type, Boolean functional) {
      super(type);
      this.functional = functional;
   }

   InterfaceTypeInfo(Class<?> type) {
      this(type, null);
   }

   @Override
   public boolean isFunctionalInterface() {
      if (this.functional == null) {
         this.functional = Boolean.FALSE;

         try {
            if (this.asClass().isAnnotationPresent(FunctionalInterface.class)) {
               this.functional = Boolean.TRUE;
            } else {
               int count = 0;

               for (Method method : this.asClass().getMethods()) {
                  if (!method.isDefault() && !method.isSynthetic() && !method.isBridge()) {
                     count++;
                  }

                  if (count > 1) {
                     break;
                  }
               }

               if (count == 1) {
                  this.functional = Boolean.TRUE;
               }
            }
         } catch (Throwable var6) {
            var6.printStackTrace();
         }
      }

      return this.functional;
   }
}
