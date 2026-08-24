package net.astralya.hexalia.item.custom;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;

public abstract class AbstractConsumableItem extends Item {
   public AbstractConsumableItem(Properties properties) {
      super(properties);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      ItemStack original = stack.copy();
      ItemStack current = super.finishUsingItem(stack, level, user);
      if (user instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, original);
         serverPlayer.awardStat(Stats.ITEM_USED.get(this));
      }

      this.handleEffects(level, user, original);
      ItemStack container = this.getReturnContainer(original);
      if (!level.isClientSide && user instanceof Player player && !player.getAbilities().instabuild) {
         boolean isFood = original.get(DataComponents.FOOD) != null;
         if (!isFood) {
            current.shrink(1);
         }

         if (current.isEmpty()) {
            return container;
         }

         if (!container.isEmpty() && !player.getInventory().add(container)) {
            player.drop(container, false);
         }
      }

      return current;
   }

   protected abstract void handleEffects(Level var1, LivingEntity var2, ItemStack var3);

   protected abstract ItemStack getReturnContainer(ItemStack var1);

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      return ItemUtils.startUsingInstantly(level, player, hand);
   }

   public int getUseDuration(ItemStack stack, LivingEntity user) {
      return 32;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.DRINK;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      Component line = this.getTooltip(stack);
      if (line != null) {
         tooltip.add(line);
      }
   }

   protected Component getTooltip(ItemStack stack) {
      return null;
   }
}
