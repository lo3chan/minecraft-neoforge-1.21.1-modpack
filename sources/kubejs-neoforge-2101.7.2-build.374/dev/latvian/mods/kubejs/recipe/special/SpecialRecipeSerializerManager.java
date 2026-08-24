package dev.latvian.mods.kubejs.recipe.special;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;

public class SpecialRecipeSerializerManager implements KubeEvent {
   public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
   private final Map<ResourceLocation, Boolean> data = new HashMap<>();

   public void reset() {
      synchronized (this.data) {
         this.data.clear();
      }
   }

   @Override
   public void afterPosted(EventResult result) {
      NeoForge.EVENT_BUS.post(new SpecialRecipeSerializerManager.AfterPost());
   }

   public boolean isSpecial(Recipe<?> recipe) {
      return this.data.getOrDefault(BuiltInRegistries.RECIPE_SERIALIZER.getKey(recipe.getSerializer()), recipe.isSpecial());
   }

   public void ignoreSpecialFlag(ResourceLocation id) {
      synchronized (this.data) {
         this.data.put(id, false);
      }
   }

   public void addSpecialFlag(ResourceLocation id) {
      synchronized (this.data) {
         this.data.put(id, true);
      }
   }

   public void ignoreSpecialMod(String modid) {
      synchronized (this.data) {
         for (Entry<ResourceKey<RecipeSerializer<?>>, RecipeSerializer<?>> entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
            if (entry.getKey().location().getNamespace().equals(modid)) {
               this.data.put(entry.getKey().location(), false);
            }
         }
      }
   }

   public void addSpecialMod(String modid) {
      synchronized (this.data) {
         for (Entry<ResourceKey<RecipeSerializer<?>>, RecipeSerializer<?>> entry : BuiltInRegistries.RECIPE_SERIALIZER.entrySet()) {
            if (entry.getKey().location().getNamespace().equals(modid)) {
               this.data.put(entry.getKey().location(), true);
            }
         }
      }
   }

   public static final class AfterPost extends Event {
   }
}
