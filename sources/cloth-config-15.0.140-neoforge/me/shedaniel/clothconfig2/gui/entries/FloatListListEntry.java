package me.shedaniel.clothconfig2.gui.entries;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@OnlyIn(Dist.CLIENT)
public class FloatListListEntry extends AbstractTextFieldListListEntry<Float, FloatListListEntry.FloatListCell, FloatListListEntry> {
   private float minimum = -1.0F / 0.0F;
   private float maximum = 1.0F / 0.0F;

   @Deprecated
   @Internal
   public FloatListListEntry(
      Component fieldName,
      List<Float> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Float>> saveConsumer,
      Supplier<List<Float>> defaultValue,
      Component resetButtonKey
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
   }

   @Deprecated
   @Internal
   public FloatListListEntry(
      Component fieldName,
      List<Float> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Float>> saveConsumer,
      Supplier<List<Float>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
   }

   @Deprecated
   @Internal
   public FloatListListEntry(
      Component fieldName,
      List<Float> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Float>> saveConsumer,
      Supplier<List<Float>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 1
      // 02: aload 2
      // 03: iload 3
      // 04: aload 4
      // 06: aload 5
      // 08: aload 6
      // 0a: aload 7
      // 0c: iload 8
      // 0e: iload 9
      // 10: iload 10
      // 12: invokedynamic apply ()Ljava/util/function/BiFunction; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, me/shedaniel/clothconfig2/gui/entries/FloatListListEntry$FloatListCell.<init> (Ljava/lang/Float;Lme/shedaniel/clothconfig2/gui/entries/FloatListListEntry;)V, (Ljava/lang/Float;Lme/shedaniel/clothconfig2/gui/entries/FloatListListEntry;)Lme/shedaniel/clothconfig2/gui/entries/FloatListListEntry$FloatListCell; ]
      // 17: invokespecial me/shedaniel/clothconfig2/gui/entries/AbstractTextFieldListListEntry.<init> (Lnet/minecraft/network/chat/Component;Ljava/util/List;ZLjava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Lnet/minecraft/network/chat/Component;ZZZLjava/util/function/BiFunction;)V
      // 1a: aload 0
      // 1b: ldc -Infinity
      // 1d: putfield me/shedaniel/clothconfig2/gui/entries/FloatListListEntry.minimum F
      // 20: aload 0
      // 21: ldc Infinity
      // 23: putfield me/shedaniel/clothconfig2/gui/entries/FloatListListEntry.maximum F
      // 26: return
   }

   public FloatListListEntry setMaximum(float maximum) {
      this.maximum = maximum;
      return this;
   }

   public FloatListListEntry setMinimum(float minimum) {
      this.minimum = minimum;
      return this;
   }

   public FloatListListEntry self() {
      return this;
   }

   public static class FloatListCell
      extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Float, FloatListListEntry.FloatListCell, FloatListListEntry> {
      public FloatListCell(Float value, FloatListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected Float substituteDefault(@Nullable Float value) {
         return value == null ? 0.0F : value;
      }

      @Override
      protected boolean isValidText(@NotNull String text) {
         return text.chars().allMatch(c -> Character.isDigit(c) || c == 45 || c == 46);
      }

      public Float getValue() {
         try {
            return Float.valueOf(this.widget.getValue());
         } catch (NumberFormatException var2) {
            return 0.0F;
         }
      }

      @Override
      public Optional<Component> getError() {
         try {
            float i = Float.parseFloat(this.widget.getValue());
            if (i > this.listListEntry.maximum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.listListEntry.maximum}));
            }

            if (i < this.listListEntry.minimum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.listListEntry.minimum}));
            }
         } catch (NumberFormatException var2) {
            return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_float"));
         }

         return Optional.empty();
      }
   }
}
