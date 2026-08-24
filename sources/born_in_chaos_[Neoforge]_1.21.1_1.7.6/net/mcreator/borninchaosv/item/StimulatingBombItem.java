package net.mcreator.borninchaosv.item;

import net.mcreator.borninchaosv.entity.StimulatingBombprojectileEntity;
import net.mcreator.borninchaosv.procedures.StimulatingBombPoslieIspolzovaniiaSnariadaProcedure;
import net.mcreator.borninchaosv.procedures.StimulatingBombPriPoluchieniiPriedmietaPoRietsieptuProcedure;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public class StimulatingBombItem extends Item {
   public StimulatingBombItem() {
      super(new Properties().stacksTo(16).rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.SPEAR;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 72000;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
      if (entity.getAbilities().instabuild || this.findAmmo(entity) != ItemStack.EMPTY) {
         ar = InteractionResultHolder.success(entity.getItemInHand(hand));
         entity.startUsingItem(hand);
      }

      return ar;
   }

   public void onCraftedBy(ItemStack itemstack, Level world, Player entity) {
      super.onCraftedBy(itemstack, world, entity);
      StimulatingBombPriPoluchieniiPriedmietaPoRietsieptuProcedure.execute(entity);
   }

   public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
      if (!world.isClientSide() && entity instanceof ServerPlayer player) {
         ItemStack stack = this.findAmmo(player);
         if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
            StimulatingBombprojectileEntity projectile = StimulatingBombprojectileEntity.shoot(world, entity, world.getRandom());
            if (player.getAbilities().instabuild) {
               projectile.pickup = Pickup.CREATIVE_ONLY;
            } else if (stack.isDamageableItem()) {
               if (world instanceof ServerLevel serverLevel) {
                  stack.hurtAndBreak(1, serverLevel, player, _stkprov -> {});
               }
            } else {
               stack.shrink(1);
            }

            StimulatingBombPoslieIspolzovaniiaSnariadaProcedure.execute(entity);
         }
      }
   }

   private ItemStack findAmmo(Player player) {
      ItemStack stack = ProjectileWeaponItem.getHeldProjectile(player, e -> e.getItem() == StimulatingBombprojectileEntity.PROJECTILE_ITEM.getItem());
      if (stack == ItemStack.EMPTY) {
         for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack teststack = (ItemStack)player.getInventory().items.get(i);
            if (teststack != null && teststack.getItem() == StimulatingBombprojectileEntity.PROJECTILE_ITEM.getItem()) {
               stack = teststack;
               break;
            }
         }
      }

      return stack;
   }
}
