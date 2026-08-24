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
public class LongListListEntry extends AbstractTextFieldListListEntry<Long, LongListListEntry.LongListCell, LongListListEntry> {
   private long minimum = -9223372036854775808L;
   private long maximum = 9223372036854775807L;

   @Deprecated
   @Internal
   public LongListListEntry(
      Component fieldName,
      List<Long> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Long>> saveConsumer,
      Supplier<List<Long>> defaultValue,
      Component resetButtonKey
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
   }

   @Deprecated
   @Internal
   public LongListListEntry(
      Component fieldName,
      List<Long> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Long>> saveConsumer,
      Supplier<List<Long>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
   }

   @Deprecated
   @Internal
   public LongListListEntry(
      Component fieldName,
      List<Long> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<Long>> saveConsumer,
      Supplier<List<Long>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.main.DecompilerContext.getCurrentContext(DecompilerContext.java:67)
      //   at org.jetbrains.java.decompiler.main.DecompilerContext.getStructContext(DecompilerContext.java:137)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
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
      // 12: invokedynamic apply ()Ljava/util/function/BiFunction; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, me/shedaniel/clothconfig2/gui/entries/LongListListEntry$LongListCell.<init> (Ljava/lang/Long;Lme/shedaniel/clothconfig2/gui/entries/LongListListEntry;)V, (Ljava/lang/Long;Lme/shedaniel/clothconfig2/gui/entries/LongListListEntry;)Lme/shedaniel/clothconfig2/gui/entries/LongListListEntry$LongListCell; ]
      // 17: invokespecial me/shedaniel/clothconfig2/gui/entries/AbstractTextFieldListListEntry.<init> (Lnet/minecraft/network/chat/Component;Ljava/util/List;ZLjava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Lnet/minecraft/network/chat/Component;ZZZLjava/util/function/BiFunction;)V
      // 1a: aload 0
      // 1b: ldc2_w -9223372036854775808
      // 1e: putfield me/shedaniel/clothconfig2/gui/entries/LongListListEntry.minimum J
      // 21: aload 0
      // 22: ldc2_w 9223372036854775807
      // 25: putfield me/shedaniel/clothconfig2/gui/entries/LongListListEntry.maximum J
      // 28: return
   }

   public LongListListEntry setMaximum(long maximum) {
      this.maximum = maximum;
      return this;
   }

   public LongListListEntry setMinimum(long minimum) {
      this.minimum = minimum;
      return this;
   }

   public LongListListEntry self() {
      return this;
   }

   public static class LongListCell extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<Long, LongListListEntry.LongListCell, LongListListEntry> {
      public LongListCell(Long value, LongListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected Long substituteDefault(@Nullable Long value) {
         return value == null ? 0L : value;
      }

      @Override
      protected boolean isValidText(@NotNull String text) {
         return text.chars().allMatch(c -> Character.isDigit(c) || c == 45);
      }

      public Long getValue() {
         try {
            return Long.valueOf(this.widget.getValue());
         } catch (NumberFormatException var2) {
            return 0L;
         }
      }

      @Override
      public Optional<Component> getError() {
         try {
            long l = Long.parseLong(this.widget.getValue());
            if (l > this.listListEntry.maximum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_large", new Object[]{this.listListEntry.maximum}));
            }

            if (l < this.listListEntry.minimum) {
               return Optional.of(Component.translatable("text.cloth-config.error.too_small", new Object[]{this.listListEntry.minimum}));
            }
         } catch (NumberFormatException var3) {
            return Optional.of(Component.translatable("text.cloth-config.error.not_valid_number_long"));
         }

         return Optional.empty();
      }
   }
}
