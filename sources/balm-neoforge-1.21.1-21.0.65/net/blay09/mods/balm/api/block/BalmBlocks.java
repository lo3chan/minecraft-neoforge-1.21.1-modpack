package net.blay09.mods.balm.api.block;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface BalmBlocks {
   static Properties blockProperties(ResourceLocation identifier) {
      return Properties.of();
   }

   static ResourceKey<Block> blockId(ResourceLocation identifier) {
      return ResourceKey.create(Registries.BLOCK, identifier);
   }

   DeferredObject<Block> registerBlock(Function<ResourceLocation, Block> var1, ResourceLocation var2);

   DeferredObject<Item> registerBlockItem(Function<ResourceLocation, BlockItem> var1, ResourceLocation var2, @Nullable ResourceLocation var3);

   void register(
      Function<ResourceLocation, Block> var1, BiFunction<Block, ResourceLocation, BlockItem> var2, ResourceLocation var3, @Nullable ResourceLocation var4
   );

   default DeferredObject<Item> registerBlockItem(Function<ResourceLocation, BlockItem> supplier, ResourceLocation identifier) {
      return this.registerBlockItem(supplier, identifier, identifier.withPath(identifier.getNamespace()));
   }

   default void register(
      Function<ResourceLocation, Block> blockSupplier, BiFunction<Block, ResourceLocation, BlockItem> blockItemSupplier, ResourceLocation identifier
   ) {
      this.register(blockSupplier, blockItemSupplier, identifier, identifier.withPath(identifier.getNamespace()));
   }

   @Deprecated
   default Properties blockProperties() {
      return Properties.of();
   }

   @Deprecated
   default DeferredObject<Block> registerBlock(Supplier<Block> supplier, ResourceLocation identifier) {
      return this.registerBlock(id -> supplier.get(), identifier);
   }

   @Deprecated
   default DeferredObject<Item> registerBlockItem(Supplier<BlockItem> supplier, ResourceLocation identifier) {
      return this.registerBlockItem(supplier, identifier, identifier.withPath(identifier.getNamespace()));
   }

   @Deprecated
   default DeferredObject<Item> registerBlockItem(Supplier<BlockItem> supplier, ResourceLocation identifier, @Nullable ResourceLocation creativeTab) {
      return this.registerBlockItem(id -> supplier.get(), identifier, creativeTab);
   }

   @Deprecated
   default void register(Supplier<Block> blockSupplier, Supplier<BlockItem> blockItemSupplier, ResourceLocation identifier) {
      this.register(blockSupplier, blockItemSupplier, identifier, identifier.withPath(identifier.getNamespace()));
   }

   @Deprecated
   default void register(
      Supplier<Block> blockSupplier, Supplier<BlockItem> blockItemSupplier, ResourceLocation identifier, @Nullable ResourceLocation creativeTab
   ) {
      this.registerBlock(blockSupplier, identifier);
      this.registerBlockItem(id -> blockItemSupplier.get(), identifier, creativeTab);
   }

   BalmBlocks scoped(String var1);
}
