/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.sorting.serializers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface ISortingSerializer<T> {
    public List<T> read(Path var1) throws IOException;

    public void write(Path var1, List<T> var2) throws IOException;
}

