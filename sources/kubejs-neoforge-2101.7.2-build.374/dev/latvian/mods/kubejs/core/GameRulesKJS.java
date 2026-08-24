package dev.latvian.mods.kubejs.core;

import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Value;
import org.jetbrains.annotations.Nullable;

public interface GameRulesKJS {
   @Nullable
   Value<?> kjs$get(String rule);

   void kjs$set(String rule, String value);

   default String kjs$getString(String rule) {
      Value<? extends Value<?>> o = (Value<? extends Value<?>>)this.kjs$get(rule);
      return o == null ? "" : o.serialize();
   }

   default boolean kjs$getBoolean(String rule) {
      return this.kjs$get(rule) instanceof BooleanValue v && v.get();
   }

   default int kjs$getInt(String rule) {
      return this.kjs$get(rule) instanceof IntegerValue v ? v.get() : 0;
   }
}
