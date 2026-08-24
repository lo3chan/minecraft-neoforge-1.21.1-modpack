package net.mehvahdjukaar.moonlight.api.util;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.mehvahdjukaar.moonlight.api.block.ISoftFluidTankProvider;
import net.mehvahdjukaar.moonlight.api.fluids.SoftFluidTank;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacement;
import net.mehvahdjukaar.moonlight.api.item.additional_placements.AdditionalItemPlacementsAPI;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.mehvahdjukaar.moonlight.core.mixins.accessor.DispenserBlockAccessor;
import net.mehvahdjukaar.moonlight.core.mixins.accessor.DispenserBlockEntityAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import org.jetbrains.annotations.ApiStatus.Internal;

public class DispenserHelper {
   private static final Map<Item, List<DispenseItemBehavior>> MODDED_BEHAVIORS = new HashMap<>();
   private static final Map<DispenserHelper.Priority, List<Consumer<DispenserHelper.Event>>> EVENT_LISTENERS = Map.of(
      DispenserHelper.Priority.LOW, new ArrayList<>(), DispenserHelper.Priority.NORMAL, new ArrayList<>(), DispenserHelper.Priority.HIGH, new ArrayList<>()
   );
   @Deprecated(
      forRemoval = true
   )
   public static final DefaultDispenseItemBehavior PLACE_BLOCK_BEHAVIOR = new DispenserHelper.PlaceBlockDispenseBehavior();
   private static final DefaultDispenseItemBehavior SHOOT_BEHAVIOR = new DefaultDispenseItemBehavior();

   public static void addListener(Consumer<DispenserHelper.Event> listener, DispenserHelper.Priority priority) {
      EVENT_LISTENERS.get(priority).add(listener);
   }

   @Internal
   public static void reload(final RegistryAccess registryAccess, boolean isClient) {
      if (!isClient) {
         final Set<Item> failed = new HashSet<>();
         Map<Item, DispenseItemBehavior> originals = new HashMap<>();

         for (Entry<Item, List<DispenseItemBehavior>> e : MODDED_BEHAVIORS.entrySet()) {
            Item item = e.getKey();
            ReferenceOpenHashSet<DispenseItemBehavior> expected = new ReferenceOpenHashSet(e.getValue());
            DispenseItemBehavior current = (DispenseItemBehavior)DispenserBlock.DISPENSER_REGISTRY.get(item);
            if (current instanceof DispenserHelper.AdditionalDispenserBehavior behavior) {
               Set<DispenserHelper.AdditionalDispenserBehavior> visited = new ReferenceOpenHashSet();
               DispenseItemBehavior original = unwrapBehavior(behavior, visited);
               if (expected.contains(original)) {
                  expected.remove(original);
                  original = null;
               }

               if (expected.equals(visited)) {
                  originals.put(item, original);
               } else {
                  Moonlight.LOGGER.warn("Failed to unwrap original behavior for item: {}, {}, {}", item, current, expected);
                  failed.add(item);
               }
            } else if (expected.size() == 1 && expected.stream().findAny().get() == current) {
               originals.put(item, null);
            } else {
               failed.add(item);
               Moonlight.LOGGER.error("Failed to restore original behavior for item: {}, {}", item, current);
            }
         }

         for (Entry<Item, DispenseItemBehavior> ex : originals.entrySet()) {
            Item item = ex.getKey();
            DispenseItemBehavior behavior = ex.getValue();
            if (behavior != null) {
               DispenserBlock.registerBehavior(item, behavior);
            } else {
               DispenserBlock.DISPENSER_REGISTRY.remove(item);
            }
         }

         MODDED_BEHAVIORS.clear();
         DispenserHelper.Event event = new DispenserHelper.Event() {
            @Override
            public void register(Item i, DispenseItemBehavior behavior) {
               if (!failed.contains(i)) {
                  DispenserHelper.MODDED_BEHAVIORS.computeIfAbsent(i, k -> new ArrayList<>()).add(behavior);
                  DispenserBlock.registerBehavior(i, behavior);
               }
            }

            @Override
            public RegistryAccess getRegistryAccess() {
               return registryAccess;
            }
         };
         EVENT_LISTENERS.get(DispenserHelper.Priority.LOW).forEach(l -> l.accept(event));
         EVENT_LISTENERS.get(DispenserHelper.Priority.NORMAL).forEach(l -> l.accept(event));
         EVENT_LISTENERS.get(DispenserHelper.Priority.HIGH).forEach(l -> l.accept(event));
      }
   }

