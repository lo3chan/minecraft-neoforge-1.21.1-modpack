package vazkii.psi.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;
import vazkii.psi.common.block.BlockConjured;
import vazkii.psi.common.block.base.ModBlocks;

public class PsiItemModelGenerator extends ItemModelProvider {
   public PsiItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "psi", existingFileHelper);
   }

   private void pointToBlock(Item item) {
      String name = BuiltInRegistries.ITEM.getKey(item).getPath();
      ((ItemModelBuilder)this.getBuilder(name)).parent(new UncheckedModelFile(Psi.location("block/" + name)));
   }

   protected void registerModels() {
      this.pointToBlock(((Block)ModBlocks.psidustBlock.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalBlock.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psigemBlock.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalPlateBlack.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalPlateWhite.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalPlateBlackLight.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalPlateWhiteLight.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalEbony.get()).asItem());
      this.pointToBlock(((Block)ModBlocks.psimetalIvory.get()).asItem());
      this.pointToBlock(((BlockConjured)ModBlocks.conjured.get()).asItem());
   }

   @NotNull
   public String getName() {
      return "Psi item models";
   }
}
