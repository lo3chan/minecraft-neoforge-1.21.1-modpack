package mezz.jei.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.tags.TagKey;

public class TagUtil {
   public static <VALUE, STACK> Optional<TagKey<?>> getTagEquivalent(
      Collection<STACK> stacks, Function<STACK, VALUE> stackToValue, Supplier<Stream<com.mojang.datafixers.util.Pair<TagKey<VALUE>, Named<VALUE>>>> tagSupplier
   ) {
      List<VALUE> values = stacks.stream().map(stackToValue).toList();
      return tagSupplier.get().filter(e -> {
         Named<VALUE> tag = (Named<VALUE>)e.getSecond();
         return areEquivalent(tag, values);
      }).<TagKey<?>>map(com.mojang.datafixers.util.Pair::getFirst).findFirst();
   }

   private static <VALUE> boolean areEquivalent(Named<VALUE> tag, List<VALUE> values) {
      int count = tag.size();
      if (count != values.size()) {
         return false;
      } else {
         for (int i = 0; i < count; i++) {
            VALUE tagValue = (VALUE)tag.get(i).value();
            VALUE value = values.get(i);
            if (!value.equals(tagValue)) {
               return false;
            }
         }

         return true;
      }
   }
}
