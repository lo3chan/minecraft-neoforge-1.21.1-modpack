package com.aetherteam.aether.item.materials;

import com.aetherteam.aether.client.AetherSoundEvents;
import com.aetherteam.aether.item.materials.behavior.ItemUseConversion;
import com.aetherteam.aether.recipe.AetherRecipeTypes;
import com.aetherteam.aether.recipe.recipes.block.SwetBallRecipe;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;

public class SwetBallItem extends Item implements ItemUseConversion<SwetBallRecipe> {
   public SwetBallItem(Properties properties) {
      super(properties);
   }

   public InteractionResult useOn(UseOnContext context) {
      InteractionResult result = this.convertBlock((RecipeType)AetherRecipeTypes.SWET_BALL_CONVERSION.get(), context);
      if (context.getLevel().isClientSide() && result == InteractionResult.SUCCESS) {
         context.getLevel()
            .playSound(
               context.getPlayer(),
               context.getClickedPos(),
               (SoundEvent)AetherSoundEvents.ITEM_SWET_BALL_USE.get(),
               SoundSource.BLOCKS,
               0.8F,
               1.0F + (context.getLevel().getRandom().nextFloat() - context.getLevel().getRandom().nextFloat()) * 0.2F
            );
      }

      return result;
   }
}
