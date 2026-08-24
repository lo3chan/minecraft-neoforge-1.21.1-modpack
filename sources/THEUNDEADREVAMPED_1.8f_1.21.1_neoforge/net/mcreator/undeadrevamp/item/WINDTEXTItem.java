package net.mcreator.undeadrevamp.item;

import net.mcreator.undeadrevamp.entity.WINDTEXTProjectileEntity;
import net.mcreator.undeadrevamp.procedures.WINDTEXTItemInHandTickProcedure;
import net.mcreator.undeadrevamp.procedures.WINDTEXTLivingEntityIsHitWithItemProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WINDTEXTItem extends Item {
   public WINDTEXTItem() {
      super(new Properties().durability(100).rarity(Rarity.COMMON));
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 72000;
   }

   public float getDestroySpeed(ItemStack itemstack, BlockState state) {
      return 0.0F;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
      if (entity.getAbilities().instabuild || this.findAmmo(entity) != ItemStack.EMPTY) {
         ar = InteractionResultHolder.success(entity.getItemInHand(hand));
         entity.startUsingItem(hand);
      }

      return ar;
   }

   public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
      boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
      WINDTEXTLivingEntityIsHitWithItemProcedure.execute(entity);
      return retval;
   }

   public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
      super.inventoryTick(itemstack, world, entity, slot, selected);
      if (selected) {
         WINDTEXTItemInHandTickProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
      }
   }

   public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
      if (!world.isClientSide() && entity instanceof ServerPlayer player) {
         ItemStack stack = this.findAmmo(player);
         if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
            WINDTEXTProjectileEntity projectile = WINDTEXTProjectileEntity.shoot(world, entity, world.getRandom());
            itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
            if (player.getAbilities().instabuild) {
               projectile.pickup = Pickup.CREATIVE_ONLY;
            } else if (stack.isDamageableItem()) {
               if (world instanceof ServerLevel serverLevel) {
                  stack.hurtAndBreak(1, serverLevel, player, _stkprov -> {});
               }
            } else {
               stack.shrink(1);
            }
         }
      }
   }

   private ItemStack findAmmo(Player player) {
      ItemStack stack = ProjectileWeaponItem.getHeldProjectile(player, e -> e.getItem() == WINDTEXTProjectileEntity.PROJECTILE_ITEM.getItem());
      if (stack == ItemStack.EMPTY) {
         for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack teststack = (ItemStack)player.getInventory().items.get(i);
            if (teststack != null && teststack.getItem() == WINDTEXTProjectileEntity.PROJECTILE_ITEM.getItem()) {
               stack = teststack;
               break;
            }
         }
      }

      return stack;
   }
}
