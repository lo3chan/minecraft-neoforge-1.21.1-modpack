/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
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
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class ItemStackHelper
implements IIngredientHelper<ItemStack> {
    private final StackHelper stackHelper;
    private final IColorHelper colorHelper;
    private final TagKey<Item> itemHiddenFromRecipeViewers;
    private final TagKey<Block> blockHiddenFromRecipeViewers;

    public ItemStackHelper(StackHelper stackHelper, IColorHelper colorHelper) {
        this.stackHelper = stackHelper;
        this.colorHelper = colorHelper;
        this.itemHiddenFromRecipeViewers = TagKey.create((ResourceKey)Registries.ITEM, (ResourceLocation)Tags.HIDDEN_FROM_RECIPE_VIEWERS);
        this.blockHiddenFromRecipeViewers = TagKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)Tags.HIDDEN_FROM_RECIPE_VIEWERS);
    }

    @Override
    public IIngredientType<ItemStack> getIngredientType() {
        return VanillaTypes.ITEM_STACK;
    }

    @Override
    public String getDisplayName(ItemStack ingredient) {
        Component displayNameTextComponent = ingredient.getHoverName();
        String displayName = displayNameTextComponent.getString();
        ErrorUtil.checkNotNull(displayName, "itemStack.getDisplayName()");
        return displayName;
    }

    @Override
    public String getUniqueId(ItemStack ingredient, UidContext context) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        return this.stackHelper.getUniqueIdentifierForStack(ingredient, context);
    }

    @Override
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

    @Override
    public Object getGroupingUid(ItemStack ingredient) {
        return ingredient.getItem();
    }

    @Override
    public boolean hasSubtypes(ItemStack ingredient) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        return this.stackHelper.hasSubtypes(ingredient);
    }

    @Override
    public String getWildcardId(ItemStack ingredient) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        return StackHelper.getRegistryNameForStack(ingredient);
    }

    @Override
    public String getDisplayModId(ItemStack ingredient) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        IPlatformItemStackHelper itemStackHelper = Services.PLATFORM.getItemStackHelper();
        return itemStackHelper.getCreatorModId(ingredient).or(() -> ItemStackHelper.getNamespace(ingredient)).orElseThrow(() -> {
            String stackInfo = this.getErrorInfo(ingredient);
            return new IllegalStateException("null registryName for: " + stackInfo);
        });
    }

    private static Optional<String> getNamespace(ItemStack ingredient) {
        ResourceLocation key = RegistryUtil.getRegistry(Registries.ITEM).getKey((Object)ingredient.getItem());
        return Optional.ofNullable(key).map(ResourceLocation::getNamespace);
    }

    @Override
    public long getAmount(ItemStack ingredient) {
        return ingredient.getCount();
    }

    @Override
    public ItemStack copyWithAmount(ItemStack ingredient, long amount) {
        ItemStack copy = ingredient.copy();
        int intAmount = Math.toIntExact(amount);
        copy.setCount(intAmount);
        return copy;
    }

    @Override
    public Iterable<Integer> getColors(ItemStack ingredient) {
        return this.colorHelper.getColors(ingredient, 2);
    }

    @Override
    public ResourceLocation getResourceLocation(ItemStack ingredient) {
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        Item item = ingredient.getItem();
        ResourceLocation key = RegistryUtil.getRegistry(Registries.ITEM).getKey((Object)item);
        if (key == null) {
            String stackInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("item has no key in the Item registry: " + stackInfo);
        }
        return key;
    }

    @Override
    public ItemStack getCheatItemStack(ItemStack ingredient) {
        return ingredient;
    }

    @Override
    public ItemStack copyIngredient(ItemStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public ItemStack normalizeIngredient(ItemStack ingredient) {
        if (ingredient.getCount() == 1) {
            return ingredient;
        }
        int originalCount = ingredient.getCount();
        ingredient.setCount(1);
        ItemStack copy = ingredient.copy();
        ingredient.setCount(originalCount);
        return copy;
    }

    @Override
    public boolean isValidIngredient(ItemStack ingredient) {
        return !ingredient.isEmpty();
    }

    @Override
    public boolean isIngredientOnServer(ItemStack ingredient) {
        Item item = ingredient.getItem();
        Registry registry = RegistryUtil.getRegistry(Registries.ITEM);
        return registry.getKey((Object)item) != null;
    }

    @Override
    public Stream<ResourceLocation> getTagStream(ItemStack ingredient) {
        Stream<ResourceLocation> itemTagStream = ingredient.getTags().map(TagKey::location);
        Item item = ingredient.getItem();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
            IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
            if (clientConfig.lookupBlockTagsEnabled().getValue().booleanValue()) {
                Stream<ResourceLocation> blockTagStream = blockItem.getBlock().defaultBlockState().getTags().map(TagKey::location);
                return Streams.concat((Stream[])new Stream[]{itemTagStream, blockTagStream});
            }
        }
        return itemTagStream;
    }

    @Override
    public boolean isHiddenFromRecipeViewersByTags(ItemStack ingredient) {
        return this.isHiddenFromRecipeViewersByTags((Holder<Item>)ingredient.getItemHolder());
    }

    @Override
    public boolean isHiddenFromRecipeViewersByTags(ITypedIngredient<ItemStack> ingredient) {
        Item item = ingredient.getBaseIngredient(VanillaTypes.ITEM_STACK);
        Holder.Reference itemHolder = item.builtInRegistryHolder();
        return this.isHiddenFromRecipeViewersByTags((Holder<Item>)itemHolder);
    }

    @Override
    private boolean isHiddenFromRecipeViewersByTags(Holder<Item> itemHolder) {
        if (itemHolder.is(this.itemHiddenFromRecipeViewers)) {
            return true;
        }
        Object object = itemHolder.value();
        if (object instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)object;
            IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
            IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
            if (clientConfig.lookupBlockTagsEnabled().getValue().booleanValue()) {
                Block block = blockItem.getBlock();
                Holder.Reference blockHolder = block.builtInRegistryHolder();
                return blockHolder.is(this.blockHiddenFromRecipeViewers);
            }
        }
        return false;
    }

    @Override
    public String getErrorInfo(@Nullable ItemStack ingredient) {
        return ErrorUtil.getItemStackInfo(ingredient);
    }

    @Override
    public Optional<TagKey<?>> getTagKeyEquivalent(Collection<ItemStack> ingredients) {
        Registry itemRegistry = RegistryUtil.getRegistry(Registries.ITEM);
        return TagUtil.getTagEquivalent(ingredients, ItemStack::getItem, () -> itemRegistry.getTags());
    }
}

