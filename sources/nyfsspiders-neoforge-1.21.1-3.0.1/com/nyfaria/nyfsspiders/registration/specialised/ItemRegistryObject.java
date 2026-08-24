package com.nyfaria.nyfsspiders.registration.specialised;

import com.nyfaria.nyfsspiders.registration.RegistryObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public interface ItemRegistryObject<I extends Item> extends RegistryObject<Item, I>, ItemLike {
   @NotNull
   default Item asItem() {
      return this.get().asItem();
   }
}
