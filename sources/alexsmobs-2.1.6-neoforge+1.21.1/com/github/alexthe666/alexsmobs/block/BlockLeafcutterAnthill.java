package com.github.alexthe666.alexsmobs.block;

import com.github.alexthe666.alexsmobs.entity.EntityLeafcutterAnt;
import com.github.alexthe666.alexsmobs.entity.EntityManedWolf;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMPlatform;
import com.github.alexthe666.alexsmobs.tileentity.AMTileEntityRegistry;
import com.github.alexthe666.alexsmobs.tileentity.TileEntityLeafcutterAnthill;
import com.mojang.serialization.MapCodec;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.BeeReleaseStatus;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

public class BlockLeafcutterAnthill extends BaseEntityBlock {
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return AMPlatform.unsupportedBlockCodec();
   }

   public BlockLeafcutterAnthill() {
      super(Properties.of().sound(SoundType.GRAVEL).strength(0.75F));
   }

   protected ItemInteractionResult useItemOn(
      ItemStack amStack, BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit
   ) {
      return AMCompat.itemResult(this.amUse(state, worldIn, pos, player, handIn, hit));
   }

   private InteractionResult amUse(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill) {
         TileEntityLeafcutterAnthill hill = (TileEntityLeafcutterAnthill)worldIn.getBlockEntity(pos);
         ItemStack heldItem = player.getItemInHand(handIn);
         if (heldItem.getItem() == AMItemRegistry.GONGYLIDIA.get() && hill.hasQueen()) {
            hill.releaseQueens();
            if (!player.isCreative()) {
               heldItem.shrink(1);
            }
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public RenderShape getRenderShape(BlockState p_149645_1_) {
      return RenderShape.MODEL;
   }

   public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
      this.amDropAnthill(worldIn, pos, player);
      return super.playerWillDestroy(worldIn, pos, state, player);
   }

   private void amDropAnthill(Level worldIn, BlockPos pos, Player player) {
      if (!worldIn.isClientSide()
         && player.isCreative()
         && AMCompat.gameRule(worldIn, AMCompat.Rule.BLOCK_DROPS)
         && worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill anthivetileentity) {
         ItemStack itemstack = new ItemStack(this);
         boolean flag = !anthivetileentity.hasNoAnts();
         if (!flag) {
            return;
         }

         if (flag) {
            CompoundTag compoundnbt = new CompoundTag();
            compoundnbt.put("Ants", anthivetileentity.getAnts());
            AMCompat.addTagElement(itemstack, "BlockEntityTag", compoundnbt);
         }

         CompoundTag compoundnbt1 = new CompoundTag();
         AMCompat.addTagElement(itemstack, "BlockStateTag", compoundnbt1);
         ItemEntity itementity = new ItemEntity(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemstack);
         itementity.setDefaultPickUpDelay();
         worldIn.addFreshEntity(itementity);
      }
   }

   public void fallOn(Level worldIn, BlockState state, BlockPos pos, Entity entityIn, float fallDistance) {
      this.amStompAnthill(worldIn, pos, entityIn);
      super.fallOn(worldIn, state, pos, entityIn, fallDistance);
   }

   private void amStompAnthill(Level worldIn, BlockPos pos, Entity entityIn) {
      if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityManedWolf)) {
         this.angerNearbyAnts(worldIn, (LivingEntity)entityIn, pos);
         if (!worldIn.isClientSide() && worldIn.getBlockEntity(pos) instanceof TileEntityLeafcutterAnthill) {
            TileEntityLeafcutterAnthill beehivetileentity = (TileEntityLeafcutterAnthill)worldIn.getBlockEntity(pos);
            beehivetileentity.angerAnts((LivingEntity)entityIn, worldIn.getBlockState(pos), BeeReleaseStatus.EMERGENCY);
            if (entityIn instanceof ServerPlayer) {
               AMAdvancementTriggerRegistry.STOMP_LEAFCUTTER_ANTHILL.trigger((ServerPlayer)entityIn);
            }
         }
      }
   }

   public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
      super.playerDestroy(worldIn, player, pos, state, te, stack);
      if (!worldIn.isClientSide()
         && te instanceof TileEntityLeafcutterAnthill beehivetileentity
         && AMCompat.enchantLevel(Enchantments.SILK_TOUCH, stack, worldIn) == 0) {
         beehivetileentity.angerAnts(player, state, BeeReleaseStatus.EMERGENCY);
         worldIn.updateNeighbourForOutputSignal(pos, this);
         this.angerNearbyAnts(worldIn, pos);
      }
   }

   private void angerNearbyAnts(Level world, BlockPos pos) {
      List<EntityLeafcutterAnt> list = world.getEntitiesOfClass(EntityLeafcutterAnt.class, new AABB(pos).inflate(20.0, 6.0, 20.0));
      if (!list.isEmpty()) {
         List<Player> list1 = world.getEntitiesOfClass(Player.class, new AABB(pos).inflate(20.0, 6.0, 20.0));
         if (list1.isEmpty()) {
            return;
         }

         int i = list1.size();

         for (EntityLeafcutterAnt beeentity : list) {
            if (beeentity.getTarget() == null) {
               beeentity.setTarget((LivingEntity)list1.get(world.getRandom().nextInt(i)));
            }
         }
      }
   }

   private void angerNearbyAnts(Level world, LivingEntity entity, BlockPos pos) {
      List<EntityLeafcutterAnt> list = world.getEntitiesOfClass(EntityLeafcutterAnt.class, new AABB(pos).inflate(20.0, 6.0, 20.0));
      if (!list.isEmpty()) {
         for (EntityLeafcutterAnt beeentity : list) {
            if (beeentity.getTarget() == null) {
               beeentity.setTarget(entity);
            }
         }
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TileEntityLeafcutterAnthill(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152180_, BlockState p_152181_, BlockEntityType<T> p_152182_) {
      return p_152180_.isClientSide()
         ? null
         : createTickerHelper(p_152182_, AMTileEntityRegistry.LEAFCUTTER_ANTHILL.get(), TileEntityLeafcutterAnthill::serverTick);
   }
}
