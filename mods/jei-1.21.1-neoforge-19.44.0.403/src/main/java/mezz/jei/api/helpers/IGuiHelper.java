/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package mezz.jei.api.helpers;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.api.gui.widgets.IScrollGridWidgetFactory;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IGuiHelper {
    default public IDrawableStatic createDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height) {
        return this.drawableBuilder(resourceLocation, u, v, width, height).build();
    }

    public IDrawableBuilder drawableBuilder(ResourceLocation var1, int var2, int var3, int var4, int var5);

    @Deprecated(since="19.38.0")
    public IDrawableStatic createDrawableSprite(TextureAtlas var1, ResourceLocation var2);

    public IDrawableStatic createDrawableSprite(TextureAtlas var1, ResourceLocation var2, int var3, int var4);

    public IScalableDrawable createScalableDrawableSprite(TextureAtlas var1, ResourceLocation var2);

    public IDrawableAnimated createAnimatedDrawable(IDrawableStatic var1, int var2, IDrawableAnimated.StartDirection var3, boolean var4);

    public IDrawableAnimated createAnimatedDrawable(IDrawableStatic var1, ITickTimer var2, IDrawableAnimated.StartDirection var3);

    public IDrawableStatic getSlotDrawable();

    public IDrawableStatic getOutputSlot();

    public IDrawableStatic getRecipeArrow();

    public IDrawableStatic getRecipeArrowFilled();

    public IDrawableAnimated createAnimatedRecipeArrow(int var1);

    public IDrawableStatic getRecipePlusSign();

    public IDrawableStatic getRecipeFlameFilled();

    public IDrawableStatic getRecipeFlameEmpty();

    public IDrawableAnimated createAnimatedRecipeFlame(int var1);

    public IDrawableStatic createBlankDrawable(int var1, int var2);

    default public IDrawable createDrawableItemStack(ItemStack ingredient) {
        return this.createDrawableIngredient(VanillaTypes.ITEM_STACK, ingredient);
    }

    default public IDrawable createDrawableItemLike(ItemLike itemLike) {
        return this.createDrawableIngredient(VanillaTypes.ITEM_STACK, itemLike.asItem().getDefaultInstance());
    }

    public <V> IDrawable createDrawableIngredient(IIngredientType<V> var1, V var2);

    public <V> IDrawable createDrawableIngredient(ITypedIngredient<V> var1);

    public ICraftingGridHelper createCraftingGridHelper();

    @Deprecated(since="19.19.3", forRemoval=true)
    public IScrollGridWidgetFactory<?> createScrollGridFactory(int var1, int var2);

    @Deprecated(since="19.18.9", forRemoval=true)
    public IScrollBoxWidget createScrollBoxWidget(IDrawable var1, int var2, int var3, int var4);

    public IScrollBoxWidget createScrollBoxWidget(int var1, int var2, int var3, int var4);

    @Deprecated(since="19.18.9", forRemoval=true)
    public int getScrollBoxScrollbarExtraWidth();

    public IRecipeWidget createWidgetFromDrawable(IDrawable var1, int var2, int var3);

    public ITickTimer createTickTimer(int var1, int var2, boolean var3);
}

