package net.blay09.mods.balm.api;

import java.util.Collection;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public interface BalmRegistries {
   @Deprecated
   default ResourceLocation getKey(Item item) {
      return BuiltInRegistries.ITEM.getKey(item);
   }

   @Deprecated
   default ResourceLocation getKey(Block block) {
      return BuiltInRegistries.BLOCK.getKey(block);
   }

   @Deprecated
   default ResourceLocation getKey(Fluid fluid) {
      return BuiltInRegistries.FLUID.getKey(fluid);
   }

   @Deprecated
   default ResourceLocation getKey(EntityType<?> entityType) {
      return BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
   }

   @Deprecated
   default ResourceLocation getKey(MenuType<?> menuType) {
      return BuiltInRegistries.MENU.getKey(menuType);
   }

   @Deprecated
   default Collection<ResourceLocation> getItemKeys() {
      return BuiltInRegistries.ITEM.keySet();
   }

   @Deprecated
   default Item getItem(ResourceLocation key) {
      return (Item)BuiltInRegistries.ITEM.get(key);
   }

   @Deprecated
   default Block getBlock(ResourceLocation key) {
      return (Block)BuiltInRegistries.BLOCK.get(key);
   }

   @Deprecated
   default Fluid getFluid(ResourceLocation key) {
      return (Fluid)BuiltInRegistries.FLUID.get(key);
   }

   @Deprecated
   default MobEffect getMobEffect(ResourceLocation key) {
      return (MobEffect)BuiltInRegistries.MOB_EFFECT.get(key);
   }

   @Deprecated
   default TagKey<Item> getItemTag(ResourceLocation key) {
      return TagKey.create(Registries.ITEM, key);
   }

   @Deprecated
   default Attribute getAttribute(ResourceLocation key) {
      return (Attribute)BuiltInRegistries.ATTRIBUTE.get(key);
   }

   void enableMilkFluid();

   Fluid getMilkFluid();

   @Deprecated
   <T> DeferredObject<T> register(Registry<T> var1, Function<ResourceLocation, T> var2, ResourceLocation var3);
}
