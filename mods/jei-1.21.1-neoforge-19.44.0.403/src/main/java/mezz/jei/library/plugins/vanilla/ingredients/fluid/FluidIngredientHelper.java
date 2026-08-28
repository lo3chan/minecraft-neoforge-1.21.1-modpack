/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.MoreObjects$ToStringHelper
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.component.DataComponentPatch
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.plugins.vanilla.ingredients.fluid;

import com.google.common.base.MoreObjects;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import mezz.jei.api.constants.Tags;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.util.RegistryUtil;
import mezz.jei.common.util.TagUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class FluidIngredientHelper<T>
implements IIngredientHelper<T> {
    private final ISubtypeManager subtypeManager;
    private final IColorHelper colorHelper;
    private final IPlatformFluidHelperInternal<T> platformFluidHelper;
    private final Registry<Fluid> registry;
    private final IIngredientTypeWithSubtypes<Fluid, T> fluidType;
    private final TagKey<Fluid> hiddenFromRecipeViewers;

    public FluidIngredientHelper(ISubtypeManager subtypeManager, IColorHelper colorHelper, IPlatformFluidHelperInternal<T> platformFluidHelper) {
        this.subtypeManager = subtypeManager;
        this.colorHelper = colorHelper;
        this.platformFluidHelper = platformFluidHelper;
        this.registry = RegistryUtil.getRegistry(Registries.FLUID);
        this.fluidType = platformFluidHelper.getFluidIngredientType();
        this.hiddenFromRecipeViewers = TagKey.create((ResourceKey)Registries.FLUID, (ResourceLocation)Tags.HIDDEN_FROM_RECIPE_VIEWERS);
    }

    public IIngredientTypeWithSubtypes<Fluid, T> getIngredientType() {
        return this.fluidType;
    }

    @Override
    public String getDisplayName(T ingredient) {
        Component displayName = this.platformFluidHelper.getDisplayName(ingredient);
        return displayName.getString();
    }

    @Override
    public String getUniqueId(T ingredient, UidContext context) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        ResourceLocation registryName = this.getRegistryName(ingredient, fluid);
        StringBuilder result = new StringBuilder().append("fluid:").append(registryName);
        String subtypeInfo = this.subtypeManager.getSubtypeInfo(this.fluidType, ingredient, context);
        if (!subtypeInfo.isEmpty()) {
            result.append(":");
            result.append(subtypeInfo);
        }
        return result.toString();
    }

    @Override
    public Object getGroupingUid(T ingredient) {
        return this.fluidType.getBase(ingredient);
    }

    @Override
    public String getWildcardId(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        ResourceLocation registryName = this.getRegistryName(ingredient, fluid);
        return "fluid:" + String.valueOf(registryName);
    }

    @Override
    public Object getUid(T ingredient, UidContext context) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        Object subtypeData = this.subtypeManager.getSubtypeData(this.fluidType, ingredient, context);
        if (subtypeData != null) {
            return List.of(fluid, subtypeData);
        }
        return fluid;
    }

    @Override
    public long getAmount(T ingredient) {
        return this.platformFluidHelper.getAmount(ingredient);
    }

    @Override
    public T copyWithAmount(T ingredient, long amount) {
        return this.platformFluidHelper.copyWithAmount(ingredient, amount);
    }

    @Override
    public Iterable<Integer> getColors(T ingredient) {
        return this.platformFluidHelper.getStillFluidSprite(ingredient).map(fluidStillSprite -> {
            int renderColor = this.platformFluidHelper.getColorTint(ingredient);
            return this.colorHelper.getColors((TextureAtlasSprite)fluidStillSprite, renderColor, 1);
        }).orElseGet(List::of);
    }

    @Override
    public ResourceLocation getResourceLocation(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        return this.getRegistryName(ingredient, fluid);
    }

    private ResourceLocation getRegistryName(T ingredient, Fluid fluid) {
        ResourceLocation key = this.registry.getKey((Object)fluid);
        if (key == null) {
            String ingredientInfo = this.getErrorInfo(ingredient);
            throw new IllegalStateException("null registry name for: " + ingredientInfo);
        }
        return key;
    }

    @Override
    public ItemStack getCheatItemStack(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        Item filledBucket = fluid.getBucket();
        return filledBucket.getDefaultInstance();
    }

    @Override
    public T copyIngredient(T ingredient) {
        return this.platformFluidHelper.copy(ingredient);
    }

    @Override
    public T normalizeIngredient(T ingredient) {
        return this.platformFluidHelper.normalize(ingredient);
    }

    @Override
    public Stream<ResourceLocation> getTagStream(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        return this.registry.getResourceKey((Object)fluid).flatMap(arg_0 -> this.registry.getHolder(arg_0)).map(Holder::tags).orElse(Stream.of(new TagKey[0])).map(TagKey::location);
    }

    @Override
    public boolean isHiddenFromRecipeViewersByTags(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        return this.registry.getResourceKey((Object)fluid).flatMap(arg_0 -> this.registry.getHolder(arg_0)).map(holder -> holder.is(this.hiddenFromRecipeViewers)).orElse(false);
    }

    @Override
    public String getErrorInfo(@Nullable T ingredient) {
        if (ingredient == null) {
            return "null";
        }
        MoreObjects.ToStringHelper toStringHelper = MoreObjects.toStringHelper(ingredient.getClass());
        Fluid fluid = this.fluidType.getBase(ingredient);
        if (fluid != null) {
            Component displayName = this.platformFluidHelper.getDisplayName(ingredient);
            toStringHelper.add("Fluid", (Object)displayName.getString());
        } else {
            toStringHelper.add("Fluid", (Object)"null");
        }
        toStringHelper.add("Amount", this.platformFluidHelper.getAmount(ingredient));
        DataComponentPatch components = this.platformFluidHelper.getComponentsPatch(ingredient);
        if (!components.isEmpty()) {
            toStringHelper.add("Components", (Object)components.toString());
        }
        return toStringHelper.toString();
    }

    @Override
    public Optional<TagKey<?>> getTagKeyEquivalent(Collection<T> ingredients) {
        Registry fluidRegistry = RegistryUtil.getRegistry(Registries.FLUID);
        return TagUtil.getTagEquivalent(ingredients, this.fluidType::getBase, () -> fluidRegistry.getTags());
    }

    @Override
    public boolean isIngredientOnServer(T ingredient) {
        Fluid fluid = this.fluidType.getBase(ingredient);
        Registry registry = RegistryUtil.getRegistry(Registries.FLUID);
        return registry.getKey((Object)fluid) != null;
    }
}

