package net.blay09.mods.balm.neoforge.block;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.blay09.mods.balm.api.item.BalmItems;
import net.blay09.mods.balm.common.NamespaceResolver;
import net.blay09.mods.balm.common.StaticNamespaceResolver;
import net.blay09.mods.balm.neoforge.DeferredRegisters;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

public record NeoForgeBalmBlocks(NamespaceResolver namespaceResolver, BalmItems items) implements BalmBlocks {
   @Override
   public DeferredObject<Block> registerBlock(Function<ResourceLocation, Block> supplier, ResourceLocation identifier) {
      DeferredRegister<Block> register = DeferredRegisters.get(Registries.BLOCK, identifier.getNamespace());
      DeferredHolder<Block, Block> registryObject = register.register(identifier.getPath(), supplier);
      return new DeferredObject<>(identifier, registryObject, registryObject::isBound);
   }

   @Override
   public DeferredObject<Item> registerBlockItem(
      Function<ResourceLocation, BlockItem> supplier, ResourceLocation identifier, @Nullable ResourceLocation creativeTab
   ) {
      return this.items.registerItem(supplier::apply, identifier, creativeTab);
   }

   @Override
   public void register(
      Function<ResourceLocation, Block> blockSupplier,
      BiFunction<Block, ResourceLocation, BlockItem> blockItemSupplier,
      ResourceLocation identifier,
      @Nullable ResourceLocation creativeTab
   ) {
      DeferredObject<Block> deferredBlock = this.registerBlock(blockSupplier, identifier);
      this.registerBlockItem(id -> blockItemSupplier.apply(deferredBlock.get(), id), identifier, creativeTab);
   }

   @Override
   public BalmBlocks scoped(String modId) {
      return new NeoForgeBalmBlocks(new StaticNamespaceResolver(modId), this.items.scoped(modId));
   }
}
