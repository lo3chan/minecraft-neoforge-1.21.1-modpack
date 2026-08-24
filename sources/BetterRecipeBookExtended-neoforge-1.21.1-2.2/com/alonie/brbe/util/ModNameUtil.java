package com.alonie.brbe.util;

import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class ModNameUtil {
   private ModNameUtil() {
   }

   public static Component getFormattedModName(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
         String modName = resolveModName(namespace);
         return Component.literal(modName).withStyle(new ChatFormatting[]{ChatFormatting.BLUE, ChatFormatting.ITALIC});
      } else {
         return Component.empty();
      }
   }

   public static String resolveModName(String namespace) {
      String jadeKey = "jade.modName." + namespace;
      if (I18n.exists(jadeKey)) {
         return I18n.get(jadeKey, new Object[0]);
      } else {
         String modName = resolveViaFabricLoader(namespace);
         return modName != null ? modName : namespace;
      }
   }

   private static String resolveViaFabricLoader(String namespace) {
      try {
         Class<?> loaderClass = Class.forName("net.fabricmc.loader.api.FabricLoader");
         Object loader = loaderClass.getMethod("getInstance").invoke(null);
         Object container = loaderClass.getMethod("getModContainer", String.class).invoke(loader, namespace);
         if (container != null) {
            Optional<?> opt = (Optional<?>)container;
            if (opt.isPresent()) {
               Object meta = opt.get().getClass().getMethod("getMetadata").invoke(opt.get());
               if (meta.getClass().getMethod("getName").invoke(meta) instanceof String s && !s.isEmpty()) {
                  return s;
               }
            }
         }
      } catch (Throwable var8) {
      }

      return null;
   }
}
