package org.dimdev.limlib.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab.Builder;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.commons.lang3.function.TriConsumer;
import org.dimdev.limlib.util.DataValue;
import org.jetbrains.annotations.NotNull;

public interface IRegister {
   <T> void registerCallback(Registry<T> var1, TriConsumer<Registry<T>, ResourceLocation, T> var2);

   <T, V extends T> V register(ResourceKey<Registry<T>> var1, String var2, V var3);

   <T, V extends T> V register(ResourceKey<Registry<T>> var1, ResourceLocation var2, V var3);

   <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> var1, String var2, V var3);

   <T, V extends T> Holder<T> registerHolder(ResourceKey<Registry<T>> var1, ResourceLocation var2, V var3);

   default <T extends Item> T registerItem(String id, T obj) {
      return this.register(Registries.ITEM, id, obj);
   }

   default <T extends Block> T registerBlock(String id, T obj) {
      return this.register(Registries.BLOCK, id, obj);
   }

   default <T extends BlockEntityType<?>> T registerBlockEntityType(String id, T obj) {
      return this.register(Registries.BLOCK_ENTITY_TYPE, id, obj);
   }

   default <T extends MapCodec<? extends ChunkGenerator>> T registerChunkGenerator(String id, T obj) {
      return this.register(Registries.CHUNK_GENERATOR, id, obj);
   }

   default <T extends EntityType<?>> T registerEntityType(String id, T obj) {
      return this.register(Registries.ENTITY_TYPE, id, obj);
   }

   default <T extends Fluid> T registerFluid(String id, T obj) {
      return this.register(Registries.FLUID, id, obj);
   }

   default <T extends SoundEvent> T registerSoundEvent(String id, T obj) {
      return this.register(Registries.SOUND_EVENT, id, obj);
   }

   default <T extends MenuType<?>> T registerMenu(String id, T obj) {
      return this.register(Registries.MENU, id, obj);
   }

   default <T extends RecipeSerializer<?>> T registerRecipeSerializer(String id, T obj) {
      return this.register(Registries.RECIPE_SERIALIZER, id, obj);
   }

   default <T extends RecipeType<?>> T registerRecipeType(String id, T obj) {
      return this.register(Registries.RECIPE_TYPE, id, obj);
   }

   default CreativeModeTab registerCreativeModeTab(String id, Function<Builder, Builder> consumer) {
      return this.register(Registries.CREATIVE_MODE_TAB, id, this.createTab(consumer));
   }

   CreativeModeTab createTab(Function<Builder, Builder> var1);

   default <T extends ParticleType<?>> T registerParticleType(String id, T obj) {
      return this.register(Registries.PARTICLE_TYPE, id, obj);
   }

   default <T extends Potion> T registerPotion(String id, T obj) {
      return this.register(Registries.POTION, id, obj);
   }

   default <T extends Enchantment> T registerEnchantment(String id, T obj) {
      return this.register(Registries.ENCHANTMENT, id, obj);
   }

   default <T extends ArmorMaterial> T registerArmorMaterial(String id, T obj) {
      return this.register(Registries.ARMOR_MATERIAL, id, obj);
   }

   default <T extends DamageType> T registerDamageType(String id, T obj) {
      return this.register(Registries.DAMAGE_TYPE, id, obj);
   }

   default <T extends DataComponentType<?>> T registerDataComponentType(String id, T obj) {
      return this.register(Registries.DATA_COMPONENT_TYPE, id, obj);
   }

   default ResourceLocation registerCustomStat(String id, ResourceLocation obj) {
      return this.register(Registries.CUSTOM_STAT, id, obj);
   }

   default <T extends LootTable> T registerLootTable(String id, T obj) {
      return this.register(Registries.LOOT_TABLE, id, obj);
   }

   default <T extends CriterionTrigger<?>> T registerTriggerType(String id, T obj) {
      return this.register(Registries.TRIGGER_TYPE, id, obj);
   }

   default <T extends WorldCarver<?>> T registerCarver(String id, T obj) {
      return this.register(Registries.CARVER, id, obj);
   }

   default <T extends StructureProcessor> StructureProcessorType<T> registerStructureProcessor(String id, final MapCodec<T> codec) {
      return this.register(Registries.STRUCTURE_PROCESSOR, id, new StructureProcessorType<T>() {
         @NotNull
         public MapCodec<T> codec() {
            return codec;
         }
      });
   }

   void registerRunnable(ResourceKey<? extends Registry<?>> var1, Runnable var2);

   <T> Registry<T> createRegistry(ResourceKey<Registry<T>> var1, ResourceLocation var2, boolean var3);

   default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, ResourceLocation defaultId) {
      return this.createRegistry(key, defaultId, false);
   }

   default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key, boolean sync) {
      return this.createRegistry(key, null, sync);
   }

   default <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
      return this.createRegistry(key, null, false);
   }

   <T> DataValue<T> registerDataValue(String var1, Supplier<T> var2, Codec<T> var3, StreamCodec<? super RegistryFriendlyByteBuf, T> var4);

   void registerRunDataValue(Runnable var1);

   void modifyCreativeTab(ResourceKey<CreativeModeTab> var1, Consumer<IRegister.CreativeTabEntries> var2);

   public interface CreativeTabEntries extends Output {
      void addAfter(ItemStack var1, Collection<ItemStack> var2, TabVisibility var3);

      default void addAfter(ItemStack after, Collection<ItemStack> stacks) {
         this.addAfter(after, stacks, TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void addAfter(ItemStack after, ItemStack... stacks) {
         this.addAfter(after, List.of(stacks));
      }

      default void addAfter(ItemLike after, Collection<ItemStack> stacks, TabVisibility visibility) {
         this.addAfter(stack(after), stacks, visibility);
      }

      default void addAfter(ItemLike after, Collection<ItemStack> stacks) {
         this.addAfter(stack(after), stacks);
      }

      default void addAfter(ItemLike after, ItemStack... stacks) {
         this.addAfter(stack(after), List.of(stacks));
      }

      default void addAfter(ItemLike after, ItemLike... items) {
         this.addAfter(stack(after), stacks(items));
      }

      void addBefore(ItemStack var1, Collection<ItemStack> var2, TabVisibility var3);

      default void addBefore(ItemStack before, Collection<ItemStack> stacks) {
         this.addBefore(before, stacks, TabVisibility.PARENT_AND_SEARCH_TABS);
      }

      default void addBefore(ItemStack before, ItemStack... stacks) {
         this.addBefore(before, List.of(stacks));
      }

      default void addBefore(ItemLike before, Collection<ItemStack> stacks, TabVisibility visibility) {
         this.addBefore(stack(before), stacks, visibility);
      }

      default void addBefore(ItemLike before, Collection<ItemStack> stacks) {
         this.addBefore(stack(before), stacks);
      }

      default void addBefore(ItemLike before, ItemStack... stacks) {
         this.addBefore(stack(before), List.of(stacks));
      }

      default void addBefore(ItemLike before, ItemLike... items) {
         this.addBefore(stack(before), stacks(items));
      }

      private static ItemStack stack(ItemLike item) {
         return new ItemStack(item);
      }

      private static List<ItemStack> stacks(ItemLike... items) {
         List<ItemStack> stacks = new ArrayList<>(items.length);

         for (ItemLike item : items) {
            stacks.add(stack(item));
         }

         return stacks;
      }
   }
}
