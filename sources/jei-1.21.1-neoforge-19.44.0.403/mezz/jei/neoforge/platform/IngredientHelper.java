package mezz.jei.neoforge.platform;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import mezz.jei.common.platform.IPlatformIngredientHelper;
import mezz.jei.common.util.RegistryUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionBrewing.Mix;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;

public class IngredientHelper implements IPlatformIngredientHelper {
   @Nullable
   private static List<Holder<Item>> itemsWithCustomEnchantmentSupport;

   @Override
   public Ingredient createShulkerDyeIngredient(DyeColor color) {
      DyeItem dye = DyeItem.byColor(color);
      TagKey<Item> colorTag = color.getTag();
      Value colorList = new TagValue(colorTag);
      Registry<Item> itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
      Iterable<Holder<Item>> coloredItems = itemRegistry.getTagOrEmpty(colorTag);
      boolean contains = StreamSupport.stream(coloredItems.spliterator(), false).anyMatch(h -> h.value() == dye);
      Stream<Value> colorIngredientStream;
      if (!contains) {
         ItemStack dyeStack = new ItemStack(dye);
         Value dyeList = new ItemValue(dyeStack);
         colorIngredientStream = Stream.of(dyeList, colorList);
      } else {
         colorIngredientStream = Stream.of(colorList);
      }

      return Ingredient.fromValues(colorIngredientStream);
   }

   @Override
   public List<Ingredient> getPotionContainers(PotionBrewing potionBrewing) {
      return potionBrewing.containers;
   }

   @Override
   public Stream<Ingredient> getPotionIngredients(PotionBrewing potionBrewing) {
      return Stream.concat(potionBrewing.containerMixes.stream(), potionBrewing.potionMixes.stream()).map(Mix::ingredient);
   }

   @Override
   public float getCompostValue(ItemStack itemStack) {
      return ComposterBlock.getValue(itemStack);
   }

   @Override
   public HolderSet<Item> getSupportedItems(Holder<Enchantment> enchantment) {
      HolderSet<Item> supportedItems = ((Enchantment)enchantment.value()).getSupportedItems();
      if (needsFiltering(supportedItems, enchantment)) {
         supportedItems = filterSupportedItems(supportedItems, enchantment);
      }

      List<Holder<Item>> customSupportedItems = getCustomSupportedItems(supportedItems, enchantment);
      if (customSupportedItems.isEmpty()) {
         return supportedItems;
      } else {
         List<Holder<Item>> result = new ArrayList<>();
         supportedItems.forEach(result::add);
         result.addAll(customSupportedItems);
         return HolderSet.direct(result);
      }
   }

   private static boolean needsFiltering(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
      for (Holder<Item> itemHolder : supportedItems) {
         if (!supportsEnchantment(itemHolder, enchantment)) {
            return true;
         }
      }

      return false;
   }

   private static HolderSet<Item> filterSupportedItems(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
      List<Holder<Item>> filteredSupportedItems = new ArrayList<>();

      for (Holder<Item> supportedItem : supportedItems) {
         if (supportsEnchantment(supportedItem, enchantment)) {
            filteredSupportedItems.add(supportedItem);
         }
      }

      return HolderSet.direct(filteredSupportedItems);
   }

   private static List<Holder<Item>> getCustomSupportedItems(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
      List<Holder<Item>> customSupportedItems = new ArrayList<>();

      for (Holder<Item> itemHolder : getItemsWithCustomEnchantmentSupport()) {
         if (!supportedItems.contains(itemHolder) && supportsEnchantment(itemHolder, enchantment)) {
            customSupportedItems.add(itemHolder);
         }
      }

      return customSupportedItems;
   }

   private static List<Holder<Item>> getItemsWithCustomEnchantmentSupport() {
      if (itemsWithCustomEnchantmentSupport == null) {
         itemsWithCustomEnchantmentSupport = RegistryUtil.getRegistry(Registries.ITEM)
            .holders()
            .filter(IngredientHelper::hasCustomEnchantmentSupport)
            .map(itemHolder -> itemHolder)
            .toList();
      }

      return itemsWithCustomEnchantmentSupport;
   }

   private static boolean hasCustomEnchantmentSupport(Holder<Item> itemHolder) {
      try {
         Method method = ((Item)itemHolder.value()).getClass().getMethod("supportsEnchantment", ItemStack.class, Holder.class);
         Class<?> declaringClass = method.getDeclaringClass();
         return declaringClass != Item.class && declaringClass != IItemExtension.class;
      } catch (NoSuchMethodException var3) {
         throw new IllegalStateException("Unable to find supportsEnchantment method on Item", var3);
      }
   }

   private static boolean supportsEnchantment(Holder<Item> itemHolder, Holder<Enchantment> enchantment) {
      ItemStack itemStack = ((Item)itemHolder.value()).getDefaultInstance();
      return itemStack.supportsEnchantment(enchantment);
   }
}
