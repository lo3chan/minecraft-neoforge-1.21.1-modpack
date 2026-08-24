package vazkii.patchouli.client.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.util.ItemStackUtil;

public sealed interface BookIcon permits BookIcon.StackIcon, BookIcon.TextureIcon {
   void render(GuiGraphics var1, int var2, int var3);

   static BookIcon from(String str, Provider registries) {
      if (str.endsWith(".png")) {
         return new BookIcon.TextureIcon(ResourceLocation.tryParse(str));
      } else {
         try {
            ItemStack stack = ItemStackUtil.loadStackFromString(str, registries);
            return new BookIcon.StackIcon(stack);
         } catch (Exception var3) {
            PatchouliAPI.LOGGER.warn("Invalid icon item stack: {}", var3.getMessage());
            return new BookIcon.StackIcon(ItemStack.EMPTY);
         }
      }
   }

   public record StackIcon(ItemStack stack) implements BookIcon {
      @Override
      public void render(GuiGraphics graphics, int x, int y) {
         graphics.renderItem(this.stack(), x, y);
         graphics.renderItemDecorations(Minecraft.getInstance().font, this.stack(), x, y);
      }
   }

   public record TextureIcon(ResourceLocation texture) implements BookIcon {
      @Override
      public void render(GuiGraphics graphics, int x, int y) {
         graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
         graphics.blit(this.texture(), x, y, 0.0F, 0.0F, 16, 16, 16, 16);
      }
   }
}
