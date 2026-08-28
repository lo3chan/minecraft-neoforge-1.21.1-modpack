/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.gui.events;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Set;
import java.util.stream.Collectors;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.runtime.IScreenHelper;
import mezz.jei.common.config.DebugConfig;
import mezz.jei.common.gui.JeiTooltip;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.RectDebugger;
import mezz.jei.gui.overlay.IngredientListOverlay;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;

public class GuiEventHandler {
    private final IngredientListOverlay ingredientListOverlay;
    private final IScreenHelper screenHelper;
    private final BookmarkOverlay bookmarkOverlay;

    public GuiEventHandler(IScreenHelper screenHelper, BookmarkOverlay bookmarkOverlay, IngredientListOverlay ingredientListOverlay) {
        this.screenHelper = screenHelper;
        this.bookmarkOverlay = bookmarkOverlay;
        this.ingredientListOverlay = ingredientListOverlay;
    }

    public void onGuiInit(Screen screen) {
        Set<ImmutableRect2i> guiExclusionAreas = this.screenHelper.getGuiExclusionAreas(screen).map(ImmutableRect2i::new).collect(Collectors.toUnmodifiableSet());
        this.ingredientListOverlay.getScreenPropertiesUpdater().updateScreen(screen).updateExclusionAreas(guiExclusionAreas).update();
        this.bookmarkOverlay.getScreenPropertiesUpdater().updateScreen(screen).updateExclusionAreas(guiExclusionAreas).update();
    }

    public void onGuiOpen(Screen screen) {
        this.ingredientListOverlay.getScreenPropertiesUpdater().updateScreen(screen).update();
        this.bookmarkOverlay.getScreenPropertiesUpdater().updateScreen(screen).update();
    }

    public void onClientTick() {
        this.ingredientListOverlay.tick();
        this.bookmarkOverlay.tick();
    }

    public void drawForContainerScreen(AbstractContainerScreen<?> screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        @Nullable IGuiProperties guiProperties = this.screenHelper.getGuiProperties(screen).orElse(null);
        this.drawOverlayForegrounds(guiGraphics, mouseX, mouseY, true);
        this.drawPostForeground((Screen)screen, guiProperties, guiGraphics, mouseX, mouseY);
    }

    public void drawForScreen(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        @Nullable IGuiProperties guiProperties = this.screenHelper.getGuiProperties(screen).orElse(null);
        this.updateOverlayProperties(screen, guiProperties);
        this.drawOverlayBackgrounds(guiGraphics);
        if (screen instanceof AbstractContainerScreen) {
            return;
        }
        this.drawOverlayForegrounds(guiGraphics, mouseX, mouseY, false);
        this.drawPostForeground(screen, guiProperties, guiGraphics, mouseX, mouseY);
    }

    private void updateOverlayProperties(Screen screen, @Nullable IGuiProperties guiProperties) {
        Set<ImmutableRect2i> guiExclusionAreas = this.screenHelper.getGuiExclusionAreas(screen).map(ImmutableRect2i::new).collect(Collectors.toUnmodifiableSet());
        this.ingredientListOverlay.getScreenPropertiesUpdater().updateGuiProperties(guiProperties).updateExclusionAreas(guiExclusionAreas).update();
        this.bookmarkOverlay.getScreenPropertiesUpdater().updateGuiProperties(guiProperties).updateExclusionAreas(guiExclusionAreas).update();
    }

    private void drawOverlayBackgrounds(GuiGraphics guiGraphics) {
        this.ingredientListOverlay.drawBackground(guiGraphics);
        this.bookmarkOverlay.drawBackground(guiGraphics);
    }

    private void drawOverlayForegrounds(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean drawScreenForeground) {
        Minecraft minecraft = Minecraft.getInstance();
        DeltaTracker deltaTracker = minecraft.getTimer();
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        if (drawScreenForeground) {
            this.bookmarkOverlay.drawOnForeground(guiGraphics, mouseX, mouseY);
            this.ingredientListOverlay.drawOnForeground(guiGraphics, mouseX, mouseY);
        }
        this.ingredientListOverlay.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
        this.bookmarkOverlay.drawForeground(minecraft, guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void drawPostForeground(Screen screen, @Nullable IGuiProperties guiProperties, GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        if (guiProperties != null && screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen guiContainer = (AbstractContainerScreen)screen;
            int guiLeft = guiProperties.guiLeft();
            int guiTop = guiProperties.guiTop();
            this.screenHelper.getGuiClickableArea(guiContainer, mouseX - guiLeft, mouseY - guiTop).filter(IGuiClickableArea::isTooltipEnabled).findFirst().ifPresent(area -> {
                JeiTooltip tooltip = new JeiTooltip();
                area.getTooltip(tooltip);
                if (tooltip.isEmpty()) {
                    tooltip.add((FormattedText)Component.translatable((String)"jei.tooltip.show.recipes"));
                }
                tooltip.draw(guiGraphics, mouseX, mouseY);
            });
        }
        this.ingredientListOverlay.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
        this.bookmarkOverlay.drawTooltips(minecraft, guiGraphics, mouseX, mouseY);
        if (DebugConfig.isDebugGuisEnabled()) {
            this.drawDebugInfoForScreen(screen, guiProperties, guiGraphics);
        }
    }

    public boolean renderCompactPotionIndicators() {
        return this.ingredientListOverlay.isListDisplayed();
    }

    private void drawDebugInfoForScreen(Screen screen, @Nullable IGuiProperties guiProperties, GuiGraphics guiGraphics) {
        RectDebugger.INSTANCE.draw(guiGraphics);
        if (guiProperties != null) {
            Set guiExclusionAreas = this.screenHelper.getGuiExclusionAreas(screen).collect(Collectors.toUnmodifiableSet());
            RenderSystem.disableDepthTest();
            for (Rect2i area : guiExclusionAreas) {
                guiGraphics.fill(RenderType.gui(), area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight(), 0x44FF0000);
            }
            guiGraphics.fill(RenderType.gui(), guiProperties.guiLeft(), guiProperties.guiTop(), guiProperties.guiRight(), guiProperties.guiBottom(), 0x22CCCC00);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }
}

