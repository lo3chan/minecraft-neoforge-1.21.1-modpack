package net.cibernet.alchemancy.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.cibernet.alchemancy.item.components.InfusedPropertiesHelper;
import net.cibernet.alchemancy.properties.special.BindingProperty;
import net.cibernet.alchemancy.registries.AlchemancyProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen.ItemPickerMenu;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({CreativeModeInventoryScreen.class})
public class CreativeScreenMixin {
   @WrapMethod(
      method = {"slotClicked"}
   )
   public void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type, Operation<Void> original) {
      original.call(new Object[]{slot, slotId, mouseButton, type});
      ItemStack carried = ((ItemPickerMenu)((CreativeModeInventoryScreen)this).getMenu()).getCarried();
      if (mouseButton != 0 && slotId >= 0 && InfusedPropertiesHelper.hasProperty(carried, AlchemancyProperties.BINDING)) {
         BindingProperty.toggleBind(carried, slot.getItem());
      }
   }

   @WrapOperation(
      method = {"slotClicked"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleCreativeModeItemAdd(Lnet/minecraft/world/item/ItemStack;I)V",
         ordinal = 0
      )}
   )
   public void clearItems(MultiPlayerGameMode instance, ItemStack stack, int slotId, Operation<Void> original, @Local(ordinal = 2) int j) {
      if (!InfusedPropertiesHelper.hasProperty((ItemStack)Minecraft.getInstance().player.inventoryMenu.getItems().get(j), AlchemancyProperties.UNMOVABLE)) {
         original.call(new Object[]{instance, stack, slotId});
      }
   }
}
