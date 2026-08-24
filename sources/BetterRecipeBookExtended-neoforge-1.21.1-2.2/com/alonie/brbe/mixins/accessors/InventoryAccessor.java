package com.alonie.brbe.mixins.accessors;

import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Inventory.class})
public interface InventoryAccessor {
   @Accessor("compartments")
   List<NonNullList<ItemStack>> getCompartments();
}
