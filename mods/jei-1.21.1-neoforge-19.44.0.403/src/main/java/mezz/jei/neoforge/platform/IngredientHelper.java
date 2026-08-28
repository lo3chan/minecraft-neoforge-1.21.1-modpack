/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.DyeItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.alchemy.PotionBrewing
 *  net.minecraft.world.item.alchemy.PotionBrewing$Mix
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  net.minecraft.world.item.crafting.Ingredient$TagValue
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.ComposterBlock
 *  net.neoforged.neoforge.common.extensions.IItemExtension
 *  org.jetbrains.annotations.Nullable
 */
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.jetbrains.annotations.Nullable;

public class IngredientHelper
implements IPlatformIngredientHelper {
    @Nullable
    private static List<Holder<Item>> itemsWithCustomEnchantmentSupport;

    @Override
    public Ingredient createShulkerDyeIngredient(DyeColor color) {
        Stream<Ingredient.TagValue> colorIngredientStream;
        DyeItem dye = DyeItem.byColor((DyeColor)color);
        TagKey colorTag = color.getTag();
        Ingredient.TagValue colorList = new Ingredient.TagValue(colorTag);
        Registry itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
        Iterable coloredItems = itemRegistry.getTagOrEmpty(colorTag);
        boolean contains = StreamSupport.stream(coloredItems.spliterator(), false).anyMatch(h -> h.value() == dye);
        if (!contains) {
            ItemStack dyeStack = new ItemStack((ItemLike)dye);
            Ingredient.ItemValue dyeList = new Ingredient.ItemValue(dyeStack);
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
        return Stream.concat(potionBrewing.containerMixes.stream(), potionBrewing.potionMixes.stream()).map(PotionBrewing.Mix::ingredient);
    }

    @Override
    public float getCompostValue(ItemStack itemStack) {
        return ComposterBlock.getValue((ItemStack)itemStack);
    }

    @Override
    public HolderSet<Item> getSupportedItems(Holder<Enchantment> enchantment) {
        List<Holder<Item>> customSupportedItems;
        HolderSet<Item> supportedItems = ((Enchantment)enchantment.value()).getSupportedItems();
        if (IngredientHelper.needsFiltering(supportedItems, enchantment)) {
            supportedItems = IngredientHelper.filterSupportedItems(supportedItems, enchantment);
        }
        if ((customSupportedItems = IngredientHelper.getCustomSupportedItems(supportedItems, enchantment)).isEmpty()) {
            return supportedItems;
        }
        ArrayList<Holder<Item>> result = new ArrayList<Holder<Item>>();
        supportedItems.forEach(result::add);
        result.addAll(customSupportedItems);
        return HolderSet.direct(result);
    }

    private static boolean needsFiltering(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
        for (Holder itemHolder : supportedItems) {
            if (IngredientHelper.supportsEnchantment((Holder<Item>)itemHolder, enchantment)) continue;
            return true;
        }
        return false;
    }

    private static HolderSet<Item> filterSupportedItems(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
        ArrayList<Holder> filteredSupportedItems = new ArrayList<Holder>();
        for (Holder supportedItem : supportedItems) {
            if (!IngredientHelper.supportsEnchantment((Holder<Item>)supportedItem, enchantment)) continue;
            filteredSupportedItems.add(supportedItem);
        }
        return HolderSet.direct(filteredSupportedItems);
    }

    private static List<Holder<Item>> getCustomSupportedItems(HolderSet<Item> supportedItems, Holder<Enchantment> enchantment) {
        ArrayList<Holder<Item>> customSupportedItems = new ArrayList<Holder<Item>>();
        for (Holder<Item> itemHolder : IngredientHelper.getItemsWithCustomEnchantmentSupport()) {
            if (supportedItems.contains(itemHolder) || !IngredientHelper.supportsEnchantment(itemHolder, enchantment)) continue;
            customSupportedItems.add(itemHolder);
        }
        return customSupportedItems;
    }

    private static List<Holder<Item>> getItemsWithCustomEnchantmentSupport() {
        if (itemsWithCustomEnchantmentSupport == null) {
            itemsWithCustomEnchantmentSupport = RegistryUtil.getRegistry(Registries.ITEM).holders().filter(IngredientHelper::hasCustomEnchantmentSupport).map(itemHolder -> itemHolder).toList();
        }
        return itemsWithCustomEnchantmentSupport;
    }

    private static boolean hasCustomEnchantmentSupport(Holder<Item> itemHolder) {
        try {
            Method method = ((Item)itemHolder.value()).getClass().getMethod("supportsEnchantment", ItemStack.class, Holder.class);
            Class<?> declaringClass = method.getDeclaringClass();
            return declaringClass != Item.class && declaringClass != IItemExtension.class;
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unable to find supportsEnchantment method on Item", e);
        }
    }

    private static boolean supportsEnchantment(Holder<Item> itemHolder, Holder<Enchantment> enchantment) {
        ItemStack itemStack = ((Item)itemHolder.value()).getDefaultInstance();
        return itemStack.supportsEnchantment(enchantment);
    }
}

