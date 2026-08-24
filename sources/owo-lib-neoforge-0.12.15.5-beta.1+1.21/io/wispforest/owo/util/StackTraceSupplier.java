package io.wispforest.owo.util;

import java.util.function.Supplier;

public record StackTraceSupplier(StackTraceElement[] stackTrace, Supplier<String> message) implements Supplier<String> {
   public String get() {
      return this.message.get();
   }
}
