package net.blay09.mods.inventoryessentials.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({AbstractContainerScreen.class})
public interface AbstractContainerScreenAccessor {
   @Accessor
   int getLeftPos();

   @Accessor
   int getTopPos();

   @Accessor
   Slot getHoveredSlot();

   @Invoker
   boolean callHasClickedOutside(double var1, double var3, int var5, int var6, int var7);
}
