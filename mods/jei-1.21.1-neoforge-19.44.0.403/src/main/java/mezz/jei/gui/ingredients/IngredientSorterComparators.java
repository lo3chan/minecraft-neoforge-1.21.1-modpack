/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  java.lang.MatchException
 *  net.minecraft.core.HolderSet$ListBacked
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 */
package mezz.jei.gui.ingredients;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.common.config.IngredientSortStage;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.gui.config.IngredientTypeSortingConfig;
import mezz.jei.gui.config.ModNameSortingConfig;
import mezz.jei.gui.ingredients.IListElementInfo;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IngredientSorterComparators {
    private static final Set<ResourceLocation> IGNORED_TAGS = Set.of(ResourceLocation.fromNamespaceAndPath((String)"itemfilters", (String)"check_nbt"));
    private final IIngredientManager ingredientManager;
    private final ModNameSortingConfig modNameSortingConfig;
    private final IngredientTypeSortingConfig ingredientTypeSortingConfig;
    private final Set<String> modNames;

    public IngredientSorterComparators(IIngredientManager ingredientManager, ModNameSortingConfig modNameSortingConfig, IngredientTypeSortingConfig ingredientTypeSortingConfig, Set<String> modNames) {
        this.ingredientManager = ingredientManager;
        this.modNameSortingConfig = modNameSortingConfig;
        this.ingredientTypeSortingConfig = ingredientTypeSortingConfig;
        this.modNames = modNames;
    }

    public Comparator<IListElementInfo<?>> getComparator(List<IngredientSortStage> ingredientSorterStages) {
        return ingredientSorterStages.stream().map(this::getComparator).reduce(Comparator::thenComparing).orElseGet(this::getDefault);
    }

    public Comparator<IListElementInfo<?>> getComparator(IngredientSortStage ingredientSortStage) {
        return switch (ingredientSortStage) {
            default -> throw new MatchException(null, null);
            case IngredientSortStage.ALPHABETICAL -> IngredientSorterComparators.getAlphabeticalComparator();
            case IngredientSortStage.CREATIVE_MENU -> IngredientSorterComparators.getCreativeMenuComparator();
            case IngredientSortStage.INGREDIENT_TYPE -> this.getIngredientTypeComparator();
            case IngredientSortStage.MOD_NAME -> this.getModNameComparator();
            case IngredientSortStage.TAG -> this.getTagComparator();
            case IngredientSortStage.ARMOR -> IngredientSorterComparators.getArmorComparator();
            case IngredientSortStage.MAX_DURABILITY -> IngredientSorterComparators.getMaxDurabilityComparator();
        };
    }

    public Comparator<IListElementInfo<?>> getDefault() {
        return this.getModNameComparator().thenComparing(this.getIngredientTypeComparator()).thenComparing(IngredientSorterComparators.getCreativeMenuComparator());
    }

    private static Comparator<IListElementInfo<?>> getCreativeMenuComparator() {
        return Comparator.comparingInt(IListElementInfo::getCreatedIndex);
    }

    private static Comparator<IListElementInfo<?>> getAlphabeticalComparator() {
        return Comparator.comparing(i -> (String)i.getNames().getFirst());
    }

    private Comparator<IListElementInfo<?>> getModNameComparator() {
        return this.modNameSortingConfig.getComparatorFromMappedValues(this.modNames);
    }

    private Comparator<IListElementInfo<?>> getIngredientTypeComparator() {
        Collection<IIngredientType<?>> ingredientTypes = this.ingredientManager.getRegisteredIngredientTypes();
        Set ingredientTypeStrings = ingredientTypes.stream().map(IngredientTypeSortingConfig::getIngredientTypeString).collect(Collectors.toSet());
        return this.ingredientTypeSortingConfig.getComparatorFromMappedValues(ingredientTypeStrings);
    }

    private static Comparator<IListElementInfo<?>> getMaxDurabilityComparator() {
        Comparator<IListElementInfo> maxDamage = Comparator.comparing(o -> IngredientSorterComparators.getItemStack(o).getMaxDamage());
        return maxDamage.reversed();
    }

    private Comparator<IListElementInfo<?>> getTagComparator() {
        Comparator<IListElementInfo> isTagged = Comparator.comparing(this::hasTag);
        Comparator<IListElementInfo> tag = Comparator.comparing(this::getTagForSorting);
        return isTagged.reversed().thenComparing(tag);
    }

    private static Comparator<IListElementInfo<?>> getArmorComparator() {
        Comparator<IListElementInfo> isArmorComp = Comparator.comparing(o -> IngredientSorterComparators.isArmor(IngredientSorterComparators.getItemStack(o)));
        Comparator<IListElementInfo> armorSlot = Comparator.comparing(o -> IngredientSorterComparators.getArmorSlotIndex(IngredientSorterComparators.getItemStack(o)));
        Comparator<IListElementInfo> armorDamage = Comparator.comparing(o -> IngredientSorterComparators.getArmorDamageReduce(IngredientSorterComparators.getItemStack(o)));
        Comparator<IListElementInfo> armorToughness = Comparator.comparing(o -> Float.valueOf(IngredientSorterComparators.getArmorToughness(IngredientSorterComparators.getItemStack(o))));
        Comparator<IListElementInfo> maxDamage = Comparator.comparing(o -> IngredientSorterComparators.getArmorDurability(IngredientSorterComparators.getItemStack(o)));
        return isArmorComp.reversed().thenComparing(armorSlot.reversed()).thenComparing(armorDamage.reversed()).thenComparing(armorToughness.reversed()).thenComparing(maxDamage.reversed());
    }

    private static boolean isArmor(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof ArmorItem;
    }

    private static int getArmorSlotIndex(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            return armorItem.getEquipmentSlot().getFilterFlag();
        }
        return 0;
    }

    private static int getArmorDamageReduce(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            return armorItem.getDefense();
        }
        return 0;
    }

    private static float getArmorToughness(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof ArmorItem) {
            ArmorItem armorItem = (ArmorItem)item;
            return armorItem.getToughness();
        }
        return 0.0f;
    }

    private static int getArmorDurability(ItemStack itemStack) {
        if (IngredientSorterComparators.isArmor(itemStack)) {
            return itemStack.getMaxDamage();
        }
        return 0;
    }

    private String getTagForSorting(IListElementInfo<?> elementInfo) {
        return elementInfo.getTagIds(this.ingredientManager).max(Comparator.comparing(IngredientSorterComparators::tagCount)).map(ResourceLocation::getPath).orElse("");
    }

    private static int tagCount(ResourceLocation tagId) {
        if (IGNORED_TAGS.contains(tagId)) {
            return 0;
        }
        TagKey tagKey = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)tagId);
        return RegistryUtil.getRegistry(Registries.ITEM).getTag(tagKey).map(HolderSet.ListBacked::size).orElse(0);
    }

    private boolean hasTag(IListElementInfo<?> elementInfo) {
        return !this.getTagForSorting(elementInfo).isEmpty();
    }

    public static <V> ItemStack getItemStack(IListElementInfo<V> ingredientInfo) {
        ITypedIngredient<V> ingredient = ingredientInfo.getTypedIngredient();
        V v = ingredient.getIngredient();
        if (v instanceof ItemStack) {
            ItemStack itemStack = (ItemStack)v;
            return itemStack;
        }
        return ItemStack.EMPTY;
    }
}

