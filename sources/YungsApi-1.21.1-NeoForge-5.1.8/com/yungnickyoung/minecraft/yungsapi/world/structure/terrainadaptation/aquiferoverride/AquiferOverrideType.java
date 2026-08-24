package com.yungnickyoung.minecraft.yungsapi.world.structure.terrainadaptation.aquiferoverride;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public interface AquiferOverrideType<C extends AquiferOverride> {
   Map<ResourceLocation, AquiferOverrideType<?>> AQUIFER_OVERRIDE_TYPE_BY_NAME = new HashMap<>();
   Map<AquiferOverrideType<?>, ResourceLocation> AQUIFER_OVERRIDE_NAME_BY_TYPE = new HashMap<>();
   Codec<AquiferOverrideType<?>> AQUIFER_OVERRIDE_TYPE_CODEC = ResourceLocation.CODEC
      .flatXmap(
         resourceLocation -> Optional.ofNullable(AQUIFER_OVERRIDE_TYPE_BY_NAME.get(resourceLocation))
            .<DataResult>map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "Unknown Aquifer Override type: " + resourceLocation)),
         type -> Optional.of(AQUIFER_OVERRIDE_NAME_BY_TYPE.get(type))
            .<DataResult>map(DataResult::success)
            .orElseGet(() -> DataResult.error(() -> "No ID found for Aquifer Override type " + type + ". Is it registered?"))
      );
   Codec<AquiferOverride> AQUIFER_OVERRIDE_CODEC = AQUIFER_OVERRIDE_TYPE_CODEC.dispatch("type", AquiferOverride::type, AquiferOverrideType::codec);
   AquiferOverrideType<NoneAquiferOverride> NONE = register("none", NoneAquiferOverride.CODEC);
   AquiferOverrideType<ReplaceAquiferOverride> REPLACE = register("replace", ReplaceAquiferOverride.CODEC);
   AquiferOverrideType<SolidifyAquiferOverride> SOLIDIFY = register("solidify", SolidifyAquiferOverride.CODEC);

   static <C extends AquiferOverride> AquiferOverrideType<C> register(ResourceLocation resourceLocation, MapCodec<C> codec) {
      AquiferOverrideType<C> type = () -> codec;
      AQUIFER_OVERRIDE_TYPE_BY_NAME.put(resourceLocation, type);
      AQUIFER_OVERRIDE_NAME_BY_TYPE.put(type, resourceLocation);
      return type;
   }

   private static <C extends AquiferOverride> AquiferOverrideType<C> register(String id, MapCodec<C> codec) {
      return register(ResourceLocation.fromNamespaceAndPath("yungsapi", id), codec);
   }

   MapCodec<C> codec();
}