   private static DispenseItemBehavior unwrapBehavior(
      DispenserHelper.AdditionalDispenserBehavior behavior, Set<DispenserHelper.AdditionalDispenserBehavior> visited
   ) {
      visited.add(behavior);
      DispenseItemBehavior inner = behavior.fallback;
      return inner instanceof DispenserHelper.AdditionalDispenserBehavior ab ? unwrapBehavior(ab, visited) : inner;
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerCustomBehavior(DispenserHelper.AdditionalDispenserBehavior behavior) {
      DispenserBlock.registerBehavior(behavior.item, behavior);
   }

   @Deprecated(
      forRemoval = true
   )
   public static void registerPlaceBlockBehavior(ItemLike block) {
      DispenserBlock.registerBehavior(block, PLACE_BLOCK_BEHAVIOR);
   }

   public static class AddItemToInventoryBehavior extends DispenserHelper.AdditionalDispenserBehavior {
      public AddItemToInventoryBehavior(Item item) {
         super(item);
      }

      @Override
      protected InteractionResultHolder<ItemStack> customBehavior(BlockSource source, ItemStack stack) {
         ServerLevel world = source.level();
         BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         if (world.getBlockEntity(blockpos) instanceof WorldlyContainer tile) {
            if (tile.canPlaceItem(0, stack)) {
               if (tile.isEmpty()) {
                  tile.setItem(0, stack.split(1));
               } else {
                  tile.getItem(0).grow(1);
                  stack.shrink(1);
               }

               return InteractionResultHolder.success(stack);
            } else {
               return InteractionResultHolder.fail(stack);
            }
         } else {
            return InteractionResultHolder.pass(stack);
         }
      }
   }

   public abstract static class AdditionalDispenserBehavior implements DispenseItemBehavior {
      private final DispenseItemBehavior fallback;
      private final Item item;

      protected AdditionalDispenserBehavior(Item item) {
         this.item = item;
         this.fallback = DispenserBlockAccessor.getDispenserRegistry().get(item);
      }

      public final ItemStack dispense(BlockSource source, ItemStack stack) {
         try {
            InteractionResultHolder<ItemStack> result = this.customBehavior(source, stack);
            InteractionResult type = result.getResult();
            if (type != InteractionResult.PASS) {
               boolean success = type.consumesAction();
               this.playSound(source, success);
               this.playAnimation(source, (Direction)source.state().getValue(DispenserBlock.FACING));
               if (success) {
                  ItemStack resultStack = (ItemStack)result.getObject();
                  if (resultStack.getItem() == stack.getItem()) {
                     return resultStack;
                  }

                  return this.fillItemInDispenser(source, stack, (ItemStack)result.getObject());
               }
            }
         } catch (Exception var7) {
         }

         return this.fallback.dispense(source, stack);
      }

      protected abstract InteractionResultHolder<ItemStack> customBehavior(BlockSource var1, ItemStack var2);

      protected void playSound(BlockSource source, boolean success) {
         source.level().levelEvent(success ? 1000 : 1001, source.pos(), 0);
      }

      protected void playAnimation(BlockSource source, Direction direction) {
         source.level().levelEvent(2000, source.pos(), direction.get3DDataValue());
      }

      private ItemStack fillItemInDispenser(BlockSource source, ItemStack empty, ItemStack filled) {
         empty.shrink(1);
         if (empty.isEmpty()) {
            return filled.copy();
         } else {
            if (!this.mergeDispenserItem(source.blockEntity(), filled)) {
               DispenserHelper.SHOOT_BEHAVIOR.dispense(source, filled.copy());
            }

            return empty;
         }
      }

      private boolean mergeDispenserItem(DispenserBlockEntity te, ItemStack filled) {
         NonNullList<ItemStack> stacks = ((DispenserBlockEntityAccessor)te).getItems();

         for (int i = 0; i < te.getContainerSize(); i++) {
            ItemStack s = (ItemStack)stacks.get(i);
            if (s.isEmpty() || s.getItem() == filled.getItem() && s.getMaxStackSize() > s.getCount()) {
               filled.grow(s.getCount());
               te.setItem(i, filled);
               return true;
            }
         }

         return false;
      }
   }

   public interface Event {
      void register(Item var1, DispenseItemBehavior var2);

      default void register(DispenserHelper.AdditionalDispenserBehavior behavior) {
         this.register(behavior.item, behavior);
      }

      default void registerPlaceBlock(ItemLike i) {
         this.register(i.asItem(), new DispenserHelper.PlaceBlockBehavior(i.asItem()));
      }

      RegistryAccess getRegistryAccess();
   }

   public static class FillFluidHolderBehavior extends DispenserHelper.AdditionalDispenserBehavior {
      public FillFluidHolderBehavior(Item item) {
         super(item);
      }

      @Override
      protected InteractionResultHolder<ItemStack> customBehavior(BlockSource source, ItemStack stack) {
         BlockPos blockpos = source.pos().relative((Direction)source.state().getValue(DispenserBlock.FACING));
         BlockEntity te = source.level().getBlockEntity(blockpos);
         if (te instanceof ISoftFluidTankProvider tile) {
            if (tile.canInteractWithSoftFluidTank()) {
               SoftFluidTank tank = tile.getSoftFluidTank();
               if (!tank.isFull()) {
                  ItemStack returnStack = tank.interactWithItem(stack, source.level(), blockpos, false);
                  if (returnStack != null) {
                     te.setChanged();
                     return InteractionResultHolder.success(returnStack);
                  }
               }
            }

            return InteractionResultHolder.fail(stack);
         } else {
            return InteractionResultHolder.pass(stack);
         }
      }
   }

   public static class PlaceBlockBehavior extends DispenserHelper.AdditionalDispenserBehavior {
      public PlaceBlockBehavior(Item item) {
         super(item);
      }

      @Override
      protected InteractionResultHolder<ItemStack> customBehavior(BlockSource source, ItemStack stack) {
         Item item = stack.getItem();
         Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
         BlockPos blockpos = source.pos().relative(direction);
         DirectionalPlaceContext context = new DirectionalPlaceContext(source.level(), blockpos, direction, stack, direction);
         InteractionResult result = null;
         AdditionalItemPlacement placement = AdditionalItemPlacementsAPI.getBehavior(item);
         if (placement != null) {
            result = placement.overridePlace(context);
         }

         if ((result == null || !result.consumesAction()) && item instanceof BlockItem bi) {
            result = bi.place(context);
         }

         return result != null && result.consumesAction() ? new InteractionResultHolder(result, stack) : InteractionResultHolder.pass(stack);
      }
   }

   @Deprecated(
      forRemoval = true
   )
   public static class PlaceBlockDispenseBehavior extends OptionalDispenseItemBehavior {
      public ItemStack execute(BlockSource source, ItemStack stack) {
         this.setSuccess(false);
         if (stack.getItem() instanceof BlockItem bi) {
            Direction direction = (Direction)source.state().getValue(DispenserBlock.FACING);
            BlockPos blockpos = source.pos().relative(direction);
            InteractionResult result = bi.place(new DirectionalPlaceContext(source.level(), blockpos, direction, stack, direction));
            this.setSuccess(result.consumesAction());
         }

         return stack;
      }
   }

   public static enum Priority {
      LOW,
      NORMAL,
      HIGH;
   }
}
