package fuzs.puzzleslib.api.container.v1;

import java.util.function.IntFunction;
import java.util.function.ObjIntConsumer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

public final class ContainerSerializationHelper extends ContainerHelper {
   public static final String TAG_ITEMS = "Items";
   public static final String TAG_SLOT = "Slot";

   private ContainerSerializationHelper() {
   }

   public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, Provider lookupProvider) {
      return ContainerHelper.saveAllItems(tag, items, lookupProvider);
   }

   public static CompoundTag saveAllItems(CompoundTag tag, Container container, Provider lookupProvider) {
      return saveAllItems(tag, container, true, lookupProvider);
   }

   public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, Provider lookupProvider) {
      return saveAllItems(tagKey, tag, items, true, lookupProvider);
   }

   public static CompoundTag saveAllItems(CompoundTag tag, Container container, boolean saveEmpty, Provider lookupProvider) {
      return saveAllItems("Items", tag, container.getContainerSize(), container::getItem, saveEmpty, lookupProvider);
   }

   public static CompoundTag saveAllItems(CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty, Provider lookupProvider) {
      return ContainerHelper.saveAllItems(tag, items, saveEmpty, lookupProvider);
   }

   public static CompoundTag saveAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, boolean saveEmpty, Provider lookupProvider) {
      return saveAllItems(tagKey, tag, items.size(), items::get, saveEmpty, lookupProvider);
   }

   public static CompoundTag saveAllItems(
      String tagKey, CompoundTag tag, int size, IntFunction<ItemStack> itemGetter, boolean saveEmpty, Provider lookupProvider
   ) {
      ListTag listTag = createTag(size, itemGetter, lookupProvider);
      if (!listTag.isEmpty() || saveEmpty) {
         tag.put(tagKey, listTag);
      }

      return tag;
   }

   public static ListTag createTag(int size, IntFunction<ItemStack> itemGetter, Provider lookupProvider) {
      ListTag listTag = new ListTag();

      for (int i = 0; i < size; i++) {
         ItemStack itemStack = itemGetter.apply(i);
         if (!itemStack.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putByte("Slot", (byte)i);
            listTag.add(itemStack.save(lookupProvider, compoundTag));
         }
      }

      return listTag;
   }

   public static void loadAllItems(CompoundTag tag, NonNullList<ItemStack> items, Provider lookupProvider) {
      ContainerHelper.loadAllItems(tag, items, lookupProvider);
   }

   public static void loadAllItems(CompoundTag tag, Container container, Provider lookupProvider) {
      loadAllItems("Items", tag, container.getContainerSize(), (stack, value) -> container.setItem(value, stack), lookupProvider);
   }

   public static void loadAllItems(String tagKey, CompoundTag tag, NonNullList<ItemStack> items, Provider lookupProvider) {
      loadAllItems(tagKey, tag, items.size(), (stack, value) -> items.set(value, stack), lookupProvider);
   }

   public static void loadAllItems(String tagKey, CompoundTag tag, int size, ObjIntConsumer<ItemStack> itemSetter, Provider lookupProvider) {
      ListTag listTag = tag.getList(tagKey, 10);
      fromTag(listTag, size, itemSetter, lookupProvider);
   }

   public static void fromTag(ListTag listTag, int size, ObjIntConsumer<ItemStack> itemSetter, Provider lookupProvider) {
      for (int i = 0; i < listTag.size(); i++) {
         CompoundTag compoundTag = listTag.getCompound(i);
         int slot = compoundTag.getByte("Slot") & 255;
         if (slot < size) {
            itemSetter.accept(ItemStack.parse(lookupProvider, compoundTag).orElse(ItemStack.EMPTY), slot);
         }
      }
   }
}
