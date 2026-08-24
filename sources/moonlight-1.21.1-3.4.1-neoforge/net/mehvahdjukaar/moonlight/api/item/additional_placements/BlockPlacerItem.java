package net.mehvahdjukaar.moonlight.api.item.additional_placements;

import java.util.Map;
import net.mehvahdjukaar.moonlight.api.MoonlightRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class BlockPlacerItem extends BlockItem {
   private FoodProperties mimicFood;
   private Block mimicBlock;
   private SoundType overrideSound;

   public static BlockPlacerItem get() {
      return MoonlightRegistry.BLOCK_PLACER.get();
   }

   public BlockPlacerItem(Block pBlock, Properties pProperties) {
      super(pBlock, pProperties);
   }

   public void registerBlocks(Map<Block, Item> pBlockToItemMap, Item pItem) {
   }

   @Nullable
   public BlockState mimicGetPlacementState(BlockPlaceContext pContext, Block toPlace) {
      this.mimicBlock = toPlace;
      BlockState r = this.getPlacementState(pContext);
      this.mimicBlock = null;
      return r;
   }

   public InteractionResult mimicUseOn(UseOnContext pContext, Block toPlace, FoodProperties foodProperties) {
      this.mimicFood = foodProperties;
      this.mimicBlock = toPlace;
      InteractionResult r = super.useOn(pContext);
      this.mimicFood = null;
      this.mimicBlock = null;
      return r;
   }

   public InteractionResult mimicPlace(BlockPlaceContext pContext, Block toPlace, @Nullable SoundType overrideSound) {
      this.overrideSound = overrideSound;
      this.mimicBlock = toPlace;
      InteractionResult r = super.place(pContext);
      this.overrideSound = null;
      this.mimicBlock = null;
      return r;
   }

   public Block getBlock() {
      return this.mimicBlock != null ? this.mimicBlock : super.getBlock();
   }

   @Nullable
   public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
      return this.mimicFood;
   }

   protected SoundEvent getPlaceSound(BlockState pState) {
      return this.overrideSound != null ? this.overrideSound.getPlaceSound() : super.getPlaceSound(pState);
   }

   public boolean canPlace(BlockPlaceContext pContext, BlockState pState) {
      this.mimicBlock = pState.getBlock();
      boolean r = super.canPlace(pContext, pState);
      this.mimicBlock = null;
      return r;
   }

   public String getDescriptionId() {
      return "x";
   }
}
