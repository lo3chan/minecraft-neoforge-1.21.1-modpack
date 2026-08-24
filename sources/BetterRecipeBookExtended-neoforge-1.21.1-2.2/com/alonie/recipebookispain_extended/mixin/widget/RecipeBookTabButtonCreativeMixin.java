package com.alonie.recipebookispain_extended.mixin.widget;

import com.alonie.recipebookispain_extended.access.CreativeTabButtonAccess;
import com.alonie.recipebookispain_extended.access.RecipeGroupButtonPlacement;
import com.alonie.recipebookispain_extended.access.RecipeGroupButtonPlacementAccess;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RecipeBookTabButton.class})
public abstract class RecipeBookTabButtonCreativeMixin implements CreativeTabButtonAccess, RecipeGroupButtonPlacementAccess {
   @Unique
   private CreativeModeTab rbip$creativeTab;
   @Unique
   private RecipeGroupButtonPlacement rbip$placement = RecipeGroupButtonPlacement.NORMAL;
   @Unique
   private static final ResourceLocation TEX_BOTTOM = ResourceLocation.fromNamespaceAndPath("recipe-book-is-pain-extended", "textures/rbip/bottom_tab.png");
   @Unique
   private static final ResourceLocation TEX_BOTTOM_SEL = ResourceLocation.fromNamespaceAndPath(
      "recipe-book-is-pain-extended", "textures/rbip/bottom_tab_selected.png"
   );
   @Unique
   private static final ResourceLocation TEX_TOP = ResourceLocation.fromNamespaceAndPath("recipe-book-is-pain-extended", "textures/rbip/top_tab.png");
   @Unique
   private static final ResourceLocation TEX_TOP_SEL = ResourceLocation.fromNamespaceAndPath(
      "recipe-book-is-pain-extended", "textures/rbip/top_tab_selected.png"
   );
   @Unique
   private static final int TAB_W = 35;
   @Unique
   private static final int TAB_H = 27;
   @Unique
   private static final int ROT_TAB_W = 27;
   @Unique
   private static final int ROT_TAB_H = 35;

   @Override
   public void rbip$setCreativeTab(CreativeModeTab tab) {
      this.rbip$creativeTab = tab;
   }

   @Override
   public CreativeModeTab rbip$getCreativeTab() {
      return this.rbip$creativeTab;
   }

   @Override
   public void rbip$setPlacement(RecipeGroupButtonPlacement p) {
      this.rbip$placement = p;
   }

   @Override
   public RecipeGroupButtonPlacement rbip$getPlacement() {
      return this.rbip$placement;
   }

   @Inject(
      method = {"startAnimation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void rbip$cancelCreativeTabBounce(CallbackInfo ci) {
      if (this.rbip$creativeTab != null) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"renderIcon"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void rbip$renderCreativeIcon(GuiGraphics gui, ItemRenderer itemRenderer, CallbackInfo ci) {
      if (this.rbip$creativeTab != null) {
         ci.cancel();
         ItemStack icon = this.rbip$creativeTab.getIconItem();
         if (!icon.isEmpty()) {
            RecipeBookTabButton self = (RecipeBookTabButton)this;
            int xOff = self.isStateTriggered() ? -2 : 0;
            gui.renderFakeItem(icon, self.getX() + 9 + xOff, self.getY() + 5);
         }
      }
   }

   @Inject(
      method = {"renderWidget"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void rbip$renderRotated(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      if (this.rbip$placement != RecipeGroupButtonPlacement.NORMAL) {
         if (this.rbip$creativeTab != null) {
            ci.cancel();
            this.rbip$drawRotatedBackground(gui);
            ItemStack icon = this.rbip$creativeTab.getIconItem();
            if (!icon.isEmpty()) {
               gui.renderFakeItem(icon, this.rbip$getRotatedIconX(), this.rbip$getRotatedIconY());
            }
         }
      }
   }

   @Unique
   private void rbip$drawRotatedBackground(GuiGraphics gui) {
      RecipeBookTabButton self = (RecipeBookTabButton)this;
      boolean selected = self.isStateTriggered();
      int localX = selected ? -2 : 0;
      PoseStack pose = gui.pose();
      pose.pushPose();
      if (this.rbip$placement == RecipeGroupButtonPlacement.BOTTOM) {
         pose.translate(self.getX(), self.getY() + 35, 0.0F);
         pose.mulPose(Axis.ZP.rotationDegrees(-90.0F));
      } else {
         pose.translate(self.getX() + 27, self.getY(), 0.0F);
         pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
      }

      ResourceLocation tex = this.rbip$getRotatedTexture();
      gui.blit(tex, localX, 0, 0.0F, 0.0F, 35, 27, 35, 27);
      pose.popPose();
   }

   @Unique
   private int rbip$getRotatedIconX() {
      RecipeBookTabButton self = (RecipeBookTabButton)this;
      int off = this.rbip$placement == RecipeGroupButtonPlacement.TOP ? 1 : 0;
      return self.getX() + 5 + off;
   }

   @Unique
   private int rbip$getRotatedIconY() {
      RecipeBookTabButton self = (RecipeBookTabButton)this;
      int y = self.getY() + 9;
      if (this.rbip$placement == RecipeGroupButtonPlacement.TOP) {
         return y - 1;
      } else {
         return this.rbip$placement == RecipeGroupButtonPlacement.BOTTOM ? y + 1 : y;
      }
   }

   @Unique
   private ResourceLocation rbip$getRotatedTexture() {
      RecipeBookTabButton self = (RecipeBookTabButton)this;
      boolean selected = self.isStateTriggered();
      if (this.rbip$placement == RecipeGroupButtonPlacement.BOTTOM) {
         return selected ? TEX_BOTTOM_SEL : TEX_BOTTOM;
      } else {
         return selected ? TEX_TOP_SEL : TEX_TOP;
      }
   }
}
