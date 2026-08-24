package net.joefoxe.hexerei.world.terrablender;

import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.joefoxe.hexerei.config.HexConfig;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.world.biome.ModBiomes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.Regions;

public class ModRegion extends Region {
   public ModRegion(ResourceLocation name, RegionType type, int weight) {
      super(name, type, weight);
   }

   public static void init() {
      Regions.register(new ModRegion.SwampRegion(HexereiUtil.getResource("overworld"), (Integer)HexConfig.WILLOW_SWAMP_RARITY.get()));
   }

   private static class SwampRegion extends Region {
      public SwampRegion(ResourceLocation resourceLocation, int weight) {
         super(resourceLocation, RegionType.OVERWORLD, weight);
      }

      public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
         this.addModifiedVanillaOverworldBiomes(mapper, builder -> builder.replaceBiome(Biomes.SWAMP, ModBiomes.WILLOW_SWAMP));
      }
   }
}
