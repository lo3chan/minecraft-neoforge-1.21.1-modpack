/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 */
package mezz.jei.gui.overlay.ingredients;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mezz.jei.common.gui.elements.ScalableDrawable;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;

public final class GuiExclusionAreaShadow {
    static final int SHADOW_SIZE = 4;

    private GuiExclusionAreaShadow() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void draw(GuiGraphics guiGraphics, ScalableDrawable shadow, ImmutableRect2i backgroundArea, Set<ImmutableRect2i> guiExclusionAreas) {
        List<ImmutableRect2i> shadowAreas = GuiExclusionAreaShadow.calculateShadowAreas(backgroundArea, guiExclusionAreas);
        if (shadowAreas.isEmpty()) {
            return;
        }
        guiGraphics.enableScissor(backgroundArea.x(), backgroundArea.y(), backgroundArea.x() + backgroundArea.width(), backgroundArea.y() + backgroundArea.height());
        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            for (ImmutableRect2i shadowArea : shadowAreas) {
                shadow.draw(guiGraphics, shadowArea);
            }
        }
        finally {
            RenderSystem.disableBlend();
            guiGraphics.disableScissor();
        }
    }

    static List<ImmutableRect2i> calculateShadowAreas(ImmutableRect2i backgroundArea, Set<ImmutableRect2i> guiExclusionAreas) {
        if (backgroundArea.isEmpty() || guiExclusionAreas.isEmpty()) {
            return List.of();
        }
        ArrayList<ImmutableRect2i> shadowAreas = new ArrayList<ImmutableRect2i>();
        for (ImmutableRect2i exclusionArea : guiExclusionAreas) {
            if (exclusionArea.isEmpty() || !exclusionArea.intersects(backgroundArea)) continue;
            shadowAreas.add(new ImmutableRect2i(exclusionArea.x() - 4, exclusionArea.y() - 4, exclusionArea.width() + 8, exclusionArea.height() + 8));
        }
        return List.copyOf(shadowAreas);
    }
}

