package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapLike;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.io.StringWriter;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface JsonUtils {
   @HideFromJS
   Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().serializeNulls().create();
   MapLike<JsonElement> MAP_LIKE = MapLike.forMap(Map.of(), JsonOps.INSTANCE);

   static JsonElement copy(@Nullable JsonElement element) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonNull, com/google/gson/JsonArray, com/google/gson/JsonObject ]
      // 0b: tableswitch 193 -1 2 29 35 46 108
      // 28: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 2b: goto cd
      // 2e: aload 1
      // 2f: checkcast com/google/gson/JsonNull
      // 32: astore 3
      // 33: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 36: goto cd
      // 39: aload 1
      // 3a: checkcast com/google/gson/JsonArray
      // 3d: astore 4
      // 3f: new com/google/gson/JsonArray
      // 42: dup
      // 43: invokespecial com/google/gson/JsonArray.<init> ()V
      // 46: astore 5
      // 48: aload 4
      // 4a: invokevirtual com/google/gson/JsonArray.iterator ()Ljava/util/Iterator;
      // 4d: astore 6
      // 4f: aload 6
      // 51: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 56: ifeq 72
      // 59: aload 6
      // 5b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 60: checkcast com/google/gson/JsonElement
      // 63: astore 7
      // 65: aload 5
      // 67: aload 7
      // 69: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.copy (Lcom/google/gson/JsonElement;)Lcom/google/gson/JsonElement;
      // 6c: invokevirtual com/google/gson/JsonArray.add (Lcom/google/gson/JsonElement;)V
      // 6f: goto 4f
      // 72: aload 5
      // 74: goto cd
      // 77: aload 1
      // 78: checkcast com/google/gson/JsonObject
      // 7b: astore 5
      // 7d: new com/google/gson/JsonObject
      // 80: dup
      // 81: invokespecial com/google/gson/JsonObject.<init> ()V
      // 84: astore 6
      // 86: aload 5
      // 88: invokevirtual com/google/gson/JsonObject.entrySet ()Ljava/util/Set;
      // 8b: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 90: astore 7
      // 92: aload 7
      // 94: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 99: ifeq c7
      // 9c: aload 7
      // 9e: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // a3: checkcast java/util/Map$Entry
      // a6: astore 8
      // a8: aload 6
      // aa: aload 8
      // ac: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // b1: checkcast java/lang/String
      // b4: aload 8
      // b6: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // bb: checkcast com/google/gson/JsonElement
      // be: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.copy (Lcom/google/gson/JsonElement;)Lcom/google/gson/JsonElement;
      // c1: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // c4: goto 92
      // c7: aload 6
      // c9: goto cd
      // cc: aload 0
      // cd: areturn
   }

   static JsonElement of(Context cx, @Nullable Object o) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 1
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonElement, dev/latvian/mods/kubejs/util/JsonSerializable, java/lang/CharSequence, java/lang/Boolean, java/lang/Number, java/lang/Character ]
      // 0b: tableswitch 149 -1 5 143 41 52 69 89 107 125
      // 34: aload 2
      // 35: checkcast com/google/gson/JsonElement
      // 38: astore 4
      // 3a: aload 4
      // 3c: goto dc
      // 3f: aload 2
      // 40: checkcast dev/latvian/mods/kubejs/util/JsonSerializable
      // 43: astore 5
      // 45: aload 5
      // 47: aload 0
      // 48: invokeinterface dev/latvian/mods/kubejs/util/JsonSerializable.toJson (Ldev/latvian/mods/rhino/Context;)Lcom/google/gson/JsonElement; 2
      // 4d: goto dc
      // 50: aload 2
      // 51: checkcast java/lang/CharSequence
      // 54: astore 6
      // 56: new com/google/gson/JsonPrimitive
      // 59: dup
      // 5a: aload 1
      // 5b: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 5e: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/String;)V
      // 61: goto dc
      // 64: aload 2
      // 65: checkcast java/lang/Boolean
      // 68: astore 7
      // 6a: new com/google/gson/JsonPrimitive
      // 6d: dup
      // 6e: aload 7
      // 70: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Boolean;)V
      // 73: goto dc
      // 76: aload 2
      // 77: checkcast java/lang/Number
      // 7a: astore 8
      // 7c: new com/google/gson/JsonPrimitive
      // 7f: dup
      // 80: aload 8
      // 82: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Number;)V
      // 85: goto dc
      // 88: aload 2
      // 89: checkcast java/lang/Character
      // 8c: astore 9
      // 8e: new com/google/gson/JsonPrimitive
      // 91: dup
      // 92: aload 9
      // 94: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Character;)V
      // 97: goto dc
      // 9a: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 9d: goto dc
      // a0: aload 0
      // a1: aload 1
      // a2: invokevirtual dev/latvian/mods/rhino/Context.isMapLike (Ljava/lang/Object;)Z
      // a5: ifeq b0
      // a8: aload 0
      // a9: aload 1
      // aa: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.objectOf (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonObject;
      // ad: goto dc
      // b0: aload 0
      // b1: aload 1
      // b2: invokevirtual dev/latvian/mods/rhino/Context.isListLike (Ljava/lang/Object;)Z
      // b5: ifeq c0
      // b8: aload 0
      // b9: aload 1
      // ba: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.arrayOf (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonArray;
      // bd: goto dc
      // c0: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // c3: dup
      // c4: ldc "Unsure how to convert '%s' to JSON!"
      // c6: bipush 1
      // c7: anewarray 78
      // ca: dup
      // cb: bipush 0
      // cc: aload 1
      // cd: aastore
      // ce: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // d1: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;)V
      // d4: aload 0
      // d5: invokestatic dev/latvian/mods/kubejs/script/SourceLine.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/script/SourceLine;
      // d8: invokevirtual dev/latvian/mods/kubejs/error/KubeRuntimeException.source (Ldev/latvian/mods/kubejs/script/SourceLine;)Ldev/latvian/mods/kubejs/error/KubeRuntimeException;
      // db: athrow
      // dc: areturn
   }

   static JsonPrimitive primitiveOf(Context cx, @Nullable Object o) {
      return of(cx, o) instanceof JsonPrimitive p ? p : null;
   }

   @Nullable
   static JsonObject objectOf(Context cx, @Nullable Object map) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 000: aload 1
      // 001: astore 2
      // 002: bipush 0
      // 003: istore 3
      // 004: aload 2
      // 005: iload 3
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonObject, java/lang/CharSequence ]
      // 00b: tableswitch 127 -1 1 25 33 48
      // 024: aconst_null
      // 025: astore 4
      // 027: aload 4
      // 029: goto 1b9
      // 02c: aload 2
      // 02d: checkcast com/google/gson/JsonObject
      // 030: astore 5
      // 032: aload 5
      // 034: astore 4
      // 036: aload 4
      // 038: goto 1b9
      // 03b: aload 2
      // 03c: checkcast java/lang/CharSequence
      // 03f: astore 6
      // 041: getstatic dev/latvian/mods/kubejs/util/JsonUtils.GSON Lcom/google/gson/Gson;
      // 044: aload 1
      // 045: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 048: ldc com/google/gson/JsonObject
      // 04a: invokevirtual com/google/gson/Gson.fromJson (Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;
      // 04d: checkcast com/google/gson/JsonObject
      // 050: astore 7
      // 052: aload 7
      // 054: ifnonnull 061
      // 057: new com/google/gson/JsonParseException
      // 05a: dup
      // 05b: ldc "JSON object string cannot be null"
      // 05d: invokespecial com/google/gson/JsonParseException.<init> (Ljava/lang/String;)V
      // 060: athrow
      // 061: aload 7
      // 063: astore 4
      // 065: aload 4
      // 067: goto 1b9
      // 06a: astore 7
      // 06c: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // 06f: dup
      // 070: ldc "Failed to parse JsonObject from '%s'"
      // 072: bipush 1
      // 073: anewarray 78
      // 076: dup
      // 077: bipush 0
      // 078: aload 1
      // 079: aastore
      // 07a: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // 07d: aload 7
      // 07f: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
      // 082: aload 0
      // 083: invokestatic dev/latvian/mods/kubejs/script/SourceLine.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/script/SourceLine;
      // 086: invokevirtual dev/latvian/mods/kubejs/error/KubeRuntimeException.source (Ldev/latvian/mods/kubejs/script/SourceLine;)Ldev/latvian/mods/kubejs/error/KubeRuntimeException;
      // 089: athrow
      // 08a: aload 0
      // 08b: aload 1
      // 08c: getstatic dev/latvian/mods/rhino/type/TypeInfo.RAW_MAP Ldev/latvian/mods/rhino/type/TypeInfo;
      // 08f: invokevirtual dev/latvian/mods/rhino/Context.jsToJava (Ljava/lang/Object;Ldev/latvian/mods/rhino/type/TypeInfo;)Ljava/lang/Object;
      // 092: checkcast java/util/Map
      // 095: astore 7
      // 097: aload 7
      // 099: ifnull 19d
      // 09c: new com/google/gson/JsonObject
      // 09f: dup
      // 0a0: invokespecial com/google/gson/JsonObject.<init> ()V
      // 0a3: astore 8
      // 0a5: aload 7
      // 0a7: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 0ac: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 0b1: astore 9
      // 0b3: aload 9
      // 0b5: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 0ba: ifeq 194
      // 0bd: aload 9
      // 0bf: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0c4: checkcast java/util/Map$Entry
      // 0c7: astore 10
      // 0c9: aload 0
      // 0ca: aload 10
      // 0cc: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 0d1: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.of (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonElement;
      // 0d4: astore 11
      // 0d6: aload 11
      // 0d8: instanceof com/google/gson/JsonPrimitive
      // 0db: ifeq 180
      // 0de: aload 11
      // 0e0: checkcast com/google/gson/JsonPrimitive
      // 0e3: astore 12
      // 0e5: aload 12
      // 0e7: invokevirtual com/google/gson/JsonPrimitive.isNumber ()Z
      // 0ea: ifeq 180
      // 0ed: aload 12
      // 0ef: invokevirtual com/google/gson/JsonPrimitive.getAsNumber ()Ljava/lang/Number;
      // 0f2: astore 14
      // 0f4: aload 14
      // 0f6: instanceof java/lang/Double
      // 0f9: ifeq 180
      // 0fc: aload 14
      // 0fe: checkcast java/lang/Double
      // 101: astore 13
      // 103: aload 13
      // 105: invokevirtual java/lang/Double.doubleValue ()D
      // 108: ldc2_w 9.223372036854776E18
      // 10b: dcmpg
      // 10c: ifgt 180
      // 10f: aload 13
      // 111: invokevirtual java/lang/Double.doubleValue ()D
      // 114: ldc2_w -9.223372036854776E18
      // 117: dcmpl
      // 118: iflt 180
      // 11b: aload 13
      // 11d: invokevirtual java/lang/Double.doubleValue ()D
      // 120: aload 13
      // 122: invokevirtual java/lang/Double.longValue ()J
      // 125: l2d
      // 126: dcmpl
      // 127: ifne 180
      // 12a: aload 13
      // 12c: invokevirtual java/lang/Double.longValue ()J
      // 12f: lstore 14
      // 131: lload 14
      // 133: ldc2_w -2147483648
      // 136: lcmp
      // 137: iflt 162
      // 13a: lload 14
      // 13c: ldc2_w 2147483647
      // 13f: lcmp
      // 140: ifgt 162
      // 143: aload 8
      // 145: aload 10
      // 147: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 14c: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 14f: new com/google/gson/JsonPrimitive
      // 152: dup
      // 153: lload 14
      // 155: l2i
      // 156: invokestatic java/lang/Integer.valueOf (I)Ljava/lang/Integer;
      // 159: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Number;)V
      // 15c: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 15f: goto 17d
      // 162: aload 8
      // 164: aload 10
      // 166: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 16b: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 16e: new com/google/gson/JsonPrimitive
      // 171: dup
      // 172: lload 14
      // 174: invokestatic java/lang/Long.valueOf (J)Ljava/lang/Long;
      // 177: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Number;)V
      // 17a: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 17d: goto 191
      // 180: aload 8
      // 182: aload 10
      // 184: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 189: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 18c: aload 11
      // 18e: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 191: goto 0b3
      // 194: aload 8
      // 196: astore 4
      // 198: aload 4
      // 19a: goto 1b9
      // 19d: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // 1a0: dup
      // 1a1: ldc "Expected a map-like object or JSON object string, got '%s'"
      // 1a3: bipush 1
      // 1a4: anewarray 78
      // 1a7: dup
      // 1a8: bipush 0
      // 1a9: aload 1
      // 1aa: aastore
      // 1ab: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // 1ae: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;)V
      // 1b1: aload 0
      // 1b2: invokestatic dev/latvian/mods/kubejs/script/SourceLine.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/script/SourceLine;
      // 1b5: invokevirtual dev/latvian/mods/kubejs/error/KubeRuntimeException.source (Ldev/latvian/mods/kubejs/script/SourceLine;)Ldev/latvian/mods/kubejs/error/KubeRuntimeException;
      // 1b8: athrow
      // 1b9: areturn
   }

   @Nullable
   static JsonArray arrayOf(Context cx, @Nullable Object array) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 1
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonArray, java/lang/CharSequence, java/lang/Iterable ]
      // 0b: tableswitch 200 -1 2 29 37 52 134
      // 28: aconst_null
      // 29: astore 4
      // 2b: aload 4
      // 2d: goto ef
      // 30: aload 2
      // 31: checkcast com/google/gson/JsonArray
      // 34: astore 5
      // 36: aload 5
      // 38: astore 4
      // 3a: aload 4
      // 3c: goto ef
      // 3f: aload 2
      // 40: checkcast java/lang/CharSequence
      // 43: astore 6
      // 45: getstatic dev/latvian/mods/kubejs/util/JsonUtils.GSON Lcom/google/gson/Gson;
      // 48: aload 6
      // 4a: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 4f: ldc com/google/gson/JsonArray
      // 51: invokevirtual com/google/gson/Gson.fromJson (Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;
      // 54: checkcast com/google/gson/JsonArray
      // 57: astore 7
      // 59: aload 7
      // 5b: ifnonnull 68
      // 5e: new com/google/gson/JsonParseException
      // 61: dup
      // 62: ldc "JSON array string cannot be null"
      // 64: invokespecial com/google/gson/JsonParseException.<init> (Ljava/lang/String;)V
      // 67: athrow
      // 68: aload 7
      // 6a: astore 4
      // 6c: aload 4
      // 6e: goto ef
      // 71: astore 7
      // 73: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // 76: dup
      // 77: ldc "Failed to parse JsonArray from '%s'"
      // 79: bipush 1
      // 7a: anewarray 78
      // 7d: dup
      // 7e: bipush 0
      // 7f: aload 1
      // 80: aastore
      // 81: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // 84: aload 7
      // 86: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
      // 89: aload 0
      // 8a: invokestatic dev/latvian/mods/kubejs/script/SourceLine.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/script/SourceLine;
      // 8d: invokevirtual dev/latvian/mods/kubejs/error/KubeRuntimeException.source (Ldev/latvian/mods/kubejs/script/SourceLine;)Ldev/latvian/mods/kubejs/error/KubeRuntimeException;
      // 90: athrow
      // 91: aload 2
      // 92: checkcast java/lang/Iterable
      // 95: astore 7
      // 97: new com/google/gson/JsonArray
      // 9a: dup
      // 9b: invokespecial com/google/gson/JsonArray.<init> ()V
      // 9e: astore 8
      // a0: aload 7
      // a2: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // a7: astore 9
      // a9: aload 9
      // ab: invokeinterface java/util/Iterator.hasNext ()Z 1
      // b0: ifeq ca
      // b3: aload 9
      // b5: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // ba: astore 10
      // bc: aload 8
      // be: aload 0
      // bf: aload 10
      // c1: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.of (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonElement;
      // c4: invokevirtual com/google/gson/JsonArray.add (Lcom/google/gson/JsonElement;)V
      // c7: goto a9
      // ca: aload 8
      // cc: astore 4
      // ce: aload 4
      // d0: goto ef
      // d3: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // d6: dup
      // d7: ldc "Expected an iterable or JSON array string, got %s"
      // d9: bipush 1
      // da: anewarray 78
      // dd: dup
      // de: bipush 0
      // df: aload 1
      // e0: aastore
      // e1: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // e4: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;)V
      // e7: aload 0
      // e8: invokestatic dev/latvian/mods/kubejs/script/SourceLine.of (Ldev/latvian/mods/rhino/Context;)Ldev/latvian/mods/kubejs/script/SourceLine;
      // eb: invokevirtual dev/latvian/mods/kubejs/error/KubeRuntimeException.source (Ldev/latvian/mods/kubejs/script/SourceLine;)Ldev/latvian/mods/kubejs/error/KubeRuntimeException;
      // ee: athrow
      // ef: areturn
   }

   @Nullable
   static Object toObject(@Nullable JsonElement json) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonNull, com/google/gson/JsonObject, com/google/gson/JsonArray ]
      // 0b: tableswitch 196 -1 2 29 33 42 128
      // 28: aconst_null
      // 29: goto d3
      // 2c: aload 1
      // 2d: checkcast com/google/gson/JsonNull
      // 30: astore 3
      // 31: aconst_null
      // 32: goto d3
      // 35: aload 1
      // 36: checkcast com/google/gson/JsonObject
      // 39: astore 4
      // 3b: new java/util/LinkedHashMap
      // 3e: dup
      // 3f: invokespecial java/util/LinkedHashMap.<init> ()V
      // 42: astore 5
      // 44: aload 4
      // 46: invokevirtual com/google/gson/JsonObject.entrySet ()Ljava/util/Set;
      // 49: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 4e: astore 6
      // 50: aload 6
      // 52: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 57: ifeq 86
      // 5a: aload 6
      // 5c: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 61: checkcast java/util/Map$Entry
      // 64: astore 7
      // 66: aload 5
      // 68: aload 7
      // 6a: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 6f: checkcast java/lang/String
      // 72: aload 7
      // 74: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 79: checkcast com/google/gson/JsonElement
      // 7c: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.toObject (Lcom/google/gson/JsonElement;)Ljava/lang/Object;
      // 7f: invokevirtual java/util/LinkedHashMap.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 82: pop
      // 83: goto 50
      // 86: aload 5
      // 88: goto d3
      // 8b: aload 1
      // 8c: checkcast com/google/gson/JsonArray
      // 8f: astore 5
      // 91: new java/util/ArrayList
      // 94: dup
      // 95: aload 5
      // 97: invokevirtual com/google/gson/JsonArray.size ()I
      // 9a: invokespecial java/util/ArrayList.<init> (I)V
      // 9d: astore 6
      // 9f: aload 5
      // a1: invokevirtual com/google/gson/JsonArray.iterator ()Ljava/util/Iterator;
      // a4: astore 7
      // a6: aload 7
      // a8: invokeinterface java/util/Iterator.hasNext ()Z 1
      // ad: ifeq ca
      // b0: aload 7
      // b2: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // b7: checkcast com/google/gson/JsonElement
      // ba: astore 8
      // bc: aload 6
      // be: aload 8
      // c0: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.toObject (Lcom/google/gson/JsonElement;)Ljava/lang/Object;
      // c3: invokevirtual java/util/ArrayList.add (Ljava/lang/Object;)Z
      // c6: pop
      // c7: goto a6
      // ca: aload 6
      // cc: goto d3
      // cf: aload 0
      // d0: invokestatic dev/latvian/mods/kubejs/util/JsonUtils.toPrimitive (Lcom/google/gson/JsonElement;)Ljava/lang/Object;
      // d3: areturn
   }

   static String toString(JsonElement json) {
      return GSON.toJson(json);
   }

   static String toPrettyString(JsonElement json) {
      StringWriter writer = new StringWriter();
      JsonWriter jsonWriter = new JsonWriter(writer);
      jsonWriter.setIndent("\t");
      GSON.toJson(json, jsonWriter);
      return writer.toString();
   }

   static JsonElement fromString(@Nullable String string) {
      if (string != null && !string.isEmpty() && !string.equals("null")) {
         try {
            return (JsonElement)GSON.fromJson(string, JsonElement.class);
         } catch (Exception var2) {
            var2.printStackTrace();
            return JsonNull.INSTANCE;
         }
      } else {
         return JsonNull.INSTANCE;
      }
   }

   @Nullable
   static Object toPrimitive(@Nullable JsonElement element) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.consts.PrimitiveConstant.getString()" because "this.superClass" is null
      //   at org.jetbrains.java.decompiler.struct.StructClass.getRecordComponents(StructClass.java:216)
      //   at org.jetbrains.java.decompiler.modules.decompiler.IfPatternMatchProcessor.identifyRecordPatternMatch(IfPatternMatchProcessor.java:294)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.identifySwitchRecordPatternMatch(SwitchPatternMatchProcessor.java:526)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processStatement(SwitchPatternMatchProcessor.java:167)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:42)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatchingRec(SwitchPatternMatchProcessor.java:37)
      //   at org.jetbrains.java.decompiler.modules.decompiler.SwitchPatternMatchProcessor.processPatternMatching(SwitchPatternMatchProcessor.java:23)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:330)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: iload 2
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonPrimitive ]
      // 0b: lookupswitch 101 2 -1 101 0 25
      // 24: aload 1
      // 25: checkcast com/google/gson/JsonPrimitive
      // 28: astore 4
      // 2a: aload 4
      // 2c: invokevirtual com/google/gson/JsonPrimitive.isBoolean ()Z
      // 2f: ifeq 3f
      // 32: aload 4
      // 34: invokevirtual com/google/gson/JsonPrimitive.getAsBoolean ()Z
      // 37: invokestatic java/lang/Boolean.valueOf (Z)Ljava/lang/Boolean;
      // 3a: astore 3
      // 3b: aload 3
      // 3c: goto 73
      // 3f: aload 4
      // 41: invokevirtual com/google/gson/JsonPrimitive.isNumber ()Z
      // 44: ifeq 51
      // 47: aload 4
      // 49: invokevirtual com/google/gson/JsonPrimitive.getAsNumber ()Ljava/lang/Number;
      // 4c: astore 3
      // 4d: aload 3
      // 4e: goto 73
      // 51: aload 4
      // 53: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 56: invokestatic java/lang/Double.parseDouble (Ljava/lang/String;)D
      // 59: pop2
      // 5a: aload 4
      // 5c: invokevirtual com/google/gson/JsonPrimitive.getAsNumber ()Ljava/lang/Number;
      // 5f: astore 3
      // 60: aload 3
      // 61: goto 73
      // 64: astore 5
      // 66: aload 4
      // 68: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 6b: astore 3
      // 6c: aload 3
      // 6d: goto 73
      // 70: aconst_null
      // 71: astore 3
      // 72: aload 3
      // 73: areturn
   }
}
