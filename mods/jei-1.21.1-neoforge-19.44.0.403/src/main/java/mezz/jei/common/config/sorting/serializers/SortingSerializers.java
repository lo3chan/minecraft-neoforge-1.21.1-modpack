/*
 * Decompiled with CFR 0.152.
 */
package mezz.jei.common.config.sorting.serializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.List;
import mezz.jei.common.config.sorting.serializers.ISortingSerializer;

public final class SortingSerializers {
    public static final ISortingSerializer<String> STRING = new ISortingSerializer<String>(){

        @Override
        public List<String> read(Path path) throws IOException {
            return Files.readAllLines(path);
        }

        @Override
        public void write(Path path, List<String> sorted) throws IOException {
            Files.write(path, sorted, new OpenOption[0]);
        }
    };

    private SortingSerializers() {
    }
}

