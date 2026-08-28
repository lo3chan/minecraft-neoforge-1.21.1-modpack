/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnegative
 */
package mezz.jei.common.util;

import javax.annotation.Nonnegative;

public record ImmutableSize2i(@Nonnegative int width, @Nonnegative int height) {
    public static final ImmutableSize2i EMPTY = new ImmutableSize2i(0, 0);

    public int getArea() {
        return this.width * this.height;
    }
}

