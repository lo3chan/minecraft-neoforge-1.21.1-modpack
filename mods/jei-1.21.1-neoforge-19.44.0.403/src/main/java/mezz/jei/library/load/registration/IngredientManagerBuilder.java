/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.serialization.Codec
 *  java.util.SequencedMap
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.material.Fluid
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.load.registration;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.SequencedMap;
import javax.annotation.Nullable;
import mezz.jei.api.helpers.IColorHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.ISubtypeManager;
import mezz.jei.api.registration.IExtraIngredientRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.common.platform.IPlatformFluidHelperInternal;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ErrorUtil;
import mezz.jei.library.ingredients.IngredientInfo;
import mezz.jei.library.ingredients.IngredientManager;
import mezz.jei.library.ingredients.RegisteredIngredients;
import mezz.jei.library.ingredients.TypedIngredient;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IngredientManagerBuilder
implements IModIngredientRegistration,
IIngredientAliasRegistration,
IExtraIngredientRegistration {
    private static final Logger LOGGER = LogManager.getLogger();
    private final SequencedMap<IIngredientType<?>, IngredientInfo<?>> ingredientInfos = new LinkedHashMap();
    private final ISubtypeManager subtypeManager;
    private final IColorHelper colorHelper;

    public IngredientManagerBuilder(ISubtypeManager subtypeManager, IColorHelper colorHelper) {
        this.subtypeManager = subtypeManager;
        this.colorHelper = colorHelper;
    }

    @Override
    public <V> void register(IIngredientType<V> ingredientType, Collection<V> allIngredients, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer) {
        this.registerInternal(ingredientType, allIngredients, ingredientHelper, ingredientRenderer, null);
    }

    @Override
    public <V> void register(IIngredientType<V> ingredientType, Collection<V> allIngredients, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer, Codec<V> ingredientCodec) {
        ErrorUtil.checkNotNull(ingredientCodec, "ingredientCodec");
        this.registerInternal(ingredientType, allIngredients, ingredientHelper, ingredientRenderer, ingredientCodec);
    }

    private <V> void registerInternal(IIngredientType<V> ingredientType, Collection<V> allIngredients, IIngredientHelper<V> ingredientHelper, IIngredientRenderer<V> ingredientRenderer, @Nullable Codec<V> ingredientCodec) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(allIngredients, "allIngredients");
        ErrorUtil.checkNotNull(ingredientHelper, "ingredientHelper");
        ErrorUtil.checkNotNull(ingredientRenderer, "ingredientRenderer");
        Preconditions.checkArgument((ingredientRenderer.getWidth() == 16 ? 1 : 0) != 0, (Object)"the default ingredient renderer registered here will be used for drawing ingredients in the ingredient list, and it must have a width of 16");
        Preconditions.checkArgument((ingredientRenderer.getHeight() == 16 ? 1 : 0) != 0, (Object)"the default ingredient renderer registered here will be used for drawing ingredients in the ingredient list, and it must have a height of 16");
        if (this.ingredientInfos.containsKey(ingredientType)) {
            throw new IllegalArgumentException("Ingredient type has already been registered: " + String.valueOf(ingredientType.getIngredientClass()));
        }
        ArrayList allTypedIngredients = new ArrayList(allIngredients.size());
        for (V ingredient : allIngredients) {
            if (!ingredientHelper.isIngredientOnServer(ingredient)) {
                String errorInfo = ingredientHelper.getErrorInfo(ingredient);
                LOGGER.warn("Attempted to add an Ingredient that is not on the server: {}", (Object)errorInfo);
                continue;
            }
            ITypedIngredient<V> typedIngredient = TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, false);
            if (typedIngredient == null) {
                LOGGER.warn("Detected an invalid ingredient during ingredient registration: {}", (Object)ingredientHelper.getErrorInfo(ingredient));
                continue;
            }
            allTypedIngredients.add(typedIngredient);
        }
        this.ingredientInfos.put(ingredientType, new IngredientInfo<V>(ingredientType, allTypedIngredients, ingredientHelper, ingredientRenderer, ingredientCodec));
    }

    @Override
    public <V> void addExtraIngredients(IIngredientType<V> ingredientType, Collection<V> extraIngredients) {
        ErrorUtil.checkNotNull(ingredientType, "ingredientType");
        ErrorUtil.checkNotNull(extraIngredients, "extraIngredients");
        IngredientInfo<V> castIngredientInfo = this.getIngredientInfo(ingredientType);
        IIngredientHelper<V> ingredientHelper = castIngredientInfo.getIngredientHelper();
        ArrayList extraTypedIngredients = new ArrayList(extraIngredients.size());
        for (V ingredient : extraIngredients) {
            if (!ingredientHelper.isIngredientOnServer(ingredient)) {
                String errorInfo = ingredientHelper.getErrorInfo(ingredient);
                LOGGER.warn("Attempted to add an extra Ingredient that is not on the server: {}", (Object)errorInfo);
                continue;
            }
            ITypedIngredient<V> typedIngredient = TypedIngredient.createAndFilterInvalid(ingredientHelper, ingredientType, ingredient, false);
            if (typedIngredient == null) {
                LOGGER.warn("Detected an invalid ingredient when adding extra ingredients: {}", (Object)ingredientHelper.getErrorInfo(ingredient));
                continue;
            }
            extraTypedIngredients.add(typedIngredient);
        }
        castIngredientInfo.addIngredients(extraTypedIngredients);
    }

    @Override
    public <I> void addAlias(IIngredientType<I> type, I ingredient, String alias) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        ErrorUtil.checkNotNull(alias, "alias");
        IngredientManagerBuilder.checkIngredientType(type, ingredient);
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(type);
        ingredientInfo.addIngredientAlias(ingredient, alias);
    }

    @Override
    public <B, I> void addAlias(IIngredientTypeWithSubtypes<B, I> type, B baseIngredient, String alias) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(baseIngredient, "baseIngredient");
        ErrorUtil.checkNotNull(alias, "alias");
        IngredientManagerBuilder.checkBaseIngredientType(type, baseIngredient);
        IngredientInfo ingredientInfo = this.getIngredientInfo(type);
        ingredientInfo.addBaseIngredientAlias(baseIngredient, alias);
    }

    @Override
    public void addAlias(Fluid fluid, String alias) {
        ErrorUtil.checkNotNull(fluid, "fluid");
        ErrorUtil.checkNotNull(alias, "alias");
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        IIngredientTypeWithSubtypes fluidIngredientType = fluidHelper.getFluidIngredientType();
        this.addAlias(fluidIngredientType, (Object)fluid, alias);
    }

    @Override
    public <I> void addAlias(ITypedIngredient<I> typedIngredient, String alias) {
        ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
        ErrorUtil.checkNotNull(alias, "alias");
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(typedIngredient.getType());
        ingredientInfo.addIngredientAlias(typedIngredient, alias);
    }

    @Override
    public <I> void addAliases(IIngredientType<I> type, I ingredient, Collection<String> aliases) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(ingredient, "ingredient");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IngredientManagerBuilder.checkIngredientType(type, ingredient);
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(type);
        ingredientInfo.addIngredientAliases(ingredient, aliases);
    }

    @Override
    public <B, I> void addAliases(IIngredientTypeWithSubtypes<B, I> type, B baseIngredient, Collection<String> aliases) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(baseIngredient, "baseIngredient");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IngredientManagerBuilder.checkBaseIngredientType(type, baseIngredient);
        IngredientInfo ingredientInfo = this.getIngredientInfo(type);
        ingredientInfo.addBaseIngredientAliases(baseIngredient, aliases);
    }

    @Override
    public void addAliases(Fluid fluid, Collection<String> aliases) {
        ErrorUtil.checkNotNull(fluid, "fluid");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IPlatformFluidHelperInternal<?> fluidHelper = Services.PLATFORM.getFluidHelper();
        IIngredientTypeWithSubtypes fluidIngredientType = fluidHelper.getFluidIngredientType();
        this.addAliases(fluidIngredientType, (Object)fluid, aliases);
    }

    @Override
    public <I> void addAliases(ITypedIngredient<I> typedIngredient, Collection<String> aliases) {
        ErrorUtil.checkNotNull(typedIngredient, "typedIngredient");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(typedIngredient.getType());
        ingredientInfo.addIngredientAliases(typedIngredient, aliases);
    }

    @Override
    public <I> void addAliases(IIngredientType<I> type, Collection<I> ingredients, String alias) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        ErrorUtil.checkNotNull(alias, "alias");
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(type);
        for (I ingredient : ingredients) {
            ingredientInfo.addIngredientAlias(ingredient, alias);
        }
    }

    @Override
    public <I> void addAliases(Collection<ITypedIngredient<I>> typedIngredients, String alias) {
        ErrorUtil.checkNotNull(typedIngredients, "typedIngredients");
        ErrorUtil.checkNotNull(alias, "alias");
        IngredientInfo<I> ingredientInfo = null;
        for (ITypedIngredient<I> typedIngredient : typedIngredients) {
            IIngredientType<I> ingredientType = typedIngredient.getType();
            if (ingredientInfo == null) {
                ingredientInfo = this.getIngredientInfo(ingredientType);
            }
            ingredientInfo.addIngredientAlias(typedIngredient, alias);
        }
    }

    @Override
    public <I> void addAliases(IIngredientType<I> type, Collection<I> ingredients, Collection<String> aliases) {
        ErrorUtil.checkNotNull(type, "type");
        ErrorUtil.checkNotNull(ingredients, "ingredients");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IngredientInfo<I> ingredientInfo = this.getIngredientInfo(type);
        for (I ingredient : ingredients) {
            ingredientInfo.addIngredientAliases(ingredient, aliases);
        }
    }

    @Override
    public <I> void addAliases(Collection<ITypedIngredient<I>> typedIngredients, Collection<String> aliases) {
        ErrorUtil.checkNotNull(typedIngredients, "typedIngredients");
        ErrorUtil.checkNotNull(aliases, "aliases");
        IngredientInfo<I> ingredientInfo = null;
        for (ITypedIngredient<I> typedIngredient : typedIngredients) {
            IIngredientType<I> ingredientType = typedIngredient.getType();
            if (ingredientInfo == null) {
                ingredientInfo = this.getIngredientInfo(ingredientType);
            }
            ingredientInfo.addIngredientAliases(typedIngredient, aliases);
        }
    }

    private static <I> void checkIngredientType(IIngredientType<I> type, I ingredient) {
        Class<I> ingredientClass = type.getIngredientClass();
        if (!ingredientClass.isInstance(ingredient)) {
            throw new IllegalArgumentException(String.format("ingredient (%s) must be an instance of %s", ingredient.getClass(), ingredientClass));
        }
    }

    private static <B, I> void checkBaseIngredientType(IIngredientTypeWithSubtypes<B, I> type, B baseIngredient) {
        Class<B> ingredientBaseClass = type.getIngredientBaseClass();
        if (!ingredientBaseClass.isInstance(baseIngredient)) {
            throw new IllegalArgumentException(String.format("baseIngredient (%s) must be an instance of %s", baseIngredient.getClass(), ingredientBaseClass));
        }
    }

    private <T> IngredientInfo<T> getIngredientInfo(IIngredientType<T> ingredientType) {
        IngredientInfo ingredientInfo = (IngredientInfo)this.ingredientInfos.get(ingredientType);
        if (ingredientInfo == null) {
            throw new IllegalArgumentException("Ingredient type has not been registered: " + ingredientType.getUid());
        }
        IngredientInfo cast = ingredientInfo;
        return cast;
    }

    @Override
    public ISubtypeManager getSubtypeManager() {
        return this.subtypeManager;
    }

    @Override
    public IColorHelper getColorHelper() {
        return this.colorHelper;
    }

    public IngredientManager build() {
        RegisteredIngredients registeredIngredients = new RegisteredIngredients(this.ingredientInfos);
        return new IngredientManager(registeredIngredients);
    }
}

