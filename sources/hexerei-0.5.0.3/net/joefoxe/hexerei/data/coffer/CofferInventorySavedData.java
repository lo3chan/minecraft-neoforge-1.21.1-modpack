package net.joefoxe.hexerei.data.coffer;

import java.time.DateTimeException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.joefoxe.hexerei.Hexerei;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedData.Factory;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class CofferInventorySavedData extends SavedData {
   protected static final String DATA_NAME = "hexerei_coffer_inventories";
   private final Map<UUID, NonNullList<ItemStack>> inventories = new HashMap<>();
   private final Map<UUID, ZonedDateTime> lastModified = new HashMap<>();
   private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

   public Map<UUID, ZonedDateTime> getLastModified() {
      return new HashMap<>(this.lastModified);
   }

   public NonNullList<ItemStack> getInventory(UUID cofferId) {
      if (cofferId == null) {
         return NonNullList.withSize(36, ItemStack.EMPTY);
      } else if (this.inventories.containsKey(cofferId)) {
         return this.inventories.get(cofferId);
      } else {
         this.inventories.put(cofferId, NonNullList.withSize(36, ItemStack.EMPTY));
         return this.inventories.get(cofferId);
      }
   }

   public void setLastModified(UUID cofferId) {
      this.lastModified.put(cofferId, ZonedDateTime.now(ZoneOffset.UTC));
   }

   private static CofferInventorySavedData create(CompoundTag tag, Provider registries) {
      CofferInventorySavedData data = new CofferInventorySavedData();
      data.load(tag, registries);
      return data;
   }

   public void load(CompoundTag pCompoundTag, Provider registries) {
      this.inventories.clear();
      this.lastModified.clear();
      if (pCompoundTag.contains("Inventories", 10)) {
         CompoundTag inventoriesTag = pCompoundTag.getCompound("Inventories");

         for (String cofferIdStr : inventoriesTag.getAllKeys()) {
            UUID cofferId = UUID.fromString(cofferIdStr);
            CompoundTag cofferTag = inventoriesTag.getCompound(cofferIdStr);
            if (cofferTag.contains("LastModified", 8)) {
               try {
                  ZonedDateTime modifiedTime = ZonedDateTime.parse(cofferTag.getString("LastModified"), FORMATTER);
                  this.lastModified.put(cofferId, modifiedTime);
               } catch (DateTimeException var13) {
                  Hexerei.LOGGER.error("Failed to parse last modified time for coffer {}", cofferId, var13);
                  this.lastModified.put(cofferId, ZonedDateTime.now(ZoneOffset.UTC));
               }
            }

            NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
            if (cofferTag.contains("Items", 9)) {
               ListTag itemsTag = cofferTag.getList("Items", 10);

               for (int i = 0; i < itemsTag.size(); i++) {
                  CompoundTag slotTag = itemsTag.getCompound(i);
                  int slot = slotTag.getInt("Slot");
                  if (slot >= 0 && slot < items.size()) {
                     items.set(slot, ItemStack.parse(registries, slotTag.getCompound("Item")).orElse(ItemStack.EMPTY));
                  }
               }
            }

            this.inventories.put(cofferId, items);
         }
      }
   }

   public CompoundTag save(CompoundTag pCompoundTag, Provider registries) {
      CompoundTag inventoriesTag = new CompoundTag();

      for (Entry<UUID, NonNullList<ItemStack>> entry : this.inventories.entrySet()) {
         UUID cofferId = entry.getKey();
         if (cofferId != null) {
            NonNullList<ItemStack> items = entry.getValue();
            CompoundTag cofferTag = new CompoundTag();
            ListTag itemsTag = new ListTag();
            if (this.lastModified.containsKey(cofferId)) {
               cofferTag.putString("LastModified", this.lastModified.get(cofferId).format(FORMATTER));
            }

            for (int slot = 0; slot < items.size(); slot++) {
               ItemStack stack = (ItemStack)items.get(slot);
               if (!stack.isEmpty()) {
                  CompoundTag slotTag = new CompoundTag();
                  slotTag.putInt("Slot", slot);
                  Tag itemTag = stack.save(registries, slotTag);
                  slotTag.put("Item", itemTag);
                  itemsTag.add(slotTag);
               }
            }

            cofferTag.put("Items", itemsTag);
            inventoriesTag.put(cofferId.toString(), cofferTag);
         }
      }

      pCompoundTag.put("Inventories", inventoriesTag);
      return pCompoundTag;
   }

   public static Factory<CofferInventorySavedData> factory() {
      return new Factory(CofferInventorySavedData::new, CofferInventorySavedData::create, null);
   }

   public static CofferInventorySavedData get(ServerLevel world) {
      return (CofferInventorySavedData)world.getServer().overworld().getDataStorage().computeIfAbsent(factory(), "hexerei_coffer_inventories");
   }

   public static CofferInventorySavedData get() {
      return (CofferInventorySavedData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(factory(), "hexerei_coffer_inventories");
   }
}
