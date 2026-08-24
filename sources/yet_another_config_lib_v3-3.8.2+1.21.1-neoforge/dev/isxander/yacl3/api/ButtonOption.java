package dev.isxander.yacl3.api;

import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.ButtonOptionImpl;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface ButtonOption extends Option<BiConsumer<YACLScreen, ButtonOption>> {
   BiConsumer<YACLScreen, ButtonOption> action();

   static ButtonOption.Builder createBuilder() {
      return new ButtonOptionImpl.BuilderImpl();
   }

   public interface Builder {
      ButtonOption.Builder name(@NotNull Component var1);

      ButtonOption.Builder text(@NotNull Component var1);

      ButtonOption.Builder description(@NotNull OptionDescription var1);

      ButtonOption.Builder action(@NotNull BiConsumer<YACLScreen, ButtonOption> var1);

      @Deprecated
      ButtonOption.Builder action(@NotNull Consumer<YACLScreen> var1);

      ButtonOption.Builder available(boolean var1);

      ButtonOption build();
   }
}
