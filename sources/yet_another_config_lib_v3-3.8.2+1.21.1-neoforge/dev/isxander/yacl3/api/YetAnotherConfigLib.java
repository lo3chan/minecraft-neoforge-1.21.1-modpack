package dev.isxander.yacl3.api;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.config.ConfigInstance;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.YetAnotherConfigLibImpl;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface YetAnotherConfigLib {
   Component title();

   ImmutableList<ConfigCategory> categories();

   Runnable saveFunction();

   Consumer<YACLScreen> initConsumer();

   Screen generateScreen(@Nullable Screen var1);

   static YetAnotherConfigLib.Builder createBuilder() {
      return new YetAnotherConfigLibImpl.BuilderImpl();
   }

   static <T> YetAnotherConfigLib create(ConfigClassHandler<T> configHandler, YetAnotherConfigLib.ConfigBackedBuilder<T> builder) {
      return builder.build(configHandler.defaults(), configHandler.instance(), createBuilder().save(configHandler::save)).build();
   }

   @Deprecated
   static <T> YetAnotherConfigLib create(ConfigInstance<T> configInstance, YetAnotherConfigLib.ConfigBackedBuilder<T> builder) {
      return builder.build(configInstance.getDefaults(), configInstance.getConfig(), createBuilder().save(configInstance::save)).build();
   }

   public interface Builder {
      YetAnotherConfigLib.Builder title(@NotNull Component var1);

      YetAnotherConfigLib.Builder category(@NotNull ConfigCategory var1);

      default YetAnotherConfigLib.Builder category(@NotNull Supplier<ConfigCategory> categorySupplier) {
         return this.category(categorySupplier.get());
      }

      default YetAnotherConfigLib.Builder categoryIf(boolean condition, @NotNull ConfigCategory category) {
         return condition ? this.category(category) : this;
      }

      default YetAnotherConfigLib.Builder categoryIf(boolean condition, @NotNull Supplier<ConfigCategory> categorySupplier) {
         return condition ? this.category(categorySupplier) : this;
      }

      YetAnotherConfigLib.Builder categories(@NotNull Collection<? extends ConfigCategory> var1);

      default YetAnotherConfigLib.Builder categories(@NotNull Supplier<Collection<? extends ConfigCategory>> categoriesSupplier) {
         return this.categories(categoriesSupplier.get());
      }

      default YetAnotherConfigLib.Builder categoriesIf(boolean condition, @NotNull Collection<? extends ConfigCategory> categories) {
         return condition ? this.categories(categories) : this;
      }

      default YetAnotherConfigLib.Builder categoriesIf(boolean condition, @NotNull Supplier<Collection<? extends ConfigCategory>> categoriesSupplier) {
         return condition ? this.categories(categoriesSupplier) : this;
      }

      YetAnotherConfigLib.Builder save(@NotNull Runnable var1);

      YetAnotherConfigLib.Builder screenInit(@NotNull Consumer<YACLScreen> var1);

      YetAnotherConfigLib build();
   }

   @FunctionalInterface
   public interface ConfigBackedBuilder<T> {
      YetAnotherConfigLib.Builder build(T var1, T var2, YetAnotherConfigLib.Builder var3);
   }
}
