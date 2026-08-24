package com.github.alexthe666.alexsmobs.world;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;
import net.neoforged.neoforge.common.world.StructureModifier.Phase;

public class AMMobSpawnStructureModifier implements StructureModifier {
   public static Supplier<? extends MapCodec<? extends StructureModifier>> SERIALIZER;

   public void modify(Holder<Structure> structure, Phase phase, Builder builder) {
      if (phase == Phase.ADD) {
         AMWorldRegistry.modifyStructure(structure, builder);
      }
   }

   public MapCodec<? extends StructureModifier> codec() {
      return (MapCodec<? extends StructureModifier>)SERIALIZER.get();
   }

   public static MapCodec<AMMobSpawnStructureModifier> makeCodec() {
      return MapCodec.unit(AMMobSpawnStructureModifier::new);
   }
}
