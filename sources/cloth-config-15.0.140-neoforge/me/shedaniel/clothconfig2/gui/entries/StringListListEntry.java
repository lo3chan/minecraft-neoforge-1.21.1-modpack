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
public class StringListListEntry extends AbstractTextFieldListListEntry<String, StringListListEntry.StringListCell, StringListListEntry> {
   @Deprecated
   @Internal
   public StringListListEntry(
      Component fieldName,
      List<String> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<String>> saveConsumer,
      Supplier<List<String>> defaultValue,
      Component resetButtonKey
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, false);
   }

   @Deprecated
   @Internal
   public StringListListEntry(
      Component fieldName,
      List<String> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<String>> saveConsumer,
      Supplier<List<String>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart
   ) {
      this(fieldName, value, defaultExpanded, tooltipSupplier, saveConsumer, defaultValue, resetButtonKey, requiresRestart, true, true);
   }

   @Deprecated
   @Internal
   public StringListListEntry(
      Component fieldName,
      List<String> value,
      boolean defaultExpanded,
      Supplier<Optional<Component[]>> tooltipSupplier,
      Consumer<List<String>> saveConsumer,
      Supplier<List<String>> defaultValue,
      Component resetButtonKey,
      boolean requiresRestart,
      boolean deleteButtonEnabled,
      boolean insertInFront
   ) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.replaceNode(ConcurrentHashMap.java:1111)
      //   at java.base/java.util.concurrent.ConcurrentHashMap.remove(ConcurrentHashMap.java:1102)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:98)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
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
      // 12: invokedynamic apply ()Ljava/util/function/BiFunction; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, me/shedaniel/clothconfig2/gui/entries/StringListListEntry$StringListCell.<init> (Ljava/lang/String;Lme/shedaniel/clothconfig2/gui/entries/StringListListEntry;)V, (Ljava/lang/String;Lme/shedaniel/clothconfig2/gui/entries/StringListListEntry;)Lme/shedaniel/clothconfig2/gui/entries/StringListListEntry$StringListCell; ]
      // 17: invokespecial me/shedaniel/clothconfig2/gui/entries/AbstractTextFieldListListEntry.<init> (Lnet/minecraft/network/chat/Component;Ljava/util/List;ZLjava/util/function/Supplier;Ljava/util/function/Consumer;Ljava/util/function/Supplier;Lnet/minecraft/network/chat/Component;ZZZLjava/util/function/BiFunction;)V
      // 1a: return
   }

   public StringListListEntry self() {
      return this;
   }

   public static class StringListCell
      extends AbstractTextFieldListListEntry.AbstractTextFieldListCell<String, StringListListEntry.StringListCell, StringListListEntry> {
      public StringListCell(String value, StringListListEntry listListEntry) {
         super(value, listListEntry);
      }

      @Nullable
      protected String substituteDefault(@Nullable String value) {
         return value == null ? "" : value;
      }

      @Override
      protected boolean isValidText(@NotNull String text) {
         return true;
      }

      public String getValue() {
         return this.widget.getValue();
      }

      @Override
      public Optional<Component> getError() {
         return Optional.empty();
      }
   }
}
