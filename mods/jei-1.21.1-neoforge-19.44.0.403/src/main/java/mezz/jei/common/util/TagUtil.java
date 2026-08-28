/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.core.HolderSet$Named
 *  net.minecraft.tags.TagKey
 */
package mezz.jei.common.util;

import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;

public class TagUtil {
    public static <VALUE, STACK> Optional<TagKey<?>> getTagEquivalent(Collection<STACK> stacks, Function<STACK, VALUE> stackToValue, Supplier<Stream<Pair<TagKey<VALUE>, HolderSet.Named<VALUE>>>> tagSupplier) {
        List values = stacks.stream().map(stackToValue).toList();
        return tagSupplier.get().filter(e -> {
            HolderSet.Named tag = (HolderSet.Named)e.getSecond();
            return TagUtil.areEquivalent(tag, values);
        }).map(Pair::getFirst).findFirst();
    }

    private static <VALUE> boolean areEquivalent(HolderSet.Named<VALUE> tag, List<VALUE> values) {
        int count = tag.size();
        if (count != values.size()) {
            return false;
        }
        for (int i = 0; i < count; ++i) {
            Object tagValue = tag.get(i).value();
            VALUE value = values.get(i);
            if (value.equals(tagValue)) continue;
            return false;
        }
        return true;
    }
}

