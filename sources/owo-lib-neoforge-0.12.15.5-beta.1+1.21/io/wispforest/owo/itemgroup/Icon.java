package io.wispforest.owo.itemgroup;

import io.wispforest.owo.client.texture.AnimatedTextureDrawable;
import io.wispforest.owo.client.texture.SpriteSheetMetadata;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@FunctionalInterface
public interface Icon {
   @OnlyIn(Dist.CLIENT)
   void render(GuiGraphics var1, int var2, int var3, int var4, int var5, float var6);

   static Icon of(ItemStack stack) {
      return new Icon() {
         @Override
         public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
            context.renderFakeItem(stack, x, y);
         }
      };
   }

   static Icon of(ItemLike item) {
      return of(new ItemStack(item));
   }

   static Icon of(ResourceLocation texture, int u, int v, int textureWidth, int textureHeight) {
      return new Icon() {
         @Override
         public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
            context.blit(texture, x, y, u, v, 16, 16, textureWidth, textureHeight);
         }
      };
   }

   static Icon of(ResourceLocation texture, int textureSize, int frameDelay, boolean loop) {
      final AnimatedTextureDrawable widget = new AnimatedTextureDrawable(0, 0, 16, 16, texture, new SpriteSheetMetadata(textureSize, 16), frameDelay, loop);
      return new Icon() {
         @Override
         public void render(GuiGraphics context, int x, int y, int mouseX, int mouseY, float delta) {
            widget.render(x, y, context, mouseX, mouseY, delta);
         }
      };
   }
}
