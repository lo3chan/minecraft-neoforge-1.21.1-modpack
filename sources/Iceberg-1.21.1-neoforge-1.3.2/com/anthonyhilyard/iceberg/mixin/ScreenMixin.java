package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.services.Services;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Screen.class})
public class ScreenMixin {
   @Inject(
      method = {"getTooltipFromItem(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"},
      at = {@At("HEAD")}
   )
   private static List<Component> getTooltipFromItem(Minecraft minecraft, ItemStack itemStack, CallbackInfoReturnable<List<Component>> info) {
      if (Services.getPlatformHelper().isModLoaded("andromeda")) {
         try {
            Field tooltipStackField = GuiGraphics.class.getDeclaredField("icebergTooltipStack");
            tooltipStackField.setAccessible(true);
            tooltipStackField.set(null, itemStack);
         } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException var4) {
            Iceberg.LOGGER.error(ExceptionUtils.getStackTrace(var4));
         }
      }

      return (List<Component>)info.getReturnValue();
   }
}
