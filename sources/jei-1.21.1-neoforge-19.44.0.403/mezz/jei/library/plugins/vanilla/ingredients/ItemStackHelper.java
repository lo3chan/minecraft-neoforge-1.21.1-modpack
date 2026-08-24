package mezz.jei.library.plugins.vanilla.ingredients;

import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.constants.Tags;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.platform.IPlatformItemStackHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.common.util.StackHelper;
import mezz.jei.common.util.TagUtil;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ItemStackHelper implements IIngredientHelper<ItemStack> {
   private final StackHelper stackHelper;
   private final IColorHelper colorHelper;
   private final TagKey<Item> itemHiddenFromRecipeViewers;
   private final TagKey<Block> blockHiddenFromRecipeViewers;

   public ItemStackHelper(StackHelper stackHelper, IColorHelper colorHelper) {
      this.stackHelper = stackHelper;
      this.colorHelper = colorHelper;
      this.itemHiddenFromRecipeViewers = TagKey.create(Registries.ITEM, Tags.HIDDEN_FROM_RECIPE_VIEWERS);
      this.blockHiddenFromRecipeViewers = TagKey.create(Registries.BLOCK, Tags.HIDDEN_FROM_RECIPE_VIEWERS);
   }

   @Override
   public IIngredientType<ItemStack> getIngredientType() {
      return VanillaTypes.ITEM_STACK;
   }

   public String getDisplayName(ItemStack ingredient) {
      Component displayNameTextComponent = ingredient.getHoverName();
      String displayName = displayNameTextComponent.getString();
      ErrorUtil.checkNotNull(displayName, "itemStack.getDisplayName()");
      return displayName;
   }

   public String getUniqueId(ItemStack ingredient, UidContext context) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      return this.stackHelper.getUniqueIdentifierForStack(ingredient, context);
   }

   public Object getUid(ItemStack ingredient, UidContext context) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      ErrorUtil.checkNotNull(context, "type");
      return this.stackHelper.getUidForStack(ingredient, context);
   }

   @Override
   public Object getUid(ITypedIngredient<ItemStack> typedIngredient, UidContext context) {
      ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
      ErrorUtil.checkNotNull(context, "type");
      return this.stackHelper.getUidForStack(typedIngredient, context);
   }

   @Override
   public Object getGroupingUid(ITypedIngredient<ItemStack> typedIngredient) {
      return typedIngredient.getBaseIngredient(VanillaTypes.ITEM_STACK);
   }

   public Object getGroupingUid(ItemStack ingredient) {
      return ingredient.getItem();
   }

   public boolean hasSubtypes(ItemStack ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      return this.stackHelper.hasSubtypes(ingredient);
   }

   public String getWildcardId(ItemStack ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      return StackHelper.getRegistryNameForStack(ingredient);
   }

   public String getDisplayModId(ItemStack ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      IPlatformItemStackHelper itemStackHelper = Services.PLATFORM.getItemStackHelper();
      return itemStackHelper.getCreatorModId(ingredient).or(() -> getNamespace(ingredient)).orElseThrow(() -> {
         String stackInfo = this.getErrorInfo(ingredient);
         return new IllegalStateException("null registryName for: " + stackInfo);
      });
   }

   private static Optional<String> getNamespace(ItemStack ingredient) {
      ResourceLocation key = RegistryUtil.getRegistry(Registries.ITEM).getKey(ingredient.getItem());
      return Optional.ofNullable(key).map(ResourceLocation::getNamespace);
   }

   public long getAmount(ItemStack ingredient) {
      return ingredient.getCount();
   }

   public ItemStack copyWithAmount(ItemStack ingredient, long amount) {
      ItemStack copy = ingredient.copy();
      int intAmount = Math.toIntExact(amount);
      copy.setCount(intAmount);
      return copy;
   }

   public Iterable<Integer> getColors(ItemStack ingredient) {
      return this.colorHelper.getColors(ingredient, 2);
   }

   public ResourceLocation getResourceLocation(ItemStack ingredient) {
      ErrorUtil.checkNotNull(ingredient, "ingredient");
      Item item = ingredient.getItem();
      ResourceLocation key = RegistryUtil.getRegistry(Registries.ITEM).getKey(item);
      if (key == null) {
         String stackInfo = this.getErrorInfo(ingredient);
         throw new IllegalStateException("item has no key in the Item registry: " + stackInfo);
      } else {
         return key;
      }
   }

   public ItemStack getCheatItemStack(ItemStack ingredient) {
      return ingredient;
   }

   public ItemStack copyIngredient(ItemStack ingredient) {
      return ingredient.copy();
   }

   public ItemStack normalizeIngredient(ItemStack ingredient) {
      if (ingredient.getCount() == 1) {
         return ingredient;
      } else {
         int originalCount = ingredient.getCount();
         ingredient.setCount(1);
         ItemStack copy = ingredient.copy();
         ingredient.setCount(originalCount);
         return copy;
      }
   }

   public boolean isValidIngredient(ItemStack ingredient) {
      return !ingredient.isEmpty();
   }

   public boolean isIngredientOnServer(ItemStack ingredient) {
      Item item = ingredient.getItem();
      Registry<Item> registry = RegistryUtil.getRegistry(Registries.ITEM);
      return registry.getKey(item) != null;
   }

   public Stream<ResourceLocation> getTagStream(ItemStack ingredient) {
      Stream<ResourceLocation> itemTagStream = ingredient.getTags().map(TagKey::location);
      if (ingredient.getItem() instanceof BlockItem blockItem) {
         IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
         IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
         if (clientConfig.lookupBlockTagsEnabled().getValue()) {
            Stream<ResourceLocation> blockTagStream = blockItem.getBlock().defaultBlockState().getTags().map(TagKey::location);
            return Streams.concat(new Stream[]{itemTagStream, blockTagStream});
         }
      }

      return itemTagStream;
   }

   public boolean isHiddenFromRecipeViewersByTags(ItemStack ingredient) {
      return this.isHiddenFromRecipeViewersByTags(ingredient.getItemHolder());
   }

   @Override
   public boolean isHiddenFromRecipeViewersByTags(ITypedIngredient<ItemStack> ingredient) {
      Item item = ingredient.getBaseIngredient(VanillaTypes.ITEM_STACK);
      Reference<Item> itemHolder = item.builtInRegistryHolder();
      return this.isHiddenFromRecipeViewersByTags((Holder<Item>)itemHolder);
   }

   private boolean isHiddenFromRecipeViewersByTags(Holder<Item> itemHolder) {
      if (itemHolder.is(this.itemHiddenFromRecipeViewers)) {
         return true;
      } else {
         if (itemHolder.value() instanceof BlockItem blockItem) {
            IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
            IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
            if (clientConfig.lookupBlockTagsEnabled().getValue()) {
               Block block = blockItem.getBlock();
               Reference<Block> blockHolder = block.builtInRegistryHolder();
               return blockHolder.is(this.blockHiddenFromRecipeViewers);
            }
         }

         return false;
      }
   }

   public String getErrorInfo(@Nullable ItemStack ingredient) {
      return ErrorUtil.getItemStackInfo(ingredient);
   }

   @Override
   public Optional<TagKey<?>> getTagKeyEquivalent(Collection<ItemStack> ingredients) {
      Registry<Item> itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
      return TagUtil.getTagEquivalent(ingredients, ItemStack::getItem, itemRegistry::getTags);
   }
}
