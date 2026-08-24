package com.iafenvoy.jupiter.config.type;

import java.util.Map;

public class MapConfigType<T> extends ConfigType<Map<String, T>> {
   private final ConfigType<T> singleType;

   public MapConfigType(ConfigType<T> singleType) {
      super(ConfigType.Type.MAP);
      this.singleType = singleType;
   }

   public ConfigType<T> getSingleType() {
      return this.singleType;
   }
}
