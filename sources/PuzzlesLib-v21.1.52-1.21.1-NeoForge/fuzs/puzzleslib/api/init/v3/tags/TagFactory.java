package fuzs.puzzleslib.api.init.v3.tags;

import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

@FunctionalInterface
public interface TagFactory {
   TagFactory MINECRAFT = make("minecraft");
   TagFactory COMMON = make("c");
   TagFactory FABRIC = make("fabric");
   TagFactory NEOFORGE = make("neoforge");
   TagFactory FORGE = make("forge");
   TagFactory CURIOS = make("curios");
   TagFactory TRINKETS = make("trinkets");

   static TagFactory make(String modId) {
      return () -> modId;
   }

   String modId();

   default <T> TagKey<T> registerTagKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
      return TagKey.create(registryKey, ResourceLocationHelper.fromNamespaceAndPath(this.modId(), path));
   }

   default TagKey<Block> registerBlockTag(String path) {
      return this.registerTagKey(Registries.BLOCK, path);
   }

   default TagKey<Item> registerItemTag(String path) {
      return this.registerTagKey(Registries.ITEM, path);
   }

   default TagKey<Fluid> registerFluidTag(String path) {
      return this.registerTagKey(Registries.FLUID, path);
   }

   default TagKey<EntityType<?>> registerEntityTypeTag(String path) {
      return this.registerTagKey(Registries.ENTITY_TYPE, path);
   }

   default TagKey<Enchantment> registerEnchantmentTag(String path) {
      return this.registerTagKey(Registries.ENCHANTMENT, path);
   }

   default TagKey<Biome> registerBiomeTag(String path) {
      return this.registerTagKey(Registries.BIOME, path);
   }

   default TagKey<GameEvent> registerGameEventTag(String path) {
      return this.registerTagKey(Registries.GAME_EVENT, path);
   }

   default TagKey<DamageType> registerDamageTypeTag(String path) {
      return this.registerTagKey(Registries.DAMAGE_TYPE, path);
   }
}
