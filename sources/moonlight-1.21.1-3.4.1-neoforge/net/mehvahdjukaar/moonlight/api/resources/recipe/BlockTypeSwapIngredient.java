package net.mehvahdjukaar.moonlight.api.resources.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.DataResult.Error;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.mehvahdjukaar.moonlight.api.resources.recipe.platform.BlockTypeSwapIngredientImpl;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public abstract class BlockTypeSwapIngredient<T extends BlockType> {
   protected final Ingredient inner;
   protected final T fromType;
   protected final T toType;
   protected final BlockTypeRegistry<T> registry;
   private List<ItemStack> items;
   public static final ResourceLocation ID = Moonlight.res("block_type_swap");
   public static final MapCodec<BlockTypeSwapIngredient<?>> CODEC = makeCodec(false);
   public static final MapCodec<BlockTypeSwapIngredient<?>> CODEC_NONEMPTY = makeCodec(true);
   public static final StreamCodec<RegistryFriendlyByteBuf, BlockTypeSwapIngredient<?>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, BlockTypeSwapIngredient<?>>() {
      public BlockTypeSwapIngredient<?> decode(RegistryFriendlyByteBuf object) {
         Ingredient inner = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(object);
         BlockTypeRegistry<?> reg = (BlockTypeRegistry<?>)BlockTypeRegistry.getRegistryStreamCodec().decode(object);
         StreamCodec<ByteBuf, ? extends BlockType> slowCodec = (StreamCodec<ByteBuf, ? extends BlockType>)reg.getStreamCodecExplicit();

         try {
            BlockType from = (BlockType)slowCodec.decode(object);
            BlockType to = (BlockType)slowCodec.decode(object);
            return BlockTypeSwapIngredient.create(inner, from, to, (BlockTypeRegistry<BlockType>)reg);
         } catch (DecoderException var7) {
            throw new RuntimeException("Failed to decode block type swap ingredient", var7);
         }
      }

      public void encode(RegistryFriendlyByteBuf buf, BlockTypeSwapIngredient<?> ing) {
         Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing.inner);
         BlockTypeRegistry.getRegistryStreamCodec().encode(buf, ing.registry);
         StreamCodec streamCodec = ing.registry.getStreamCodecExplicit();
         streamCodec.encode(buf, ing.fromType);
         streamCodec.encode(buf, ing.toType);
      }
   };

   protected BlockTypeSwapIngredient(Ingredient inner, T fromType, T toType, BlockTypeRegistry<T> reg) {
      Preconditions.checkNotNull(toType, "Found null to block type for BlockTypeSwapIngredient");
      Preconditions.checkNotNull(fromType, "Found null from block type for BlockTypeSwapIngredient");
      this.inner = inner;
      this.fromType = fromType;
      this.toType = toType;
      this.registry = reg;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof BlockTypeSwapIngredient<?> ing && this.inner.equals(ing.inner) && this.fromType == ing.fromType && this.toType == ing.toType;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.inner, this.fromType, this.toType);
   }

   public Ingredient getInner() {
      return this.inner;
   }

   public boolean test(ItemStack stack) {
      if (stack != null) {
         for (ItemStack itemStack : this.getMatchingStacks()) {
            if (itemStack.is(stack.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   public final List<ItemStack> convertItems(List<ItemStack> toConvert) {
      List<ItemStack> newItems = new ArrayList<>();
      boolean success = false;

      for (ItemStack it : toConvert) {
         T type = this.registry.getBlockTypeOf(it.getItem());
         if (type != this.fromType) {
            break;
         }

         Item newItem = BlockType.changeItemType(it.getItem(), this.fromType, this.toType);
         if (newItem != null) {
            newItems.add(it.transmuteCopy(newItem));
            success = true;
         }
      }

      if (!success) {
         newItems.addAll(toConvert);
      }

      return newItems;
   }

   public List<ItemStack> getMatchingStacks() {
      if (this.items == null) {
         this.items = this.convertItems(Arrays.asList(this.inner.getItems()));
      }

      return this.items;
   }

   @NotNull
   private static MapCodec<BlockTypeSwapIngredient<?>> makeCodec(final boolean nonEmpty) {
      return new MapCodec<BlockTypeSwapIngredient<?>>() {
         public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of("block_type", "from", "to", "ingredient").map(ops::createString);
         }

         public <T> DataResult<BlockTypeSwapIngredient<?>> decode(DynamicOps<T> ops, MapLike<T> input) {
            Codec<Ingredient> ingCodec = nonEmpty ? Ingredient.CODEC_NONEMPTY : Ingredient.CODEC;
            DataResult<Ingredient> ingResult = ingCodec.parse(ops, input.get(ops.createString("ingredient")));
            if (ingResult.isError()) {
               return DataResult.error(() -> "Failed to decode inner ingredient: " + ((Error)ingResult.error().get()).message() + " on " + input);
            } else {
               Ingredient inner = (Ingredient)ingResult.result().orElseThrow();
               T blockType = (T)input.get(ops.createString("block_type"));
               DataResult<BlockTypeRegistry<?>> blockTypeResult = BlockTypeRegistry.getRegistryCodec().parse(ops, blockType);
               if (blockTypeResult.isError()) {
                  return DataResult.error(
                     () -> "Failed to decode block type registry: " + blockType + " " + ((Error)blockTypeResult.error().get()).message() + " on " + input
                  );
               } else {
                  BlockTypeRegistry<?> reg = (BlockTypeRegistry<?>)blockTypeResult.result().orElseThrow();
                  T fromType = (T)ops.createString("from");
                  DataResult<?> fromResult = reg.getCodec().parse(ops, input.get(fromType));
                  if (fromResult.isError()) {
                     return DataResult.error(
                        () -> "Failed to decode 'from' block type: " + fromType + " " + ((Error)fromResult.error().get()).message() + " on " + input
                     );
                  } else {
                     BlockType from = (BlockType)fromResult.result().orElseThrow();
                     T toType = (T)ops.createString("to");
                     DataResult<?> toResult = reg.getCodec().parse(ops, input.get(toType));
                     if (toResult.isError()) {
                        return DataResult.error(
                           () -> "Failed to decode 'to' block type: " + toType + " " + ((Error)toResult.error().get()).message() + " on " + input
                        );
                     } else {
                        BlockType to = (BlockType)toResult.result().orElseThrow();
                        return DataResult.success(BlockTypeSwapIngredient.create(inner, from, to, (BlockTypeRegistry<BlockType>)reg));
                     }
                  }
               }
            }
         }

         public <T> RecordBuilder<T> encode(BlockTypeSwapIngredient<?> ingr, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            Codec<Ingredient> ingCodec = nonEmpty ? Ingredient.CODEC_NONEMPTY : Ingredient.CODEC;
            prefix.add(ops.createString("ingredient"), ingCodec.encodeStart(ops, ingr.inner));
            prefix.add(ops.createString("block_type"), BlockTypeRegistry.getRegistryCodec().encodeStart(ops, ingr.registry));
            Codec codec = ingr.registry.getCodec();
            prefix.add(ops.createString("from"), codec.encodeStart(ops, ingr.fromType));
            prefix.add(ops.createString("to"), codec.encodeStart(ops, ingr.toType));
            return prefix;
         }
      };
   }

   public static <T extends BlockType> Ingredient create(Ingredient var0, T var1, T var2) {
      return BlockTypeSwapIngredientImpl.create(var0, var1, var2);
   }

   public static <T extends BlockType> BlockTypeSwapIngredient<T> create(Ingredient var0, T var1, T var2, BlockTypeRegistry<T> var3) {
      return BlockTypeSwapIngredientImpl.create(var0, (T)var1, (T)var2, var3);
   }
}
