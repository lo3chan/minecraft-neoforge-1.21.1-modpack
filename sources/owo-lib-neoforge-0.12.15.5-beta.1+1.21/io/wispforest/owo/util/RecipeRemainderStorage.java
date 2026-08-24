package io.wispforest.owo.util;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class RecipeRemainderStorage {
   private static final Map<ResourceLocation, Map<Item, ItemStack>> REMAINDERS = new HashMap<>();

   private RecipeRemainderStorage() {
   }

   public static void store(ResourceLocation recipe, Map<Item, ItemStack> remainders) {
      REMAINDERS.put(recipe, remainders);
   }

   public static boolean has(ResourceLocation recipe) {
      return REMAINDERS.containsKey(recipe);
   }

   public static Map<Item, ItemStack> get(ResourceLocation recipe) {
      return REMAINDERS.get(recipe);
   }

   public static void onServerStart(AddReloadListenerEvent event) {
      event.addListener((ResourceManagerReloadListener)manager -> REMAINDERS.clear());
   }
}
