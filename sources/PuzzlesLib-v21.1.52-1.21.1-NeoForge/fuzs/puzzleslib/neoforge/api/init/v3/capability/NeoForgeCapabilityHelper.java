package fuzs.puzzleslib.neoforge.api.init.v3.capability;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.neoforge.api.core.v1.NeoForgeModContainerHelper;
import java.util.Objects;
import java.util.function.BiConsumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

public final class NeoForgeCapabilityHelper {
   private NeoForgeCapabilityHelper() {
   }

   @SafeVarargs
   public static void registerChestBlock(Holder<? extends ChestBlock>... chestBlocks) {
      register(
         (registerCapabilitiesEvent, chestBlock) -> registerCapabilitiesEvent.registerBlock(ItemHandler.BLOCK, (level, pos, state, blockEntity, side) -> {
            Container container = ChestBlock.getContainer((ChestBlock)state.getBlock(), state, level, pos, true);
            Objects.requireNonNull(container, "chest container is null");
            return new InvWrapper(container);
         }, new Block[]{chestBlock}), chestBlocks
      );
   }

   @SafeVarargs
   public static <T extends BlockEntity & Container> void registerBlockEntityContainer(Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes) {
      registerBlockEntity((blockEntity, direction) -> new InvWrapper((Container)blockEntity), blockEntityTypes);
   }

   @SafeVarargs
   public static <T extends BlockEntity & WorldlyContainer> void registerWorldlyBlockEntityContainer(
      Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes
   ) {
      registerBlockEntity(
         (blockEntity, direction) -> (IItemHandler)(direction != null
            ? new SidedInvWrapper((WorldlyContainer)blockEntity, direction)
            : new InvWrapper((Container)blockEntity)),
         blockEntityTypes
      );
   }

   @SafeVarargs
   public static <T extends BlockEntity & WorldlyContainer> void registerRestrictedBlockEntityContainer(
      Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes
   ) {
      registerBlockEntity((blockEntity, direction) -> new SidedInvWrapper((WorldlyContainer)blockEntity, null), blockEntityTypes);
   }

   @SafeVarargs
   public static <T extends BlockEntity> void registerBlockEntity(
      ICapabilityProvider<T, Direction, IItemHandler> capabilityProvider, Holder<? extends BlockEntityType<? extends T>>... blockEntityTypes
   ) {
      register((evt, blockEntityType) -> evt.registerBlockEntity(ItemHandler.BLOCK, blockEntityType, capabilityProvider), blockEntityTypes);
   }

   @SafeVarargs
   public static <T extends Entity & Container> void registerEntityContainer(Holder<? extends EntityType<? extends T>>... entityTypes) {
      register((evt, entityType) -> {
         evt.registerEntity(ItemHandler.ENTITY, entityType, (entity, aVoid) -> new InvWrapper((Container)entity));
         evt.registerEntity(ItemHandler.ENTITY_AUTOMATION, entityType, (entity, direction) -> new InvWrapper((Container)entity));
      }, entityTypes);
   }

   @SafeVarargs
   public static void registerItemContainer(ICapabilityProvider<ItemStack, Void, IItemHandler> capabilityProvider, Holder<? extends Item>... items) {
      register((evt, item) -> evt.registerItem(ItemHandler.ITEM, capabilityProvider, new ItemLike[]{item}), items);
   }

   @SafeVarargs
   public static <T> void register(BiConsumer<RegisterCapabilitiesEvent, T> consumer, Holder<? extends T>... types) {
      Preconditions.checkState(types.length > 0, "capability provider types is empty");
      ResourceLocation resourceLocation = ((ResourceKey)types[0].unwrapKey().orElseThrow()).location();
      NeoForgeModContainerHelper.getOptionalModEventBus(resourceLocation.getNamespace()).ifPresent(eventBus -> eventBus.addListener(evt -> {
         for (Holder<? extends T> holder : types) {
            consumer.accept(evt, (T)holder.value());
         }
      }));
   }
}
