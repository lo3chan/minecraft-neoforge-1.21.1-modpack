package com.nyfaria.nyfsspiders.registration.specialised;

import com.nyfaria.nyfsspiders.registration.RegistryObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public interface BlockRegistryObject<B extends Block> extends RegistryObject<Block, B>, ItemLike {
   default BlockState defaultBlockState() {
      return this.get().defaultBlockState();
   }

   @NotNull
   default Item asItem() {
      return this.get().asItem();
   }
}
