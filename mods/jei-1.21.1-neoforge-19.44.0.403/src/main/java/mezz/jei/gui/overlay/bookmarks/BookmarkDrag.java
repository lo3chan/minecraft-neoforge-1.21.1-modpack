/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.phys.Vec2
 */
package mezz.jei.gui.overlay.bookmarks;

import java.util.List;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.common.Internal;
import mezz.jei.common.config.IClientConfig;
import mezz.jei.common.util.ImmutablePoint2i;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import mezz.jei.common.util.SafeIngredientUtil;
import mezz.jei.gui.bookmarks.IBookmark;
import mezz.jei.gui.input.UserInput;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import mezz.jei.gui.overlay.bookmarks.IBookmarkDragTarget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

public class BookmarkDrag<T> {
    private final BookmarkOverlay bookmarkOverlay;
    private final List<IBookmarkDragTarget> targets;
    private final IIngredientRenderer<T> ingredientRenderer;
    private final ITypedIngredient<T> ingredient;
    private final double mouseStartX;
    private final double mouseStartY;
    private final IBookmark bookmark;
    private final ImmutableRect2i origin;
    private final long dragCanStartTime;

    public BookmarkDrag(BookmarkOverlay bookmarkOverlay, List<IBookmarkDragTarget> targets, IIngredientRenderer<T> ingredientRenderer, ITypedIngredient<T> ingredient, IBookmark bookmark, double mouseX, double mouseY, ImmutableRect2i origin) {
        this.bookmarkOverlay = bookmarkOverlay;
        this.targets = targets;
        this.ingredientRenderer = ingredientRenderer;
        this.ingredient = ingredient;
        this.bookmark = bookmark;
        this.origin = origin;
        this.mouseStartX = mouseX;
        this.mouseStartY = mouseY;
        IClientConfig clientConfig = Internal.getJeiClientConfigs().getClientConfig();
        this.dragCanStartTime = System.currentTimeMillis() + (long)clientConfig.dragDelayMs().getValue().intValue();
    }

    public static boolean canStart(BookmarkDrag<?> drag, double mouseX, double mouseY) {
        Vec2 center;
        if (System.currentTimeMillis() < drag.dragCanStartTime) {
            return false;
        }
        ImmutableRect2i origin = drag.origin;
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

    public void update(int mouseX, int mouseY) {
        if (this.bookmark.isVisible() && !BookmarkDrag.canStart(this, mouseX, mouseY)) {
            return;
        }
        this.bookmark.setVisible(false);
        this.bookmarkOverlay.getScreenPropertiesUpdater().updateMouseExclusionArea(new ImmutablePoint2i(mouseX, mouseY)).update();
    }

    public boolean drawItem(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.bookmark.isVisible()) {
            return false;
        }
        SafeIngredientUtil.render(guiGraphics, this.ingredientRenderer, this.ingredient, mouseX - 8, mouseY - 8);
        return true;
    }

    public boolean onClick(UserInput input) {
        if (this.bookmark.isVisible()) {
            return false;
        }
        for (IBookmarkDragTarget target : this.targets) {
            ImmutableRect2i area = target.getArea();
            if (!MathUtil.contains(area, input.getMouseX(), input.getMouseY()) || input.isSimulate()) continue;
            target.accept(this.bookmark);
            this.stop();
            return true;
        }
        if (!input.isSimulate()) {
            this.stop();
        }
        return false;
    }

    public void stop() {
        this.bookmark.setVisible(true);
        this.bookmarkOverlay.getScreenPropertiesUpdater().updateMouseExclusionArea(null).update();
    }
}

