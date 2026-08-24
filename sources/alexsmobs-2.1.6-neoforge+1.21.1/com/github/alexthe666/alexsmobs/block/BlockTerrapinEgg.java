package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.AMEntityRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTerrapin;
import com.github.alexthe666.alexsmobs.entity.util.TerrapinTypes;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityTerrapinEgg;
import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockTerrapinEgg extends BaseEntityBlock {
   public static final IntegerProperty HATCH = BlockStateProperties.HATCH;
   public static final IntegerProperty EGGS = BlockStateProperties.EGGS;
   private static final VoxelShape ONE_EGG_SHAPE = Block.box(3.0, 0.0, 3.0, 12.0, 7.0, 12.0);
   private static final VoxelShape MULTI_EGG_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 7.0, 15.0);

   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockTerrapinEgg() {
      super(Properties.of().mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.METAL).randomTicks().noOcclusion());
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HATCH, 0)).setValue(EGGS, 1));
   }

   public static boolean hasProperHabitat(BlockGetter reader, BlockPos blockReader) {
      return isProperHabitat(reader, blockReader.below());
   }

   public RenderShape getRenderShape(BlockState p_149645_1_) {
      return RenderShape.MODEL;
   }

   public static boolean isProperHabitat(BlockGetter reader, BlockPos pos) {
      return reader.getBlockState(pos).is(BlockTags.SAND) || reader.getBlockState(pos).is(AMTagRegistry.CROCODILE_SPAWNS);
   }

   public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
      this.tryTrample(worldIn, pos, entityIn, 100);
      super.stepOn(worldIn, pos, state, entityIn);
   }

   public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!(entityIn instanceof Zombie)) {
         this.tryTrample(worldIn, pos, entityIn, 3);
      }

      super.fallOn(worldIn, state, pos, entityIn, fallDistance);
   }

   private void tryTrample(Level worldIn, BlockPos pos, Entity trampler, int chances) {
      if (this.canTrample(worldIn, trampler) && !worldIn.isClientSide() && worldIn.getRandom().nextInt(chances) == 0) {
         BlockState blockstate = worldIn.getBlockState(pos);
         this.removeOneEgg(worldIn, pos, blockstate);
      }
   }

   private void removeOneEgg(Level worldIn, BlockPos pos, BlockState state) {
      worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + worldIn.getRandom().nextFloat() * 0.2F);
      int i = (Integer)state.getValue(EGGS);
      if (i <= 1) {
         worldIn.destroyBlock(pos, false);
      } else {
         worldIn.setBlock(pos, (BlockState)state.setValue(EGGS, i - 1), 2);
         worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
         worldIn.levelEvent(2001, pos, Block.getId(state));
      }
   }

   public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
      if (this.canGrow(worldIn, pos) && hasProperHabitat(worldIn, pos)) {
         int i = (Integer)state.getValue(HATCH);
         if (i < 2) {
            worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_CRACK, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
            worldIn.setBlock(pos, (BlockState)state.setValue(HATCH, i + 1), 2);
         } else {
            worldIn.playSound(null, pos, SoundEvents.TURTLE_EGG_HATCH, SoundSource.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
            worldIn.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(state));
            worldIn.removeBlock(pos, false);

            for (int j = 0; j < state.getValue(EGGS); j++) {
               worldIn.levelEvent(2001, pos, Block.getId(state));
               EntityTerrapin turtleentity = AMCompat.create(AMEntityRegistry.TERRAPIN.get(), worldIn);
               turtleentity.setAge(-24000);
               if (worldIn.getBlockEntity(pos) instanceof TileEntityTerrapinEgg eggTE) {
                  eggTE.addAttributesToOffspring(turtleentity, random);
               }

               turtleentity.setFromBucket(true);
               turtleentity.moveTo(pos.getX() + 0.3 + j * 0.2, pos.getY(), pos.getZ() + 0.3, 0.0F, 0.0F);
               worldIn.addFreshEntity(turtleentity);
            }
         }
      }
   }

   public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (hasProperHabitat(worldIn, pos) && !worldIn.isClientSide()) {
         worldIn.levelEvent(2005, pos, 0);
      }
   }

   private boolean canGrow(Level worldIn, BlockPos pos) {
      float f = AMCompat.timeOfDay(worldIn, pos);
      return f < 0.69 && f > 0.65 ? true : worldIn.getRandom().nextInt(15) == 0;
   }

   public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
      super.playerDestroy(worldIn, player, pos, state, te, stack);
      this.removeOneEgg(worldIn, pos, state);
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
      return useContext.getItemInHand().getItem() == this.asItem() && (Integer)state.getValue(EGGS) < 4 || super.canBeReplaced(state, useContext);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
      return blockstate.getBlock() == this
         ? (BlockState)blockstate.setValue(EGGS, Math.min(4, (Integer)blockstate.getValue(EGGS) + 1))
         : super.getStateForPlacement(context);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(EGGS) > 1 ? MULTI_EGG_SHAPE : ONE_EGG_SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HATCH, EGGS});
   }

   private boolean canTrample(Level worldIn, Entity trampler) {
      if (trampler instanceof EntityTerrapin || trampler instanceof Bat) {
         return false;
      } else {
         return !(trampler instanceof LivingEntity) ? false : trampler instanceof Player || AMPlatform.mobGriefing(worldIn, trampler);
      }
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      ItemStack pickaxe = AMCompat.asItemStack((ItemStack)builder.getOptionalParameter(LootContextParams.TOOL));
      BlockEntity blockentity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      boolean silkTouch = false;
      if (pickaxe != null) {
         silkTouch = AMCompat.enchantLevel(Enchantments.SILK_TOUCH, pickaxe, builder.getLevel()) > 0;
      }

      if (silkTouch && blockentity instanceof TileEntityTerrapinEgg) {
         ItemStack stack = new ItemStack((ItemLike)AMBlockRegistry.TERRAPIN_EGG.get());
         TileEntityTerrapinEgg egg = (TileEntityTerrapinEgg)blockentity;
         CompoundTag tag = new CompoundTag();
         CompoundTag parent1 = new CompoundTag();
         CompoundTag parent2 = new CompoundTag();
         boolean flag = false;
         if (egg.parent1 != null) {
            flag = true;
            egg.parent1.writeToNBT(parent1);
         }

         if (egg.parent2 != null) {
            flag = true;
            egg.parent2.writeToNBT(parent2);
         }

         if (flag) {
            AMCompat.put(tag, "Parent1Data", parent1);
            AMCompat.put(tag, "Parent2Data", parent2);
            AMCompat.addTagElement(stack, "BlockEntityTag", tag);
         }

         return List.of(stack);
      } else {
         return List.of();
      }
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flags) {
      super.appendHoverText(stack, context, list, flags);
      this.appendParentTooltip(stack, list);
   }

   private void appendParentTooltip(ItemStack stack, List<Component> list) {
      CompoundTag compoundtag = AMCompat.getBlockEntityData(stack);
      if (compoundtag != null && compoundtag.contains("Parent1Data") && compoundtag.contains("Parent2Data")) {
         TerrapinTypes parent1Type = TerrapinTypes.values()[Mth.clamp(
            AMCompat.getInt(AMCompat.getCompound(compoundtag, "Parent1Data"), "TerrapinType"), 0, TerrapinTypes.values().length - 1
         )];
         TerrapinTypes parent2Type = TerrapinTypes.values()[Mth.clamp(
            AMCompat.getInt(AMCompat.getCompound(compoundtag, "Parent2Data"), "TerrapinType"), 0, TerrapinTypes.values().length - 1
         )];
         String s1 = Component.translatable(parent1Type.getTranslationName()).getString();
         String s2 = Component.translatable(parent2Type.getTranslationName()).getString();
         list.add(Component.translatable("block.alexsmobs.terrapin_egg.desc", new Object[]{s1, s2}).withStyle(ChatFormatting.GRAY));
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean b) {
      if (state.is(AMBlockRegistry.TERRAPIN_EGG.get()) && (Integer)state.getValue(EGGS) <= 1) {
         super.onRemove(state, level, pos, state2, b);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TileEntityTerrapinEgg(pos, state);
   }
}
