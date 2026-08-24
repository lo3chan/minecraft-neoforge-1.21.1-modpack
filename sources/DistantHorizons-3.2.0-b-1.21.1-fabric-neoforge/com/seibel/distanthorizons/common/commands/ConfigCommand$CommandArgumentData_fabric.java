package com.seibel.distanthorizons.common.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.seibel.distanthorizons.core.config.types.ConfigEntry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.class_2168;

class ConfigCommand$CommandArgumentData_fabric<T> {
   public final Class<T> argumentClass;
   public final Function<ConfigEntry<T>, ArgumentType<T>> argumentTypeFunction;
   private final BiFunction<CommandContext<class_2168>, String, T> valueGetter;

   public ConfigCommand$CommandArgumentData_fabric(
      Class<T> argumentClass, Supplier<ArgumentType<T>> argumentTypeSupplier, BiFunction<CommandContext<class_2168>, String, T> valueGetter
   ) {
      this(argumentClass, configEntry -> argumentTypeSupplier.get(), valueGetter);
   }

   public ConfigCommand$CommandArgumentData_fabric(
      Class<T> argumentClass, Function<ConfigEntry<T>, ArgumentType<T>> argumentTypeFunction, BiFunction<CommandContext<class_2168>, String, T> valueGetter
   ) {
      this.argumentClass = argumentClass;
      this.argumentTypeFunction = argumentTypeFunction;
      this.valueGetter = valueGetter;
   }

   public ArgumentType<T> getArgumentType(ConfigEntry<T> configEntry) {
      return this.argumentTypeFunction.apply(configEntry);
   }

   public T getValue(CommandContext<class_2168> commandContext, String argumentName) {
      return this.valueGetter.apply(commandContext, argumentName);
   }
}
