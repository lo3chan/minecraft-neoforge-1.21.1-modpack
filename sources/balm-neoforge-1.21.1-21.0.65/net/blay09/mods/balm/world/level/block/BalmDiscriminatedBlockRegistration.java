package net.blay09.mods.balm.world.level.block;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public interface BalmDiscriminatedBlockRegistration<T> extends Map<T, BalmBlockRegistration> {
   default BalmDiscriminatedBlockRegistration<T> withDefaultItems() {
      this.forEach((discriminator, it) -> it.withDefaultItem());
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withDefaultItems(Function<Properties, Properties> propertiesBuilder) {
      this.forEach((discriminator, it) -> it.withItem(BlockItem::new, propertiesBuilder));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withDefaultItems(BiFunction<T, Properties, Properties> propertiesBuilder) {
      this.forEach(
         (discriminator, it) -> it.withItem(BlockItem::new, (Supplier<Properties>)(() -> propertiesBuilder.apply((T)discriminator, new Properties())))
      );
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withDefaultItems(Supplier<Properties> propertiesSupplier) {
      this.forEach((discriminator, it) -> it.withItem(BlockItem::new, propertiesSupplier));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withDefaultItems(Properties properties) {
      this.forEach((discriminator, it) -> it.withItem(BlockItem::new, properties));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(BiFunction<Block, Properties, BlockItem> constructor) {
      this.forEach((discriminator, it) -> it.withItem(constructor, Function.identity()));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(
      BiFunction<Block, Properties, BlockItem> constructor, Function<Properties, Properties> propertiesBuilder
   ) {
      this.forEach((discriminator, it) -> it.withItem(constructor, (Supplier<Properties>)(() -> propertiesBuilder.apply(new Properties()))));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(
      BiFunction<Block, Properties, BlockItem> constructor, BiFunction<T, Properties, Properties> propertiesBuilder
   ) {
      this.forEach((discrimination, it) -> it.withItem(constructor, (Supplier<Properties>)(() -> propertiesBuilder.apply((T)discrimination, new Properties()))));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(BiFunction<Block, Properties, BlockItem> constructor, Properties properties) {
      this.forEach((discriminator, it) -> it.withItem(constructor, (Supplier<Properties>)(() -> properties)));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(BiFunction<Block, Properties, BlockItem> constructor, Supplier<Properties> properties) {
      this.forEach((discriminator, it) -> it.withItem(constructor, properties));
      return this;
   }

   default BalmDiscriminatedBlockRegistration<T> withItems(
      Function<T, String> nameFunction, BiFunction<Block, Properties, BlockItem> constructor, BiFunction<T, Properties, Properties> propertiesBuilder
   ) {
      this.forEach(
         (discrimination, it) -> it.withItem(
            nameFunction.apply((T)discrimination), constructor, properties -> propertiesBuilder.apply((T)discrimination, properties)
         )
      );
      return this;
   }

   DiscriminatedBlocks<T> asDiscriminatedBlocks();
}
