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

public class CycleTicker
implements ICycler {
    private static final int MAX_INDEX = 100000;
    private static final int TICKS_PER_UPDATE = 20;
    private int tick = 0;
    private int index;

    public static CycleTicker createWithRandomOffset() {
        int cycleOffset = (int)(Math.random() * 100000.0);
        return new CycleTicker(cycleOffset);
    }

    private CycleTicker(int cycleOffset) {
        this.index = cycleOffset;
    }

    @Override
    public <T> Optional<T> getCycled(List<@Nullable T> list) {
        if (list.isEmpty()) {
            return Optional.empty();
        }
        int index = this.index % list.size();
        T value = list.get(index);
        return Optional.ofNullable(value);
    }

    public boolean tick() {
        if (Internal.getKeyMappings().getPauseRecipeCycling().isDown()) {
            return false;
        }
        ++this.tick;
        if (this.tick >= 20) {
            this.tick = 0;
            ++this.index;
            return true;
        }
        return false;
    }
}

