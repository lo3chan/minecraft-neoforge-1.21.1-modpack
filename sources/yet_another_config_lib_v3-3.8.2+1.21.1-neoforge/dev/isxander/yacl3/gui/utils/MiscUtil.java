package dev.isxander.yacl3.gui.utils;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class MiscUtil {
   public static <T> T getFromRegistry(Registry<T> registry, ResourceLocation identifier) {
      return (T)registry.get(identifier);
   }
}
