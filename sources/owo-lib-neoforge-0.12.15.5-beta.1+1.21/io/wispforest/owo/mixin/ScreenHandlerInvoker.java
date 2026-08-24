package io.wispforest.owo.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({AbstractContainerMenu.class})
public interface ScreenHandlerInvoker {
   @Invoker("moveItemStackTo")
   boolean owo$insertItem(ItemStack var1, int var2, int var3, boolean var4);
}
