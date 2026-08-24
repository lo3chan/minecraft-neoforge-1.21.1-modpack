package net.blay09.mods.inventoryessentials.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.blay09.mods.inventoryessentials.InventoryEssentialsConfig;
import net.blay09.mods.inventoryessentials.InventoryEssentialsIgnores;
import net.blay09.mods.inventoryessentials.mixin.AbstractContainerScreenAccessor;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyConflictContext;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.blay09.mods.kuma.api.ScreenInputEvent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

public class ModKeyMappings {
   public static ManagedKeyMapping keySingleTransfer;
   public static ManagedKeyMapping keyBulkTransfer;
   public static ManagedKeyMapping keyBulkTransferSingle;
   public static ManagedKeyMapping keyBulkTransferAll;
   public static ManagedKeyMapping keyBulkDrop;
   public static ManagedKeyMapping keyScreenBulkDrop;
   public static ManagedKeyMapping keyDragTransfer;
   public static ManagedKeyMapping keySortInventory;
   public static ManagedKeyMapping keyRestockContainer;
   public static ManagedKeyMapping keyRestockInventory;
   public static ManagedKeyMapping keyDumpToContainer;

   public static void initialize() {
      keySingleTransfer = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "single_transfer"))
         .withDefault(InputBinding.mouse(0, KeyModifiers.of(new KeyModifier[]{KeyModifier.CONTROL})))
         .handleScreenInput(
            event -> handleSlotInput(
               event,
               () -> InventoryEssentialsConfig.getActive().enableSingleTransfer,
               (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).singleTransfer(screen, slot)
            )
         )
         .build();
      keyBulkTransfer = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "bulk_transfer"))
         .withDefault(InputBinding.mouse(0, KeyModifiers.of(new KeyModifier[]{KeyModifier.SHIFT, KeyModifier.CONTROL})))
         .handleScreenInput(
            event -> handleSlotInput(
               event,
               () -> InventoryEssentialsConfig.getActive().enableBulkTransfer,
               (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).bulkTransferByType(screen, slot)
            )
         )
         .build();
      keyBulkTransferSingle = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "bulk_transfer_single"))
         .withDefault(InputBinding.mouse(1, KeyModifiers.ofCustom(new Key[]{InputConstants.getKey(32, -1)})))
         .handleScreenInput(
            event -> handleSlotInput(
               event,
               () -> InventoryEssentialsConfig.getActive().enableBulkTransferSingle,
               (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).bulkTransferSingle(screen, slot)
            )
         )
         .build();
      keyBulkTransferAll = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "bulk_transfer_all"))
         .withDefault(InputBinding.mouse(0, KeyModifiers.ofCustom(new Key[]{InputConstants.getKey(32, -1)})))
         .handleScreenInput(
            event -> handleSlotInput(
               event,
               () -> InventoryEssentialsConfig.getActive().enableBulkTransferAll,
               (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).bulkTransferAll(screen, slot)
            )
         )
         .build();
      keyBulkDrop = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "bulk_drop"))
         .withDefault(InputBinding.key(81, KeyModifiers.of(new KeyModifier[]{KeyModifier.SHIFT, KeyModifier.CONTROL})))
         .handleScreenInput(
            event -> handleSlotInput(
               event,
               () -> InventoryEssentialsConfig.getActive().enableBulkDrop,
               (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).dropByType(screen, slot)
            )
         )
         .build();
      keyScreenBulkDrop = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "screen_bulk_drop"))
         .withDefault(InputBinding.mouse(0, KeyModifiers.of(new KeyModifier[]{KeyModifier.SHIFT})))
         .handleScreenInput(
            event -> {
               if (!InventoryEssentialsConfig.getActive().enableBulkDrop) {
                  return false;
               } else if (InventoryEssentialsIgnores.shouldIgnoreScreen(event.screen())) {
                  return false;
               } else if (!(event.screen() instanceof AbstractContainerScreen<?> containerScreen)) {
                  return false;
               } else {
                  AbstractContainerScreenAccessor var5 = (AbstractContainerScreenAccessor)containerScreen;
                  int button = keyScreenBulkDrop.getBinding().key().getValue();
                  boolean clickedOutside = var5.callHasClickedOutside(event.mouseX(), event.mouseY(), var5.getLeftPos(), var5.getTopPos(), button);
                  return clickedOutside
                     && InventoryEssentialsClient.getInventoryControls(containerScreen).dropByType(containerScreen, containerScreen.getMenu().getCarried());
               }
            }
         )
         .build();
      keyDragTransfer = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "drag_transfer"))
         .withDefault(InputBinding.key(340))
         .withContext(KeyConflictContext.SCREEN)
         .forceVirtual()
         .build();
      keySortInventory = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "sort_inventory"))
         .withDefault(InputBinding.mouse(2))
         .handleScreenInput(
            event -> handleSlotInput(event, () -> true, (screen, slot) -> InventoryEssentialsClient.getInventoryControls(screen).sort(screen, slot))
         )
         .build();
      keyRestockContainer = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "restock_container"))
         .withContext(KeyConflictContext.SCREEN)
         .handleScreenInput(
            event -> {
               if (InventoryEssentialsIgnores.shouldIgnoreScreen(event.screen())) {
                  return false;
               } else {
                  return event.screen() instanceof AbstractContainerScreen<?> containerScreen
                     ? InventoryEssentialsClient.getInventoryControls(containerScreen).restockContainer(containerScreen)
                     : false;
               }
            }
         )
         .build();
      keyRestockInventory = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "restock_inventory"))
         .withContext(KeyConflictContext.SCREEN)
         .handleScreenInput(
            event -> {
               if (InventoryEssentialsIgnores.shouldIgnoreScreen(event.screen())) {
                  return false;
               } else {
                  return event.screen() instanceof AbstractContainerScreen<?> containerScreen
                     ? InventoryEssentialsClient.getInventoryControls(containerScreen).restockInventory(containerScreen)
                     : false;
               }
            }
         )
         .build();
      keyDumpToContainer = Kuma.createKeyMapping(ResourceLocation.fromNamespaceAndPath("inventoryessentials", "dump_to_container"))
         .withContext(KeyConflictContext.SCREEN)
         .handleScreenInput(
            event -> {
               if (InventoryEssentialsIgnores.shouldIgnoreScreen(event.screen())) {
                  return false;
               } else {
                  return event.screen() instanceof AbstractContainerScreen<?> containerScreen
                     ? InventoryEssentialsClient.getInventoryControls(containerScreen).dumpToContainer(containerScreen)
                     : false;
               }
            }
         )
         .build();
   }

   private static boolean handleSlotInput(ScreenInputEvent event, Supplier<Boolean> predicate, BiFunction<AbstractContainerScreen<?>, Slot, Boolean> handler) {
      if (!predicate.get()) {
         return false;
      } else if (InventoryEssentialsIgnores.shouldIgnoreScreen(event.screen())) {
         return false;
      } else if (event.screen() instanceof AbstractContainerScreen<?> containerScreen) {
         Slot var5 = ((AbstractContainerScreenAccessor)containerScreen).getHoveredSlot();
         return InventoryEssentialsIgnores.shouldIgnoreSlot(containerScreen, var5) ? false : handler.apply(containerScreen, var5);
      } else {
         return false;
      }
   }
}
