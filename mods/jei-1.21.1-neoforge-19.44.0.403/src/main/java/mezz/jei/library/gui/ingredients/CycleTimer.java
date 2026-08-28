/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package mezz.jei.library.gui.ingredients;

import java.util.List;
import java.util.Optional;
import mezz.jei.common.Internal;
import mezz.jei.library.gui.ingredients.ICycler;
import org.jetbrains.annotations.Nullable;

public class CycleTimer
implements ICycler {
    private static final CycleTimer ZERO_OFFSET = new CycleTimer(0);
    private static final int MAX_INDEX = 100000;
    private static final int CYCLE_TIME_MS = 1000;
    private final int cycleOffset;
    private int index;

    public static CycleTimer create(int offset) {
        if (offset == 0) {
            return ZERO_OFFSET;
        }
        return new CycleTimer(offset);
    }

    public static CycleTimer createWithRandomOffset() {
        int cycleOffset = (int)(Math.random() * 100000.0);
        return new CycleTimer(cycleOffset);
    }

    private CycleTimer(int cycleOffset) {
        this.cycleOffset = cycleOffset;
        long now = System.currentTimeMillis();
        this.index = CycleTimer.calculateIndex(now, cycleOffset);
    }

    private static int calculateIndex(long now, int cycleOffset) {
        long index = now / 1000L % 100000L + (long)cycleOffset;
        return Math.toIntExact(index);
    }

    @Override
    public <T> Optional<T> getCycled(List<@Nullable T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        if (!Internal.getKeyMappings().getPauseRecipeCycling().isDown()) {
            long now = System.currentTimeMillis();
            this.index = CycleTimer.calculateIndex(now, this.cycleOffset);
        }
        int index = this.index % list.size();
        T value = list.get(index);
        return Optional.ofNullable(value);
    }
}

