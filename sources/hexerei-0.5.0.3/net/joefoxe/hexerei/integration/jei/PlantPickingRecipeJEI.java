package net.joefoxe.hexerei.integration.jei;

import com.google.common.collect.Lists;
import java.util.List;
import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.PickableDoublePlant;
import net.joefoxe.hexerei.block.custom.PickablePlant;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PlantPickingRecipeJEI {
   private ItemStack INPUT;
   private final ItemStack OUTPUT_ITEM;
   private final ItemStack OUTPUT_ITEM2;

   public PlantPickingRecipeJEI(PickableDoublePlant plantBlock) {
      this.INPUT = plantBlock.asItem().getDefaultInstance();
      this.OUTPUT_ITEM = ((Item)Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.firstOutput).get())
         .getDefaultInstance();
      this.OUTPUT_ITEM2 = ((Item)Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.secondOutput).get())
         .getDefaultInstance();
   }

   public PlantPickingRecipeJEI(PickablePlant plantBlock) {
      this.INPUT = plantBlock.asItem().getDefaultInstance();
      this.OUTPUT_ITEM = ((Item)Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.firstOutput).get())
         .getDefaultInstance();
      this.OUTPUT_ITEM2 = ((Item)Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getOptional(plantBlock.secondOutput).get())
         .getDefaultInstance();
   }

   public ItemStack getInput() {
      return this.INPUT;
   }

   public ItemStack getOutputItem() {
      return this.OUTPUT_ITEM;
   }

   public ItemStack getOutputItem2() {
      return this.OUTPUT_ITEM2;
   }

   public static List<PlantPickingRecipeJEI> getRecipeList() {
      List<PlantPickingRecipeJEI> recipeList = Lists.newArrayList();
      recipeList.add(new PlantPickingRecipeJEI((PickablePlant)ModBlocks.BELLADONNA_PLANT.get()));
      recipeList.add(new PlantPickingRecipeJEI((PickablePlant)ModBlocks.MANDRAKE_PLANT.get()));
      recipeList.add(new PlantPickingRecipeJEI((PickableDoublePlant)ModBlocks.MUGWORT_BUSH.get()));
      recipeList.add(new PlantPickingRecipeJEI((PickableDoublePlant)ModBlocks.YELLOW_DOCK_BUSH.get()));
      return recipeList;
   }
}
