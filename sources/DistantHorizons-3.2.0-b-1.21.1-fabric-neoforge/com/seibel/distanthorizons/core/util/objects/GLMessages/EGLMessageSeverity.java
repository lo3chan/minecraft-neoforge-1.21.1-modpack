package com.seibel.distanthorizons.core.util.objects.GLMessages;

import java.util.HashMap;

public enum EGLMessageSeverity {
   HIGH,
   MEDIUM,
   LOW,
   NOTIFICATION;

   public final String name = super.toString().toUpperCase();
   static final HashMap<String, EGLMessageSeverity> ENUM_BY_NAME = new HashMap<>();

   @Override
   public final String toString() {
      return this.name;
   }

   public static EGLMessageSeverity get(String name) {
      return ENUM_BY_NAME.get(name.toUpperCase());
   }

   static {
      for (EGLMessageSeverity severity : values()) {
         ENUM_BY_NAME.put(severity.name, severity);
      }
   }
}
