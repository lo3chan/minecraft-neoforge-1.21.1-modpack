package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.option.OptionState;
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
import me.lucko.spark.lib.adventure.text.serializer.json.JSONOptions;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl extends TypeAdapter<Component> {
   static final Type COMPONENT_LIST_TYPE = (new TypeToken<List<Component>>() {}).getType();
   static final Type TRANSLATABLE_ARGUMENT_LIST_TYPE = (new TypeToken<List<TranslationArgument>>() {}).getType();
   private final boolean emitCompactTextComponent;
   private final Gson gson;

   static TypeAdapter<Component> create(final OptionState features, final Gson gson) {
      return new ComponentSerializerImpl(features.value(JSONOptions.EMIT_COMPACT_TEXT_COMPONENT), gson).nullSafe();
   }

   private ComponentSerializerImpl(final boolean emitCompactTextComponent, final Gson gson) {
      this.emitCompactTextComponent = emitCompactTextComponent;
      this.gson = gson;
   }

   public BuildableComponent<?, ?> read(final JsonReader in) throws IOException {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.StackOverflowError
      //   at org.jetbrains.java.decompiler.struct.gen.VarType.remap(VarType.java:428)
      //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.remap(GenericType.java:350)
      //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.remap(GenericType.java:372)
      //   at org.jetbrains.java.decompiler.struct.gen.generics.GenericType.getGenericSuperType(GenericType.java:693)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$19(InvocationExprent.java:1623)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.lambda$isMappingInBounds$20(InvocationExprent.java:1663)
      //
      // Bytecode:
      // 000: aload 1
      // 001: invokevirtual com/google/gson/stream/JsonReader.peek ()Lcom/google/gson/stream/JsonToken;
      // 004: astore 2
      // 005: aload 2
      // 006: getstatic com/google/gson/stream/JsonToken.STRING Lcom/google/gson/stream/JsonToken;
      // 009: if_acmpeq 01a
      // 00c: aload 2
      // 00d: getstatic com/google/gson/stream/JsonToken.NUMBER Lcom/google/gson/stream/JsonToken;
      // 010: if_acmpeq 01a
      // 013: aload 2
      // 014: getstatic com/google/gson/stream/JsonToken.BOOLEAN Lcom/google/gson/stream/JsonToken;
      // 017: if_acmpne 022
      // 01a: aload 1
      // 01b: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/GsonHacks.readString (Lcom/google/gson/stream/JsonReader;)Ljava/lang/String;
      // 01e: invokestatic me/lucko/spark/lib/adventure/text/Component.text (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TextComponent;
      // 021: areturn
      // 022: aload 2
      // 023: getstatic com/google/gson/stream/JsonToken.BEGIN_ARRAY Lcom/google/gson/stream/JsonToken;
      // 026: if_acmpne 06f
      // 029: aconst_null
      // 02a: astore 3
      // 02b: aload 1
      // 02c: invokevirtual com/google/gson/stream/JsonReader.beginArray ()V
      // 02f: aload 1
      // 030: invokevirtual com/google/gson/stream/JsonReader.hasNext ()Z
      // 033: ifeq 058
      // 036: aload 0
      // 037: aload 1
      // 038: invokevirtual me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.read (Lcom/google/gson/stream/JsonReader;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 03b: astore 4
      // 03d: aload 3
      // 03e: ifnonnull 04c
      // 041: aload 4
      // 043: invokeinterface me/lucko/spark/lib/adventure/text/BuildableComponent.toBuilder ()Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 1
      // 048: astore 3
      // 049: goto 055
      // 04c: aload 3
      // 04d: aload 4
      // 04f: invokeinterface me/lucko/spark/lib/adventure/text/ComponentBuilder.append (Lme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 054: pop
      // 055: goto 02f
      // 058: aload 3
      // 059: ifnonnull 064
      // 05c: aload 1
      // 05d: invokevirtual com/google/gson/stream/JsonReader.getPath ()Ljava/lang/String;
      // 060: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.notSureHowToDeserialize (Ljava/lang/Object;)Lcom/google/gson/JsonParseException;
      // 063: athrow
      // 064: aload 1
      // 065: invokevirtual com/google/gson/stream/JsonReader.endArray ()V
      // 068: aload 3
      // 069: invokeinterface me/lucko/spark/lib/adventure/text/ComponentBuilder.build ()Lme/lucko/spark/lib/adventure/text/BuildableComponent; 1
      // 06e: areturn
      // 06f: aload 2
      // 070: getstatic com/google/gson/stream/JsonToken.BEGIN_OBJECT Lcom/google/gson/stream/JsonToken;
      // 073: if_acmpeq 07e
      // 076: aload 1
      // 077: invokevirtual com/google/gson/stream/JsonReader.getPath ()Ljava/lang/String;
      // 07a: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.notSureHowToDeserialize (Ljava/lang/Object;)Lcom/google/gson/JsonParseException;
      // 07d: athrow
      // 07e: new com/google/gson/JsonObject
      // 081: dup
      // 082: invokespecial com/google/gson/JsonObject.<init> ()V
      // 085: astore 3
      // 086: invokestatic java/util/Collections.emptyList ()Ljava/util/List;
      // 089: astore 4
      // 08b: aconst_null
      // 08c: astore 5
      // 08e: aconst_null
      // 08f: astore 6
      // 091: aconst_null
      // 092: astore 7
      // 094: aconst_null
      // 095: astore 8
      // 097: aconst_null
      // 098: astore 9
      // 09a: aconst_null
      // 09b: astore 10
      // 09d: aconst_null
      // 09e: astore 11
      // 0a0: aconst_null
      // 0a1: astore 12
      // 0a3: aconst_null
      // 0a4: astore 13
      // 0a6: aconst_null
      // 0a7: astore 14
      // 0a9: bipush 0
      // 0aa: istore 15
      // 0ac: aconst_null
      // 0ad: astore 16
      // 0af: aconst_null
      // 0b0: astore 17
      // 0b2: aconst_null
      // 0b3: astore 18
      // 0b5: aconst_null
      // 0b6: astore 19
      // 0b8: aload 1
      // 0b9: invokevirtual com/google/gson/stream/JsonReader.beginObject ()V
      // 0bc: aload 1
      // 0bd: invokevirtual com/google/gson/stream/JsonReader.hasNext ()Z
      // 0c0: ifeq 27a
      // 0c3: aload 1
      // 0c4: invokevirtual com/google/gson/stream/JsonReader.nextName ()Ljava/lang/String;
      // 0c7: astore 20
      // 0c9: aload 20
      // 0cb: ldc "text"
      // 0cd: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0d0: ifeq 0dc
      // 0d3: aload 1
      // 0d4: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/GsonHacks.readString (Lcom/google/gson/stream/JsonReader;)Ljava/lang/String;
      // 0d7: astore 5
      // 0d9: goto 277
      // 0dc: aload 20
      // 0de: ldc "translate"
      // 0e0: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0e3: ifeq 0ef
      // 0e6: aload 1
      // 0e7: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 0ea: astore 6
      // 0ec: goto 277
      // 0ef: aload 20
      // 0f1: ldc "fallback"
      // 0f3: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 0f6: ifeq 102
      // 0f9: aload 1
      // 0fa: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 0fd: astore 7
      // 0ff: goto 277
      // 102: aload 20
      // 104: ldc "with"
      // 106: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 109: ifeq 11f
      // 10c: aload 0
      // 10d: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 110: aload 1
      // 111: getstatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.TRANSLATABLE_ARGUMENT_LIST_TYPE Ljava/lang/reflect/Type;
      // 114: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)Ljava/lang/Object;
      // 117: checkcast java/util/List
      // 11a: astore 8
      // 11c: goto 277
      // 11f: aload 20
      // 121: ldc "score"
      // 123: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 126: ifeq 195
      // 129: aload 1
      // 12a: invokevirtual com/google/gson/stream/JsonReader.beginObject ()V
      // 12d: aload 1
      // 12e: invokevirtual com/google/gson/stream/JsonReader.hasNext ()Z
      // 131: ifeq 17a
      // 134: aload 1
      // 135: invokevirtual com/google/gson/stream/JsonReader.nextName ()Ljava/lang/String;
      // 138: astore 21
      // 13a: aload 21
      // 13c: ldc "name"
      // 13e: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 141: ifeq 14d
      // 144: aload 1
      // 145: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 148: astore 9
      // 14a: goto 177
      // 14d: aload 21
      // 14f: ldc "objective"
      // 151: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 154: ifeq 160
      // 157: aload 1
      // 158: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 15b: astore 10
      // 15d: goto 177
      // 160: aload 21
      // 162: ldc "value"
      // 164: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 167: ifeq 173
      // 16a: aload 1
      // 16b: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 16e: astore 11
      // 170: goto 177
      // 173: aload 1
      // 174: invokevirtual com/google/gson/stream/JsonReader.skipValue ()V
      // 177: goto 12d
      // 17a: aload 9
      // 17c: ifnull 184
      // 17f: aload 10
      // 181: ifnonnull 18e
      // 184: new com/google/gson/JsonParseException
      // 187: dup
      // 188: ldc "A score component requires a name and objective"
      // 18a: invokespecial com/google/gson/JsonParseException.<init> (Ljava/lang/String;)V
      // 18d: athrow
      // 18e: aload 1
      // 18f: invokevirtual com/google/gson/stream/JsonReader.endObject ()V
      // 192: goto 277
      // 195: aload 20
      // 197: ldc "selector"
      // 199: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 19c: ifeq 1a8
      // 19f: aload 1
      // 1a0: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 1a3: astore 12
      // 1a5: goto 277
      // 1a8: aload 20
      // 1aa: ldc "keybind"
      // 1ac: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1af: ifeq 1bb
      // 1b2: aload 1
      // 1b3: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 1b6: astore 13
      // 1b8: goto 277
      // 1bb: aload 20
      // 1bd: ldc "nbt"
      // 1bf: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1c2: ifeq 1ce
      // 1c5: aload 1
      // 1c6: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 1c9: astore 14
      // 1cb: goto 277
      // 1ce: aload 20
      // 1d0: ldc "interpret"
      // 1d2: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1d5: ifeq 1e1
      // 1d8: aload 1
      // 1d9: invokevirtual com/google/gson/stream/JsonReader.nextBoolean ()Z
      // 1dc: istore 15
      // 1de: goto 277
      // 1e1: aload 20
      // 1e3: ldc "block"
      // 1e5: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 1e8: ifeq 1fe
      // 1eb: aload 0
      // 1ec: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 1ef: aload 1
      // 1f0: getstatic me/lucko/spark/lib/adventure/text/serializer/gson/SerializerFactory.BLOCK_NBT_POS_TYPE Ljava/lang/Class;
      // 1f3: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)Ljava/lang/Object;
      // 1f6: checkcast me/lucko/spark/lib/adventure/text/BlockNBTComponent$Pos
      // 1f9: astore 16
      // 1fb: goto 277
      // 1fe: aload 20
      // 200: ldc_w "entity"
      // 203: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 206: ifeq 212
      // 209: aload 1
      // 20a: invokevirtual com/google/gson/stream/JsonReader.nextString ()Ljava/lang/String;
      // 20d: astore 17
      // 20f: goto 277
      // 212: aload 20
      // 214: ldc_w "storage"
      // 217: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 21a: ifeq 230
      // 21d: aload 0
      // 21e: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 221: aload 1
      // 222: getstatic me/lucko/spark/lib/adventure/text/serializer/gson/SerializerFactory.KEY_TYPE Ljava/lang/Class;
      // 225: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)Ljava/lang/Object;
      // 228: checkcast me/lucko/spark/lib/adventure/key/Key
      // 22b: astore 18
      // 22d: goto 277
      // 230: aload 20
      // 232: ldc_w "extra"
      // 235: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 238: ifeq 24e
      // 23b: aload 0
      // 23c: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 23f: aload 1
      // 240: getstatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.COMPONENT_LIST_TYPE Ljava/lang/reflect/Type;
      // 243: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)Ljava/lang/Object;
      // 246: checkcast java/util/List
      // 249: astore 4
      // 24b: goto 277
      // 24e: aload 20
      // 250: ldc_w "separator"
      // 253: invokevirtual java/lang/String.equals (Ljava/lang/Object;)Z
      // 256: ifeq 263
      // 259: aload 0
      // 25a: aload 1
      // 25b: invokevirtual me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.read (Lcom/google/gson/stream/JsonReader;)Lme/lucko/spark/lib/adventure/text/BuildableComponent;
      // 25e: astore 19
      // 260: goto 277
      // 263: aload 3
      // 264: aload 20
      // 266: aload 0
      // 267: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 26a: aload 1
      // 26b: ldc_w com/google/gson/JsonElement
      // 26e: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/stream/JsonReader;Ljava/lang/reflect/Type;)Ljava/lang/Object;
      // 271: checkcast com/google/gson/JsonElement
      // 274: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 277: goto 0bc
      // 27a: aload 5
      // 27c: ifnull 28e
      // 27f: invokestatic me/lucko/spark/lib/adventure/text/Component.text ()Lme/lucko/spark/lib/adventure/text/TextComponent$Builder;
      // 282: aload 5
      // 284: invokeinterface me/lucko/spark/lib/adventure/text/TextComponent$Builder.content (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TextComponent$Builder; 2
      // 289: astore 20
      // 28b: goto 3b1
      // 28e: aload 6
      // 290: ifnull 2cb
      // 293: aload 8
      // 295: ifnull 2b5
      // 298: invokestatic me/lucko/spark/lib/adventure/text/Component.translatable ()Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder;
      // 29b: aload 6
      // 29d: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.key (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 2a2: aload 7
      // 2a4: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.fallback (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 2a9: aload 8
      // 2ab: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.arguments (Ljava/util/List;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 2b0: astore 20
      // 2b2: goto 3b1
      // 2b5: invokestatic me/lucko/spark/lib/adventure/text/Component.translatable ()Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder;
      // 2b8: aload 6
      // 2ba: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.key (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 2bf: aload 7
      // 2c1: invokeinterface me/lucko/spark/lib/adventure/text/TranslatableComponent$Builder.fallback (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/TranslatableComponent$Builder; 2
      // 2c6: astore 20
      // 2c8: goto 3b1
      // 2cb: aload 9
      // 2cd: ifnull 30d
      // 2d0: aload 10
      // 2d2: ifnull 30d
      // 2d5: aload 11
      // 2d7: ifnonnull 2f0
      // 2da: invokestatic me/lucko/spark/lib/adventure/text/Component.score ()Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder;
      // 2dd: aload 9
      // 2df: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.name (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 2e4: aload 10
      // 2e6: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.objective (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 2eb: astore 20
      // 2ed: goto 3b1
      // 2f0: invokestatic me/lucko/spark/lib/adventure/text/Component.score ()Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder;
      // 2f3: aload 9
      // 2f5: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.name (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 2fa: aload 10
      // 2fc: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.objective (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 301: aload 11
      // 303: invokeinterface me/lucko/spark/lib/adventure/text/ScoreComponent$Builder.value (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/ScoreComponent$Builder; 2
      // 308: astore 20
      // 30a: goto 3b1
      // 30d: aload 12
      // 30f: ifnull 328
      // 312: invokestatic me/lucko/spark/lib/adventure/text/Component.selector ()Lme/lucko/spark/lib/adventure/text/SelectorComponent$Builder;
      // 315: aload 12
      // 317: invokeinterface me/lucko/spark/lib/adventure/text/SelectorComponent$Builder.pattern (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/SelectorComponent$Builder; 2
      // 31c: aload 19
      // 31e: invokeinterface me/lucko/spark/lib/adventure/text/SelectorComponent$Builder.separator (Lme/lucko/spark/lib/adventure/text/ComponentLike;)Lme/lucko/spark/lib/adventure/text/SelectorComponent$Builder; 2
      // 323: astore 20
      // 325: goto 3b1
      // 328: aload 13
      // 32a: ifnull 33c
      // 32d: invokestatic me/lucko/spark/lib/adventure/text/Component.keybind ()Lme/lucko/spark/lib/adventure/text/KeybindComponent$Builder;
      // 330: aload 13
      // 332: invokeinterface me/lucko/spark/lib/adventure/text/KeybindComponent$Builder.keybind (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/KeybindComponent$Builder; 2
      // 337: astore 20
      // 339: goto 3b1
      // 33c: aload 14
      // 33e: ifnull 3a9
      // 341: aload 16
      // 343: ifnull 361
      // 346: invokestatic me/lucko/spark/lib/adventure/text/Component.blockNBT ()Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder;
      // 349: aload 14
      // 34b: iload 15
      // 34d: aload 19
      // 34f: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.nbt (Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Ljava/lang/String;ZLme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 352: checkcast me/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder
      // 355: aload 16
      // 357: invokeinterface me/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder.pos (Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Pos;)Lme/lucko/spark/lib/adventure/text/BlockNBTComponent$Builder; 2
      // 35c: astore 20
      // 35e: goto 3b1
      // 361: aload 17
      // 363: ifnull 381
      // 366: invokestatic me/lucko/spark/lib/adventure/text/Component.entityNBT ()Lme/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder;
      // 369: aload 14
      // 36b: iload 15
      // 36d: aload 19
      // 36f: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.nbt (Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Ljava/lang/String;ZLme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 372: checkcast me/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder
      // 375: aload 17
      // 377: invokeinterface me/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder.selector (Ljava/lang/String;)Lme/lucko/spark/lib/adventure/text/EntityNBTComponent$Builder; 2
      // 37c: astore 20
      // 37e: goto 3b1
      // 381: aload 18
      // 383: ifnull 3a1
      // 386: invokestatic me/lucko/spark/lib/adventure/text/Component.storageNBT ()Lme/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder;
      // 389: aload 14
      // 38b: iload 15
      // 38d: aload 19
      // 38f: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.nbt (Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;Ljava/lang/String;ZLme/lucko/spark/lib/adventure/text/Component;)Lme/lucko/spark/lib/adventure/text/NBTComponentBuilder;
      // 392: checkcast me/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder
      // 395: aload 18
      // 397: invokeinterface me/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder.storage (Lme/lucko/spark/lib/adventure/key/Key;)Lme/lucko/spark/lib/adventure/text/StorageNBTComponent$Builder; 2
      // 39c: astore 20
      // 39e: goto 3b1
      // 3a1: aload 1
      // 3a2: invokevirtual com/google/gson/stream/JsonReader.getPath ()Ljava/lang/String;
      // 3a5: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.notSureHowToDeserialize (Ljava/lang/Object;)Lcom/google/gson/JsonParseException;
      // 3a8: athrow
      // 3a9: aload 1
      // 3aa: invokevirtual com/google/gson/stream/JsonReader.getPath ()Ljava/lang/String;
      // 3ad: invokestatic me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.notSureHowToDeserialize (Ljava/lang/Object;)Lcom/google/gson/JsonParseException;
      // 3b0: athrow
      // 3b1: aload 20
      // 3b3: aload 0
      // 3b4: getfield me/lucko/spark/lib/adventure/text/serializer/gson/ComponentSerializerImpl.gson Lcom/google/gson/Gson;
      // 3b7: aload 3
      // 3b8: getstatic me/lucko/spark/lib/adventure/text/serializer/gson/SerializerFactory.STYLE_TYPE Ljava/lang/Class;
      // 3bb: invokevirtual com/google/gson/Gson.fromJson (Lcom/google/gson/JsonElement;Ljava/lang/Class;)Ljava/lang/Object;
      // 3be: checkcast me/lucko/spark/lib/adventure/text/format/Style
      // 3c1: invokeinterface me/lucko/spark/lib/adventure/text/ComponentBuilder.style (Lme/lucko/spark/lib/adventure/text/format/Style;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 3c6: aload 4
      // 3c8: invokeinterface me/lucko/spark/lib/adventure/text/ComponentBuilder.append (Ljava/lang/Iterable;)Lme/lucko/spark/lib/adventure/text/ComponentBuilder; 2
      // 3cd: pop
      // 3ce: aload 1
      // 3cf: invokevirtual com/google/gson/stream/JsonReader.endObject ()V
      // 3d2: aload 20
      // 3d4: invokeinterface me/lucko/spark/lib/adventure/text/ComponentBuilder.build ()Lme/lucko/spark/lib/adventure/text/BuildableComponent; 1
      // 3d9: areturn
   }

   private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(
      final B builder, final String nbt, final boolean interpret, @Nullable final Component separator
   ) {
      return builder.nbtPath(nbt).interpret(interpret).separator(separator);
   }

   public void write(final JsonWriter out, final Component value) throws IOException {
      if (value instanceof TextComponent && value.children().isEmpty() && !value.hasStyling() && this.emitCompactTextComponent) {
         out.value(((TextComponent)value).content());
      } else {
         out.beginObject();
         if (value.hasStyling()) {
            JsonElement style = this.gson.toJsonTree(value.style(), SerializerFactory.STYLE_TYPE);
            if (style.isJsonObject()) {
               for (Entry<String, JsonElement> entry : style.getAsJsonObject().entrySet()) {
                  out.name(entry.getKey());
                  this.gson.toJson(entry.getValue(), out);
               }
            }
         }

         if (!value.children().isEmpty()) {
            out.name("extra");
            this.gson.toJson(value.children(), COMPONENT_LIST_TYPE, out);
         }

         if (value instanceof TextComponent) {
            out.name("text");
            out.value(((TextComponent)value).content());
         } else if (value instanceof TranslatableComponent) {
            TranslatableComponent translatable = (TranslatableComponent)value;
            out.name("translate");
            out.value(translatable.key());
            String fallback = translatable.fallback();
            if (fallback != null) {
               out.name("fallback");
               out.value(fallback);
            }

            if (!translatable.arguments().isEmpty()) {
               out.name("with");
               this.gson.toJson(translatable.arguments(), TRANSLATABLE_ARGUMENT_LIST_TYPE, out);
            }
         } else if (value instanceof ScoreComponent) {
            ScoreComponent score = (ScoreComponent)value;
            out.name("score");
            out.beginObject();
            out.name("name");
            out.value(score.name());
            out.name("objective");
            out.value(score.objective());
            if (score.value() != null) {
               out.name("value");
               out.value(score.value());
            }

            out.endObject();
         } else if (value instanceof SelectorComponent) {
            SelectorComponent selector = (SelectorComponent)value;
            out.name("selector");
            out.value(selector.pattern());
            this.serializeSeparator(out, selector.separator());
         } else if (value instanceof KeybindComponent) {
            out.name("keybind");
            out.value(((KeybindComponent)value).keybind());
         } else {
            if (!(value instanceof NBTComponent)) {
               throw notSureHowToSerialize(value);
            }

            NBTComponent<?, ?> nbt = (NBTComponent<?, ?>)value;
            out.name("nbt");
            out.value(nbt.nbtPath());
            out.name("interpret");
            out.value(nbt.interpret());
            this.serializeSeparator(out, nbt.separator());
            if (value instanceof BlockNBTComponent) {
               out.name("block");
               this.gson.toJson(((BlockNBTComponent)value).pos(), SerializerFactory.BLOCK_NBT_POS_TYPE, out);
            } else if (value instanceof EntityNBTComponent) {
               out.name("entity");
               out.value(((EntityNBTComponent)value).selector());
            } else {
               if (!(value instanceof StorageNBTComponent)) {
                  throw notSureHowToSerialize(value);
               }

               out.name("storage");
               this.gson.toJson(((StorageNBTComponent)value).storage(), SerializerFactory.KEY_TYPE, out);
            }
         }

         out.endObject();
      }
   }

   private void serializeSeparator(final JsonWriter out, @Nullable final Component separator) throws IOException {
      if (separator != null) {
         out.name("separator");
         this.write(out, separator);
      }
   }

   static JsonParseException notSureHowToDeserialize(final Object element) {
      return new JsonParseException("Don't know how to turn " + element + " into a Component");
   }

   private static IllegalArgumentException notSureHowToSerialize(final Component component) {
      return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
   }
}
