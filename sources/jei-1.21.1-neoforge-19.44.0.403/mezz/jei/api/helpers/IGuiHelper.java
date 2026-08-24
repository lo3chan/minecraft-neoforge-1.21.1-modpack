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
import org.jetbrains.annotations.ApiStatus.NonExtendable;

@NonExtendable
public interface IGuiHelper {
   default IDrawableStatic createDrawable(ResourceLocation resourceLocation, int u, int v, int width, int height) {
      return this.drawableBuilder(resourceLocation, u, v, width, height).build();
   }

   IDrawableBuilder drawableBuilder(ResourceLocation var1, int var2, int var3, int var4, int var5);

   @Deprecated(
      since = "19.38.0"
   )
   IDrawableStatic createDrawableSprite(TextureAtlas var1, ResourceLocation var2);

   IDrawableStatic createDrawableSprite(TextureAtlas var1, ResourceLocation var2, int var3, int var4);

   IScalableDrawable createScalableDrawableSprite(TextureAtlas var1, ResourceLocation var2);

   IDrawableAnimated createAnimatedDrawable(IDrawableStatic var1, int var2, IDrawableAnimated.StartDirection var3, boolean var4);

   IDrawableAnimated createAnimatedDrawable(IDrawableStatic var1, ITickTimer var2, IDrawableAnimated.StartDirection var3);

   IDrawableStatic getSlotDrawable();

   IDrawableStatic getOutputSlot();

   IDrawableStatic getRecipeArrow();

   IDrawableStatic getRecipeArrowFilled();

   IDrawableAnimated createAnimatedRecipeArrow(int var1);

   IDrawableStatic getRecipePlusSign();

   IDrawableStatic getRecipeFlameFilled();

   IDrawableStatic getRecipeFlameEmpty();

   IDrawableAnimated createAnimatedRecipeFlame(int var1);

   IDrawableStatic createBlankDrawable(int var1, int var2);

   default IDrawable createDrawableItemStack(ItemStack ingredient) {
      return this.createDrawableIngredient(VanillaTypes.ITEM_STACK, ingredient);
   }

   default IDrawable createDrawableItemLike(ItemLike itemLike) {
      return this.createDrawableIngredient(VanillaTypes.ITEM_STACK, itemLike.asItem().getDefaultInstance());
   }

   <V> IDrawable createDrawableIngredient(IIngredientType<V> var1, V var2);

   <V> IDrawable createDrawableIngredient(ITypedIngredient<V> var1);

   ICraftingGridHelper createCraftingGridHelper();

   @Deprecated(
      since = "19.19.3",
      forRemoval = true
   )
   IScrollGridWidgetFactory<?> createScrollGridFactory(int var1, int var2);

   @Deprecated(
      since = "19.18.9",
      forRemoval = true
   )
   IScrollBoxWidget createScrollBoxWidget(IDrawable var1, int var2, int var3, int var4);

   IScrollBoxWidget createScrollBoxWidget(int var1, int var2, int var3, int var4);

   @Deprecated(
      since = "19.18.9",
      forRemoval = true
   )
   int getScrollBoxScrollbarExtraWidth();

   IRecipeWidget createWidgetFromDrawable(IDrawable var1, int var2, int var3);

   ITickTimer createTickTimer(int var1, int var2, boolean var3);
}
