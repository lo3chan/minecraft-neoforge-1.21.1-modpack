package vazkii.psi.common.item.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface IHUDItem {
   @OnlyIn(Dist.CLIENT)
   void drawHUD(GuiGraphics var1, float var2, int var3, int var4, ItemStack var5);
}
