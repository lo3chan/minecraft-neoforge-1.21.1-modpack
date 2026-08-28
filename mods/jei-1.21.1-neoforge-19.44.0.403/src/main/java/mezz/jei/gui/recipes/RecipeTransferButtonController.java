/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.recipes;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.buttons.IButtonState;
import mezz.jei.api.gui.buttons.IIconButtonController;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferManager;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.textures.Textures;
import mezz.jei.common.transfer.RecipeTransferErrorInternal;
import mezz.jei.common.transfer.RecipeTransferUtil;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

public class RecipeTransferButtonController
implements IIconButtonController {
    private final IRecipeLayoutDrawable<?> recipeLayout;
    private final RecipesGui recipesGui;
    @Nullable
    private IRecipeTransferError recipeTransferError;

    public RecipeTransferButtonController(IRecipeLayoutDrawable<?> recipeLayout, RecipesGui recipesGui) {
        this.recipeLayout = recipeLayout;
        this.recipesGui = recipesGui;
    }

    @Override
    public void initState(IButtonState state) {
        Textures textures = Internal.getTextures();
        state.setIcon(textures.getRecipeTransfer());
        this.updateState(state);
    }

    @Override
    public void updateState(IButtonState state) {
        LocalPlayer player = Minecraft.getInstance().player;
        AbstractContainerMenu parentContainer = this.recipesGui.getParentContainerMenu();
        if (parentContainer != null && player != null) {
            IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
            this.recipeTransferError = RecipeTransferUtil.getTransferRecipeError(recipeTransferManager, parentContainer, this.recipeLayout, (Player)player).orElse(null);
        } else {
            this.recipeTransferError = RecipeTransferErrorInternal.INSTANCE;
        }
        RecipeTransferButtonController.updateStateForTransferError(state, this.recipeTransferError);
    }

    static void updateStateForTransferError(IButtonState state, @Nullable IRecipeTransferError recipeTransferError) {
        if (recipeTransferError == null || recipeTransferError.getType().allowsTransfer) {
            state.setActive(true);
            state.setVisible(true);
        } else {
            state.setActive(false);
            IRecipeTransferError.Type type = recipeTransferError.getType();
            state.setVisible(type == IRecipeTransferError.Type.USER_FACING);
        }
    }

    @Override
    public boolean onPress(IJeiUserInput input) {
        if (!input.isSimulate()) {
            IRecipeTransferManager recipeTransferManager = Internal.getJeiRuntime().getRecipeTransferManager();
            boolean maxTransfer = Screen.hasShiftDown();
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            AbstractContainerMenu parentContainer = this.recipesGui.getParentContainerMenu();
            if (parentContainer != null && player != null && RecipeTransferUtil.transferRecipe(recipeTransferManager, parentContainer, this.recipeLayout, (Player)player, maxTransfer)) {
                this.recipesGui.onClose();
            }
        }
        return true;
    }

    @Override
    public void getTooltips(ITooltipBuilder tooltip) {
        RecipeTransferButtonController.getTooltips(this.recipeTransferError, tooltip);
    }

    static void getTooltips(@Nullable IRecipeTransferError recipeTransferError, ITooltipBuilder tooltip) {
        if (recipeTransferError == null) {
            MutableComponent tooltipTransfer = Component.translatable((String)"jei.tooltip.transfer");
            tooltip.add((FormattedText)tooltipTransfer);
        } else {
            recipeTransferError.getTooltip(tooltip);
        }
    }

    @Override
    public void drawExtras(GuiGraphics guiGraphics, Rect2i buttonArea, int mouseX, int mouseY, float partialTicks) {
        IRecipeTransferError recipeTransferError = this.recipeTransferError;
        if (recipeTransferError != null) {
            if (recipeTransferError.getType() == IRecipeTransferError.Type.COSMETIC) {
                guiGraphics.fill(RenderType.guiOverlay(), buttonArea.getX(), buttonArea.getY(), buttonArea.getX() + buttonArea.getWidth(), buttonArea.getY() + buttonArea.getHeight(), recipeTransferError.getButtonHighlightColor());
            }
            if (buttonArea.contains(mouseX, mouseY)) {
                IRecipeSlotsView recipeSlotsView = this.recipeLayout.getRecipeSlotsView();
                Rect2i recipeRect = this.recipeLayout.getRect();
                PoseStack poseStack = guiGraphics.pose();
                RecipeTransferButtonController.runWithRestoredPose(poseStack, () -> recipeTransferError.showError(guiGraphics, mouseX, mouseY, recipeSlotsView, recipeRect.getX(), recipeRect.getY()));
            }
        }
    }

    static void runWithRestoredPose(PoseStack poseStack, Runnable action) {
        poseStack.pushPose();
        try {
            action.run();
        }
        finally {
            poseStack.popPose();
        }
    }

    public int getMissingCountHint() {
        return RecipeTransferButtonController.getMissingCountHint(this.recipeTransferError);
    }

    static int getMissingCountHint(@Nullable IRecipeTransferError recipeTransferError) {
        if (recipeTransferError == null) {
            return 0;
        }
        return recipeTransferError.getMissingCountHint();
    }
}

