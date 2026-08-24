package net.blay09.mods.balm.neoforge.world;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.balm.api.Balm;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public class BalmBiomeModifier implements BiomeModifier {
   public static final BalmBiomeModifier INSTANCE = new BalmBiomeModifier();

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      NeoForgeBalmWorldGen worldGen = (NeoForgeBalmWorldGen)Balm.getWorldGen();
      worldGen.modifyBiome(biome, phase, builder);
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return NeoForgeBalmWorldGen.BALM_BIOME_MODIFIER_CODEC;
   }
}
