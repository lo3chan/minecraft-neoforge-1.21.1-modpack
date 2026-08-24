package io.github.razordevs.deep_aether.world.biomes;

import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.mojang.datafixers.util.Pair;
import io.github.razordevs.aeroblender.aether.AetherRegionType;
import io.github.razordevs.deep_aether.DeepAetherConfig;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import terrablender.api.Region;

public class DARegion extends Region {
   public DARegion(ResourceLocation name, int weight) {
      super(name, AetherRegionType.THE_AETHER, weight);
   }

   public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
      ResourceKey<Biome> YagrootSwamp = DeepAetherConfig.COMMON.disable_yagroot_swap_biomes.get() ? AetherBiomes.SKYROOT_WOODLAND : DABiomes.YAGROOT_SWAMP;
      ResourceKey<Biome> AerglowForest = DeepAetherConfig.COMMON.disable_roseroot_forest_biomes.get() ? AetherBiomes.SKYROOT_FOREST : DABiomes.AERGLOW_FOREST;
      ResourceKey<Biome> MysticAerglowForest = DeepAetherConfig.COMMON.disable_roseroot_forest_biomes.get()
         ? AetherBiomes.SKYROOT_FOREST
         : DABiomes.MYSTIC_AERGLOW_FOREST;
      ResourceKey<Biome> BlueAerglowForest = DeepAetherConfig.COMMON.disable_roseroot_forest_biomes.get()
         ? AetherBiomes.SKYROOT_FOREST
         : DABiomes.BLUE_AERGLOW_FOREST;
      ResourceKey<Biome> GoldenHeights = DeepAetherConfig.COMMON.disable_golden_heights_biomes.get() ? AetherBiomes.SKYROOT_GROVE : DABiomes.GOLDEN_HEIGHTS;
      ResourceKey<Biome> GoldenGrove = DeepAetherConfig.COMMON.disable_golden_heights_biomes.get() ? AetherBiomes.SKYROOT_GROVE : DABiomes.GOLDEN_GROVE;
      ResourceKey<Biome> AerlavenderFields = DeepAetherConfig.COMMON.disable_aerlavender_field_biomes.get()
         ? AetherBiomes.SKYROOT_MEADOW
         : DABiomes.AERLAVENDER_FIELDS;
      ResourceKey<Biome> SacredLands = DeepAetherConfig.COMMON.disable_sacred_lands_biomes.get()
         ? AetherBiomes.SKYROOT_WOODLAND
         : AetherBiomes.SKYROOT_WOODLAND;
      Parameter fullRange = Parameter.span(-1.5F, 1.5F);
      Parameter tempWoodland = Parameter.span(-1.5F, -0.8F);
      Parameter tempYagroot = Parameter.span(-0.8F, -0.4F);
      Parameter tempAerglow = Parameter.span(-0.4F, 0.0F);
      Parameter tempDefault3 = Parameter.span(0.0F, 0.4F);
      Parameter tempDefault4 = Parameter.span(0.4F, 0.8F);
      Parameter tempDefault5 = Parameter.span(0.8F, 1.5F);
      this.addBiome(mapper, new ParameterPoint(tempWoodland, fullRange, fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_WOODLAND);
      this.addBiome(mapper, new ParameterPoint(tempWoodland, tempDefault3, fullRange, fullRange, fullRange, fullRange, 0L), SacredLands);
      this.addBiome(
         mapper, new ParameterPoint(tempYagroot, Parameter.span(-1.5F, -0.2F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
      this.addBiome(mapper, new ParameterPoint(tempYagroot, Parameter.span(-0.2F, 1.5F), fullRange, fullRange, fullRange, fullRange, 0L), YagrootSwamp);
      this.addBiome(mapper, new ParameterPoint(tempAerglow, Parameter.span(-1.5F, -0.6F), fullRange, fullRange, fullRange, fullRange, 0L), BlueAerglowForest);
      this.addBiome(mapper, new ParameterPoint(tempAerglow, Parameter.span(-0.6F, 0.1F), fullRange, fullRange, fullRange, fullRange, 0L), AerglowForest);
      this.addBiome(mapper, new ParameterPoint(tempAerglow, Parameter.span(0.1F, 0.3F), fullRange, fullRange, fullRange, fullRange, 0L), MysticAerglowForest);
      this.addBiome(
         mapper, new ParameterPoint(tempAerglow, Parameter.span(0.3F, 1.5F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE
      );
      this.addBiome(mapper, new ParameterPoint(tempDefault3, Parameter.span(-0.33F, 0.33F), fullRange, fullRange, fullRange, fullRange, 0L), SacredLands);
      this.addBiome(
         mapper, new ParameterPoint(tempDefault3, Parameter.span(-1.5F, -0.4F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE
      );
      this.addBiome(
         mapper, new ParameterPoint(tempDefault3, Parameter.span(-0.4F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST
      );
      this.addBiome(mapper, new ParameterPoint(tempDefault3, Parameter.span(0.0F, 1.5F), fullRange, fullRange, fullRange, fullRange, 0L), AerlavenderFields);
      this.addBiome(mapper, new ParameterPoint(tempDefault4, Parameter.span(-1.5F, -0.5F), fullRange, fullRange, fullRange, fullRange, 0L), AerglowForest);
      this.addBiome(mapper, new ParameterPoint(tempDefault4, Parameter.span(-0.5F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0L), GoldenGrove);
      this.addBiome(mapper, new ParameterPoint(tempDefault4, Parameter.span(-0.1F, 1.5F), fullRange, fullRange, fullRange, fullRange, 0L), GoldenHeights);
      this.addBiome(mapper, new ParameterPoint(tempDefault5, Parameter.span(-1.5F, 0.7F), fullRange, fullRange, fullRange, fullRange, 0L), BlueAerglowForest);
      this.addBiome(
         mapper, new ParameterPoint(tempDefault5, Parameter.span(0.7F, 1.5F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST
      );
   }
}
