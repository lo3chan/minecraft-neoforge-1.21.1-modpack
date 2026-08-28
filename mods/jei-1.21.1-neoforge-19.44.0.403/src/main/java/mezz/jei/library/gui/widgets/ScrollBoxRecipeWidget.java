/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.network.chat.FormattedText
 *  org.joml.Matrix4f
 */
package mezz.jei.library.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.config.IJeiClientConfigs;
import mezz.jei.common.gui.elements.DrawableBlank;
import mezz.jei.common.gui.elements.DrawableWrappedText;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.library.gui.widgets.AbstractScrollWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.FormattedText;
import org.joml.Matrix4f;

public class ScrollBoxRecipeWidget
extends AbstractScrollWidget
implements IScrollBoxWidget,
IJeiInputHandler {
    private IDrawable contents = DrawableBlank.EMPTY;

    public ScrollBoxRecipeWidget(int width, int height, int xPos, int yPos) {
        super(new ImmutableRect2i(xPos, yPos, width, height));
    }

    @Override
    public int getContentAreaWidth() {
        return this.contentsArea.width();
    }

    @Override
    public int getContentAreaHeight() {
        return this.contentsArea.height();
    }

    @Override
    public IScrollBoxWidget setContents(IDrawable contents) {
        this.contents = contents;
        return this;
    }

    @Override
    public IScrollBoxWidget setContents(List<FormattedText> text) {
        this.contents = new DrawableWrappedText(text, this.getContentAreaWidth());
        return this;
    }

    @Override
    protected int getVisibleAmount() {
        return this.contentsArea.height();
    }

    @Override
    protected int getHiddenAmount() {
        return Math.max(this.contents.getHeight() - this.contentsArea.height(), 0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void drawContents(GuiGraphics guiGraphics, double mouseX, double mouseY, float scrollOffsetY) {
        PoseStack poseStack = guiGraphics.pose();
        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        ScreenRectangle scissorArea = MathUtil.transform(this.contentsArea, pose);
        guiGraphics.enableScissor(scissorArea.left(), scissorArea.top(), scissorArea.right(), scissorArea.bottom());
        poseStack.pushPose();
        float scrollAmount = (float)this.getHiddenAmount() * scrollOffsetY;
        poseStack.translate(0.0, (double)(-scrollAmount), 0.0);
        try {
            this.contents.draw(guiGraphics);
        }
        finally {
            poseStack.popPose();
            guiGraphics.disableScissor();
        }
    }

    @Override
    protected float calculateScrollAmount(double scrollDeltaY) {
        IJeiClientConfigs jeiClientConfigs = Internal.getJeiClientConfigs();
        IClientConfig clientConfig = jeiClientConfigs.getClientConfig();
        int smoothScrollRate = clientConfig.smoothScrollRate().getValue();
        int totalHeight = this.contents.getHeight();
        double scrollAmount = scrollDeltaY * (double)smoothScrollRate;
        return (float)(scrollAmount / (double)totalHeight);
    }
}

