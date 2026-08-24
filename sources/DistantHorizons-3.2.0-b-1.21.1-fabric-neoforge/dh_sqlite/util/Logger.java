package dh_sqlite.util;

import java.util.function.Supplier;

public interface Logger {
   void trace(Supplier<String> supplier);

   void info(Supplier<String> supplier);

   void warn(Supplier<String> supplier);

   void error(Supplier<String> supplier, Throwable throwable);
}
