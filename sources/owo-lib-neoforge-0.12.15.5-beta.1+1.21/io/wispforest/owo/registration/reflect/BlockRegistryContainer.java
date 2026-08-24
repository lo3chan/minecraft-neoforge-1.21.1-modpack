package io.wispforest.owo.registration.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public interface BlockRegistryContainer extends AutoRegistryContainer<Block> {
   @Override
   default Registry<Block> getRegistry() {
      return BuiltInRegistries.BLOCK;
   }

   @Override
   default Class<Block> getTargetFieldType() {
      return Block.class;
   }

   default void postProcessField(String namespace, Block value, String identifier, Field field) {
      if (!field.isAnnotationPresent(BlockRegistryContainer.NoBlockItem.class)) {
         Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(namespace, identifier), this.createBlockItem(value, identifier));
      }
   }

   default BlockItem createBlockItem(Block block, String identifier) {
      return new BlockItem(block, new Properties());
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   public @interface NoBlockItem {
   }
}
