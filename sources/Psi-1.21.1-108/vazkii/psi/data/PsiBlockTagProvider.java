package vazkii.psi.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.block.base.ModBlocks;
import vazkii.psi.common.lib.ModTags;

public class PsiBlockTagProvider extends BlockTagsProvider {
   public PsiBlockTagProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
      super(output, lookupProvider, "psi", existingFileHelper);
   }

   @NotNull
   public String getName() {
      return "Psi block tags";
   }

   protected void addTags(Provider pProvider) {
      this.tag(ModTags.Blocks.BLOCK_PSIMETAL).add((Block)ModBlocks.psimetalBlock.get());
      this.tag(ModTags.Blocks.BLOCK_PSIGEM).add((Block)ModBlocks.psigemBlock.get());
      this.tag(ModTags.Blocks.BLOCK_EBONY_PSIMETAL).add((Block)ModBlocks.psimetalEbony.get());
      this.tag(ModTags.Blocks.BLOCK_IVORY_PSIMETAL).add((Block)ModBlocks.psimetalIvory.get());
      this.tag(Blocks.STORAGE_BLOCKS).add((Block)ModBlocks.psimetalBlock.get());
      this.tag(Blocks.STORAGE_BLOCKS).add((Block)ModBlocks.psigemBlock.get());
      this.tag(Blocks.STORAGE_BLOCKS).add((Block)ModBlocks.psimetalEbony.get());
      this.tag(Blocks.STORAGE_BLOCKS).add((Block)ModBlocks.psimetalIvory.get());
      this.tag(BlockTags.AIR).add((Block)ModBlocks.conjured.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.cadAssembler.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.programmer.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psidustBlock.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalBlock.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psigemBlock.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalPlateBlack.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalPlateBlackLight.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalPlateWhite.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalPlateWhiteLight.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalEbony.get());
      this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add((Block)ModBlocks.psimetalIvory.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalBlock.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psigemBlock.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalPlateBlack.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalPlateBlackLight.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalPlateWhite.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalPlateWhiteLight.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalEbony.get());
      this.tag(BlockTags.NEEDS_IRON_TOOL).add((Block)ModBlocks.psimetalIvory.get());
   }
}
