package net.blay09.mods.balm.api.proxy;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ModProxy<T> {
   ModProxy<T> with(String var1, String var2);

   ModProxy<T> with(String var1, String var2, String var3);

   ModProxy<T> withMultiplexer(Function<List<T>, T> var1);

   ModProxy<T> withFallback(T var1);

   T build();

   Supplier<T> buildLazily();
}
