package net.mcreator.undeadrevamp.item;

import java.util.List;
import net.mcreator.undeadrevamp.entity.ArapoholiasprayProjectileEntity;
import net.mcreator.undeadrevamp.procedures.ArapoholiasprayRangedItemUsedProcedure;
import net.mcreator.undeadrevamp.procedures.MinosperfumerightclickedProcedure;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class ArapoholiasprayItem extends Item {
   public ArapoholiasprayItem() {
      super(new Properties().durability(64).rarity(Rarity.COMMON));
   }

   public UseAnim getUseAnimation(ItemStack itemstack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
      return 72000;
   }

   public float getDestroySpeed(ItemStack itemstack, BlockState state) {
      return 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemstack, TooltipContext context, List<Component> list, TooltipFlag flag) {
      super.appendHoverText(itemstack, context, list, flag);
      list.add(Component.translatable("item.undead_revamp2.arapoholiaspray.description_0"));
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
      InteractionResultHolder<ItemStack> ar = InteractionResultHolder.fail(entity.getItemInHand(hand));
      if (entity.getAbilities().instabuild || this.findAmmo(entity) != ItemStack.EMPTY) {
         ar = InteractionResultHolder.success(entity.getItemInHand(hand));
         entity.startUsingItem(hand);
      }

      return ar;
   }

   public void releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
      MinosperfumerightclickedProcedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
      if (!world.isClientSide() && entity instanceof ServerPlayer player) {
         ItemStack stack = this.findAmmo(player);
         if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
            ArapoholiasprayProjectileEntity projectile = ArapoholiasprayProjectileEntity.shoot(world, entity, world.getRandom());
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

            ArapoholiasprayRangedItemUsedProcedure.execute(entity, itemstack);
         }
      }
   }

   private ItemStack findAmmo(Player player) {
      return new ItemStack(ArapoholiasprayProjectileEntity.PROJECTILE_ITEM.getItem());
   }
}
