package dev.isxander.yacl3.impl;

import com.google.common.collect.ImmutableList;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.PlaceholderCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.impl.utils.YACLConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class YetAnotherConfigLibImpl implements YetAnotherConfigLib {
   private final Component title;
   private final ImmutableList<ConfigCategory> categories;
   private final Runnable saveFunction;
   private final Consumer<YACLScreen> initConsumer;
   private boolean generated = false;

   public YetAnotherConfigLibImpl(Component title, ImmutableList<ConfigCategory> categories, Runnable saveFunction, Consumer<YACLScreen> initConsumer) {
      this.title = title;
      this.categories = categories;
      this.saveFunction = saveFunction;
      this.initConsumer = initConsumer;
   }

   @Override
   public Screen generateScreen(Screen parent) {
      if (this.generated) {
         throw new UnsupportedOperationException(
            "To prevent memory leaks, you should only generate a Screen once per instance. Please re-build the instance to generate another GUI."
         );
      } else {
         YACLConstants.LOGGER.info("Generating YACL screen");
         this.generated = true;
         return new YACLScreen(this, parent);
      }
   }

   @Override
   public Component title() {
      return this.title;
   }

   @Override
   public ImmutableList<ConfigCategory> categories() {
      return this.categories;
   }

   @Override
   public Runnable saveFunction() {
      return this.saveFunction;
   }

   @Override
   public Consumer<YACLScreen> initConsumer() {
      return this.initConsumer;
   }

   @Internal
   public static final class BuilderImpl implements YetAnotherConfigLib.Builder {
      private Component title;
      private final List<ConfigCategory> categories = new ArrayList<>();
      private Runnable saveFunction = () -> {};
      private Consumer<YACLScreen> initConsumer = screen -> {};

      @Override
      public YetAnotherConfigLib.Builder title(@NotNull Component title) {
         Validate.notNull(title, "`title` cannot be null", new Object[0]);
         this.title = title;
         return this;
      }

      @Override
      public YetAnotherConfigLib.Builder category(@NotNull ConfigCategory category) {
         Validate.notNull(category, "`category` cannot be null", new Object[0]);
         this.categories.add(category);
         return this;
      }

      @Override
      public YetAnotherConfigLib.Builder categories(@NotNull Collection<? extends ConfigCategory> categories) {
         Validate.notNull(categories, "`categories` cannot be null", new Object[0]);
         this.categories.addAll(categories);
         return this;
      }

      @Override
      public YetAnotherConfigLib.Builder save(@NotNull Runnable saveFunction) {
         Validate.notNull(saveFunction, "`saveFunction` cannot be null", new Object[0]);
         this.saveFunction = saveFunction;
         return this;
      }

      @Override
      public YetAnotherConfigLib.Builder screenInit(@NotNull Consumer<YACLScreen> initConsumer) {
         Validate.notNull(initConsumer, "`initConsumer` cannot be null", new Object[0]);
         this.initConsumer = initConsumer;
         return this;
      }

      @Override
      public YetAnotherConfigLib build() {
         Validate.notNull(this.title, "`title must not be null to build `YetAnotherConfigLib`", new Object[0]);
         Validate.notEmpty(this.categories, "`categories` must not be empty to build `YetAnotherConfigLib`", new Object[0]);
         Validate.isTrue(
            !this.categories.stream().allMatch(category -> category instanceof PlaceholderCategory),
            "At least one regular category is required to build `YetAnotherConfigLib`",
            new Object[0]
         );
         return new YetAnotherConfigLibImpl(this.title, ImmutableList.copyOf(this.categories), this.saveFunction, this.initConsumer);
      }
   }
}
