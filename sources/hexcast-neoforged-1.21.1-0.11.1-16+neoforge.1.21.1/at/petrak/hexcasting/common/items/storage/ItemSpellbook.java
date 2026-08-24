package at.petrak.hexcasting.common.items.storage;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.api.utils.NBTHelper;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemSpellbook extends Item implements IotaHolderItem, VariantItem {
   public static String TAG_SELECTED_PAGE = "page_idx";
   public static String TAG_PAGES = "pages";
   public static String TAG_PAGE_NAMES = "page_names";
   public static String TAG_SEALED = "sealed_pages";
   public static final String TAG_VARIANT = "variant";
   public static final int MAX_PAGES = 64;

   public ItemSpellbook(Properties properties) {
      super(properties);
   }

   public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag isAdvanced) {
      boolean sealed = isSealed(stack);
      boolean empty = false;
      if (NBTHelper.hasNumber(stack, TAG_SELECTED_PAGE)) {
         int pageIdx = NBTHelper.getInt(stack, TAG_SELECTED_PAGE);
         int highest = highestPage(stack);
         if (highest != 0) {
            if (sealed) {
               tooltip.add(
                  Component.translatable(
                        "hexcasting.tooltip.spellbook.page.sealed",
                        new Object[]{
                           Component.literal(String.valueOf(pageIdx)).withStyle(ChatFormatting.WHITE),
                           Component.literal(String.valueOf(highest)).withStyle(ChatFormatting.WHITE),
                           Component.translatable("hexcasting.tooltip.spellbook.sealed").withStyle(ChatFormatting.GOLD)
                        }
                     )
                     .withStyle(ChatFormatting.GRAY)
               );
            } else {
               tooltip.add(
                  Component.translatable(
                        "hexcasting.tooltip.spellbook.page",
                        new Object[]{
                           Component.literal(String.valueOf(pageIdx)).withStyle(ChatFormatting.WHITE),
                           Component.literal(String.valueOf(highest)).withStyle(ChatFormatting.WHITE)
                        }
                     )
                     .withStyle(ChatFormatting.GRAY)
               );
            }
         } else {
            empty = true;
         }
      } else {
         empty = true;
      }

      if (empty) {
         boolean overridden = NBTHelper.hasString(stack, "VisualOverride");
         if (sealed) {
            if (overridden) {
               tooltip.add(Component.translatable("hexcasting.tooltip.spellbook.sealed").withStyle(ChatFormatting.GOLD));
            } else {
               tooltip.add(
                  Component.translatable(
                        "hexcasting.tooltip.spellbook.empty.sealed",
                        new Object[]{Component.translatable("hexcasting.tooltip.spellbook.sealed").withStyle(ChatFormatting.GOLD)}
                     )
                     .withStyle(ChatFormatting.GRAY)
               );
            }
         } else if (!overridden) {
            tooltip.add(Component.translatable("hexcasting.tooltip.spellbook.empty").withStyle(ChatFormatting.GRAY));
         }
      }

      IotaHolderItem.appendHoverText(this, stack, tooltip, isAdvanced);
      super.appendHoverText(stack, level, tooltip, isAdvanced);
   }

   public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      int index = getPage(stack, 0);
      NBTHelper.putInt(stack, TAG_SELECTED_PAGE, index);
      int shiftedIdx = Math.max(1, index);
      String nameKey = String.valueOf(shiftedIdx);
      CompoundTag names = NBTHelper.getCompound(stack, TAG_PAGE_NAMES);
      if (names == null) {
         names = new CompoundTag();
      }

      if (stack.has(DataComponents.CUSTOM_NAME)) {
         names.putString(nameKey, Serializer.toJson(stack.getHoverName(), RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
      } else {
         names.remove(nameKey);
      }

      if (names.isEmpty()) {
         NBTHelper.remove(stack, TAG_PAGE_NAMES);
      } else {
         NBTHelper.putCompound(stack, TAG_PAGE_NAMES, names);
      }
   }

   public static boolean arePagesEmpty(ItemStack stack) {
      CompoundTag tag = NBTHelper.getCompound(stack, TAG_PAGES);
      return tag == null || tag.isEmpty();
   }

   @Nullable
   @Override
   public CompoundTag readIotaTag(ItemStack stack) {
      int idx = getPage(stack, 1);
      String key = String.valueOf(idx);
      CompoundTag tag = NBTHelper.getCompound(stack, TAG_PAGES);
      return tag != null && tag.contains(key, 10) ? tag.getCompound(key) : null;
   }

   @Nullable
   @Override
   public Iota emptyIota(ItemStack stack) {
      return new NullIota();
   }

   @Override
   public boolean writeable(ItemStack stack) {
      return !isSealed(stack);
   }

   @Override
   public boolean canWrite(ItemStack stack, Iota datum) {
      return datum == null || !isSealed(stack);
   }

   @Override
   public void writeDatum(ItemStack stack, Iota datum) {
      if (datum == null || !isSealed(stack)) {
         int idx = getPage(stack, 1);
         String key = String.valueOf(idx);
         CompoundTag pages = NBTHelper.getCompound(stack, TAG_PAGES);
         if (pages == null) {
            pages = new CompoundTag();
         }

         if (datum == null) {
            pages.remove(key);
            CompoundTag sealed = NBTHelper.getCompound(stack, TAG_SEALED);
            if (sealed != null) {
               sealed.remove(key);
               if (sealed.isEmpty()) {
                  NBTHelper.remove(stack, TAG_SEALED);
               } else {
                  NBTHelper.putCompound(stack, TAG_SEALED, sealed);
               }
            }
         } else {
            pages.put(key, IotaType.serialize(datum));
            NBTHelper.putInt(stack, TAG_SELECTED_PAGE, idx);
         }

         if (pages.isEmpty()) {
            NBTHelper.remove(stack, TAG_PAGES);
         } else {
            NBTHelper.putCompound(stack, TAG_PAGES, pages);
         }
      }
   }

   public static int getPage(ItemStack stack, int ifEmpty) {
      if (arePagesEmpty(stack)) {
         return ifEmpty;
      } else if (NBTHelper.hasNumber(stack, TAG_SELECTED_PAGE)) {
         int index = NBTHelper.getInt(stack, TAG_SELECTED_PAGE);
         if (index == 0) {
            index = 1;
         }

         return index;
      } else {
         return 1;
      }
   }

   public static void setSealed(ItemStack stack, boolean sealed) {
      int index = getPage(stack, 1);
      String nameKey = String.valueOf(index);
      CompoundTag names = NBTHelper.getOrCreateCompound(stack, TAG_SEALED);
      if (!sealed) {
         names.remove(nameKey);
      } else {
         names.putBoolean(nameKey, true);
      }

      if (names.isEmpty()) {
         NBTHelper.remove(stack, TAG_SEALED);
      } else {
         NBTHelper.putCompound(stack, TAG_SEALED, names);
      }
   }

   public static boolean isSealed(ItemStack stack) {
      int index = getPage(stack, 1);
      String nameKey = String.valueOf(index);
      CompoundTag names = NBTHelper.getCompound(stack, TAG_SEALED);
      return NBTHelper.getBoolean(names, nameKey);
   }

   public static int highestPage(ItemStack stack) {
      CompoundTag tag = NBTHelper.getCompound(stack, TAG_PAGES);
      return tag == null ? 0 : tag.getAllKeys().stream().flatMap(s -> {
         try {
            return Stream.of(Integer.parseInt(s));
         } catch (NumberFormatException var2) {
            return Stream.empty();
         }
      }).max(Integer::compare).orElse(0);
   }

   public static int rotatePageIdx(ItemStack stack, boolean increase) {
      int idx = getPage(stack, 0);
      if (idx != 0) {
         idx += increase ? 1 : -1;
         idx = Math.max(1, idx);
      }

      idx = Mth.clamp(idx, 0, 64);
      NBTHelper.putInt(stack, TAG_SELECTED_PAGE, idx);
      CompoundTag names = NBTHelper.getCompound(stack, TAG_PAGE_NAMES);
      int shiftedIdx = Math.max(1, idx);
      String nameKey = String.valueOf(shiftedIdx);
      String name = NBTHelper.getString(names, nameKey);
      if (name != null) {
         stack.set(DataComponents.CUSTOM_NAME, Serializer.fromJson(name, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)));
      } else {
         stack.remove(DataComponents.CUSTOM_NAME);
      }

      return idx;
   }

   @Override
   public int numVariants() {
      return 8;
   }

   @Override
   public void setVariant(ItemStack stack, int variant) {
      if (!isSealed(stack)) {
         NBTHelper.putInt(stack, "variant", this.clampVariant(variant));
      }
   }
}
