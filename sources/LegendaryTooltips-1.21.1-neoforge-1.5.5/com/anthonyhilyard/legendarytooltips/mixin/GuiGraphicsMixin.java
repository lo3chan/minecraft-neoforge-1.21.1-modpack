package com.anthonyhilyard.legendarytooltips.mixin;

import com.anthonyhilyard.iceberg.component.IExtendedText;
import com.anthonyhilyard.iceberg.component.IExtendedText.TextAlignment;
import com.anthonyhilyard.iceberg.services.Services;
import com.anthonyhilyard.iceberg.util.ITooltipAccess;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.anthonyhilyard.iceberg.util.Tooltips.TooltipRenderContext;
import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;
import com.anthonyhilyard.legendarytooltips.tooltip.ItemModelComponent;
import com.anthonyhilyard.legendarytooltips.tooltip.TooltipScroll;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin({GuiGraphics.class})
public class GuiGraphicsMixin {
   @Unique
   private int numTitleLines = 0;
   @Unique
   private int titleStart = 0;
   @Unique
   private int currentMouseX = 0;
   @Unique
   private int currentMouseY = 0;
   @Unique
   private int tooltipY = 0;
   @Unique
   private int startScrollIndex = 0;
   @Unique
   private int originalHeight = 0;
   @Unique
   private boolean hasItemModel = false;
   @Shadow
   private boolean managed;
   @Unique
   private boolean enableScissor = false;
   @Unique
   private boolean scissorEnabled = false;
   @Unique
   Method getXMethod = null;
   @Unique
   Method getYMethod = null;
   @Unique
   Method hasClickedOutsideMethod = null;
   @Unique
   private static int currentIndex = 0;
   @Unique
   private static int maxIndex = 0;
   @Unique
   private static int arsNouveauOffsetX = 0;
   @Unique
   private static int arsNouveauOffsetY = 0;
   @Unique
   private static boolean arsNouveauComponent = false;

   @ModifyVariable(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      ordinal = 0,
      at = @At(
         value = "LOAD",
         ordinal = 0
      ),
      argsOnly = true
   )
   private List<ClientTooltipComponent> mutableComponents(List<ClientTooltipComponent> components) {
      List<ClientTooltipComponent> var2 = new ArrayList<>(components);
      this.numTitleLines = Tooltips.calculateTitleLines(var2);
      this.titleStart = Tooltips.calculateTitleStart(var2);
      this.startScrollIndex = this.titleStart + this.numTitleLines;
      this.hasItemModel = var2.stream().anyMatch(component -> component instanceof ItemModelComponent);
      return var2;
   }

