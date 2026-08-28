/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.ingredients;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public interface ICycler {
    public <T> Optional<T> getCycled(List<@Nullable T> var1);
}

