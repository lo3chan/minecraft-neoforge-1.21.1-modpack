package com.iafenvoy.origins.util;

import com.iafenvoy.origins.Origins;
import com.mojang.brigadier.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;

public final class EntitySelectorCompiler {
   private static final EntitySelector DEFAULT;
   private static final Map<String, EntitySelector> CACHE = new LinkedHashMap<>();

   public static EntitySelector compile(String selector) {
      return CACHE.computeIfAbsent(selector, s -> {
         try {
            return new EntitySelectorParser(new StringReader(s), true).parse();
         } catch (Exception var3) {
            Origins.LOGGER.error("Failed to compile EntitySelector {}", selector, var3);
            return DEFAULT;
         }
      });
   }

   static {
      EntitySelector selector = null;

      try {
         selector = new EntitySelectorParser(new StringReader("@a[distance=...0]"), true).parse();
      } catch (Exception var2) {
         Origins.LOGGER.error("Failed to compile default EntitySelector", var2);
      }

      DEFAULT = selector;
   }
}
