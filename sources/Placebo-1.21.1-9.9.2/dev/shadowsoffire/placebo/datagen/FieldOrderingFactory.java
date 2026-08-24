package dev.shadowsoffire.placebo.datagen;

import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.Nullable;

public interface FieldOrderingFactory {
   @Nullable
   Comparator<String> getKeyComparator(JsonElement var1, Path var2);

   static void register(FieldOrderingFactory factory) {
      Objects.requireNonNull(factory, "Cannot register a null FieldOrderingFactory");
      FieldOrderingFactory.Impl.FACTORIES.add(factory);
   }

   static FieldOrderingFactory forType(String objPath, Consumer<Object2IntOpenHashMap<String>> orderBuilder) {
      return FilteredOrderingFactory.builder().forObjectPath(objPath).orderMap(orderBuilder).build();
   }

   static FieldOrderingFactory forSubtypedObject(String objPath, String type, Consumer<Object2IntOpenHashMap<String>> orderBuilder) {
      return FilteredOrderingFactory.builder().forObjectPath(objPath).forObjectSubtype(type).orderMap(orderBuilder).build();
   }

   public static class Impl {
      private static final List<FieldOrderingFactory> FACTORIES = new ArrayList<>();

      public static Comparator<String> getComparatorFor(JsonElement json, Path path) {
         for (FieldOrderingFactory factory : FACTORIES) {
            Comparator<String> comparator = factory.getKeyComparator(json, path);
            if (comparator != null) {
               return comparator;
            }
         }

         return DataProvider.KEY_COMPARATOR;
      }
   }
}
