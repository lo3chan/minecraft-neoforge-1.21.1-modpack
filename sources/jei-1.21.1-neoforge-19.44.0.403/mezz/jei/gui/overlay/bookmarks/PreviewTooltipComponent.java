package mezz.jei.gui.overlay.bookmarks;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.Internal;
import mezz.jei.common.transfer.RecipeTransferUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class PreviewTooltipComponent<R> implements ClientTooltipComponent, TooltipComponent {
   private static final int UPDATE_INTERVAL_MS = 2000;
   private final IRecipeLayoutDrawable<R> drawable;
   @Nullable
   private IRecipeTransferError transferError;
   private long lastUpdateTime = 0L;

   public PreviewTooltipComponent(IRecipeLayoutDrawable<R> drawable) {
      this.drawable = drawable;
   }

   public int getHeight() {
      return this.drawable.getRect().getHeight() + 10;
   }

   public int getWidth(Font font) {
      return this.drawable.getRect().getWidth() + 4;
   }

   public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
      PoseStack pose = guiGraphics.pose();
      pose.pushPose();
      pose.translate(x + 2, y + 5, 0.0F);
      this.drawable.drawRecipe(guiGraphics, 0, 0);
      this.updateTransferError();
      if (this.transferError != null) {
         Rect2i recipeRect = this.drawable.getRect();
         this.transferError.showError(guiGraphics, x, y, this.drawable.getRecipeSlotsView(), recipeRect.getX(), recipeRect.getY());
      }

      pose.popPose();
   }

   public void tick() {
      this.drawable.tick();
   }

   private void updateTransferError() {
      long currentTime = System.currentTimeMillis();
      if (currentTime - this.lastUpdateTime >= 2000L) {
         this.lastUpdateTime = currentTime;
         Minecraft minecraft = Minecraft.getInstance();
         LocalPlayer player = minecraft.player;
         if (player == null) {
            this.transferError = null;
         } else {
            if (Minecraft.getInstance().screen instanceof AbstractContainerScreen<?> containerScreen) {
               AbstractContainerMenu container = containerScreen.getMenu();
               IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
               this.transferError = RecipeTransferUtil.getTransferRecipeError(recipeTransferManager, container, this.drawable, player).orElse(null);
            } else {
               this.transferError = null;
            }
         }
      }
   }
}
