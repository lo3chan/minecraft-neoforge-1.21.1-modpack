package com.iafenvoy.jupiter.util;

import net.minecraft.resources.ResourceLocation;

public final class RLUtil {
   public static ResourceLocation id(String id) {
      return ResourceLocation.fromNamespaceAndPath("jupiter", id);
   }

   public static ResourceLocation id(String namespace, String id) {
      return ResourceLocation.fromNamespaceAndPath(namespace, id);
   }

   public static ResourceLocation tryParse(String id) {
      try {
         return ResourceLocation.parse(id);
      } catch (Exception var2) {
         return null;
      }
   }
}
