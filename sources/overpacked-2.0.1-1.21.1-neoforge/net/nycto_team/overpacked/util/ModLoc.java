package net.nycto_team.overpacked.util;

import net.minecraft.resources.ResourceLocation;

public class ModLoc {
   public static ResourceLocation get(String path) {
      return ResourceLocation.fromNamespaceAndPath("overpacked", path);
   }
}
