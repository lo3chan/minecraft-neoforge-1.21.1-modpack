package com.aetherteam.aether.item.food;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.miscellaneous.ConsumableItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class GummySwetItem extends Item implements ConsumableItem {
   public GummySwetItem() {
      super(new Properties().rarity(AetherItems.AETHER_LOOT).food(AetherFoods.GUMMY_SWET));
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack heldStack = player.getItemInHand(hand);
      if (this.getFoodProperties(heldStack, player) != null) {
         FoodProperties foodProperties = this.getFoodProperties(heldStack, player);
         if (foodProperties != null && player.canEat(foodProperties.canAlwaysEat())) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(heldStack);
         } else {
            return InteractionResultHolder.fail(heldStack);
         }
      } else if (player.getHealth() < player.getMaxHealth() && !player.isCreative()) {
         player.startUsingItem(hand);
         return InteractionResultHolder.consume(heldStack);
      } else {
         return InteractionResultHolder.fail(heldStack);
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if (this.getFoodProperties(stack, user) != null) {
         return user.eat(level, stack);
      } else {
         user.heal(user.getMaxHealth());
         this.consume(this, stack, user);
         return stack;
      }
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.EAT;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return 16;
   }

   @Nullable
   public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
      return AetherConfig.SERVER_SPEC.isLoaded() && AetherConfig.SERVER.healing_gummy_swets.get() ? null : super.getFoodProperties(stack, entity);
   }
}
