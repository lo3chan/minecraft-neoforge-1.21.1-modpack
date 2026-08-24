package io.wispforest.owo.mixin.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphics.ScissorStack;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({GuiGraphics.class})
public interface DrawContextInvoker {
   @Invoker("renderTooltipInternal")
   void owo$renderTooltipFromComponents(Font var1, List<ClientTooltipComponent> var2, int var3, int var4, ClientTooltipPositioner var5);

   @Accessor("pose")
   PoseStack owo$getMatrices();

   @Mutable
   @Accessor("pose")
   void owo$setMatrices(PoseStack var1);

   @Accessor("scissorStack")
   ScissorStack owo$getScissorStack();

   @Mutable
   @Accessor("scissorStack")
   void owo$setScissorStack(ScissorStack var1);
}
