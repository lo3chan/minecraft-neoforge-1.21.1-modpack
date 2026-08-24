package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.client.particle.AMParticleRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityVoidPortal;
import com.github.alexthe666.alexsmobs.misc.AMCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ItemDimensionalCarver extends Item {
   public static final int MAX_TIME = 200;

   public ItemDimensionalCarver(Properties props) {
      super(props);
   }

   protected static BlockHitResult rayTracePortal(Level worldIn, Player player, Fluid fluidMode) {
      float f = player.getXRot();
      float f1 = player.getYRot();
      Vec3 vector3d = player.getEyePosition(1.0F);
      float f11 = -f1 * 0.017453292F - 3.1415927F;
      float f12 = -f * 0.017453292F;
      float f2 = Mth.cos(f11);
      float f3 = Mth.sin(f11);
      float f4 = -Mth.cos(f12);
      float f5 = Mth.sin(f12);
      float f6 = f3 * f4;
      float f7 = f2 * f4;
      double d0 = 1.5;
      Vec3 vector3d1 = vector3d.add(f6 * 1.5, f5 * 1.5, f7 * 1.5);
      return worldIn.clip(new ClipContext(vector3d, vector3d1, Block.OUTLINE, fluidMode, player));
   }

   public int getItemStackLimit(ItemStack stack) {
      return 1;
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      if (itemstack.getDamageValue() >= itemstack.getMaxDamage()) {
         return AMCompat.fail(itemstack);
      } else {
         playerIn.startUsingItem(handIn);
         HitResult raytraceresult = rayTracePortal(worldIn, playerIn, Fluid.ANY);
         Direction dir = Direction.orderedByNearest(playerIn)[0];
         double x = raytraceresult.getLocation().x - dir.getNormal().getX() * 0.1F;
         double y = raytraceresult.getLocation().y - dir.getNormal().getY() * 0.1F;
         double z = raytraceresult.getLocation().z - dir.getNormal().getZ() * 0.1F;
         CompoundTag carverTag = AMCompat.getOrCreateTag(itemstack);
         if (AMCompat.getBoolean(carverTag, "HASBLOCK")) {
            x = AMCompat.getDouble(carverTag, "BLOCKX");
            y = AMCompat.getDouble(carverTag, "BLOCKY");
            z = AMCompat.getDouble(carverTag, "BLOCKZ");
         } else {
            carverTag.putBoolean("HASBLOCK", true);
            carverTag.putDouble("BLOCKX", x);
            carverTag.putDouble("BLOCKY", y);
            carverTag.putDouble("BLOCKZ", z);
            AMCompat.setTag(itemstack, carverTag);
         }

         worldIn.addParticle((ParticleOptions)AMParticleRegistry.INVERT_DIG.get(), x, y, z, playerIn.getId(), 0.0, 0.0);
         return AMCompat.consume(itemstack);
      }
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return this.getUseDuration(stack);
   }

   public int getUseDuration(ItemStack stack) {
      return 200;
   }

   public float getXpRepairRatio(ItemStack stack) {
      return 100.0F;
   }

   public void onUseTick(Level level, LivingEntity player, ItemStack itemstack, int count) {
      player.swing(player.getUsedItemHand());
      RandomSource random = player.getRandom();
      if (count % 5 == 0) {
         player.gameEvent(GameEvent.ITEM_INTERACT_START);
         player.playSound(SoundEvents.NETHERITE_BLOCK_HIT, 1.0F, 0.5F + random.nextFloat());
      }

      boolean flag = false;
      CompoundTag carverTag = AMCompat.getOrCreateTag(itemstack);
      if (AMCompat.getBoolean(carverTag, "HASBLOCK")) {
         double x = AMCompat.getDouble(carverTag, "BLOCKX");
         double y = AMCompat.getDouble(carverTag, "BLOCKY");
         double z = AMCompat.getDouble(carverTag, "BLOCKZ");
         if (random.nextFloat() < 0.2) {
            player.level()
               .addParticle(
                  (ParticleOptions)AMParticleRegistry.WORM_PORTAL.get(),
                  x + random.nextGaussian() * 0.10000000149011612,
                  y + random.nextGaussian() * 0.10000000149011612,
                  z + random.nextGaussian() * 0.10000000149011612,
                  random.nextGaussian() * 0.10000000149011612,
                  -0.10000000149011612,
                  random.nextGaussian() * 0.10000000149011612
               );
         }

         if (player.distanceToSqr(x, y, z) > 9.0) {
            flag = true;
            if (player instanceof Player) {
               AMCompat.addCooldown(((Player)player).getCooldowns(), this, 40);
            }
         }

         if (count == 1 && !player.level().isClientSide()) {
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
            player.playSound(SoundEvents.GLASS_BREAK, 1.0F, 0.5F);
            EntityVoidPortal portal = new EntityVoidPortal(player.level(), this);
            portal.setPos(x, y, z);
            Direction dir = Direction.orderedByNearest(player)[0].getOpposite();
            if (dir == Direction.UP) {
               dir = Direction.DOWN;
            }

            portal.setAttachmentFacing(dir);
            player.level().addFreshEntity(portal);
            this.onPortalOpen(player.level(), player, portal, dir);
            AMCompat.hurtAndBreak(itemstack, 1, player, player.getUsedItemHand());
            flag = true;
            if (player instanceof Player) {
               AMCompat.addCooldown(((Player)player).getCooldowns(), this, 200);
            }
         }
      }

      if (flag) {
         player.stopUsingItem();
         carverTag.putBoolean("HASBLOCK", false);
         carverTag.putDouble("BLOCKX", 0.0);
         carverTag.putDouble("BLOCKY", 0.0);
         carverTag.putDouble("BLOCKZ", 0.0);
         AMCompat.setTag(itemstack, carverTag);
      }
   }

   public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      this.releaseUsingImpl(stack, worldIn, entityLiving, timeLeft);
   }

   private void releaseUsingImpl(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
      CompoundTag carverTag = AMCompat.getOrCreateTag(stack);
      carverTag.putBoolean("HASBLOCK", false);
      carverTag.putDouble("BLOCKX", 0.0);
      carverTag.putDouble("BLOCKY", 0.0);
      carverTag.putDouble("BLOCKZ", 0.0);
      AMCompat.setTag(stack, carverTag);
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      return !ItemStack.isSameItem(oldStack, newStack);
   }

   public void onPortalOpen(Level worldIn, LivingEntity player, EntityVoidPortal portal, Direction dir) {
      portal.setLifespan(1200);
      ResourceKey<Level> respawnDimension = Level.OVERWORLD;
      BlockPos respawnPosition = player.getSleepingPos().isPresent()
         ? (BlockPos)player.getSleepingPos().get()
         : player.level().getHeightmapPos(Types.MOTION_BLOCKING, BlockPos.ZERO);
      if (player instanceof ServerPlayer serverPlayer) {
         respawnDimension = serverPlayer.getRespawnDimension();
         if (serverPlayer.getRespawnPosition() != null) {
            respawnPosition = serverPlayer.getRespawnPosition();
         }
      }

      portal.exitDimension = respawnDimension;
      portal.setDestination(respawnPosition.above(2));
   }
}
