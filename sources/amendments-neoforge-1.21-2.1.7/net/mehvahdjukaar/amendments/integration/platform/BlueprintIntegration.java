package net.mehvahdjukaar.amendments.integration.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureModificationContext;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletterManager;
import com.teamabnormals.blueprint.common.world.modification.structure.StructureRepaletter.Replacer;
import net.mehvahdjukaar.amendments.Amendments;
import net.mehvahdjukaar.amendments.common.block.StructureCauldronHack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlueprintIntegration {
   public static void init() {
      StructureCauldronHack.register();
      StructureRepaletterManager.registerRepalleter(Amendments.res("blockstate_replace"), BlueprintIntegration.BlockStateRepaletter.CODEC);
   }

   public record BlockStateRepaletter(Block replacesBlock, BlockState replacesWith, float chance) implements StructureRepaletter, Replacer {
      public static final MapCodec<BlueprintIntegration.BlockStateRepaletter> CODEC = RecordCodecBuilder.mapCodec(
         i -> i.group(
               BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replaces_block").forGetter(BlueprintIntegration.BlockStateRepaletter::replacesBlock),
               BlockState.CODEC.fieldOf("replaces_with").forGetter(BlueprintIntegration.BlockStateRepaletter::replacesWith),
               Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(BlueprintIntegration.BlockStateRepaletter::chance)
            )
            .apply(i, BlueprintIntegration.BlockStateRepaletter::new)
      );

      @Nullable
      public BlockState getReplacement(ServerLevelAccessor serverLevelAccessor, BlockState state, RandomSource randomSource) {
         return state.is(this.replacesBlock) && randomSource.nextFloat() < this.chance ? this.replacesWith : null;
      }

      public Replacer createReplacer(StructureModificationContext context) {
         return this;
      }

      public MapCodec<? extends Replacer> savedTagCodec() {
         return CODEC;
      }

      public MapCodec<? extends StructureRepaletter> codec() {
         return CODEC;
      }
   }
}
