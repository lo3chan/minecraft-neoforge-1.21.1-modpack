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
public class IntegerListListEntry extends AbstractTextFieldListListEntry<Integer, IntegerListListEntry.IntegerListCell, IntegerListListEntry> {
   private int minimum = -2147483648;
   private int maximum = 2147483647;

   @Deprecated
   @Internal
   public IntegerListListEntry(
      Component fieldName,
      List<Integer> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Integer>> saveConsumer,
      Supplier<List<Integer>> defaultValue,
      Component resetButtonKey
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
   }

   @Deprecated
   @Internal
   public IntegerListListEntry(
      Component fieldName,
      List<Integer> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Integer>> saveConsumer,
      Supplier<List<Integer>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
   }

   @Deprecated
   @Internal
   public IntegerListListEntry(
      Component fieldName,
      List<Integer> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Integer>> saveConsumer,
      Supplier<List<Integer>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.stream.StreamOpFlag.fromCharacteristics(StreamOpFlag.java:750)
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
      // 12: invokedynamic apply ()Ljava/util/function/BiFunction; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, me/shedaniel/clothconfig2/gui/entries/IntegerListListEntry$IntegerListCell.<init> (Ljava/lang/Integer;Lme/shedaniel/clothconfig2/gui/entries/IntegerListListEntry;)V, (Ljava/lang/Integer;Lme/shedaniel/clothconfig2/gui/entries/IntegerListListEntry;)Lme/shedaniel/clothconfig2/gui/entries/IntegerListListEntry$IntegerListCell; ]
      // 17: invokespecial me/shedaniel/clothconfig2/gui/entries/AbstractTextFieldListListEntry.<init> (Lnet/minecraft/network/chat/Component;Ljava/util/List;ZLjava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Lnet/minecraft/network/chat/Component;ZZZLjava/util/function/BiFunction;)V
      // 1a: aload 0
      // 1b: ldc -2147483648
      // 1d: putfield me/shedaniel/clothconfig2/gui/entries/IntegerListListEntry.minimum I
      // 20: aload 0
      // 21: ldc 2147483647
      // 23: putfield me/shedaniel/clothconfig2/gui/entries/IntegerListListEntry.maximum I
      // 26: return
   }

   public IntegerListListEntry setMaximum(int maximum) {
      this.maximum = maximum;
      return this;
   }

   public IntegerListListEntry setMinimum(int minimum) {
      this.minimum = minimum;
      return this;
   }

   public IntegerListListEntry self() {
      return this;
   }

   public static class IntegerListCell
      extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Integer, IntegerListListEntry.IntegerListCell, IntegerListListEntry> {
      public IntegerListCell(Integer value, IntegerListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected Integer substituteDefault(@Nullable Integer value) {
         return value == null ? 0 : value;
      }

      @Override
      protected boolean isValidText(@NotNull String text) {
         return text.chars().allMatch(c -> Character.isDigit(c) || c == 45);
      }

      public Integer getValue() {
         try {
            return Integer.valueOf(this.widget.getValue());
         } catch (NumberFormatException var2) {
            return 0;
         }
      }

      @Override
      public Optional<Component> getError() {
         try {
            int i = Integer.parseInt(this.widget.getValue());
            if (i > this.listListEntry.maximum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.listListEntry.maximum}));
            }

            if (i < this.listListEntry.minimum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.listListEntry.minimum}));
            }
         } catch (NumberFormatException var2) {
            return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_int"));
         }

         return Optional.empty();
      }
   }
}
