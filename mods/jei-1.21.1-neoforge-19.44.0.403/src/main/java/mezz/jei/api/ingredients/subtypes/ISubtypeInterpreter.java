/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.api.ingredients.subtypes;

import mezz.jei.api.ingredients.subtypes.UidContext;
import org.jetbrains.annotations.Nullable;

public interface ISubtypeInterpreter<T> {
    @Nullable
    public Object getSubtypeData(T var1, UidContext var2);

    @Deprecated(since="19.9.0")
    public String getLegacyStringSubtypeInfo(T var1, UidContext var2);
}

