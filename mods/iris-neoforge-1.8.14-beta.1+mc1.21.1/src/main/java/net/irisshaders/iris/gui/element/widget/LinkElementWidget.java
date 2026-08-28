/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenAxis
 *  net.minecraft.client.gui.navigation.ScreenDirection
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 */
package net.irisshaders.iris.gui.element.widget;

import java.util.Optional;
import net.irisshaders.iris.gui.GuiUtil;
import net.irisshaders.iris.gui.NavigationController;
import net.irisshaders.iris.gui.element.widget.CommentedElementWidget;
import net.irisshaders.iris.gui.screen.ShaderPackScreen;
import net.irisshaders.iris.shaderpack.option.menu.OptionMenuLinkElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public class LinkElementWidget
extends CommentedElementWidget<OptionMenuLinkElement> {
    private static final Component ARROW = Component.literal((String)">");
    private final String targetScreenId;
    private final MutableComponent label;
    private NavigationController navigation;
    private MutableComponent trimmedLabel = null;
    private boolean isLabelTrimmed = false;

    public LinkElementWidget(OptionMenuLinkElement element) {
        super(element);
        this.targetScreenId = element.targetScreenId;
        this.label = GuiUtil.translateOrDefault(Component.literal((String)element.targetScreenId), "screen." + element.targetScreenId, new Object[0]);
    }

    @Override
    public void init(ShaderPackScreen screen, NavigationController navigation) {
        this.navigation = navigation;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta, boolean hovered) {
        GuiUtil.bindIrisWidgetsTexture();
        GuiUtil.drawButton(guiGraphics, this.bounds.position().x(), this.bounds.position().y(), this.bounds.width(), this.bounds.height(), hovered || this.isFocused(), false);
        Font font = Minecraft.getInstance().font;
        int maxLabelWidth = this.bounds.width() - 9;
        if (font.width((FormattedText)this.label) > maxLabelWidth) {
            this.isLabelTrimmed = true;
        }
        if (this.trimmedLabel == null) {
            this.trimmedLabel = GuiUtil.shortenText(font, this.label, maxLabelWidth);
        }
        int labelWidth = font.width((FormattedText)this.trimmedLabel);
        guiGraphics.drawString(font, (Component)this.trimmedLabel, this.bounds.getCenterInAxis(ScreenAxis.HORIZONTAL) - (int)((double)labelWidth * 0.5) - (int)(0.5 * (double)Math.max(labelWidth - (this.bounds.width() - 18), 0)), this.bounds.position().y() + 7, 0xFFFFFF);
        guiGraphics.drawString(font, ARROW, this.bounds.getBoundInDirection(ScreenDirection.RIGHT) - 9, this.bounds.position().y() + 7, 0xFFFFFF);
        if (hovered && this.isLabelTrimmed) {
            ShaderPackScreen.TOP_LAYER_RENDER_QUEUE.add(() -> GuiUtil.drawTextPanel(font, guiGraphics, (Component)this.label, mouseX + 2, mouseY - 16));
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            this.navigation.open(this.targetScreenId);
            GuiUtil.playButtonClickSound();
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int pInt1, int pInt2) {
        if (keyCode == 257) {
            this.navigation.open(this.targetScreenId);
            GuiUtil.playButtonClickSound();
            return true;
        }
        return super.keyPressed(keyCode, pInt1, pInt2);
    }

    @Override
    public Optional<Component> getCommentTitle() {
        return Optional.of(this.label);
    }

    @Override
    public Optional<Component> getCommentBody() {
        String translation = "screen." + this.targetScreenId + ".comment";
        return Optional.ofNullable(I18n.exists((String)translation) ? Component.translatable((String)translation) : null);
    }
}

