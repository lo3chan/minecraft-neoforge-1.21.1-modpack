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
import net.minecraft.core.HolderSet.ListBacked;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IngredientSorterComparators {
   private static final Set<ResourceLocation> IGNORED_TAGS = Set.of(ResourceLocation.fromNamespaceAndPath("itemfilters", "check_nbt"));
   private final IIngredientManager ingredientManager;
   private final ModNameSortingConfig modNameSortingConfig;
   private final IngredientTypeSortingConfig ingredientTypeSortingConfig;
   private final Set<String> modNames;

   public IngredientSorterComparators(
      IIngredientManager ingredientManager,
      ModNameSortingConfig modNameSortingConfig,
      IngredientTypeSortingConfig ingredientTypeSortingConfig,
      Set<String> modNames
   ) {
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
         case ALPHABETICAL -> getAlphabeticalComparator();
         case CREATIVE_MENU -> getCreativeMenuComparator();
         case INGREDIENT_TYPE -> this.getIngredientTypeComparator();
         case MOD_NAME -> this.getModNameComparator();
         case TAG -> this.getTagComparator();
         case ARMOR -> getArmorComparator();
         case MAX_DURABILITY -> getMaxDurabilityComparator();
      };
   }

   public Comparator<IListElementInfo<?>> getDefault() {
      return this.getModNameComparator().thenComparing(this.getIngredientTypeComparator()).thenComparing(getCreativeMenuComparator());
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
      Set<String> ingredientTypeStrings = ingredientTypes.stream().map(IngredientTypeSortingConfig::getIngredientTypeString).collect(Collectors.toSet());
      return this.ingredientTypeSortingConfig.getComparatorFromMappedValues(ingredientTypeStrings);
   }

   private static Comparator<IListElementInfo<?>> getMaxDurabilityComparator() {
      Comparator<IListElementInfo<?>> maxDamage = Comparator.comparing(o -> getItemStack((IListElementInfo<?>)o).getMaxDamage());
      return maxDamage.reversed();
   }

   private Comparator<IListElementInfo<?>> getTagComparator() {
      Comparator<IListElementInfo<?>> isTagged = Comparator.comparing(this::hasTag);
      Comparator<IListElementInfo<?>> tag = Comparator.comparing(this::getTagForSorting);
      return isTagged.reversed().thenComparing(tag);
   }

   private static Comparator<IListElementInfo<?>> getArmorComparator() {
      Comparator<IListElementInfo<?>> isArmorComp = Comparator.comparing(o -> isArmor(getItemStack((IListElementInfo<?>)o)));
      Comparator<IListElementInfo<?>> armorSlot = Comparator.comparing(o -> getArmorSlotIndex(getItemStack((IListElementInfo<?>)o)));
      Comparator<IListElementInfo<?>> armorDamage = Comparator.comparing(o -> getArmorDamageReduce(getItemStack((IListElementInfo<?>)o)));
      Comparator<IListElementInfo<?>> armorToughness = Comparator.comparing(o -> getArmorToughness(getItemStack((IListElementInfo<?>)o)));
      Comparator<IListElementInfo<?>> maxDamage = Comparator.comparing(o -> getArmorDurability(getItemStack((IListElementInfo<?>)o)));
      return isArmorComp.reversed()
         .thenComparing(armorSlot.reversed())
         .thenComparing(armorDamage.reversed())
         .thenComparing(armorToughness.reversed())
         .thenComparing(maxDamage.reversed());
   }

   private static boolean isArmor(ItemStack itemStack) {
      Item item = itemStack.getItem();
      return item instanceof ArmorItem;
   }

   private static int getArmorSlotIndex(ItemStack itemStack) {
      return itemStack.getItem() instanceof ArmorItem armorItem ? armorItem.getEquipmentSlot().getFilterFlag() : 0;
   }

   private static int getArmorDamageReduce(ItemStack itemStack) {
      return itemStack.getItem() instanceof ArmorItem armorItem ? armorItem.getDefense() : 0;
   }

   private static float getArmorToughness(ItemStack itemStack) {
      return itemStack.getItem() instanceof ArmorItem armorItem ? armorItem.getToughness() : 0.0F;
   }

   private static int getArmorDurability(ItemStack itemStack) {
      return isArmor(itemStack) ? itemStack.getMaxDamage() : 0;
   }

   private String getTagForSorting(IListElementInfo<?> elementInfo) {
      return elementInfo.getTagIds(this.ingredientManager)
         .max(Comparator.comparing(IngredientSorterComparators::tagCount))
         .map(ResourceLocation::getPath)
         .orElse("");
   }

   private static int tagCount(ResourceLocation tagId) {
      if (IGNORED_TAGS.contains(tagId)) {
         return 0;
      } else {
         TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
         return RegistryUtil.getRegistry(Registries.ITEM).getTag(tagKey).<Integer>map(ListBacked::size).orElse(0);
      }
   }

   private boolean hasTag(IListElementInfo<?> elementInfo) {
      return !this.getTagForSorting(elementInfo).isEmpty();
   }

   public static <V> ItemStack getItemStack(IListElementInfo<V> ingredientInfo) {
      ITypedIngredient<V> ingredient = ingredientInfo.getTypedIngredient();
      return ingredient.getIngredient() instanceof ItemStack itemStack ? itemStack : ItemStack.EMPTY;
   }
}
