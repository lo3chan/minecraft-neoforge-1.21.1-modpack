package net.joefoxe.hexerei.item.custom;

import net.joefoxe.hexerei.block.ModBlocks;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetSlab;
import net.joefoxe.hexerei.block.custom.ConnectingCarpetStairs;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.jetbrains.annotations.Nullable;

public class DyeableCarpetItem extends BlockItem {
   public DyeableCarpetItem(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext pContext) {
      BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
      Block block = blockState.getBlock();
      if (block instanceof SlabBlock
         && blockState.hasProperty(BlockStateProperties.SLAB_TYPE)
         && blockState.getValue(BlockStateProperties.SLAB_TYPE) == SlabType.BOTTOM) {
         if (((ConnectingCarpetSlab)ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetSlab)ModBlocks.INFUSED_FABRIC_CARPET_SLAB.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetSlab)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetSlab)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_SLAB.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetSlab)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetSlab)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_SLAB.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetSlab)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetSlab)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_SLAB.get()).getStateForPlacement(pContext);
         }
      }

      if (block instanceof StairBlock && blockState.hasProperty(BlockStateProperties.HALF) && blockState.getValue(BlockStateProperties.HALF) == Half.BOTTOM) {
         if (((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_STAIRS.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetStairs)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetStairs)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_STAIRS.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetStairs)ModBlocks.INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()).getStateForPlacement(pContext);
         }

         if (((ConnectingCarpetStairs)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()).parentBlock == this.getBlock()) {
            return ((ConnectingCarpetStairs)ModBlocks.WAXED_INFUSED_FABRIC_CARPET_ORNATE_STAIRS.get()).getStateForPlacement(pContext);
         }
      }

      return super.getPlacementState(pContext);
   }

   public Component getName(ItemStack pStack) {
      DyeColor color = DyeColor.WHITE;
      CustomData data = (CustomData)pStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      if (!data.isEmpty() && data.contains("color")) {
         color = DyeColor.byName(data.copyTag().getString("color"), DyeColor.WHITE);
      }

      return (Component)(color == DyeColor.WHITE
         ? super.getName(pStack)
         : Component.translatable("color.minecraft." + color.getName()).append(" ").append(super.getName(pStack)));
   }

   public interface ItemHandlerConsumer {
      void register(ItemColor var1, ItemLike... var2);
   }
}
