package com.alonie.brbe.mixins.alternativerecipes;

import com.alonie.brbe.BetterRecipeBook;
import com.alonie.brbe.mixins.accessors.OverlayRecipeButtonPosAccessor;
import com.alonie.brbe.mixins.accessors.OverlayRecipeComponentAccessor;
import com.alonie.brbe.util.BRBTextures;
import com.alonie.brbe.util.PartialCraftingUtil;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.OverlayRecipeComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"net/minecraft/client/gui/screens/recipebook/OverlayRecipeComponent$OverlayRecipeButton"}
)
public abstract class OverlayRecipeButtonMixin extends AbstractWidget {
   @Final
   @Shadow
   private boolean isCraftable;
   @Final
   @Shadow
   RecipeHolder<?> recipe;
   @Shadow
   @Final
   protected List<?> ingredientPos;
   @Shadow
   @Final
   OverlayRecipeComponent this$0;

   @Shadow
   public abstract void renderWidget(GuiGraphics var1, int var2, int var3, float var4);

   public OverlayRecipeButtonMixin(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"renderWidget"},
      cancellable = true
   )
   public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      boolean effectiveCraftable = this.isCraftable || PartialCraftingUtil.isPartiallyCraftable(this.this$0.getRecipeCollection(), this.recipe);
      ResourceLocation resourceLocation;
      if (((OverlayRecipeComponentAccessor)this.this$0).isFurnaceMenu()) {
         resourceLocation = BRBTextures.RECIPE_BOOK_PLAIN_OVERLAY_SPRITE.get(effectiveCraftable, this.isHoveredOrFocused());
      } else {
         resourceLocation = BRBTextures.RECIPE_BOOK_CRAFTING_OVERLAY_SPRITE.get(effectiveCraftable, this.isHoveredOrFocused());
      }

      gui.blitSprite(resourceLocation, this.getX(), this.getY(), this.width, this.height);
      if (PartialCraftingUtil.isPartiallyCraftable(this.this$0.getRecipeCollection(), this.recipe)) {
         gui.fill(this.getX() + 1, this.getY() + 1, this.getX() + this.width - 1, this.getY() + this.height - 1, 1627337523);
      }

      gui.pose().pushPose();
      if (BetterRecipeBook.ctx().config().alternativeRecipes.onHover && !this.isHoveredOrFocused()) {
         ItemStack recipeOutput = this.recipe.value().getResultItem(this.this$0.getRecipeCollection().registryAccess());
         gui.renderItem(recipeOutput, this.getX() + 4, this.getY() + 4);
      } else {
         gui.pose().translate(this.getX() + 2, this.getY() + 2, 150.0);

         for (Object rawPos : this.ingredientPos) {
            OverlayRecipeButtonPosAccessor pos = (OverlayRecipeButtonPosAccessor)rawPos;
            gui.pose().pushPose();
            gui.pose().translate(pos.brbe$getX(), pos.brbe$getY(), 0.0);
            if (!((OverlayRecipeComponentAccessor)this.this$0).isFurnaceMenu()) {
               gui.pose().scale(0.375F, 0.375F, 1.0F);
            }

            gui.pose().translate(-8.0, -8.0, 0.0);
            ItemStack[] ingredients = pos.brbe$getIngredients();
            if (ingredients.length > 0) {
               gui.renderItem(ingredients[Mth.floor(((OverlayRecipeComponentAccessor)this.this$0).getTime() / 30.0F) % ingredients.length], 0, 0);
            }

            gui.pose().popPose();
         }
      }

      gui.pose().popPose();
      if (BetterRecipeBook.pinnedRecipeManager.pinned.contains(this.recipe.id())) {
         gui.pose().pushPose();
         gui.pose().mulPose(gui.pose().last().pose());
         gui.blitSprite(BRBTextures.RECIPE_BOOK_OVERLAY_PIN_SPRITE, this.getX() - 4, this.getY() - 4, this.width + 8, this.height + 8);
         gui.pose().popPose();
      }

      ci.cancel();
   }
}
