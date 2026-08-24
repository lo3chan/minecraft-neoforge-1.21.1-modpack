package vazkii.psi.common.item;

import java.util.ArrayList;
import java.util.List;
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
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.cad.ISocketableController;
import vazkii.psi.common.core.handler.PsiSoundHandler;
import vazkii.psi.common.item.base.ModDataComponents;

public class ItemExosuitController extends Item implements ISocketableController {
   public ItemExosuitController(Properties properties) {
      super(properties.stacksTo(1));
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
      ItemStack itemStackIn = playerIn.getItemInHand(hand);
      if (playerIn.isShiftKeyDown()) {
         if (!worldIn.isClientSide) {
            worldIn.playSound(null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), PsiSoundHandler.compileError, SoundSource.PLAYERS, 0.25F, 1.0F);
         } else {
            playerIn.swing(hand);
         }

         ItemStack[] stacks = this.getControlledStacks(playerIn, itemStackIn);

         for (ItemStack stack : stacks) {
            ISocketable socketable = (ISocketable)stack.getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
            if (socketable != null) {
               socketable.setSelectedSlot(3);
            }
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, itemStackIn);
      } else {
         return new InteractionResultHolder(InteractionResult.PASS, itemStackIn);
      }
   }

   @Override
   public ItemStack[] getControlledStacks(Player player, ItemStack stack) {
      List<ItemStack> stacks = new ArrayList<>();

      for (int i = 0; i < 4; i++) {
         ItemStack armor = (ItemStack)player.getInventory().armor.get(3 - i);
         if (!armor.isEmpty() && ISocketable.isSocketable(armor)) {
            stacks.add(armor);
         }
      }

      return stacks.toArray(new ItemStack[0]);
   }

   @Override
   public int getDefaultControlSlot(ItemStack stack) {
      return (Integer)stack.getOrDefault(ModDataComponents.SELECTED_CONTROL_SLOT, 0);
   }

   @Override
   public void setSelectedSlot(Player player, ItemStack stack, int controlSlot, int slot) {
      stack.set(ModDataComponents.SELECTED_CONTROL_SLOT, controlSlot);
      ItemStack[] stacks = this.getControlledStacks(player, stack);
      if (controlSlot < stacks.length && !stacks[controlSlot].isEmpty()) {
         ISocketable socketable = (ISocketable)stacks[controlSlot].getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
         if (socketable != null) {
            socketable.setSelectedSlot(slot);
         }
      }
   }
}
