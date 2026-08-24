package io.github.razordevs.deep_aether.world.biomes;

import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.mojang.datafixers.util.Pair;
import io.github.razordevs.aeroblender.aether.AetherRegionType;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import terrablender.api.Region;

public class DARareRegion extends Region {
   public DARareRegion(ResourceLocation name, int weight) {
      super(name, AetherRegionType.THE_AETHER, weight);
   }

   public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
      Parameter fullRange = Parameter.span(-1.5F, 1.5F);
      Parameter tempMushroomCloud = Parameter.span(0.7F, 1.0F);
      Parameter tempSkyroot = Parameter.span(0.0F, 0.7F);
      Parameter tempCloud = Parameter.span(-0.8F, 0.0F);
      Parameter tempSkyroot2 = Parameter.span(-1.0F, -0.8F);
      this.addBiome(
         mapper, new ParameterPoint(tempMushroomCloud, fullRange, fullRange, fullRange, Parameter.span(0.0F, 1.5F), fullRange, 0L), DABiomes.LUMINESCENT_FOREST
      );
      this.addBiome(
         mapper,
         new ParameterPoint(tempMushroomCloud, Parameter.span(0.2F, 1.0F), fullRange, fullRange, Parameter.span(-1.5F, 0.0F), fullRange, 0L),
         DABiomes.CLOUD
      );
      this.addBiome(
         mapper,
         new ParameterPoint(tempMushroomCloud, Parameter.span(-1.0F, 0.2F), fullRange, fullRange, Parameter.span(-1.5F, 0.0F), fullRange, 0L),
         DABiomes.CLOUD
      );
      this.addBiome(
         mapper, new ParameterPoint(tempSkyroot, Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
      this.addBiome(
         mapper, new ParameterPoint(tempSkyroot, Parameter.span(0.0F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST
      );
      this.addBiome(
         mapper, new ParameterPoint(tempCloud, fullRange, fullRange, fullRange, Parameter.span(0.0F, 1.5F), fullRange, 0L), DABiomes.LUMINESCENT_FOREST
      );
      this.addBiome(mapper, new ParameterPoint(tempCloud, fullRange, fullRange, fullRange, Parameter.span(-1.5F, 0.0F), fullRange, 0L), DABiomes.CLOUD);
      this.addBiome(
         mapper, new ParameterPoint(tempSkyroot2, fullRange, fullRange, fullRange, Parameter.span(0.0F, 1.5F), fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
   }
}
