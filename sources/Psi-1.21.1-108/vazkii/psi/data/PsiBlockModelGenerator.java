package vazkii.psi.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.base.ModBlocks;

public class PsiBlockModelGenerator extends BlockStateProvider {
   public PsiBlockModelGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
      super(output, "psi", exFileHelper);
   }

   protected void registerStatesAndModels() {
      this.simpleBlock(
         (Block)ModBlocks.psidustBlock.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psidustBlock.get()).getPath(), Psi.location("block/psidust_block"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalBlock.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalBlock.get()).getPath(), Psi.location("block/psimetal_block"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psigemBlock.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psigemBlock.get()).getPath(), Psi.location("block/psigem_block"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalPlateBlack.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalPlateBlack.get()).getPath(), Psi.location("block/psimetal_plate_black"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalPlateWhite.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalPlateWhite.get()).getPath(), Psi.location("block/psimetal_plate_white"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalPlateBlackLight.get(),
         this.models()
            .cubeBottomTop(
               BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalPlateBlackLight.get()).getPath(),
               Psi.location("block/psimetal_plate_black_light"),
               Psi.location("block/psimetal_plate_black"),
               Psi.location("block/psimetal_plate_black")
            )
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalPlateWhiteLight.get(),
         this.models()
            .cubeBottomTop(
               BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalPlateWhiteLight.get()).getPath(),
               Psi.location("block/psimetal_plate_white_light"),
               Psi.location("block/psimetal_plate_white"),
               Psi.location("block/psimetal_plate_white")
            )
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalEbony.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalEbony.get()).getPath(), Psi.location("block/ebony_psimetal_block"))
      );
      this.simpleBlock(
         (Block)ModBlocks.psimetalIvory.get(),
         this.models().cubeAll(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.psimetalIvory.get()).getPath(), Psi.location("block/ivory_psimetal_block"))
      );
      this.simpleBlock(
         (Block)ModBlocks.conjured.get(),
         ((BlockModelBuilder)this.models().withExistingParent(BuiltInRegistries.BLOCK.getKey((Block)ModBlocks.conjured.get()).getPath(), "block/block"))
            .texture("particle", Psi.location("block/empty"))
      );
   }

   @NotNull
   public String getName() {
      return "Psi blockstates and block models";
   }
}
