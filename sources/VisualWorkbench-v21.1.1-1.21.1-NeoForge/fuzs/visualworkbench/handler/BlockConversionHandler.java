package fuzs.visualworkbench.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.block.v1.BlockConversionHelper;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.AddBlockEntityTypeBlocksCallback;
import fuzs.puzzleslib.api.event.v1.RegistryEntryAddedCallback;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerInteractEvents.UseBlock;
import fuzs.puzzleslib.api.event.v1.server.TagsUpdatedCallback;
import fuzs.puzzleslib.api.init.v3.registry.RegistryHelper;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public class BlockConversionHandler {
   public static final Component INVALID_BLOCK_COMPONENT = Component.translatable("container.invalidBlock");
   private static final BiMap<Block, Block> BLOCK_CONVERSIONS = HashBiMap.create();
   private static final Map<BlockState, BlockState> BLOCK_STATE_CONVERSIONS_CACHE = new MapMaker().weakKeys().weakValues().makeMap();

   public static RegistryEntryAddedCallback<Block> onRegistryEntryAdded(Predicate<Block> filter, UnaryOperator<Block> factory, String modId) {
      return (registry, id, block, registrar) -> {
         if (filter.test(block)) {
            ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(modId, id.getNamespace() + "/" + id.getPath());
            registrar.accept(resourceLocation, () -> {
               Block newBlock = factory.apply(block);
               BLOCK_CONVERSIONS.put(block, newBlock);
               return newBlock;
            });
         }
      };
   }

   public static BiMap<Block, Block> getBlockConversions() {
      return Maps.unmodifiableBiMap(BLOCK_CONVERSIONS);
   }

   public static AddBlockEntityTypeBlocksCallback onAddBlockEntityTypeBlocks(Reference<? extends BlockEntityType<?>> blockEntityType) {
      return consumer -> {
         for (Entry<Block, Block> entry : BLOCK_CONVERSIONS.entrySet()) {
            consumer.accept((BlockEntityType)blockEntityType.value(), entry.getValue());
         }
      };
   }

   public static UseBlock onUseBlock(TagKey<Block> unalteredBlocks, BooleanSupplier disableVanillaBlock) {
      return (player, level, interactionHand, hitResult) -> {
         if (!disableVanillaBlock.getAsBoolean()) {
            return EventResultHolder.pass();
         } else {
            BlockState blockState = level.getBlockState(hitResult.getBlockPos());
            if (BLOCK_CONVERSIONS.containsKey(blockState.getBlock()) && !blockState.is(unalteredBlocks)) {
               player.displayClientMessage(Component.empty().append(INVALID_BLOCK_COMPONENT).withStyle(ChatFormatting.RED), true);
               return EventResultHolder.interrupt(InteractionResult.sidedSuccess(level.isClientSide));
            } else {
               return EventResultHolder.pass();
            }
         }
      };
   }

   public static TagsUpdatedCallback onTagsUpdated(TagKey<Block> unalteredBlocks, Predicate<Block> filter) {
      return (registryAccess, client) -> {
         for (Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            if (entry.getValue() instanceof BlockItem blockItem) {
               Block block = blockItem.getBlock();
               setItemForBlock(filter, blockItem, block);
               setBlockForItem(unalteredBlocks, blockItem, block);
            }
         }

         BLOCK_CONVERSIONS.forEach(BlockConversionHelper::copyBoundTags);
      };
   }

   private static void setItemForBlock(Predicate<Block> filter, BlockItem blockItem, Block block) {
      if (filter.test(block)) {
         BlockConversionHelper.setItemForBlock((Block)BLOCK_CONVERSIONS.get(block), blockItem);
      }
   }

   private static void setBlockForItem(TagKey<Block> tagKey, BlockItem blockItem, Block block) {
      Block newBlock = (Block)BLOCK_CONVERSIONS.get(block);
      Block oldBlock;
      if (newBlock != null) {
         oldBlock = block;
      } else {
         oldBlock = (Block)BLOCK_CONVERSIONS.inverse().get(block);
         if (oldBlock == null) {
            return;
         }

         newBlock = block;
      }

      if (RegistryHelper.is(tagKey, oldBlock)) {
         BlockConversionHelper.setBlockForItem(blockItem, oldBlock);
      } else {
         BlockConversionHelper.setBlockForItem(blockItem, newBlock);
      }
   }

   @Nullable
   public static BlockState convertToVanillaBlock(@Nullable BlockState blockState) {
      return applyBlockConversion(blockState, true);
   }

   @Nullable
   public static BlockState convertFromVanillaBlock(@Nullable BlockState blockState) {
      return applyBlockConversion(blockState, false);
   }

   @Nullable
   private static BlockState applyBlockConversion(@Nullable BlockState blockState, boolean inverse) {
      return blockState != null ? BLOCK_STATE_CONVERSIONS_CACHE.computeIfAbsent(blockState, applyBlockConversion(inverse)) : null;
   }

   private static UnaryOperator<BlockState> applyBlockConversion(boolean inverse) {
      return blockState -> {
         BiMap<Block, Block> blockConversions = inverse ? BLOCK_CONVERSIONS.inverse() : BLOCK_CONVERSIONS;
         if (blockState != null && blockConversions.containsKey(blockState.getBlock())) {
            Block block = (Block)blockConversions.get(blockState.getBlock());
            return copyAllProperties(blockState, block.defaultBlockState());
         } else {
            return blockState;
         }
      };
   }

   private static <T extends Comparable<T>, V extends T> BlockState copyAllProperties(BlockState oldBlockState, BlockState newBlockState) {
      for (Entry<Property<?>, Comparable<?>> entry : oldBlockState.getValues().entrySet()) {
         newBlockState = (BlockState)newBlockState.trySetValue(entry.getKey(), entry.getValue());
      }

      return newBlockState;
   }
}
