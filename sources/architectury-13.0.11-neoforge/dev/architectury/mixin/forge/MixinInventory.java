package dev.architectury.mixin.forge;

import dev.architectury.extensions.ItemExtension;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Inventory.class})
public class MixinInventory {
   @Shadow
   @Final
   public NonNullList<ItemStack> armor;
   @Shadow
   @Final
   public Player player;

   @Inject(
      method = {"tick()V"},
      at = {@At("RETURN")}
   )
   private void updateItems(CallbackInfo ci) {
      for (ItemStack stack : this.armor) {
         if (stack.getItem() instanceof ItemExtension extension) {
            extension.tickArmor(stack, this.player);
         }
      }
   }
}
