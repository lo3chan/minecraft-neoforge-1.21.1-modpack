package io.wispforest.owo.mixin.itemgroup;

import java.util.Set;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.Row;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CreativeModeTab.class})
public interface ItemGroupAccessor {
   @Accessor("displayItemsGenerator")
   DisplayItemsGenerator owo$getEntryCollector();

   @Mutable
   @Accessor("displayItemsGenerator")
   void owo$setEntryCollector(DisplayItemsGenerator var1);

   @Accessor("displayItemsSearchTab")
   void owo$setSearchTabStacks(Set<ItemStack> var1);

   @Mutable
   @Accessor("displayName")
   void owo$setDisplayName(Component var1);

   @Mutable
   @Accessor("column")
   void owo$setColumn(int var1);

   @Mutable
   @Accessor("row")
   void owo$setRow(Row var1);
}
