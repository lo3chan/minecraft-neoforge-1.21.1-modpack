package de.cristelknight.cristellib.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum ConfigType implements StringRepresentable {
   TOGGLE("ENABLE_DISABLE"),
   PLACEMENT("PLACEMENT");

   private final String name;
   public static final Codec<ConfigType> CODEC = StringRepresentable.fromEnum(ConfigType::values);

   private ConfigType(String name) {
      this.name = name;
   }

   @NotNull
   public String getSerializedName() {
      return this.name;
   }
}
