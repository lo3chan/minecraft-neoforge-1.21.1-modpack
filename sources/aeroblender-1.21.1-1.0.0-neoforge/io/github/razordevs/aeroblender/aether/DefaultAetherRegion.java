package io.github.razordevs.aeroblender.aether;

import com.aetherteam.aether.data.resources.registries.AetherBiomes;
import com.mojang.datafixers.util.Pair;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import terrablender.api.Region;

public class DefaultAetherRegion extends Region {
   public static final ResourceLocation LOCATION = ResourceLocation.fromNamespaceAndPath("aether", "the_aether");

   public DefaultAetherRegion(int weight) {
      super(LOCATION, AetherRegionType.THE_AETHER, weight);
   }

   public void addBiomes(Registry<Biome> registry, Consumer<Pair<ParameterPoint, ResourceKey<Biome>>> mapper) {
      Parameter fullRange = Parameter.span(-1.0F, 1.0F);
      Parameter temps1 = Parameter.span(-1.0F, -0.8F);
      Parameter temps2 = Parameter.span(-0.8F, 0.0F);
      Parameter temps3 = Parameter.span(0.0F, 0.4F);
      Parameter temps4 = Parameter.span(0.4F, 0.93F);
      Parameter temps5 = Parameter.span(0.93F, 0.94F);
      Parameter temps6 = Parameter.span(0.94F, 1.0F);
      this.addBiome(mapper, new ParameterPoint(temps2, Parameter.span(0.0F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST);
      this.addBiome(mapper, new ParameterPoint(temps3, Parameter.span(0.0F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST);
      this.addBiome(
         mapper, new ParameterPoint(temps4, Parameter.span(-0.1F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST
      );
      this.addBiome(
         mapper, new ParameterPoint(temps5, Parameter.span(-0.3F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST
      );
      this.addBiome(mapper, new ParameterPoint(temps5, Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_FOREST);
      this.addBiome(mapper, new ParameterPoint(temps1, fullRange, fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW);
      this.addBiome(
         mapper, new ParameterPoint(temps2, Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
      this.addBiome(
         mapper, new ParameterPoint(temps5, Parameter.span(-1.0F, -0.6F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
      this.addBiome(
         mapper, new ParameterPoint(temps6, Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_MEADOW
      );
      this.addBiome(mapper, new ParameterPoint(temps3, Parameter.span(-1.0F, 0.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE);
      this.addBiome(mapper, new ParameterPoint(temps3, Parameter.span(0.8F, 1.0F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE);
      this.addBiome(
         mapper, new ParameterPoint(temps4, Parameter.span(-1.0F, -0.1F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE
      );
      this.addBiome(
         mapper, new ParameterPoint(temps5, Parameter.span(-0.6F, -0.3F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_GROVE
      );
      this.addBiome(
         mapper, new ParameterPoint(temps6, Parameter.span(-0.1F, 0.8F), fullRange, fullRange, fullRange, fullRange, 0L), AetherBiomes.SKYROOT_WOODLAND
      );
   }
}
