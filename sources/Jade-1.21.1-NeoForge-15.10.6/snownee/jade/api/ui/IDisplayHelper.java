package snownee.jade.api.ui;

import java.text.Format;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeInternals;

public interface IDisplayHelper {
   static IDisplayHelper get() {
      return JadeInternals.getDisplayHelper();
   }

   void drawItem(GuiGraphics var1, float var2, float var3, ItemStack var4, float var5, @Nullable String var6);

   void drawGradientRect(GuiGraphics var1, float var2, float var3, float var4, float var5, int var6, int var7);

   void drawBorder(GuiGraphics var1, float var2, float var3, float var4, float var5, float var6, int var7, boolean var8);

   String humanReadableNumber(double var1, String var3, boolean var4);

   String humanReadableNumber(double var1, String var3, boolean var4, @Nullable Format var5);

   void drawText(GuiGraphics var1, String var2, float var3, float var4, int var5);

   void drawText(GuiGraphics var1, FormattedText var2, float var3, float var4, int var5);

   void drawText(GuiGraphics var1, FormattedCharSequence var2, float var3, float var4, int var5);

   MutableComponent stripColor(Component var1);

   void blitSprite(GuiGraphics var1, ResourceLocation var2, int var3, int var4, int var5, int var6);

   void blitSprite(GuiGraphics var1, ResourceLocation var2, int var3, int var4, int var5, int var6, int var7);

   void blitSprite(GuiGraphics var1, ResourceLocation var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   void blitSprite(GuiGraphics var1, ResourceLocation var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11);

   float opacity();
}
