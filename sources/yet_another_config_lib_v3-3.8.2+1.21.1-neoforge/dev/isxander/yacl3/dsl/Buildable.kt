package dev.isxander.yacl3.dsl

import java.util.concurrent.CompletableFuture

public interface Buildable<T> {
   public val built: CompletableFuture<Any>

   public abstract fun build(): Any {
   }
}
