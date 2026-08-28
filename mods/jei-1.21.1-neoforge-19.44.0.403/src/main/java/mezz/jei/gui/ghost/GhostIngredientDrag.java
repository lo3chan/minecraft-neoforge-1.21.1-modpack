/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.world.phys.Vec2
 */
package mezz.jei.gui.ghost;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.gui.input.UserInput;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec2;

public class GhostIngredientDrag<T> {
    private static final int targetColor = 1075038474;
    private static final int hoverColor = -2142451431;
    private final List<HandlerData<T>> handlersData;
    private final IIngredientRenderer<T> ingredientRenderer;
    private final ITypedIngredient<T> ingredient;
    private final double mouseStartX;
    private final double mouseStartY;
    private final ImmutableRect2i origin;
    private final long dragCanStartTime;

    public GhostIngredientDrag(List<HandlerData<T>> handlersData, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> ingredient, double mouseX, double mouseY, ImmutableRect2i origin) {
        this.handlersData = handlersData;
        this.ingredientRenderer = ingredientRenderer;
        this.ingredient = ingredient;
        this.origin = origin;
        this.mouseStartX = mouseX;
        this.mouseStartY = mouseY;
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        this.dragCanStartTime = System.currentTimeMillis() + (long)clientConfig.dragDelayMs().getValue().intValue();
    }

    public void drawTargets(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (HandlerData<T> data : this.handlersData) {
            IGhostIngredientHandler<?> handler = data.handler;
            if (!handler.shouldHighlightTargets()) continue;
            GhostIngredientDrag.drawTargets(guiGraphics, mouseX, mouseY, data.targetAreas);
        }
    }

    public static boolean canStart(GhostIngredientDrag<?> drag, double mouseX, double mouseY) {
        Vec2 center;
        if (System.currentTimeMillis() < drag.dragCanStartTime) {
            return false;
        }
        ImmutableRect2i origin = drag.getOrigin();
        if (origin.isEmpty()) {
            center = new Vec2((float)drag.mouseStartX, (float)drag.mouseStartY);
        } else {
            if (origin.contains(mouseX, mouseY)) {
                return false;
            }
            center = new Vec2((float)origin.getX() + (float)origin.getWidth() / 2.0f, (float)origin.getY() + (float)origin.getHeight() / 2.0f);
        }
        double mouseXDist = (double)center.x - mouseX;
        double mouseYDist = (double)center.y - mouseY;
        double mouseDistSq = mouseXDist * mouseXDist + mouseYDist * mouseYDist;
        return mouseDistSq > 64.0;
    }

    public void drawItem(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!GhostIngredientDrag.canStart(this, mouseX, mouseY)) {
            return;
        }
        SafeIngredientUtil.render(guiGraphics, this.ingredientRenderer, this.ingredient, mouseX - 8, mouseY - 8);
    }

    public static void drawTargets(GuiGraphics guiGraphics, int mouseX, int mouseY, List<Rect2i> targetAreas) {
        RenderSystem.disableDepthTest();
        for (Rect2i area : targetAreas) {
            int color = MathUtil.contains(area, (double)mouseX, (double)mouseY) ? -2142451431 : 1075038474;
            guiGraphics.fill(RenderType.guiOverlay(), area.getX(), area.getY(), area.getX() + area.getWidth(), area.getY() + area.getHeight(), color);
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public boolean onClick(UserInput input) {
        if (!GhostIngredientDrag.canStart(this, input.getMouseX(), input.getMouseY())) {
            return false;
        }
        for (HandlerData<T> data : this.handlersData) {
            for (IGhostIngredientHandler.Target target : data.targets) {
                Rect2i area = target.getArea();
                if (!MathUtil.contains(area, input.getMouseX(), input.getMouseY())) continue;
                if (!input.isSimulate()) {
                    target.accept(this.ingredient.getIngredient());
                    data.handler.onComplete();
                }
                return true;
            }
            if (input.isSimulate()) continue;
            data.handler.onComplete();
        }
        return false;
    }

    public void stop() {
        for (HandlerData<T> data : this.handlersData) {
            data.handler.onComplete();
        }
    }

    public IIngredientRenderer<T> getIngredientRenderer() {
        return this.ingredientRenderer;
    }

    public ITypedIngredient<T> getIngredient() {
        return this.ingredient;
    }

    public ImmutableRect2i getOrigin() {
        return this.origin;
    }

    public record HandlerData<T>(IGhostIngredientHandler<?> handler, List<IGhostIngredientHandler.Target<T>> targets, List<Rect2i> targetAreas) {
        public HandlerData(IGhostIngredientHandler<?> handler, List<IGhostIngredientHandler.Target<T>> targets) {
            this(handler, targets, targets.stream().map(IGhostIngredientHandler.Target::getArea).toList());
        }
    }
}

