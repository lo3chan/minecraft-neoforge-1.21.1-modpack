package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableSet;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.OptionImpl;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public interface Option<T> {
   @NotNull
   Component name();

   @NotNull
   OptionDescription description();

   @Deprecated
   @NotNull
   Component tooltip();

   @NotNull
   Controller<T> controller();

   @NotNull
   StateManager<T> stateManager();

   @Deprecated
   @NotNull
   Binding<T> binding();

   boolean available();

   void setAvailable(boolean var1);

   @NotNull
   ImmutableSet<OptionFlag> flags();

   boolean changed();

   @NotNull
   T pendingValue();

   void requestSet(@NotNull T var1);

   boolean applyValue();

   void forgetPendingValue();

   void requestSetDefault();

   boolean isPendingValueDefault();

   default boolean canResetToDefault() {
      return true;
   }

   void addEventListener(OptionEventListener<T> var1);

   @Deprecated
   void addListener(BiConsumer<Option<T>, T> var1);

   static <T> Option.Builder<T> createBuilder() {
      return new OptionImpl.BuilderImpl<>();
   }

   @Deprecated
   static <T> Option.Builder<T> createBuilder(Class<T> typeClass) {
      return createBuilder();
   }

   public interface Builder<T> {
      Option.Builder<T> name(@NotNull Component var1);

      Option.Builder<T> description(@NotNull OptionDescription var1);

      Option.Builder<T> description(@NotNull Function<T, OptionDescription> var1);

      Option.Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> var1);

      Option.Builder<T> customController(@NotNull Function<Option<T>, Controller<T>> var1);

      Option.Builder<T> stateManager(@NotNull StateManager<T> var1);

      Option.Builder<T> binding(@NotNull Binding<T> var1);

      Option.Builder<T> binding(@NotNull T var1, @NotNull Supplier<T> var2, @NotNull Consumer<T> var3);

      Option.Builder<T> available(boolean var1);

      Option.Builder<T> flag(@NotNull OptionFlag... var1);

      Option.Builder<T> flags(@NotNull Collection<? extends OptionFlag> var1);

      Option.Builder<T> addListener(@NotNull OptionEventListener<T> var1);

      Option.Builder<T> addListeners(@NotNull Collection<OptionEventListener<T>> var1);

      @Deprecated
      Option.Builder<T> instant(boolean var1);

      @Deprecated
      Option.Builder<T> listener(@NotNull BiConsumer<Option<T>, T> var1);

      @Deprecated
      Option.Builder<T> listeners(@NotNull Collection<BiConsumer<Option<T>, T>> var1);

      Option<T> build();
   }
}
