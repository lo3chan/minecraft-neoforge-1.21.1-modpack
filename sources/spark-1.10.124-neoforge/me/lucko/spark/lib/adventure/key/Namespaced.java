package me.lucko.spark.lib.adventure.key;

import org.jetbrains.annotations.NotNull;

public interface Namespaced {
   @NotNull
   @KeyPattern.Namespace
   String namespace();
}
