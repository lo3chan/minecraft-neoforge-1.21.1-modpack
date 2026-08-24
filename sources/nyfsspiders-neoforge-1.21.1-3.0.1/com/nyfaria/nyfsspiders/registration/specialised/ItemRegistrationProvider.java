package com.nyfaria.nyfsspiders.registration.specialised;

import com.nyfaria.nyfsspiders.registration.RegistrationProvider;
import com.nyfaria.nyfsspiders.registration.RegistryObject;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public interface ItemRegistrationProvider extends RegistrationProvider<Item> {
   static ItemRegistrationProvider get(String modId) {
      return RegistrationProvider.Factory.INSTANCE.item(modId);
   }

   <I extends Item> ItemRegistryObject<I> register(String var1, Supplier<? extends I> var2);

   default ItemRegistryObject<Item> register(String name) {
      return this.register(name, new Properties(), Item::new);
   }

   default <I extends Item> ItemRegistryObject<I> register(String name, Properties properties, Function<Properties, ? extends I> func) {
      return this.register(name, () -> func.apply(properties));
   }

   default <B extends Block> ItemRegistryObject<BlockItem> registerBlockItem(RegistryObject<Block, B> block) {
      return this.registerBlockItem(block, new Properties());
   }

   default <B extends Block> ItemRegistryObject<BlockItem> registerBlockItem(RegistryObject<Block, B> block, Properties properties) {
      return this.registerBlockItem(block, properties, BlockItem::new);
   }

   default <B extends Block, I extends BlockItem> ItemRegistryObject<I> registerBlockItem(
      RegistryObject<Block, B> block, Properties properties, BiFunction<B, Properties, ? extends I> func
   ) {
      return this.register(block.getId().getPath(), () -> func.apply(block.get(), properties));
   }
}
