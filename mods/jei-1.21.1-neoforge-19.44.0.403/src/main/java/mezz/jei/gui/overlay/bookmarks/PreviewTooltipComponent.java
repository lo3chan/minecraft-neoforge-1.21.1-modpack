/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 *  org.jetbrains.annotations.Nullable
 */
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.Nullable;

public class PreviewTooltipComponent<R>
implements ClientTooltipComponent,
TooltipComponent {
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
        pose.translate((float)(x + 2), (float)(y + 5), 0.0f);
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
        if (currentTime - this.lastUpdateTime < 2000L) {
            return;
        }
        this.lastUpdateTime = currentTime;
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            this.transferError = null;
            return;
        }
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen containerScreen = (AbstractContainerScreen)screen;
            AbstractContainerMenu container = containerScreen.getMenu();
            IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
            this.transferError = RecipeTransferUtil.getTransferRecipeError(recipeTransferManager, container, this.drawable, (Player)player).orElse(null);
        } else {
            this.transferError = null;
        }
    }
}

