package com.finndog.moogs_structures.modinit;

import com.finndog.moogs_structures.modinit.registry.CustomRegistry;
import com.finndog.moogs_structures.modinit.registry.RegistryEntry;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class MoogsStructuresConditionsRegistry {
   public static final ResourceKey<Registry<Supplier<Boolean>>> MoogsStructures_JSON_CONDITIONS_KEY = ResourceKey.createRegistryKey(
      ResourceLocation.fromNamespaceAndPath("moogs_structures", "json_conditions")
   );
   public static final CustomRegistry<Supplier<Boolean>> MoogsStructures_JSON_CONDITIONS_REGISTRY = CustomRegistry.of(
      "moogs_structures", MoogsStructures_JSON_CONDITIONS_KEY, false, false, true
   );
   public static final RegistryEntry<Supplier<Boolean>> ALWAYS_TRUE = MoogsStructures_JSON_CONDITIONS_REGISTRY.register("always_true", () -> () -> true);
   public static final RegistryEntry<Supplier<Boolean>> ALWAYS_FALSE = MoogsStructures_JSON_CONDITIONS_REGISTRY.register("always_false", () -> () -> false);

   private MoogsStructuresConditionsRegistry() {
   }
}
