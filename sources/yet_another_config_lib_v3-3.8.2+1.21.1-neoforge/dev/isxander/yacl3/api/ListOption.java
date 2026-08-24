package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.impl.ListOptionImpl;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ListOption<T> extends OptionGroup, Option<List<T>> {
   @NotNull
   @Override
   ImmutableList<ListOptionEntry<T>> options();

   @Internal
   int numberOfEntries();

   @Internal
   int maximumNumberOfEntries();

   @Internal
   int minimumNumberOfEntries();

   @Internal
   ListOptionEntry<T> insertNewEntry();

   @Internal
   void insertEntry(int var1, ListOptionEntry<?> var2);

   @Internal
   int indexOf(ListOptionEntry<?> var1);

   @Internal
   void removeEntry(ListOptionEntry<?> var1);

   @Internal
   void addRefreshListener(Runnable var1);

   static <T> ListOption.Builder<T> createBuilder() {
      return new ListOptionImpl.BuilderImpl<>();
   }

   @Deprecated
   static <T> ListOption.Builder<T> createBuilder(Class<T> typeClass) {
      return createBuilder();
   }

   public interface Builder<T> {
      ListOption.Builder<T> name(@NotNull Component var1);

      ListOption.Builder<T> description(@NotNull OptionDescription var1);

      ListOption.Builder<T> initial(@NotNull Supplier<T> var1);

      ListOption.Builder<T> initial(@NotNull T var1);

      ListOption.Builder<T> controller(@NotNull Function<Option<T>, ControllerBuilder<T>> var1);

      ListOption.Builder<T> customController(@NotNull Function<ListOptionEntry<T>, Controller<T>> var1);

      ListOption.Builder<T> state(@NotNull StateManager<List<T>> var1);

      ListOption.Builder<T> binding(@NotNull Binding<List<T>> var1);

      ListOption.Builder<T> binding(@NotNull List<T> var1, @NotNull Supplier<List<T>> var2, @NotNull Consumer<List<T>> var3);

      ListOption.Builder<T> available(boolean var1);

      ListOption.Builder<T> minimumNumberOfEntries(int var1);

      ListOption.Builder<T> maximumNumberOfEntries(int var1);

      ListOption.Builder<T> insertEntriesAtEnd(boolean var1);

      ListOption.Builder<T> flag(@NotNull OptionFlag... var1);

      ListOption.Builder<T> flags(@NotNull Collection<OptionFlag> var1);

      ListOption.Builder<T> collapsed(boolean var1);

      ListOption.Builder<T> addListener(@NotNull OptionEventListener<List<T>> var1);

      ListOption.Builder<T> addListeners(@NotNull Collection<OptionEventListener<List<T>>> var1);

      ListOption.Builder<T> listener(@NotNull BiConsumer<Option<List<T>>, List<T>> var1);

      ListOption.Builder<T> listeners(@NotNull Collection<BiConsumer<Option<List<T>>, List<T>>> var1);

      ListOption<T> build();
   }
}
