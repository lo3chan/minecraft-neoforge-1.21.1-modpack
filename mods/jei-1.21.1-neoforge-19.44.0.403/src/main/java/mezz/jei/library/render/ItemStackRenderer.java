/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.common.platform.IPlatformRenderHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.library.render.batch.ItemStackBatchRendererCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ItemStackRenderer
implements IIngredientRenderer<ItemStack> {
    private final ItemStackBatchRendererCache batchRenderer = new ItemStackBatchRendererCache();

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable ItemStack ingredient) {
        this.render(guiGraphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable ItemStack ingredient, int posX, int posY) {
        if (ingredient != null) {
            RenderSystem.enableDepthTest();
            Minecraft minecraft = Minecraft.getInstance();
            Font font = this.getFontRenderer(minecraft, ingredient);
            guiGraphics.renderFakeItem(ingredient, posX, posY);
            guiGraphics.renderItemDecorations(font, ingredient, posX, posY);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public void renderBatch(GuiGraphics guiGraphics, List<BatchRenderElement<ItemStack>> batchRenderElements) {
        this.batchRenderer.renderBatch(guiGraphics, this, batchRenderElements);
    }

    @Override
    public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        Item.TooltipContext tooltipContext = Item.TooltipContext.of((Level)minecraft.level);
        return ingredient.getTooltipLines(tooltipContext, (Player)player, tooltipFlag);
    }

    @Override
    public Font getFontRenderer(Minecraft minecraft, ItemStack ingredient) {
        IPlatformRenderHelper renderHelper = Services.PLATFORM.getRenderHelper();
        return renderHelper.getFontRenderer(minecraft, ingredient);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}

