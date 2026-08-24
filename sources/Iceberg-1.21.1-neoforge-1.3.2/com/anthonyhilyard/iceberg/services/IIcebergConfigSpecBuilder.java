package com.anthonyhilyard.iceberg.services;

import com.anthonyhilyard.iceberg.config.IIcebergConfigSpec;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

public interface IIcebergConfigSpecBuilder {
   <T> Pair<T, IIcebergConfigSpec> finish(Function<IIcebergConfigSpecBuilder, T> var1);

   void reset();

   IIcebergConfigSpecBuilder comment(String var1);

   IIcebergConfigSpecBuilder comment(String... var1);

   IIcebergConfigSpecBuilder translation(String var1);

   IIcebergConfigSpecBuilder push(String var1);

   IIcebergConfigSpecBuilder push(List<String> var1);

   IIcebergConfigSpecBuilder pop();

   <T> Supplier<T> add(String var1, T var2);

   <T> Supplier<T> add(String var1, T var2, Predicate<Object> var3);

   <V extends Comparable<? super V>> Supplier<V> addInRange(String var1, V var2, V var3, V var4, Class<V> var5);

   <T> Supplier<T> addInList(String var1, T var2, Collection<? extends T> var3);

   <T> Supplier<List<? extends T>> addList(String var1, List<? extends T> var2, Predicate<Object> var3);

   <T> Supplier<List<? extends T>> addListAllowEmpty(String var1, List<? extends T> var2, Predicate<Object> var3);

   <V extends Enum<V>> Supplier<V> addEnum(String var1, V var2);

   <V extends Enum<V>> Supplier<V> addEnum(String var1, V var2, Predicate<Object> var3);

   Supplier<Boolean> add(String var1, boolean var2);

   Supplier<Double> addInRange(String var1, double var2, double var4, double var6);

   Supplier<Integer> addInRange(String var1, int var2, int var3, int var4);

   Supplier<Long> addInRange(String var1, long var2, long var4, long var6);

   Supplier<Map<String, Object>> addSubconfig(String var1, Map<String, Object> var2, Predicate<Object> var3, Predicate<Object> var4);
}
