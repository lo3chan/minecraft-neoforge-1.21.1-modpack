/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.navigation.ScreenRectangle
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.phys.Vec2
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package mezz.jei.common.util;

import java.util.Collection;
import java.util.Objects;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class MathUtil {
    private MathUtil() {
    }

    public static int divideCeil(int numerator, int denominator) {
        return (int)Math.ceil((float)numerator / (float)denominator);
    }

    public static boolean intersects(Collection<ImmutableRect2i> areas, ImmutableRect2i comparisonArea) {
        return areas.stream().anyMatch(comparisonArea::intersects);
    }

    public static boolean contains(ImmutableRect2i rect, double x, double y) {
        return rect.contains(x, y);
    }

    public static boolean contains(Rect2i rect, double x, double y) {
        return x >= (double)rect.getX() && y >= (double)rect.getY() && x < (double)(rect.getX() + rect.getWidth()) && y < (double)(rect.getY() + rect.getHeight());
    }

    public static boolean contains(ScreenRectangle rect, double x, double y) {
        return x >= (double)rect.left() && x <= (double)rect.right() && y >= (double)rect.top() && y <= (double)rect.bottom();
    }

    public static ImmutableRect2i union(ImmutableRect2i rect1, ImmutableRect2i rect2) {
        if (rect1.isEmpty()) {
            return rect2;
        }
        if (rect2.isEmpty()) {
            return rect1;
        }
        long tx2 = rect1.getWidth();
        long ty2 = rect1.getHeight();
        long rx2 = rect2.getWidth();
        long ry2 = rect2.getHeight();
        int tx1 = rect1.getX();
        int ty1 = rect1.getY();
        tx2 += (long)tx1;
        ty2 += (long)ty1;
        int rx1 = rect2.getX();
        int ry1 = rect2.getY();
        rx2 += (long)rx1;
        ry2 += (long)ry1;
        if (tx1 > rx1) {
            tx1 = rx1;
        }
        if (ty1 > ry1) {
            ty1 = ry1;
        }
        if (tx2 < rx2) {
            tx2 = rx2;
        }
        if (ty2 < ry2) {
            ty2 = ry2;
        }
        tx2 -= (long)tx1;
        ty2 -= (long)ty1;
        tx2 = Math.min(tx2, Integer.MAX_VALUE);
        ty2 = Math.min(ty2, Integer.MAX_VALUE);
        return new ImmutableRect2i(tx1, ty1, (int)tx2, (int)ty2);
    }

    public static ImmutableRect2i centerTextArea(ImmutableRect2i outer, Font fontRenderer, String text) {
        int width = fontRenderer.width(text);
        Objects.requireNonNull(fontRenderer);
        int height = 9;
        return MathUtil.centerArea(outer, width, height);
    }

    public static ImmutableRect2i centerTextArea(ImmutableRect2i outer, Font fontRenderer, FormattedCharSequence text) {
        int width = fontRenderer.width(text);
        Objects.requireNonNull(fontRenderer);
        int height = 9;
        return MathUtil.centerArea(outer, width, height);
    }

    public static ImmutableRect2i centerArea(ImmutableRect2i outer, int width, int height) {
        return new ImmutableRect2i(outer.getX() + Math.round((float)(outer.getWidth() - width) / 2.0f), outer.getY() + Math.round((float)(outer.getHeight() - height) / 2.0f), width, height);
    }

    public static double distance(Vec2 start, Vec2 end) {
        double a = start.x - end.x;
        double b = start.y - end.y;
        return Math.sqrt(a * a + b * b);
    }

    public static ScreenRectangle transform(ImmutableRect2i rect, Matrix4f pose) {
        Vector3f topLeft = new Vector3f((float)rect.x(), (float)rect.y(), 1.0f);
        Vector3f bottomRight = new Vector3f((float)(rect.x() + rect.width()), (float)(rect.y() + rect.getHeight()), 1.0f);
        topLeft = pose.transformPosition(topLeft);
        bottomRight = pose.transformPosition(bottomRight);
        int x = Math.round(topLeft.x);
        int y = Math.round(topLeft.y);
        return new ScreenRectangle(x, y, Math.round(bottomRight.x) - x, Math.round(bottomRight.y) - y);
    }
}

