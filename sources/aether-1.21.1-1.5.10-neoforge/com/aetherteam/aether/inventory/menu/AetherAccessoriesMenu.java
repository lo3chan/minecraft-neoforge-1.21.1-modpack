package com.aetherteam.aether.inventory.menu;

import com.aetherteam.aether.inventory.AetherAccessorySlots;
import com.aetherteam.aether.mixin.mixins.common.accessor.AbstractContainerMenuAccessor;
import com.aetherteam.aether.mixin.mixins.common.accessor.CraftingMenuAccessor;
import com.mojang.datafixers.util.Pair;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.menu.AccessoriesSlotGenerator;
import io.wispforest.accessories.api.slot.SlotType;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;

public class AetherAccessoriesMenu extends InventoryMenu {
   private static final Map<EquipmentSlot, ResourceLocation> TEXTURE_EMPTY_SLOTS = Map.of(
      EquipmentSlot.FEET,
      InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
      EquipmentSlot.LEGS,
      InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
      EquipmentSlot.CHEST,
      InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
      EquipmentSlot.HEAD,
      InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
   );
   private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
   private final ResultContainer resultSlots = new ResultContainer();
   private final Player owner;
   public final boolean hasButton;

   public AetherAccessoriesMenu(int containerId, Inventory playerInventory) {
      this(containerId, playerInventory, true);
   }

   public AetherAccessoriesMenu(int containerId, Inventory playerInventory, boolean hasButton) {
      super(playerInventory, playerInventory.player.level().isClientSide(), playerInventory.player);
      this.owner = playerInventory.player;
      this.slots.clear();
      AbstractContainerMenuAccessor abstractContainerMenuAccessor = (AbstractContainerMenuAccessor)this;
      abstractContainerMenuAccessor.aether$setMenuType((MenuType<?>)AetherMenuTypes.ACCESSORIES.get());
      abstractContainerMenuAccessor.aether$setContainerId(containerId);
      abstractContainerMenuAccessor.aether$getRemoteSlots().clear();
      abstractContainerMenuAccessor.aether$getLastSlots().clear();
      this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 154, 28));

      for (int i = 0; i < 2; i++) {
         for (int j = 0; j < 2; j++) {
            this.addSlot(new Slot(this.craftSlots, j + i * 2, 116 + j * 18, 18 + i * 18));
         }
      }

      int x = 77;
      int y = 8;
      AccessoriesSlotGenerator.of(
            x$0 -> this.addSlot(x$0),
            x,
            y,
            this.owner,
            new SlotTypeReference[]{
               AetherAccessorySlots.getPendantSlotType(), AetherAccessorySlots.getCapeSlotType(), AetherAccessorySlots.getShieldSlotType()
            }
         )
         .column();
      AccessoriesSlotGenerator.of(
            x$0 -> this.addSlot(x$0),
            x + 18,
            y,
            this.owner,
            new SlotTypeReference[]{AetherAccessorySlots.getRingSlotType(), AetherAccessorySlots.getGlovesSlotType()}
         )
         .column();
      AccessoriesSlotGenerator.of(x$0 -> this.addSlot(x$0), x, y + 54, this.owner, new SlotTypeReference[]{AetherAccessorySlots.getAccessorySlotType()}).row();
      this.hasButton = hasButton;

      for (int k = 0; k < 4; k++) {
         EquipmentSlot equipmentslot = SLOT_IDS[k];
         ResourceLocation resourcelocation = TEXTURE_EMPTY_SLOTS.get(equipmentslot);
         this.addSlot(new ArmorSlot(playerInventory, this.owner, equipmentslot, 36 + (3 - k), 59, 8 + k * 18, resourcelocation));
      }

      for (int l = 0; l < 3; l++) {
         for (int j1 = 0; j1 < 9; j1++) {
            this.addSlot(new Slot(playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18));
         }
      }

      for (int i1 = 0; i1 < 9; i1++) {
         this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 142));
      }

      this.addSlot(new Slot(playerInventory, 40, 116, 62) {
         public void setByPlayer(ItemStack p_270969_, ItemStack p_299918_) {
            AetherAccessoriesMenu.this.owner.onEquipItem(EquipmentSlot.OFFHAND, p_299918_, p_270969_);
            super.setByPlayer(p_270969_, p_299918_);
         }

         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
         }
      });
   }

   public void slotsChanged(Container inventory) {
      CraftingMenuAccessor.callSlotChangedCraftingGrid(this, this.owner.level(), this.owner, this.craftSlots, this.resultSlots, null);
   }

   public void removed(Player player) {
      super.removed(player);
      this.resultSlots.clearContent();
      if (!player.level().isClientSide) {
         this.clearContainer(player, this.craftSlots);
      }
   }

   public boolean stillValid(Player player) {
      return true;
   }

   public ItemStack quickMoveStack(Player player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemStack1 = slot.getItem();
         itemStack = itemStack1.copy();
         EquipmentSlot equipmentSlot = player.getEquipmentSlotForItem(itemStack);
         Collection<SlotType> accessorySlots = AccessoriesAPI.getValidSlotTypes(player, itemStack);
         if (index == 0) {
            if (!this.moveItemStackTo(itemStack1, 17, 53, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemStack1, itemStack);
         } else if (index < 5) {
            if (!this.moveItemStackTo(itemStack1, 17, 53, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 17) {
            if (!this.moveItemStackTo(itemStack1, 17, 53, false)) {
               return ItemStack.EMPTY;
            }
         } else if (equipmentSlot.getType() == Type.HUMANOID_ARMOR && !((Slot)this.slots.get(16 - equipmentSlot.getIndex())).hasItem()) {
            int i = 16 - equipmentSlot.getIndex();
            if (!this.moveItemStackTo(itemStack1, i, i + 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 53 && !accessorySlots.isEmpty() && !this.getEmptyAccessorySlots(accessorySlots).isEmpty()) {
            for (int i : this.getEmptyAccessorySlots(accessorySlots)) {
               if (!this.moveItemStackTo(itemStack1, i, i + 1, false)) {
                  return ItemStack.EMPTY;
               }
            }
         } else if (equipmentSlot == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(53)).hasItem()) {
            if (!this.moveItemStackTo(itemStack1, 53, 54, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 44) {
            if (!this.moveItemStackTo(itemStack1, 44, 53, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index < 53) {
            if (!this.moveItemStackTo(itemStack1, 17, 44, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemStack1, 17, 53, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemStack1.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, itemStack1);
         if (index == 0) {
            player.drop(itemStack1, false);
         }
      }

      return itemStack;
   }

   private Set<Integer> getEmptyAccessorySlots(Collection<SlotType> slotData) {
      Set<Integer> slots = new HashSet<>();

      for (SlotType identifier : slotData) {
         String var5 = identifier.name();
         switch (var5) {
            case "aether:pendant_slot":
               slots.add(5);
               break;
            case "aether:cape_slot":
               slots.add(6);
               break;
            case "aether:shield_slot":
               slots.add(7);
               break;
            case "aether:ring_slot":
               slots.addAll(Set.of(8, 9));
               break;
            case "aether:gloves_slot":
               slots.add(10);
               break;
            case "aether:accessory_slot":
               slots.addAll(Set.of(11, 12));
         }
      }

      slots.removeIf(index -> ((Slot)this.slots.get(index)).hasItem());
      return slots;
   }

   public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
      return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
   }
}
