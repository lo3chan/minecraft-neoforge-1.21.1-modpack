package net.blay09.mods.balm.api.world;

import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.world.level.biome.BiomeModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public interface BalmWorldGen {
   @Deprecated
   <T extends Feature<?>> DeferredObject<T> registerFeature(ResourceLocation var1, Supplier<T> var2);

   @Deprecated
   <T extends PlacementModifierType<?>> DeferredObject<T> registerPlacementModifier(ResourceLocation var1, Supplier<T> var2);

   @Deprecated
   <T extends PoiType> DeferredObject<T> registerPoiType(ResourceLocation var1, Supplier<T> var2);

   void addFeatureToBiomes(BiomePredicate var1, Decoration var2, ResourceLocation var3);

   void modifyBiome(ResourceLocation var1, BiomePredicate var2, BiomeModifier var3);
}
