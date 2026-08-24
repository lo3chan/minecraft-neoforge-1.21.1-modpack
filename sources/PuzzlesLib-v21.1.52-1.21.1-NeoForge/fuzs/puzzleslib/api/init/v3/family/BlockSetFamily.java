package fuzs.puzzleslib.api.init.v3.family;

import com.google.common.collect.ImmutableMap;
import fuzs.puzzleslib.api.core.v1.context.GameplayContentContext;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.impl.init.BlockSetFamilyRegistrar;
import fuzs.puzzleslib.impl.init.boat.TypedBoatDispenseItemBehavior;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.BlockFamily.Builder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector2i;
import org.joml.Vector2ic;

public interface BlockSetFamily {
   Map<BlockSetVariant, Holder<BlockEntityType<?>>> VARIANT_BLOCK_ENTITY_TYPE = ImmutableMap.of(
      BlockSetVariant.SIGN,
      BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.SIGN),
      BlockSetVariant.WALL_SIGN,
      BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.SIGN),
      BlockSetVariant.HANGING_SIGN,
      BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.HANGING_SIGN),
      BlockSetVariant.WALL_HANGING_SIGN,
      BuiltInRegistries.BLOCK_ENTITY_TYPE.wrapAsHolder(BlockEntityType.HANGING_SIGN)
   );
   Map<BlockSetVariant, Vector2ic> VARIANT_WOODEN_FLAMMABLE = ImmutableMap.of(
      BlockSetVariant.STAIRS,
      new Vector2i(5, 20),
      BlockSetVariant.SLAB,
      new Vector2i(5, 20),
      BlockSetVariant.FENCE,
      new Vector2i(5, 20),
      BlockSetVariant.FENCE_GATE,
      new Vector2i(5, 20)
   );
   Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> VARIANT_DISPENSE_BEHAVIOR = ImmutableMap.of(
      BlockSetVariant.BOAT,
      (Function<Holder, DispenseItemBehavior>)holder -> new TypedBoatDispenseItemBehavior((EntityType<? extends Boat>)holder.value()),
      BlockSetVariant.CHEST_BOAT,
      (Function<Holder, DispenseItemBehavior>)holder -> new TypedBoatDispenseItemBehavior((EntityType<? extends Boat>)holder.value())
   );

   static BlockSetFamily.Writable base(RegistryManager registries, Reference<Block> baseBlock, String basePath) {
      BlockSetType blockSetType = new BlockSetType(registries.makeKey(basePath).toString());
      WoodType woodType = new WoodType(registries.makeKey(basePath).toString(), blockSetType);
      return new BlockSetFamilyRegistrar(registries, baseBlock, basePath, blockSetType, woodType);
   }

   static BlockSetFamily.Writable any(RegistryManager registries, Reference<Block> baseBlock, String basePath) {
      return base(registries, baseBlock, basePath).generateFor(BlockSetVariant.STAIRS).generateFor(BlockSetVariant.SLAB).generateFor(BlockSetVariant.WALL);
   }

   static BlockSetFamily.Writable metal(RegistryManager registries, Reference<Block> baseBlock, String basePath) {
      return base(registries, baseBlock, basePath)
         .generateFor(BlockSetVariant.STAIRS)
         .generateFor(BlockSetVariant.SLAB)
         .generateFor(BlockSetVariant.DOOR)
         .generateFor(BlockSetVariant.TRAPDOOR)
         .generateFor(BlockSetVariant.PRESSURE_PLATE);
   }

   static BlockSetFamily.Writable wooden(RegistryManager registries, Reference<Block> baseBlock, String basePath) {
      return base(registries, baseBlock, basePath)
         .configureBlockFamily(blockFamily -> blockFamily.recipeGroupPrefix("wooden").recipeUnlockedBy("has_planks"))
         .generateFor(BlockSetVariant.STAIRS)
         .generateFor(BlockSetVariant.SLAB)
         .generateFor(BlockSetVariant.FENCE)
         .generateFor(BlockSetVariant.FENCE_GATE)
         .generateFor(BlockSetVariant.DOOR)
         .generateFor(BlockSetVariant.TRAPDOOR)
         .generateFor(BlockSetVariant.PRESSURE_PLATE)
         .generateFor(BlockSetVariant.BUTTON)
         .generateFor(BlockSetVariant.SIGN)
         .generateFor(BlockSetVariant.HANGING_SIGN)
         .generateFor(BlockSetVariant.BOAT)
         .generateFor(BlockSetVariant.CHEST_BOAT);
   }

   Reference<Block> getBaseBlock();

   BlockSetType getBlockSetType();

   WoodType getWoodType();

   BlockFamily getBlockFamily();

   Map<BlockSetVariant, Reference<Block>> getBlockVariants();

   Map<BlockSetVariant, Reference<Item>> getItemVariants();

   Map<BlockSetVariant, Reference<EntityType<?>>> getEntityVariants();

   default Reference<Block> getBlock(BlockSetVariant variant) {
      return this.getBlockVariants().get(variant);
   }

   default Reference<Item> getItem(BlockSetVariant variant) {
      return this.getItemVariants().get(variant);
   }

   default Reference<EntityType<?>> getEntityType(BlockSetVariant variant) {
      return this.getEntityVariants().get(variant);
   }

   default void register() {
      BlockSetType.register(this.getBlockSetType());
      WoodType.register(this.getWoodType());
   }

   default void registerFor(BiConsumer<BlockEntityType<?>, Block> consumer, Map<BlockSetVariant, Holder<BlockEntityType<?>>> variants) {
      this.getBlockVariants().forEach((variant, holder) -> {
         Holder<BlockEntityType<?>> blockEntity = variants.get(variant);
         if (blockEntity != null) {
            consumer.accept((BlockEntityType<?>)blockEntity.value(), (Block)holder.value());
         }
      });
   }

   default void registerFor(GameplayContentContext context, Map<BlockSetVariant, Vector2ic> variants) {
      this.getBlockVariants().forEach((variant, holder) -> {
         Vector2ic flammable = variants.get(variant);
         if (flammable != null) {
            context.registerFlammable(holder, flammable.x(), flammable.y());
         }
      });
   }

   default void registerFor(Map<BlockSetVariant, Function<Holder<EntityType<?>>, DispenseItemBehavior>> variants) {
      this.getEntityVariants().forEach((variant, holder) -> {
         Function<Holder<EntityType<?>>, DispenseItemBehavior> behaviorFactory = variants.get(variant);
         if (behaviorFactory != null) {
            DispenserBlock.registerBehavior((ItemLike)this.getItem(variant).value(), behaviorFactory.apply(holder));
         }
      });
   }

   public interface Context extends BlockSetFamily {
      String getName(UnaryOperator<String> var1);

      default String getNameWithPrefix(String prefix) {
         return this.getName(string -> prefix + "_" + string);
      }

      default String getNameWithSuffix(String suffix) {
         return this.getName(string -> string + "_" + suffix);
      }

      RegistryManager getRegistries();

      void registerBlock(BlockSetVariant var1, Reference<Block> var2);

      void registerItem(BlockSetVariant var1, Reference<Item> var2);

      void registerEntityType(BlockSetVariant var1, Reference<EntityType<?>> var2);
   }

   public interface Writable extends BlockSetFamily {
      BlockSetFamily.Writable generateFor(BlockSetVariant var1);

      BlockSetFamily.Writable configureBlockFamily(Consumer<Builder> var1);
   }
}
