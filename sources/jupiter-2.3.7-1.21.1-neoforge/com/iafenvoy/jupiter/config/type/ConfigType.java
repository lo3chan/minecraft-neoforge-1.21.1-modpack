package com.iafenvoy.jupiter.config.type;

public class ConfigType<T> {
   private final ConfigType.Type type;

   public ConfigType(ConfigType.Type type) {
      this.type = type;
   }

   public ConfigType.Type getType() {
      return this.type;
   }

   public static enum Type {
      SINGLE,
      LIST,
      MAP,
      DUMMY;
   }
}
