package com.aetherteam.aether.item.materials;

import com.aetherteam.aether.AetherConfig;
import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.item.materials.behavior.ItemUseConversion;
import com.aetherteam.aether.item.miscellaneous.ConsumableItem;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.block.AmbrosiumRecipe;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class AmbrosiumShardItem extends Item implements ItemUseConversion<AmbrosiumRecipe>, ConsumableItem {
   public AmbrosiumShardItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      InteractionResult result = this.convertBlock((RecipeType)AetherRecipeTypes.AMBROSIUM_ENCHANTING.get(), context);
      if (context.getLevel().isClientSide() && result == InteractionResult.SUCCESS) {
         context.getLevel()
            .playSound(
               context.getPlayer(),
               context.getClickedPos(),
               (SoundEvent)AetherSoundEvents.ITEM_AMBROSIUM_SHARD.get(),
               SoundSource.BLOCKS,
               1.0F,
               3.0F + (context.getLevel().getRandom().nextFloat() - context.getLevel().getRandom().nextFloat()) * 0.8F
            );
      }

      return result;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      if ((Boolean)AetherConfig.SERVER.edible_ambrosium.get()) {
         ItemStack itemStack = player.getItemInHand(hand);
         if (!(player.getHealth() < player.getMaxHealth()) && !player.isCreative()) {
            return InteractionResultHolder.fail(itemStack);
         } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemStack);
         }
      } else {
         return InteractionResultHolder.pass(player.getItemInHand(hand));
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
      if ((Boolean)AetherConfig.SERVER.edible_ambrosium.get()) {
         user.heal(1.0F);
         this.consume(this, stack, user);
      }

      return stack;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return AetherConfig.SERVER.edible_ambrosium.get() ? UseAnim.EAT : UseAnim.NONE;
   }

   public int getUseDuration(ItemStack stack, LivingEntity entity) {
      return AetherConfig.SERVER.edible_ambrosium.get() ? 16 : 0;
   }
}
