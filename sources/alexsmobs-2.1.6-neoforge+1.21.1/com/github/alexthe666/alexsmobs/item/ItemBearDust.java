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
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class ItemBearDust extends Item implements CustomTabBehavior {
   private final Random random = new Random();

   public ItemBearDust(Properties props) {
      super(props);
   }

   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
      ItemStack itemstack = playerIn.getItemInHand(handIn);
      playerIn.gameEvent(GameEvent.ITEM_INTERACT_START);
      worldIn.playSound(
         null,
         playerIn.getX(),
         playerIn.getY(),
         playerIn.getZ(),
         AMSoundRegistry.BEAR_DUST.get(),
         SoundSource.PLAYERS,
         0.75F,
         this.random.nextFloat() * 0.2F + 0.9F
      );
      AMCompat.addCooldown(playerIn.getCooldowns(), this, 3);
      playerIn.awardStat(Stats.ITEM_USED.get(this));
      return AMCompat.success(itemstack);
   }

   @Override
   public void fillItemCategory(Output contents) {
   }
}
