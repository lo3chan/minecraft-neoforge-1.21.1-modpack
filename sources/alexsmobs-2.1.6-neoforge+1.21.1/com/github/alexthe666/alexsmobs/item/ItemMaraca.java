package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import java.util.Random;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemMaraca extends Item {
   private final Random random = new Random();

   public ItemMaraca(Properties property) {
      super(property);
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      playerIn.gameEvent(GameEvent.ITEM_INTERACT_START);
      worldIn.playSound(
         null,
         playerIn.getX(),
         playerIn.getY(),
         playerIn.getZ(),
         AMSoundRegistry.MARACA.get(),
         SoundSource.PLAYERS,
         0.5F,
         this.random.nextFloat() * 0.4F + 0.8F
      );
      AMCompat.addCooldown(playerIn.getCooldowns(), this, 3);
      playerIn.awardStat(Stats.ITEM_USED.get(this));
      return AMCompat.success(itemstack);
   }
}
