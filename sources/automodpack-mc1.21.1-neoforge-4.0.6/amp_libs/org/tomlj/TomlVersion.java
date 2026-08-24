package amp_libs.org.tomlj;

import amp_libs.org.checkerframework.checker.nullness.qual.Nullable;

public enum TomlVersion {
   V0_4_0(null),
   V0_5_0(null),
   V1_0_0(null),
   LATEST(V1_0_0),
   HEAD(null);

   final TomlVersion canonical;

   private TomlVersion(@Nullable TomlVersion canonical) {
      this.canonical = canonical != null ? canonical : this;
   }

   boolean after(TomlVersion other) {
      return this.ordinal() > other.ordinal();
   }
}
