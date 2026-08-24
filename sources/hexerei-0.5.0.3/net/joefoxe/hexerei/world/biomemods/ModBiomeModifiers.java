package net.joefoxe.hexerei.world.biomemods;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries.Keys;

public class ModBiomeModifiers {
   public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIERS = DeferredRegister.create(Keys.BIOME_MODIFIER_SERIALIZERS, "hexerei");
   public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<ModVegetalBiomeModifiers>> VEGETAL_MODIFIER = BIOME_MODIFIERS.register(
      "vegetal",
      () -> RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModVegetalBiomeModifiers::biomes),
               PlacedFeature.CODEC.fieldOf("feature").forGetter(ModVegetalBiomeModifiers::feature)
            )
            .apply(builder, ModVegetalBiomeModifiers::new)
      )
   );
   public static DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<ModEntityBiomeModifier>> ENTITY_MODIFIER = BIOME_MODIFIERS.register(
      "entities",
      () -> RecordCodecBuilder.mapCodec(
         builder -> builder.group(
               Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModEntityBiomeModifier::biomes),
               SpawnerData.CODEC.fieldOf("entity").forGetter(ModEntityBiomeModifier::spawnerData)
            )
            .apply(builder, ModEntityBiomeModifier::new)
      )
   );

   public static void register(IEventBus eventBus) {
      BIOME_MODIFIERS.register(eventBus);
   }
}