   @ModifyVariable(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      ordinal = 2,
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/List;size()I",
         ordinal = 0
      )
   )
   private int setMinimumWidth(int width) {
      return LegendaryTooltipsConfig.getInstance().enforceMinimumWidth.get() ? Math.max(width, 48) : width;
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Ljava/util/List;size()I",
         ordinal = 0
      )}
   )
   private void adjustTitle(Font font, List<ClientTooltipComponent> components, int mouseX, int mouseY, ClientTooltipPositioner positioner, CallbackInfo info) {
      this.currentMouseX = mouseX;
      this.currentMouseY = mouseY;
      if (!components.isEmpty()) {
         boolean shouldCenter = LegendaryTooltipsConfig.getInstance().centeredTitle.get();
         boolean has3DModel = components.stream().anyMatch(c -> c instanceof ItemModelComponent);
         int componentIndex = 0;

         for (ClientTooltipComponent component : components) {
            if (componentIndex >= this.titleStart
               && componentIndex < this.titleStart + this.numTitleLines
               && component instanceof IExtendedText extendedComponent) {
               if (shouldCenter) {
                  extendedComponent.setAlignment(TextAlignment.CENTER);
               }

               if (has3DModel) {
                  extendedComponent.setPadding(ItemModelComponent.getRenderWidth() + 4, 2);
                  if (componentIndex == this.titleStart + this.numTitleLines - 1 && this.numTitleLines == 1) {
                     extendedComponent.setPadding(
                        extendedComponent.getLeftPadding(),
                        extendedComponent.getRightPadding(),
                        extendedComponent.getTopPadding(),
                        extendedComponent.getBottomPadding() + 9
                     );
                  }
               }
            }

            componentIndex++;
         }
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
   private int overrideTooltipWidthOnPosition(int width) {
      ItemStack currentStack = ((ITooltipAccess)this).getIcebergTooltipStack();
      if (!currentStack.isEmpty()) {
         int maxTooltipWidth = LegendaryTooltipsConfig.getMaxTooltipWidth();
         if (maxTooltipWidth < width) {
            return maxTooltipWidth;
         }
      }

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
   private int overrideTooltipHeightOnPosition(int height) {
      this.originalHeight = height;
      ItemStack currentStack = ((ITooltipAccess)this).getIcebergTooltipStack();
      if (!currentStack.isEmpty()) {
         int maxTooltipHeight = LegendaryTooltipsConfig.getMaxTooltipHeight();
         if (maxTooltipHeight < height) {
            return maxTooltipHeight;
         }
      }

      return height;
   }

   @ModifyVariable(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "STORE",
         ordinal = 0
      )
   )
   private Vector2ic storeTooltipY(Vector2ic pos) {
      this.tooltipY = pos.y();
      return pos;
   }

   @ModifyArg(
      method = {"lambda$renderTooltipInternal$3(IIIILnet/neoforged/neoforge/client/event/RenderTooltipEvent$Color;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V"
      ),
      index = 3
   )
   @Group(
      name = "tooltipWidth",
      max = 1
   )
   private int overrideTooltipWidthOnDraw(int width) {
      ItemStack currentStack = ((ITooltipAccess)this).getIcebergTooltipStack();
      if (!currentStack.isEmpty()) {
         int maxTooltipWidth = LegendaryTooltipsConfig.getMaxTooltipWidth();
         if (maxTooltipWidth < width) {
            return maxTooltipWidth;
         }
      }

      return width;
   }

   @ModifyArg(
      method = {"method_51743"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"
      ),
      index = 3
   )
   @Group(
      name = "tooltipWidth",
      max = 1
   )
   private int overrideTooltipWidthOnDraw2(int width) {
      return this.overrideTooltipWidthOnDraw(width);
   }

   @ModifyArg(
      method = {"lambda$renderTooltipInternal$3(IIIILnet/neoforged/neoforge/client/event/RenderTooltipEvent$Color;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIIIIIII)V"
      ),
      index = 4
   )
   @Group(
      name = "tooltipHeight",
      max = 1
   )
   private int overrideTooltipHeightOnDraw(int height) {
      ItemStack currentStack = ((ITooltipAccess)this).getIcebergTooltipStack();
      if (!currentStack.isEmpty()) {
         int maxTooltipHeight = LegendaryTooltipsConfig.getMaxTooltipHeight();
         if (maxTooltipHeight < height) {
            return maxTooltipHeight;
         }
      }

      return height;
   }

   @ModifyArg(
      method = {"method_51743"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/TooltipRenderUtil;renderTooltipBackground(Lnet/minecraft/client/gui/GuiGraphics;IIIII)V"
      ),
      index = 4
   )
   @Group(
      name = "tooltipHeight",
      max = 1
   )
   private int overrideTooltipHeightOnDraw2(int height) {
      return this.overrideTooltipHeightOnDraw(height);
   }

   @Unique
   private void startScissor(int x, int y, int width, int height) {
      int contentHeight = this.originalHeight - (y - this.tooltipY);
      int scrollHeight = y + height - (y - this.tooltipY) + 1 - (y - 1);
      if (contentHeight > scrollHeight) {
         if (Services.getPlatformHelper().isModLoaded("titanium")) {
            try {
               Class<?> basicContainerScreenClass = Class.forName("com.hrznstudio.titanium.client.screen.container.BasicContainerScreen");
               if (this.getXMethod == null) {
                  this.getXMethod = basicContainerScreenClass.getDeclaredMethod("getX");
                  this.getYMethod = basicContainerScreenClass.getDeclaredMethod("getY");
                  this.hasClickedOutsideMethod = AbstractContainerScreen.class
                     .getDeclaredMethod("hasClickedOutside", double.class, double.class, int.class, int.class, int.class);
                  this.hasClickedOutsideMethod.setAccessible(true);
               }

               if (this.getXMethod != null) {
                  Minecraft minecraft = Minecraft.getInstance();
                  Screen currentScreen = minecraft.screen;
                  if (currentScreen != null && basicContainerScreenClass.isAssignableFrom(currentScreen.getClass())) {
                     int leftPos = (Integer)this.getXMethod.invoke(currentScreen, (Object[])null);
                     int topPos = (Integer)this.getYMethod.invoke(currentScreen, (Object[])null);
                     if (!(Boolean)this.hasClickedOutsideMethod
                        .invoke(currentScreen, (double)(this.currentMouseX + leftPos), (double)(this.currentMouseY + topPos), leftPos, topPos, 0)) {
                        x += leftPos;
                        y += topPos;
                        height += topPos;
                     }
                  }
               }
            } catch (Exception var12) {
            }
         }

         TooltipRenderContext context = Tooltips.getCurrentRenderContext();
         GuiGraphics self = (GuiGraphics)this;
         this.managed = true;
         self.enableScissor(x - 1, y - 1, x + width + 1, y + height - (y - this.tooltipY) + 1);
         TooltipScroll.setTooltipVisible(context.index(), true);
         TooltipScroll.setScrollBounds(context.index(), y - 1, y + height - (y - this.tooltipY) + 1);
         TooltipScroll.setContentHeight(context.index(), contentHeight);
         self.pose().pushPose();
         self.pose().translate(0.0F, -TooltipScroll.currentScroll(context.index()), 0.0F);
         this.scissorEnabled = true;
         this.enableScissor = false;
      }
   }

   @Unique
   private void stopScissor() {
      TooltipRenderContext context = Tooltips.getCurrentRenderContext();
      GuiGraphics self = (GuiGraphics)this;
      self.pose().popPose();
      TooltipScroll.setTooltipVisible(context.index(), false);
      self.disableScissor();
      this.managed = false;
      this.scissorEnabled = false;
   }

   @ModifyArg(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/List;get(I)Ljava/lang/Object;"
      )
   )
   private int handleTooltipScroll(int index) {
      if (index <= this.startScrollIndex && this.scissorEnabled) {
         this.stopScissor();
      } else if (index == this.startScrollIndex) {
         this.enableScissor = true;
      }

      return index;
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"
      )}
   )
   private void resetComponentIteration(
      Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, CallbackInfo info
   ) {
      currentIndex = 0;
      maxIndex = list.size();
   }

   @ModifyArg(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderText(Lnet/minecraft/client/gui/Font;IILorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V"
      ),
      index = 2
   )
   private int scrollText(Font font, int currentX, int currentY, Matrix4f matrix4f, BufferSource bufferSource) {
      if (this.titleStart + this.numTitleLines > 1 && currentIndex > 0 && currentIndex < this.titleStart + this.numTitleLines) {
         currentY -= 2;
      }

      if (this.titleStart > 0 && this.hasItemModel) {
         if (this.numTitleLines == 1 && currentIndex == this.titleStart) {
            currentY += 2;
         }

         if (this.numTitleLines > 1 && currentIndex >= this.titleStart && currentIndex < this.titleStart + this.numTitleLines) {
            currentY -= 2;
         }
      }

      if (this.enableScissor) {
         Rect2i tooltipRect = Tooltips.getCurrentRect();
         this.startScissor(currentX, currentY, tooltipRect.getWidth(), tooltipRect.getHeight());
      }

      currentIndex++;
      if (currentIndex == maxIndex) {
         currentIndex = 0;
      }

      return currentY;
   }

   @ModifyArg(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"
      ),
      index = 2
   )
   private int scrollImages(Font font, int currentX, int currentY, GuiGraphics guiGraphics) {
      if (this.titleStart + this.numTitleLines > 1 && currentIndex > 0 && currentIndex < this.titleStart + this.numTitleLines) {
         currentY -= 2;
      }

      if (this.titleStart > 0 && this.hasItemModel) {
         if (this.numTitleLines == 1 && currentIndex == this.titleStart) {
            currentY += 2;
         }

         if (this.numTitleLines > 1 && currentIndex >= this.titleStart && currentIndex < this.titleStart + this.numTitleLines) {
            currentY -= 2;
         }
      }

      if (this.enableScissor) {
         Rect2i tooltipRect = Tooltips.getCurrentRect();
         this.startScissor(currentX, currentY, tooltipRect.getWidth(), tooltipRect.getHeight());
      }

      currentIndex++;
      return currentY;
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         shift = Shift.AFTER
      )}
   )
   private void fixLayering(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info) {
      TooltipRenderContext context = Tooltips.getCurrentRenderContext();
      GuiGraphics self = (GuiGraphics)this;
      float zOffset = context.index() * 30.0F;
      if (!((ITooltipAccess)self).getIcebergTooltipStack().isEmpty()) {
         zOffset += 90.0F;
      }

      self.pose().translate(0.0F, 0.0F, -zOffset);
   }

   @Inject(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;popPose()V"
      )}
   )
   private void turnOffScissor(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info) {
      if (this.scissorEnabled) {
         this.stopScissor();
      }

      this.numTitleLines = 0;
      this.titleStart = 0;
      this.tooltipY = 0;
      this.startScrollIndex = 0;
      this.hasItemModel = false;
   }

   @Redirect(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;getWidth(Lnet/minecraft/client/gui/Font;)I"
      )
   )
   private int arsNouveauCompatGetWidthProxy(
      ClientTooltipComponent instance, Font font, Font font2, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner
   ) {
      if (instance.getClass().getName().contentEquals("com.hollingsworth.arsnouveau.client.gui.SchoolTooltip$SchoolTooltipRenderer")) {
         for (ClientTooltipComponent component : list) {
            if (component instanceof ClientTextTooltip title) {
               arsNouveauOffsetX = instance.getWidth(font) - 3;
               if (LegendaryTooltipsConfig.showModelForItem(((ITooltipAccess)this).getIcebergTooltipStack())) {
                  arsNouveauOffsetY = -7;
               } else {
                  arsNouveauOffsetY = 0;
               }

               return title.getWidth(font) + (instance.getWidth(font) - font.width(title.text));
            }
         }
      }

      return instance.getWidth(font);
   }

   @ModifyArgs(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/List;get(I)Ljava/lang/Object;",
         ordinal = 1
      )
   )
   private void arsNouveauCompatComponentCheck(Args args, Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner) {
      int i = (Integer)args.get(0);
      arsNouveauComponent = i < components.size()
         && components.get(i).getClass().getName().contentEquals("com.hollingsworth.arsnouveau.client.gui.SchoolTooltip$SchoolTooltipRenderer");
   }

   @ModifyArgs(
      method = {"renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;renderImage(Lnet/minecraft/client/gui/Font;IILnet/minecraft/client/gui/GuiGraphics;)V"
      )
   )
   private void arsNouveauCompatOffsetComponent(Args args) {
      if (arsNouveauComponent
         && (
            LegendaryTooltipsConfig.getInstance().centeredTitle.get()
               || LegendaryTooltipsConfig.showModelForItem(((ITooltipAccess)this).getIcebergTooltipStack())
         )) {
         Rect2i tooltipRect = Tooltips.getCurrentRect();
         int x = (Integer)args.get(1);
         int y = (Integer)args.get(2);
         args.set(1, x + tooltipRect.getWidth() - arsNouveauOffsetX);
         args.set(2, y + arsNouveauOffsetY);
      }
   }
}
