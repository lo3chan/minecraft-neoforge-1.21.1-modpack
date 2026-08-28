/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.common.config.sorting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import mezz.jei.common.config.sorting.serializers.ISortingSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class SortingConfig<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Path path;
    private final ISortingSerializer<T> serializer;
    @Nullable
    private List<T> sorted;

    public SortingConfig(Path path, ISortingSerializer<T> serializer) {
        this.path = path;
        this.serializer = serializer;
    }

    protected abstract Comparator<T> getDefaultSortOrder();

    private void save(List<T> sorted) {
        try {
            this.serializer.write(this.path, sorted);
        }
        catch (IOException e) {
            LOGGER.error("Failed to save to file {}", (Object)this.path, (Object)e);
        }
    }

    private Optional<List<T>> loadSortedFromFile() {
        if (Files.exists(this.path, new LinkOption[0])) {
            try {
                List<T> result = this.serializer.read(this.path);
                return Optional.of(result);
            }
            catch (IOException e) {
                LOGGER.error("Failed to load from file: {}", (Object)this.path, (Object)e);
            }
        }
        return Optional.empty();
    }

    private void load(Collection<T> allValues) {
        Optional<List<List>> previousSorted = this.loadSortedFromFile();
        Comparator defaultOrder = this.getDefaultSortOrder();
        Comparator<T> sortOrder = previousSorted.map(s -> {
            Comparator<Object> existingOrder = Comparator.comparingInt(t -> SortingConfig.indexOfSort(s.indexOf(t)));
            return existingOrder.thenComparing(defaultOrder);
        }).orElse(defaultOrder);
        this.sorted = allValues.stream().distinct().sorted(sortOrder).toList();
        boolean changed = previousSorted.map(s -> !Objects.equals(s, this.sorted)).orElse(true);
        if (changed) {
            this.save(this.sorted);
        }
    }

    private static int indexOfSort(int index) {
        if (index < 0) {
            return Integer.MAX_VALUE;
        }
        return index;
    }

    private List<T> getSorted(Collection<T> allValues) {
        if (this.sorted == null) {
            this.load(allValues);
        }
        return this.sorted;
    }

    public <V> Comparator<V> getComparator(Collection<T> allValues, Function<V, T> mapping) {
        List sorted = this.getSorted(allValues);
        return Comparator.comparingInt(o -> {
            Object value = mapping.apply(o);
            return SortingConfig.indexOfSort(sorted.indexOf(value));
        });
    }
}

