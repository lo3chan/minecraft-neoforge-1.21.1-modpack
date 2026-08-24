package com.alonie.recipebookispain_extended.mixin;

import com.alonie.recipebookispain_extended.access.ItemAccess;
import java.util.Optional;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Item.class})
public class ItemMixin implements ItemAccess {
   @Unique
   public CreativeModeTab rbip$possibleGroup;

   @Override
   public Optional<CreativeModeTab> rbip$getPossibleGroup() {
      return Optional.ofNullable(this.rbip$possibleGroup);
   }

   @Override
   public void rbip$setPossibleGroup(CreativeModeTab group) {
      this.rbip$possibleGroup = group;
   }
}
