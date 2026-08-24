package dev.latvian.mods.rhino;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.StringWriter;
import java.util.Objects;

public class NativeGSON extends NativeJSON {
   private final Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().setLenient().create();

   static void initGSON(Scriptable scope, boolean sealed, Context cx) {
      register(new NativeGSON(), scope, sealed, cx);
   }

   public static JsonElement stringify0(Context param0, Object param1) {
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
      // 000: aload 1
      // 001: astore 2
      // 002: bipush 0
      // 003: istore 3
      // 004: aload 2
      // 005: iload 3
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ com/google/gson/JsonElement, java/lang/Boolean, java/lang/CharSequence, java/lang/Number, dev/latvian/mods/rhino/NativeString, dev/latvian/mods/rhino/NativeNumber, dev/latvian/mods/rhino/NativeObject, java/util/Map, java/lang/Iterable, java/lang/Object ]
      // 00b: tableswitch 486 -1 9 57 63 74 92 112 126 147 167 303 401 463
      // 044: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 047: goto 222
      // 04a: aload 2
      // 04b: checkcast com/google/gson/JsonElement
      // 04e: astore 4
      // 050: aload 4
      // 052: goto 222
      // 055: aload 2
      // 056: checkcast java/lang/Boolean
      // 059: astore 5
      // 05b: new com/google/gson/JsonPrimitive
      // 05e: dup
      // 05f: aload 5
      // 061: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/Boolean;)V
      // 064: goto 222
      // 067: aload 2
      // 068: checkcast java/lang/CharSequence
      // 06b: astore 6
      // 06d: new com/google/gson/JsonPrimitive
      // 070: dup
      // 071: aload 1
      // 072: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 075: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/String;)V
      // 078: goto 222
      // 07b: aload 2
      // 07c: checkcast java/lang/Number
      // 07f: astore 7
      // 081: aload 7
      // 083: invokestatic dev/latvian/mods/rhino/NativeGSON.numberPrimitive (Ljava/lang/Number;)Lcom/google/gson/JsonElement;
      // 086: goto 222
      // 089: aload 2
      // 08a: checkcast dev/latvian/mods/rhino/NativeString
      // 08d: astore 8
      // 08f: new com/google/gson/JsonPrimitive
      // 092: dup
      // 093: aload 0
      // 094: aload 1
      // 095: invokestatic dev/latvian/mods/rhino/ScriptRuntime.toString (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Ljava/lang/String;
      // 098: invokespecial com/google/gson/JsonPrimitive.<init> (Ljava/lang/String;)V
      // 09b: goto 222
      // 09e: aload 2
      // 09f: checkcast dev/latvian/mods/rhino/NativeNumber
      // 0a2: astore 9
      // 0a4: aload 0
      // 0a5: aload 1
      // 0a6: invokestatic dev/latvian/mods/rhino/ScriptRuntime.toNumber (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)D
      // 0a9: invokestatic java/lang/Double.valueOf (D)Ljava/lang/Double;
      // 0ac: invokestatic dev/latvian/mods/rhino/NativeGSON.numberPrimitive (Ljava/lang/Number;)Lcom/google/gson/JsonElement;
      // 0af: goto 222
      // 0b2: aload 2
      // 0b3: checkcast dev/latvian/mods/rhino/NativeObject
      // 0b6: astore 10
      // 0b8: new com/google/gson/JsonObject
      // 0bb: dup
      // 0bc: invokespecial com/google/gson/JsonObject.<init> ()V
      // 0bf: astore 11
      // 0c1: aload 10
      // 0c3: aload 0
      // 0c4: invokevirtual dev/latvian/mods/rhino/NativeObject.getIds (Ldev/latvian/mods/rhino/Context;)[Ljava/lang/Object;
      // 0c7: astore 12
      // 0c9: aload 12
      // 0cb: arraylength
      // 0cc: istore 13
      // 0ce: bipush 0
      // 0cf: istore 14
      // 0d1: iload 14
      // 0d3: iload 13
      // 0d5: if_icmpge 135
      // 0d8: aload 12
      // 0da: iload 14
      // 0dc: aaload
      // 0dd: astore 15
      // 0df: aload 15
      // 0e1: instanceof java/lang/Integer
      // 0e4: ifeq 0fe
      // 0e7: aload 15
      // 0e9: checkcast java/lang/Integer
      // 0ec: astore 17
      // 0ee: aload 10
      // 0f0: aload 0
      // 0f1: aload 17
      // 0f3: invokevirtual java/lang/Integer.intValue ()I
      // 0f6: aload 10
      // 0f8: invokevirtual dev/latvian/mods/rhino/NativeObject.get (Ldev/latvian/mods/rhino/Context;ILdev/latvian/mods/rhino/Scriptable;)Ljava/lang/Object;
      // 0fb: goto 10b
      // 0fe: aload 10
      // 100: aload 0
      // 101: aload 15
      // 103: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 106: aload 10
      // 108: invokevirtual dev/latvian/mods/rhino/NativeObject.get (Ldev/latvian/mods/rhino/Context;Ljava/lang/String;Ldev/latvian/mods/rhino/Scriptable;)Ljava/lang/Object;
      // 10b: astore 16
      // 10d: aload 16
      // 10f: invokestatic dev/latvian/mods/rhino/Wrapper.unwrapped (Ljava/lang/Object;)Ljava/lang/Object;
      // 112: astore 16
      // 114: aload 16
      // 116: invokestatic dev/latvian/mods/rhino/NativeGSON.doesNotSerialize (Ljava/lang/Object;)Z
      // 119: ifeq 11f
      // 11c: goto 12f
      // 11f: aload 11
      // 121: aload 15
      // 123: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 126: aload 0
      // 127: aload 16
      // 129: invokestatic dev/latvian/mods/rhino/NativeGSON.stringify0 (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonElement;
      // 12c: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 12f: iinc 14 1
      // 132: goto 0d1
      // 135: aload 11
      // 137: goto 222
      // 13a: aload 2
      // 13b: checkcast java/util/Map
      // 13e: astore 11
      // 140: new com/google/gson/JsonObject
      // 143: dup
      // 144: invokespecial com/google/gson/JsonObject.<init> ()V
      // 147: astore 12
      // 149: aload 11
      // 14b: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 150: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 155: astore 13
      // 157: aload 13
      // 159: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 15e: ifeq 197
      // 161: aload 13
      // 163: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 168: checkcast java/util/Map$Entry
      // 16b: astore 14
      // 16d: aload 14
      // 16f: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 174: invokestatic dev/latvian/mods/rhino/NativeGSON.doesNotSerialize (Ljava/lang/Object;)Z
      // 177: ifne 194
      // 17a: aload 12
      // 17c: aload 14
      // 17e: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 183: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 186: aload 0
      // 187: aload 14
      // 189: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 18e: invokestatic dev/latvian/mods/rhino/NativeGSON.stringify0 (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonElement;
      // 191: invokevirtual com/google/gson/JsonObject.add (Ljava/lang/String;Lcom/google/gson/JsonElement;)V
      // 194: goto 157
      // 197: aload 12
      // 199: goto 222
      // 19c: aload 2
      // 19d: checkcast java/lang/Iterable
      // 1a0: astore 12
      // 1a2: new com/google/gson/JsonArray
      // 1a5: dup
      // 1a6: invokespecial com/google/gson/JsonArray.<init> ()V
      // 1a9: astore 13
      // 1ab: aload 12
      // 1ad: invokeinterface java/lang/Iterable.iterator ()Ljava/util/Iterator; 1
      // 1b2: astore 14
      // 1b4: aload 14
      // 1b6: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 1bb: ifeq 1d5
      // 1be: aload 14
      // 1c0: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 1c5: astore 15
      // 1c7: aload 13
      // 1c9: aload 0
      // 1ca: aload 15
      // 1cc: invokestatic dev/latvian/mods/rhino/NativeGSON.stringify0 (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lcom/google/gson/JsonElement;
      // 1cf: invokevirtual com/google/gson/JsonArray.add (Lcom/google/gson/JsonElement;)V
      // 1d2: goto 1b4
      // 1d5: aload 13
      // 1d7: goto 222
      // 1da: aload 2
      // 1db: astore 13
      // 1dd: aload 13
      // 1df: invokestatic dev/latvian/mods/rhino/NativeGSON.doesNotSerialize (Ljava/lang/Object;)Z
      // 1e2: ifne 1eb
      // 1e5: bipush 10
      // 1e7: istore 3
      // 1e8: goto 004
      // 1eb: getstatic com/google/gson/JsonNull.INSTANCE Lcom/google/gson/JsonNull;
      // 1ee: goto 222
      // 1f1: new com/google/gson/JsonArray
      // 1f4: dup
      // 1f5: invokespecial com/google/gson/JsonArray.<init> ()V
      // 1f8: astore 14
      // 1fa: aload 0
      // 1fb: bipush 0
      // 1fc: invokevirtual dev/latvian/mods/rhino/Context.getCachedClassStorage (Z)Ldev/latvian/mods/rhino/CachedClassStorage;
      // 1ff: aload 1
      // 200: invokestatic dev/latvian/mods/rhino/Wrapper.unwrapped (Ljava/lang/Object;)Ljava/lang/Object;
      // 203: invokevirtual java/lang/Object.getClass ()Ljava/lang/Class;
      // 206: invokevirtual dev/latvian/mods/rhino/CachedClassStorage.get (Ljava/lang/Class;)Ldev/latvian/mods/rhino/CachedClassInfo;
      // 209: invokevirtual dev/latvian/mods/rhino/CachedClassInfo.getDebugInfo ()Ljava/util/List;
      // 20c: aload 14
      // 20e: dup
      // 20f: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 212: pop
      // 213: invokedynamic accept (Lcom/google/gson/JsonArray;)Ljava/util/function/Consumer; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)V, com/google/gson/JsonArray.add (Ljava/lang/String;)V, (Ljava/lang/String;)V ]
      // 218: invokeinterface java/util/List.forEach (Ljava/util/function/Consumer;)V 2
      // 21d: aload 14
      // 21f: goto 222
      // 222: areturn
   }

   private static JsonElement numberPrimitive(Number n) {
      double d = n.doubleValue();
      if (!Double.isFinite(d)) {
         return JsonNull.INSTANCE;
      } else {
         long l = (long)d;
         return l == d ? new JsonPrimitive(l) : new JsonPrimitive(n);
      }
   }

   private NativeGSON() {
   }

   @Override
   public String stringifyJSON(Object value, Object replacer, Object space, Context cx) {
      StringWriter stringWriter = new StringWriter();
      JsonWriter writer = new JsonWriter(stringWriter);
      String indent = null;
      if (space instanceof NativeNumber) {
         space = ScriptRuntime.toNumber(cx, space);
      } else if (space instanceof NativeString) {
         space = ScriptRuntime.toString(cx, space);
      }

      if (space instanceof Number) {
         int gapLength = (int)ScriptRuntime.toInteger(cx, space);
         gapLength = Math.min(10, gapLength);
         indent = gapLength > 0 ? " ".repeat(gapLength) : "";
      } else if (space instanceof String) {
         indent = (String)space;
         if (indent.length() > 10) {
            indent = indent.substring(0, 10);
         }
      }

      writer.setIndent(Objects.requireNonNullElse(indent, ""));
      this.gson.toJson(stringify0(cx, value), writer);
      return stringWriter.toString();
   }
}
