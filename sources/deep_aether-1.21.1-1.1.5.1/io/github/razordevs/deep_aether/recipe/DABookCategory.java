package io.github.razordevs.deep_aether.recipe;

import net.minecraft.util.StringRepresentable;
import net.minecraft.util.StringRepresentable.EnumCodec;
import org.jetbrains.annotations.NotNull;

public enum DABookCategory implements StringRepresentable {
   COMBINEABLE_FODDER("combinable_fodder"),
   COMBINEABLE_MISC("combinable_misc"),
   UNKNOWN("unknown");

   public static final EnumCodec<DABookCategory> CODEC = StringRepresentable.fromEnum(DABookCategory::values);
   private final String name;

   private DABookCategory(String name) {
      this.name = name;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }
}
