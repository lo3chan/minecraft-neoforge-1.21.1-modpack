package dev.isxander.yacl3.config.v2.api.autogen;

import dev.isxander.yacl3.api.Option;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public interface OptionAccess {
   @Nullable
   Option<?> getOption(String var1);

   void scheduleOptionOperation(String var1, Consumer<Option<?>> var2);
}
