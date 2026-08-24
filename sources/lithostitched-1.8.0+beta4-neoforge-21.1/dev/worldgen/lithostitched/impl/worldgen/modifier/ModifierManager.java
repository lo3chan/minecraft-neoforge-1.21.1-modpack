package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.event.AddWorldgenModifiersEvent;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.LevelStem;

public class ModifierManager {
   public static void applyModifiers(RegistryAccess registries, Registry<LevelStem> dimensions) {
      boolean recompileSortedFeatures = false;
      Map<ResourceLocation, WorldgenModifier> modifiers = getAllModifiers(registries);

      for (Entry<ResourceLocation, WorldgenModifier> entry : sortByPriority(modifiers)) {
         Lithostitched.debug("Applying modifier with id: {}", entry.getKey());
         entry.getValue().apply(registries);
         if (entry.getValue().shouldRecompileSortedFeatures()) {
            recompileSortedFeatures = true;
         }
      }
   }

   static List<Entry<ResourceLocation, WorldgenModifier>> sortByPriority(Map<ResourceLocation, WorldgenModifier> modifiers) {
      return modifiers.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().priority())).toList();
   }

   public static Map<ResourceLocation, WorldgenModifier> getAllModifiers(RegistryAccess registries) {
      Map<ResourceLocation, WorldgenModifier> modifiers = new HashMap<>();
      registries.lookupOrThrow(LithostitchedRegistries.WORLDGEN_MODIFIER)
         .listElements()
         .forEach(holder -> modifiers.put(holder.key().location(), (WorldgenModifier)holder.value()));
      AddWorldgenModifiersEvent.EVENT.invoker().addModifiers(registries, (id, modifier) -> {
         if (!modifiers.containsKey(id)) {
            modifiers.put(id, modifier);
         }
      });
      return modifiers;
   }

   public static <T> List<Entry<ResourceLocation, T>> getModifiersOfType(RegistryAccess registries, MapCodec<T> codec) {
      Map<ResourceLocation, WorldgenModifier> modifiers = getAllModifiers(registries);
      return modifiers.entrySet()
         .stream()
         .filter(entry -> entry.getValue().codec().equals(codec))
         .map(entry -> Map.entry(entry.getKey(), (T)entry.getValue()))
         .toList();
   }
}
