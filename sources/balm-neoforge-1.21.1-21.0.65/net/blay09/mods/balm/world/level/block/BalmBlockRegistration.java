package net.blay09.mods.balm.world.level.block;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.core.BalmHolderRegistration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public interface BalmBlockRegistration extends BalmHolderRegistration<Block> {
   default BalmBlockRegistration withDefaultItem() {
      return this.withItem(BlockItem::new);
   }

   default BalmBlockRegistration withDefaultItem(Function<Properties, Properties> propertiesBuilder) {
      return this.withItem(BlockItem::new, propertiesBuilder);
   }

   default BalmBlockRegistration withDefaultItem(Supplier<Properties> propertiesSupplier) {
      return this.withItem(BlockItem::new, propertiesSupplier);
   }

   default BalmBlockRegistration withDefaultItem(Properties properties) {
      return this.withItem(BlockItem::new, properties);
   }

   default BalmBlockRegistration withItem(BiFunction<Block, Properties, BlockItem> constructor) {
      return this.withItem(constructor, Function.identity());
   }

   default BalmBlockRegistration withItem(BiFunction<Block, Properties, BlockItem> constructor, Function<Properties, Properties> propertiesBuilder) {
      return this.withItem(constructor, (Supplier<Properties>)(() -> propertiesBuilder.apply(new Properties())));
   }

   default BalmBlockRegistration withItem(BiFunction<Block, Properties, BlockItem> constructor, Properties properties) {
      return this.withItem(constructor, (Supplier<Properties>)(() -> properties));
   }

   BalmBlockRegistration withItem(BiFunction<Block, Properties, BlockItem> var1, Supplier<Properties> var2);

   BalmBlockRegistration withItem(String var1, BiFunction<Block, Properties, BlockItem> var2, Function<Properties, Properties> var3);

   default BlockLike asBlockLike() {
      return this.asDeferredBlock();
   }

   default ItemLike asItemLike() {
      return this.asDeferredBlock();
   }

   DeferredBlock asDeferredBlock();
}
