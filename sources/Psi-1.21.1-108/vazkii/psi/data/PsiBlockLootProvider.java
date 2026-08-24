package vazkii.psi.data;

import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.block.base.ModBlocks;

public class PsiBlockLootProvider extends BlockLootSubProvider {
   protected PsiBlockLootProvider(Provider registries) {
      super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
   }

   protected void generate() {
      this.dropSelf((Block)ModBlocks.cadAssembler.get());
      this.dropSelf((Block)ModBlocks.programmer.get());
      this.dropSelf((Block)ModBlocks.psidustBlock.get());
      this.dropSelf((Block)ModBlocks.psimetalBlock.get());
      this.dropSelf((Block)ModBlocks.psigemBlock.get());
      this.dropSelf((Block)ModBlocks.psimetalPlateBlack.get());
      this.dropSelf((Block)ModBlocks.psimetalPlateBlackLight.get());
      this.dropSelf((Block)ModBlocks.psimetalPlateWhite.get());
      this.dropSelf((Block)ModBlocks.psimetalPlateWhiteLight.get());
      this.dropSelf((Block)ModBlocks.psimetalEbony.get());
      this.dropSelf((Block)ModBlocks.psimetalIvory.get());
   }

   @NotNull
   protected Iterable<Block> getKnownBlocks() {
      return ModBlocks.BLOCKS.getEntries().stream().<Block>map(DeferredHolder::get).collect(Collectors.toList());
   }
}
