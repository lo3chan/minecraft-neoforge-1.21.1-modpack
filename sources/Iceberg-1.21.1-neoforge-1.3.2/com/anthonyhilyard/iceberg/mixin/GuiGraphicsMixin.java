package com.anthonyhilyard.iceberg.mixin;

import com.anthonyhilyard.iceberg.Iceberg;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.services.Services;
import com.anthonyhilyard.iceberg.util.ITooltipAccess;
import com.anthonyhilyard.iceberg.util.Tooltips;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {GuiGraphics.class},
   priority = 1001
)
public class GuiGraphicsMixin implements ITooltipAccess {
   @Unique
   private static Field tooltipStackField = null;
   @Unique
   private int storedTooltipWidth;
   @Unique
   private int storedTooltipHeight;
   @Unique
   private Vector2ic storedPostPos;

   @Override
   public void setIcebergTooltipStack(ItemStack stack) {
      if (tooltipStackField == null) {
         try {
            String e = Services.getPlatformHelper().getPlatformName();
            byte var3 = -1;
            switch (e.hashCode()) {
               case 2096654533:
                  if (e.equals("Fabric")) {
                     var3 = 0;
                  }
               default:
                  switch (var3) {
                     case 0:
                        tooltipStackField = GuiGraphics.class.getDeclaredField("icebergTooltipStack");
                     default:
                        tooltipStackField = GuiGraphics.class.getDeclaredField("tooltipStack");
                        tooltipStackField.setAccessible(true);
                  }
            }
         } catch (Exception var5) {
            Iceberg.LOGGER.debug(ExceptionUtils.getStackTrace(var5));
         }
      }

      try {
         tooltipStackField.set(this, stack);
      } catch (Exception var4) {
      }
   }

   @Override
   public ItemStack getIcebergTooltipStack() {
      if (tooltipStackField == null) {
         try {
            String e = Services.getPlatformHelper().getPlatformName();
            byte var2 = -1;
            switch (e.hashCode()) {
               case 2096654533:
                  if (e.equals("Fabric")) {
                     var2 = 0;
                  }
               default:
                  switch (var2) {
                     case 0:
                        tooltipStackField = GuiGraphics.class.getDeclaredField("icebergTooltipStack");
                     default:
                        tooltipStackField = GuiGraphics.class.getDeclaredField("tooltipStack");
                        tooltipStackField.setAccessible(true);
                  }
            }
         } catch (Exception var4) {
            Iceberg.LOGGER.debug(ExceptionUtils.getStackTrace(var4));
         }
      }

      try {
         return (ItemStack)tooltipStackField.get(this);
      } catch (Exception var3) {
         return ItemStack.EMPTY;
      }
   }

   @ModifyArg(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
      ),
      index = 4
   )
   private int storeTooltipWidth(int width) {
      Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
      if (context.maxWidth() < width && context.maxWidth() > 0) {
         width = context.maxWidth();
      }

      this.storedTooltipWidth = width;
      return width;
   }

   @ModifyArg(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
      ),
      index = 5
   )
   private int storeTooltipHeight(int height) {
      Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
      if (context.maxHeight() < height && context.maxHeight() > 0) {
         height = context.maxHeight();
      }

      this.storedTooltipHeight = height;
      return height;
   }

   @ModifyVariable(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "STORE",
         ordinal = 0
      )
   )
   private Vector2ic storeTooltipPosition(Vector2ic pos) {
      this.storedPostPos = pos;
      return pos;
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At(
         value = "INVOKE_ASSIGN",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;positionTooltip(IIIIII)Lorg/joml/Vector2ic;"
      )},
      cancellable = true
   )
   private void storeCalculatedRect(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info) {
      Tooltips.setCurrentRect(this.storedPostPos.x(), this.storedPostPos.y(), this.storedTooltipWidth, this.storedTooltipHeight);
      if (Tooltips.getCurrentRenderContext() == Tooltips.CALCULATE_RECT_CONTEXT) {
         info.cancel();
      }
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At("HEAD")}
   )
   private void preRenderTooltipInternal(
      Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info
   ) {
      if (Tooltips.getCurrentRenderContext() != Tooltips.CALCULATE_RECT_CONTEXT) {
         Tooltips.setAnyTooltipsVisible(true);
      }

      if (Services.getPlatformHelper().isModLoaded("emi") && Tooltips.getCurrentRenderContext() == Tooltips.EMPTY_CONTEXT) {
         try {
            ItemStack tooltipStack = (ItemStack)Class.forName("com.anthonyhilyard.iceberg.compat.EMIHandler")
               .getMethod("getTooltipStack", List.class)
               .invoke(null, components);
            if (!tooltipStack.isEmpty()) {
               this.setIcebergTooltipStack(tooltipStack);
            }
         } catch (Exception var8) {
            Iceberg.LOGGER.debug(ExceptionUtils.getStackTrace(var8));
         }
      }
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At("TAIL")}
   )
   private void renderTooltipInternalTail(
      Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info
   ) {
      GuiGraphics self = (GuiGraphics)this;
      ItemStack tooltipStack = this.getIcebergTooltipStack();
      if (!components.isEmpty()) {
         Tooltips.TooltipRenderContext context = Tooltips.getCurrentRenderContext();
         RenderTooltipEvents.POSTEXT
            .invoker()
            .onPost(
               tooltipStack,
               self,
               this.storedPostPos.x(),
               this.storedPostPos.y(),
               font,
               this.storedTooltipWidth,
               this.storedTooltipHeight,
               components,
               context.comparison(),
               context.index()
            );
      }

      this.setIcebergTooltipStack(ItemStack.EMPTY);
      Tooltips.setCurrentRect(0, 0, 0, 0);
   }
}
