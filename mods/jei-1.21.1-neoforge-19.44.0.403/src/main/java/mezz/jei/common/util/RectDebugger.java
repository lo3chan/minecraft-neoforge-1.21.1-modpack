/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.RenderType
 */
package mezz.jei.common.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public final class RectDebugger {
    public static final RectDebugger INSTANCE = new RectDebugger();
    private final Map<String, Rect> rects = new HashMap<String, Rect>();

    private RectDebugger() {
    }

    public void add(ImmutableRect2i rect, int color, String id) {
        this.rects.put(id, new Rect(rect, color));
    }

    public void draw(GuiGraphics guiGraphics) {
        RenderSystem.disableDepthTest();
        for (Rect rect : this.rects.values()) {
            ImmutableRect2i rect1 = rect.rect;
            guiGraphics.fill(RenderType.guiOverlay(), rect1.getX(), rect1.getY(), rect1.getX() + rect1.getWidth(), rect1.getY() + rect1.getHeight(), rect.color);
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private record Rect(ImmutableRect2i rect, int color) {
    }
}

