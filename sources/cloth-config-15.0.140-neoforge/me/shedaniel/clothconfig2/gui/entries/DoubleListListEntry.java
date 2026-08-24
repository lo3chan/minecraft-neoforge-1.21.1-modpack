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
public class DoubleListListEntry extends AbstractTextFieldListListEntry<Double, DoubleListListEntry.DoubleListCell, DoubleListListEntry> {
   private double minimum = -1.0 / 0.0;
   private double maximum = 1.0 / 0.0;

   @Deprecated
   @Internal
   public DoubleListListEntry(
      Component fieldName,
      List<Double> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Double>> saveConsumer,
      Supplier<List<Double>> defaultValue,
      Component resetButtonKey
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
   }

   @Deprecated
   @Internal
   public DoubleListListEntry(
      Component fieldName,
      List<Double> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Double>> saveConsumer,
      Supplier<List<Double>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
   }

   @Deprecated
   @Internal
   public DoubleListListEntry(
      Component fieldName,
      List<Double> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Double>> saveConsumer,
      Supplier<List<Double>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.stream.StreamSupport.stream(StreamSupport.java:70)
      //   at java.base/java.util.Collection.stream(Collection.java:743)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$21(InvocationExprent.java:1663)
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
      // 12: invokedynamic apply ()Ljava/util/function/BiFunction; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, me/shedaniel/clothconfig2/gui/entries/DoubleListListEntry$DoubleListCell.<init> (Ljava/lang/Double;Lme/shedaniel/clothconfig2/gui/entries/DoubleListListEntry;)V, (Ljava/lang/Double;Lme/shedaniel/clothconfig2/gui/entries/DoubleListListEntry;)Lme/shedaniel/clothconfig2/gui/entries/DoubleListListEntry$DoubleListCell; ]
      // 17: invokespecial me/shedaniel/clothconfig2/gui/entries/AbstractTextFieldListListEntry.<init> (Lnet/minecraft/network/chat/Component;Ljava/util/List;ZLjava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Lnet/minecraft/network/chat/Component;ZZZLjava/util/function/BiFunction;)V
      // 1a: aload 0
      // 1b: ldc2_w -Infinity
      // 1e: putfield me/shedaniel/clothconfig2/gui/entries/DoubleListListEntry.minimum D
      // 21: aload 0
      // 22: ldc2_w Infinity
      // 25: putfield me/shedaniel/clothconfig2/gui/entries/DoubleListListEntry.maximum D
      // 28: return
   }

   public DoubleListListEntry setMaximum(Double maximum) {
      this.maximum = maximum;
      return this;
   }

   public DoubleListListEntry setMinimum(Double minimum) {
      this.minimum = minimum;
      return this;
   }

   public DoubleListListEntry self() {
      return this;
   }

   public static class DoubleListCell
      extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Double, DoubleListListEntry.DoubleListCell, DoubleListListEntry> {
      public DoubleListCell(Double value, DoubleListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected Double substituteDefault(@Nullable Double value) {
         return value == null ? 0.0 : value;
      }

      @Override
      protected boolean isValidText(@NotNull String text) {
         return text.chars().allMatch(c -> Character.isDigit(c) || c == 45 || c == 46);
      }

      public Double getValue() {
         try {
            return Double.valueOf(this.widget.getValue());
         } catch (NumberFormatException var2) {
            return 0.0;
         }
      }

      @Override
      public Optional<Component> getError() {
         try {
            double i = Double.parseDouble(this.widget.getValue());
            if (i > this.listListEntry.maximum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.listListEntry.maximum}));
            }

            if (i < this.listListEntry.minimum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.listListEntry.minimum}));
            }
         } catch (NumberFormatException var3) {
            return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_double"));
         }

         return Optional.empty();
      }
   }
}
