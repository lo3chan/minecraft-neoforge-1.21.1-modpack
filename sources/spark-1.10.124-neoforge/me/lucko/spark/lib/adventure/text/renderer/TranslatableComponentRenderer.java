package me.lucko.spark.lib.adventure.text.renderer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import me.lucko.spark.lib.adventure.text.BlockNBTComponent;
import me.lucko.spark.lib.adventure.text.BuildableComponent;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentBuilder;
import me.lucko.spark.lib.adventure.text.EntityNBTComponent;
import me.lucko.spark.lib.adventure.text.KeybindComponent;
import me.lucko.spark.lib.adventure.text.NBTComponent;
import me.lucko.spark.lib.adventure.text.NBTComponentBuilder;
import me.lucko.spark.lib.adventure.text.ScoreComponent;
import me.lucko.spark.lib.adventure.text.SelectorComponent;
import me.lucko.spark.lib.adventure.text.StorageNBTComponent;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.TranslatableComponent;
import me.lucko.spark.lib.adventure.text.TranslationArgument;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.translation.Translator;
import me.lucko.spark.lib.adventure.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TranslatableComponentRenderer<C> extends AbstractComponentRenderer<C> {
   private static final Set<Style.Merge> MERGES = Style.Merge.merges(Style.Merge.COLOR, Style.Merge.DECORATIONS, Style.Merge.INSERTION, Style.Merge.FONT);

   @NotNull
   public static TranslatableComponentRenderer<Locale> usingTranslationSource(@NotNull final Translator source) {
      Objects.requireNonNull(source, "source");
      return new TranslatableComponentRenderer<Locale>() {
         @Nullable
         protected MessageFormat translate(@NotNull final String key, @NotNull final Locale context) {
            return source.translate(key, context);
         }

         @NotNull
         protected Component renderTranslatable(@NotNull final TranslatableComponent component, @NotNull final Locale context) {
            TriState anyTranslations = source.hasAnyTranslations();
            if (anyTranslations != TriState.TRUE && anyTranslations != TriState.NOT_SET) {
               return component;
            } else {
               Component translated = source.translate(component, context);
               return translated != null ? translated : super.renderTranslatable(component, context);
            }
         }
      };
   }

   @Nullable
   protected MessageFormat translate(@NotNull final String key, @NotNull final C context) {
      return null;
   }

   @Nullable
   protected MessageFormat translate(@NotNull final String key, @Nullable final String fallback, @NotNull final C context) {
      return this.translate(key, context);
   }

   @NotNull
   @Override
   protected Component renderBlockNbt(@NotNull final BlockNBTComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:293)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 2
      // 02: invokestatic me/lucko/spark/lib/adventure/text/Component.blockNBT ()Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder;
      // 05: aload 1
      // 06: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.nbt (Ljava/lang/Object;Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Lme/lucko/spark/lib/adventure/text/NBTComponent;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 09: checkcast me/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder
      // 0c: aload 1
      // 0d: invokeinterface me/lucko/spark/lib/adventure/text/BlockNBTComponent.pos ()Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Pos; 1
      // 12: invokeinterface me/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder.pos (Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Pos;)Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder; 2
      // 17: astore 3
      // 18: aload 0
      // 19: aload 1
      // 1a: aload 3
      // 1b: aload 2
      // 1c: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 1f: areturn
   }

   @NotNull
   @Override
   protected Component renderEntityNbt(@NotNull final EntityNBTComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:293)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 2
      // 02: invokestatic me/lucko/spark/lib/adventure/text/Component.entityNBT ()Lme/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder;
      // 05: aload 1
      // 06: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.nbt (Ljava/lang/Object;Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Lme/lucko/spark/lib/adventure/text/NBTComponent;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 09: checkcast me/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder
      // 0c: aload 1
      // 0d: invokeinterface me/lucko/spark/lib/adventure/text/EntityNBTComponent.selector ()Ljava/lang/String; 1
      // 12: invokeinterface me/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder.selector (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder; 2
      // 17: astore 3
      // 18: aload 0
      // 19: aload 1
      // 1a: aload 3
      // 1b: aload 2
      // 1c: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 1f: areturn
   }

   @NotNull
   @Override
   protected Component renderStorageNbt(@NotNull final StorageNBTComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:293)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: aload 0
      // 01: aload 2
      // 02: invokestatic me/lucko/spark/lib/adventure/text/Component.storageNBT ()Lme/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder;
      // 05: aload 1
      // 06: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.nbt (Ljava/lang/Object;Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Lme/lucko/spark/lib/adventure/text/NBTComponent;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 09: checkcast me/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder
      // 0c: aload 1
      // 0d: invokeinterface me/lucko/spark/lib/adventure/text/StorageNBTComponent.storage ()Lme/lucko/spark/lib/adventure/key/Key; 1
      // 12: invokeinterface me/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder.storage (Lme/lucko/spark/lib/adventure/key/Key;)Lme/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder; 2
      // 17: astore 3
      // 18: aload 0
      // 19: aload 1
      // 1a: aload 3
      // 1b: aload 2
      // 1c: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 1f: areturn
   }

   protected <O extends NBTComponent<O, B>, B extends NBTComponentBuilder<O, B>> B nbt(@NotNull final C context, final B builder, final O oldComponent) {
      builder.nbtPath(oldComponent.nbtPath()).interpret(oldComponent.interpret());
      Component separator = oldComponent.separator();
      if (separator != null) {
         builder.separator(this.render(separator, context));
      }

      return builder;
   }

   @NotNull
   @Override
   protected Component renderKeybind(@NotNull final KeybindComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: invokestatic me/lucko/spark/lib/adventure/text/Component.keybind ()Lme/lucko/spark/lib/adventure/text/KeybindComponent$Builder;
      // 03: aload 1
      // 04: invokeinterface me/lucko/spark/lib/adventure/text/KeybindComponent.keybind ()Ljava/lang/String; 1
      // 09: invokeinterface me/lucko/spark/lib/adventure/text/KeybindComponent$Builder.keybind (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/KeybindComponent$Builder; 2
      // 0e: astore 3
      // 0f: aload 0
      // 10: aload 1
      // 11: aload 3
      // 12: aload 2
      // 13: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 16: areturn
   }

   @NotNull
   @Override
   protected Component renderScore(@NotNull final ScoreComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: invokestatic me/lucko/spark/lib/adventure/text/Component.score ()Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder;
      // 03: aload 1
      // 04: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent.name ()Ljava/lang/String; 1
      // 09: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.name (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 0e: aload 1
      // 0f: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent.objective ()Ljava/lang/String; 1
      // 14: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.objective (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 19: aload 1
      // 1a: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent.value ()Ljava/lang/String; 1
      // 1f: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.value (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 24: astore 3
      // 25: aload 0
      // 26: aload 1
      // 27: aload 3
      // 28: aload 2
      // 29: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 2c: areturn
   }

   @NotNull
   @Override
   protected Component renderSelector(@NotNull final SelectorComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: invokestatic me/lucko/spark/lib/adventure/text/Component.selector ()Lme/lucko/spark/lib/adventure/text/SelectorComponent$Builder;
      // 03: aload 1
      // 04: invokeinterface me/lucko/spark/lib/adventure/text/SelectorComponent.pattern ()Ljava/lang/String; 1
      // 09: invokeinterface me/lucko/spark/lib/adventure/text/SelectorComponent$Builder.pattern (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/SelectorComponent$Builder; 2
      // 0e: astore 3
      // 0f: aload 0
      // 10: aload 1
      // 11: aload 3
      // 12: aload 2
      // 13: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 16: areturn
   }

   @NotNull
   @Override
   protected Component renderText(@NotNull final TextComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 00: invokestatic me/lucko/spark/lib/adventure/text/Component.text ()Lme/lucko/spark/lib/adventure/text/TextComponent$Builder;
      // 03: aload 1
      // 04: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent.content ()Ljava/lang/String; 1
      // 09: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.content (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TextComponent$Builder; 2
      // 0e: astore 3
      // 0f: aload 0
      // 10: aload 1
      // 11: aload 3
      // 12: aload 2
      // 13: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 16: areturn
   }

   @NotNull
   @Override
   protected Component renderTranslatable(@NotNull final TranslatableComponent component, @NotNull final C context) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at java.base/java.util.concurrent.ConcurrentHashMap.computeIfAbsent(ConcurrentHashMap.java:1721)
      //   at org.jetbrains.java.decompiler.struct.StructContext.getClass(StructContext.java:78)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:283)
      //   at org.jetbrains.java.decompiler.struct.StructContext.instanceOf(StructContext.java:293)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1613)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 000: aload 0
      // 001: aload 1
      // 002: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.key ()Ljava/lang/String; 1
      // 007: aload 1
      // 008: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.fallback ()Ljava/lang/String; 1
      // 00d: aload 2
      // 00e: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.translate (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)Ljava/text/MessageFormat;
      // 011: astore 3
      // 012: aload 3
      // 013: ifnonnull 0b1
      // 016: invokestatic me/lucko/spark/lib/adventure/text/Component.translatable ()Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder;
      // 019: aload 1
      // 01a: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.key ()Ljava/lang/String; 1
      // 01f: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.key (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 024: aload 1
      // 025: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.fallback ()Ljava/lang/String; 1
      // 02a: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.fallback (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 02f: astore 4
      // 031: aload 1
      // 032: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.arguments ()Ljava/util/List; 1
      // 037: invokeinterface java/util/List.isEmpty ()Z 1
      // 03c: ifne 0a8
      // 03f: new java/util/ArrayList
      // 042: dup
      // 043: aload 1
      // 044: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.arguments ()Ljava/util/List; 1
      // 049: invokespecial java/util/ArrayList.<init> (Ljava/util/Collection;)V
      // 04c: astore 5
      // 04e: bipush 0
      // 04f: istore 6
      // 051: aload 5
      // 053: invokeinterface java/util/List.size ()I 1
      // 058: istore 7
      // 05a: iload 6
      // 05c: iload 7
      // 05e: if_icmpge 09e
      // 061: aload 5
      // 063: iload 6
      // 065: invokeinterface java/util/List.get (I)Ljava/lang/Object; 2
      // 06a: checkcast me/lucko/spark/lib/adventure/text/TranslationArgument
      // 06d: astore 8
      // 06f: aload 8
      // 071: invokeinterface me/lucko/spark/lib/adventure/text/TranslationArgument.value ()Ljava/lang/Object; 1
      // 076: instanceof me/lucko/spark/lib/adventure/text/Component
      // 079: ifeq 098
      // 07c: aload 5
      // 07e: iload 6
      // 080: aload 0
      // 081: aload 8
      // 083: invokeinterface me/lucko/spark/lib/adventure/text/TranslationArgument.value ()Ljava/lang/Object; 1
      // 088: checkcast me/lucko/spark/lib/adventure/text/Component
      // 08b: aload 2
      // 08c: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.render (Lme/lucko/spark/lib/adventure/text/Component;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/Component;
      // 08f: invokestatic me/lucko/spark/lib/adventure/text/TranslationArgument.component (Lme/lucko/spark/lib/adventure/text/ComponentLike;)Lme/lucko/spark/lib/adventure/text/TranslationArgument;
      // 092: invokeinterface java/util/List.set (ILjava/lang/Object;)Ljava/lang/Object; 3
      // 097: pop
      // 098: iinc 6 1
      // 09b: goto 05a
      // 09e: aload 4
      // 0a0: aload 5
      // 0a2: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.arguments (Ljava/util/List;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 0a7: pop
      // 0a8: aload 0
      // 0a9: aload 1
      // 0aa: aload 4
      // 0ac: aload 2
      // 0ad: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyleAndOptionallyDeepRender (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 0b0: areturn
      // 0b1: aload 1
      // 0b2: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.arguments ()Ljava/util/List; 1
      // 0b7: astore 4
      // 0b9: invokestatic me/lucko/spark/lib/adventure/text/Component.text ()Lme/lucko/spark/lib/adventure/text/TextComponent$Builder;
      // 0bc: astore 5
      // 0be: aload 0
      // 0bf: aload 1
      // 0c0: aload 5
      // 0c2: aload 2
      // 0c3: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.mergeStyle (Lme/lucko/spark/lib/adventure/text/Component;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)V
      // 0c6: aload 4
      // 0c8: invokeinterface java/util/List.isEmpty ()Z 1
      // 0cd: ifeq 0f6
      // 0d0: aload 5
      // 0d2: aload 3
      // 0d3: aconst_null
      // 0d4: new java/lang/StringBuffer
      // 0d7: dup
      // 0d8: invokespecial java/lang/StringBuffer.<init> ()V
      // 0db: aconst_null
      // 0dc: invokevirtual java/text/MessageFormat.format ([Ljava/lang/Object;Ljava/lang/StringBuffer;Ljava/text/FieldPosition;)Ljava/lang/StringBuffer;
      // 0df: invokevirtual java/lang/StringBuffer.toString ()Ljava/lang/String;
      // 0e2: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.content (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TextComponent$Builder; 2
      // 0e7: pop
      // 0e8: aload 0
      // 0e9: aload 1
      // 0ea: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.children ()Ljava/util/List; 1
      // 0ef: aload 5
      // 0f1: aload 2
      // 0f2: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.optionallyRenderChildrenAppendAndBuild (Ljava/util/List;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 0f5: areturn
      // 0f6: aload 4
      // 0f8: invokeinterface java/util/List.size ()I 1
      // 0fd: anewarray 305
      // 100: astore 6
      // 102: aload 3
      // 103: aload 6
      // 105: new java/lang/StringBuffer
      // 108: dup
      // 109: invokespecial java/lang/StringBuffer.<init> ()V
      // 10c: aconst_null
      // 10d: invokevirtual java/text/MessageFormat.format ([Ljava/lang/Object;Ljava/lang/StringBuffer;Ljava/text/FieldPosition;)Ljava/lang/StringBuffer;
      // 110: astore 7
      // 112: aload 3
      // 113: aload 6
      // 115: invokevirtual java/text/MessageFormat.formatToCharacterIterator (Ljava/lang/Object;)Ljava/text/AttributedCharacterIterator;
      // 118: astore 8
      // 11a: aload 8
      // 11c: invokeinterface java/text/AttributedCharacterIterator.getIndex ()I 1
      // 121: aload 8
      // 123: invokeinterface java/text/AttributedCharacterIterator.getEndIndex ()I 1
      // 128: if_icmpge 1b5
      // 12b: aload 8
      // 12d: invokeinterface java/text/AttributedCharacterIterator.getRunLimit ()I 1
      // 132: istore 9
      // 134: aload 8
      // 136: getstatic java/text/MessageFormat$Field.ARGUMENT Ljava/text/MessageFormat$Field;
      // 139: invokeinterface java/text/AttributedCharacterIterator.getAttribute (Ljava/text/AttributedCharacterIterator$Attribute;)Ljava/lang/Object; 2
      // 13e: checkcast java/lang/Integer
      // 141: astore 10
      // 143: aload 10
      // 145: ifnull 18f
      // 148: aload 4
      // 14a: aload 10
      // 14c: invokevirtual java/lang/Integer.intValue ()I
      // 14f: invokeinterface java/util/List.get (I)Ljava/lang/Object; 2
      // 154: checkcast me/lucko/spark/lib/adventure/text/TranslationArgument
      // 157: astore 11
      // 159: aload 11
      // 15b: invokeinterface me/lucko/spark/lib/adventure/text/TranslationArgument.value ()Ljava/lang/Object; 1
      // 160: instanceof me/lucko/spark/lib/adventure/text/Component
      // 163: ifeq 17d
      // 166: aload 5
      // 168: aload 0
      // 169: aload 11
      // 16b: invokeinterface me/lucko/spark/lib/adventure/text/TranslationArgument.asComponent ()Lme/lucko/spark/lib/adventure/text/Component; 1
      // 170: aload 2
      // 171: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.render (Lme/lucko/spark/lib/adventure/text/Component;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/Component;
      // 174: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.append (Lme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 179: pop
      // 17a: goto 18c
      // 17d: aload 5
      // 17f: aload 11
      // 181: invokeinterface me/lucko/spark/lib/adventure/text/TranslationArgument.asComponent ()Lme/lucko/spark/lib/adventure/text/Component; 1
      // 186: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.append (Lme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 18b: pop
      // 18c: goto 1a8
      // 18f: aload 5
      // 191: aload 7
      // 193: aload 8
      // 195: invokeinterface java/text/AttributedCharacterIterator.getIndex ()I 1
      // 19a: iload 9
      // 19c: invokevirtual java/lang/StringBuffer.substring (II)Ljava/lang/String;
      // 19f: invokestatic me/lucko/spark/lib/adventure/text/Component.text (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TextComponent;
      // 1a2: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.append (Lme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 1a7: pop
      // 1a8: aload 8
      // 1aa: iload 9
      // 1ac: invokeinterface java/text/AttributedCharacterIterator.setIndex (I)C 2
      // 1b1: pop
      // 1b2: goto 11a
      // 1b5: aload 0
      // 1b6: aload 1
      // 1b7: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent.children ()Ljava/util/List; 1
      // 1bc: aload 5
      // 1be: aload 2
      // 1bf: invokevirtual me/lucko/spark/lib/adventure/text/renderer/TranslatableComponentRenderer.optionallyRenderChildrenAppendAndBuild (Ljava/util/List;Lme/lucko/spark/lib/adventure/text/ComponentBuilder;Ljava/lang/Object;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 1c2: areturn
   }

   protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O mergeStyleAndOptionallyDeepRender(
      final Component component, final B builder, final C context
   ) {
      this.mergeStyle(component, builder, context);
      return this.optionallyRenderChildrenAppendAndBuild(component.children(), builder, context);
   }

   protected <O extends BuildableComponent<O, B>, B extends ComponentBuilder<O, B>> O optionallyRenderChildrenAppendAndBuild(
      final List<Component> children, final B builder, final C context
   ) {
      if (!children.isEmpty()) {
         children.forEach(child -> builder.append(this.render(child, context)));
      }

      return builder.build();
   }

   protected <B extends ComponentBuilder<?, ?>> void mergeStyle(final Component component, final B builder, final C context) {
      builder.mergeStyle(component, MERGES);
      builder.clickEvent(component.clickEvent());
      HoverEvent<?> hoverEvent = component.hoverEvent();
      if (hoverEvent != null) {
         builder.hoverEvent(hoverEvent.withRenderedValue(this, context));
      }
   }
}
