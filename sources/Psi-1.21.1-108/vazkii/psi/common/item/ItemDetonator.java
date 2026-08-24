package vazkii.psi.common.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.api.spell.detonator.IDetonationHandler;

public class ItemDetonator extends Item {
   public ItemDetonator(Properties properties) {
      super(properties.stacksTo(1));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
      ItemStack itemStackIn = playerIn.getItemInHand(hand);
      if (!worldIn.isClientSide) {
         IDetonationHandler.performDetonation(worldIn, playerIn);
         worldIn.playSound(
            null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), (SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 1.0F, 1.0F
         );
      } else {
         playerIn.swing(hand);
      }

      return new InteractionResultHolder(InteractionResult.SUCCESS, itemStackIn);
   }
}
