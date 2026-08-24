package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.util.NBTUtils;
import dev.latvian.mods.kubejs.util.OrderedCompoundTag;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Undefined;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface NBTWrapper {
   static boolean isTagCompound(Object o) {
      return o == null || Undefined.isUndefined(o) || o instanceof CompoundTag || o instanceof CharSequence || o instanceof Map || o instanceof JsonElement;
   }

   static boolean isTagCollection(Object o) {
      return o == null || Undefined.isUndefined(o) || o instanceof CharSequence || o instanceof Collection || o instanceof JsonArray;
   }

   @Nullable
   static Object fromTag(@Nullable Tag t) {
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
      // 000: aload 0
      // 001: astore 1
      // 002: bipush 0
      // 003: istore 2
      // 004: aload 1
      // 005: iload 2
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/nbt/EndTag, net/minecraft/nbt/StringTag, net/minecraft/nbt/NumericTag, net/minecraft/nbt/CompoundTag, net/minecraft/nbt/CollectionTag ]
      // 00b: tableswitch 277 -1 4 37 41 50 65 79 195
      // 030: aconst_null
      // 031: goto 121
      // 034: aload 1
      // 035: checkcast net/minecraft/nbt/EndTag
      // 038: astore 3
      // 039: aconst_null
      // 03a: goto 121
      // 03d: aload 1
      // 03e: checkcast net/minecraft/nbt/StringTag
      // 041: astore 4
      // 043: aload 0
      // 044: invokeinterface net/minecraft/nbt/Tag.getAsString ()Ljava/lang/String; 1
      // 049: goto 121
      // 04c: aload 1
      // 04d: checkcast net/minecraft/nbt/NumericTag
      // 050: astore 5
      // 052: aload 5
      // 054: invokevirtual net/minecraft/nbt/NumericTag.getAsNumber ()Ljava/lang/Number;
      // 057: goto 121
      // 05a: aload 1
      // 05b: checkcast net/minecraft/nbt/CompoundTag
      // 05e: astore 6
      // 060: aload 6
      // 062: invokevirtual net/minecraft/nbt/CompoundTag.isEmpty ()Z
      // 065: ifeq 06e
      // 068: invokestatic java/util/Map.of ()Ljava/util/Map;
      // 06b: goto 121
      // 06e: aload 6
      // 070: invokestatic dev/latvian/mods/kubejs/util/NBTUtils.accessTagMap (Lnet/minecraft/nbt/CompoundTag;)Ljava/util/Map;
      // 073: astore 7
      // 075: new java/util/LinkedHashMap
      // 078: dup
      // 079: aload 7
      // 07b: invokeinterface java/util/Map.size ()I 1
      // 080: invokespecial java/util/LinkedHashMap.<init> (I)V
      // 083: astore 8
      // 085: aload 7
      // 087: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 08c: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 091: astore 9
      // 093: aload 9
      // 095: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 09a: ifeq 0c9
      // 09d: aload 9
      // 09f: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 0a4: checkcast java/util/Map$Entry
      // 0a7: astore 10
      // 0a9: aload 8
      // 0ab: aload 10
      // 0ad: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 0b2: checkcast java/lang/String
      // 0b5: aload 10
      // 0b7: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 0bc: checkcast net/minecraft/nbt/Tag
      // 0bf: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.fromTag (Lnet/minecraft/nbt/Tag;)Ljava/lang/Object;
      // 0c2: invokevirtual java/util/LinkedHashMap.put (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
      // 0c5: pop
      // 0c6: goto 093
      // 0c9: aload 8
      // 0cb: goto 121
      // 0ce: aload 1
      // 0cf: checkcast net/minecraft/nbt/CollectionTag
      // 0d2: astore 7
      // 0d4: aload 7
      // 0d6: invokevirtual net/minecraft/nbt/CollectionTag.isEmpty ()Z
      // 0d9: ifeq 0e2
      // 0dc: invokestatic java/util/List.of ()Ljava/util/List;
      // 0df: goto 121
      // 0e2: new java/util/ArrayList
      // 0e5: dup
      // 0e6: aload 7
      // 0e8: invokevirtual net/minecraft/nbt/CollectionTag.size ()I
      // 0eb: invokespecial java/util/ArrayList.<init> (I)V
      // 0ee: astore 8
      // 0f0: aload 7
      // 0f2: invokevirtual net/minecraft/nbt/CollectionTag.iterator ()Ljava/util/Iterator;
      // 0f5: astore 9
      // 0f7: aload 9
      // 0f9: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 0fe: ifeq 11b
      // 101: aload 9
      // 103: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 108: checkcast net/minecraft/nbt/Tag
      // 10b: astore 10
      // 10d: aload 8
      // 10f: aload 10
      // 111: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.fromTag (Lnet/minecraft/nbt/Tag;)Ljava/lang/Object;
      // 114: invokevirtual java/util/ArrayList.add (Ljava/lang/Object;)Z
      // 117: pop
      // 118: goto 0f7
      // 11b: aload 8
      // 11d: goto 121
      // 120: aload 0
      // 121: areturn
   }

   @Nullable
   static Tag toTag(@Nullable Tag tag) {
      return tag;
   }

   @Nullable
   static Tag wrap(Context cx, @Nullable Object v) {
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
      // 006: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/nbt/EndTag, net/minecraft/nbt/Tag, dev/latvian/mods/kubejs/util/NBTSerializable, java/lang/CharSequence, java/lang/Character, java/lang/Boolean, java/lang/Number, com/google/gson/JsonPrimitive, java/util/Map, com/google/gson/JsonObject, java/util/Collection, com/google/gson/JsonArray ]
      // 00b: tableswitch 656 -1 11 65 69 79 90 107 126 143 160 322 378 473 566 581
      // 04c: aconst_null
      // 04d: goto 29c
      // 050: aload 2
      // 051: checkcast net/minecraft/nbt/EndTag
      // 054: astore 4
      // 056: aconst_null
      // 057: goto 29c
      // 05a: aload 2
      // 05b: checkcast net/minecraft/nbt/Tag
      // 05e: astore 5
      // 060: aload 5
      // 062: goto 29c
      // 065: aload 2
      // 066: checkcast dev/latvian/mods/kubejs/util/NBTSerializable
      // 069: astore 6
      // 06b: aload 6
      // 06d: aload 0
      // 06e: invokeinterface dev/latvian/mods/kubejs/util/NBTSerializable.toNBT (Ldev/latvian/mods/rhino/Context;)Lnet/minecraft/nbt/Tag; 2
      // 073: goto 29c
      // 076: aload 2
      // 077: checkcast java/lang/CharSequence
      // 07a: astore 7
      // 07c: aload 7
      // 07e: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 083: invokestatic net/minecraft/nbt/StringTag.valueOf (Ljava/lang/String;)Lnet/minecraft/nbt/StringTag;
      // 086: goto 29c
      // 089: aload 2
      // 08a: checkcast java/lang/Character
      // 08d: astore 8
      // 08f: aload 8
      // 091: invokevirtual java/lang/Character.toString ()Ljava/lang/String;
      // 094: invokestatic net/minecraft/nbt/StringTag.valueOf (Ljava/lang/String;)Lnet/minecraft/nbt/StringTag;
      // 097: goto 29c
      // 09a: aload 2
      // 09b: checkcast java/lang/Boolean
      // 09e: astore 9
      // 0a0: aload 9
      // 0a2: invokevirtual java/lang/Boolean.booleanValue ()Z
      // 0a5: invokestatic net/minecraft/nbt/ByteTag.valueOf (Z)Lnet/minecraft/nbt/ByteTag;
      // 0a8: goto 29c
      // 0ab: aload 2
      // 0ac: checkcast java/lang/Number
      // 0af: astore 10
      // 0b1: aload 10
      // 0b3: dup
      // 0b4: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 0b7: pop
      // 0b8: astore 16
      // 0ba: bipush 0
      // 0bb: istore 17
      // 0bd: aload 16
      // 0bf: iload 17
      // 0c1: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ java/lang/Byte, java/lang/Short, java/lang/Integer, java/lang/Long, java/lang/Float ]
      // 0c6: tableswitch 124 0 4 34 52 70 88 106
      // 0e8: aload 16
      // 0ea: checkcast java/lang/Byte
      // 0ed: astore 11
      // 0ef: aload 11
      // 0f1: invokevirtual java/lang/Byte.byteValue ()B
      // 0f4: invokestatic net/minecraft/nbt/ByteTag.valueOf (B)Lnet/minecraft/nbt/ByteTag;
      // 0f7: goto 14a
      // 0fa: aload 16
      // 0fc: checkcast java/lang/Short
      // 0ff: astore 12
      // 101: aload 12
      // 103: invokevirtual java/lang/Short.shortValue ()S
      // 106: invokestatic net/minecraft/nbt/ShortTag.valueOf (S)Lnet/minecraft/nbt/ShortTag;
      // 109: goto 14a
      // 10c: aload 16
      // 10e: checkcast java/lang/Integer
      // 111: astore 13
      // 113: aload 13
      // 115: invokevirtual java/lang/Integer.intValue ()I
      // 118: invokestatic net/minecraft/nbt/IntTag.valueOf (I)Lnet/minecraft/nbt/IntTag;
      // 11b: goto 14a
      // 11e: aload 16
      // 120: checkcast java/lang/Long
      // 123: astore 14
      // 125: aload 14
      // 127: invokevirtual java/lang/Long.longValue ()J
      // 12a: invokestatic net/minecraft/nbt/LongTag.valueOf (J)Lnet/minecraft/nbt/LongTag;
      // 12d: goto 14a
      // 130: aload 16
      // 132: checkcast java/lang/Float
      // 135: astore 15
      // 137: aload 15
      // 139: invokevirtual java/lang/Float.floatValue ()F
      // 13c: invokestatic net/minecraft/nbt/FloatTag.valueOf (F)Lnet/minecraft/nbt/FloatTag;
      // 13f: goto 14a
      // 142: aload 10
      // 144: invokevirtual java/lang/Number.doubleValue ()D
      // 147: invokestatic net/minecraft/nbt/DoubleTag.valueOf (D)Lnet/minecraft/nbt/DoubleTag;
      // 14a: goto 29c
      // 14d: aload 2
      // 14e: checkcast com/google/gson/JsonPrimitive
      // 151: astore 16
      // 153: aload 16
      // 155: invokevirtual com/google/gson/JsonPrimitive.isNumber ()Z
      // 158: ifeq 167
      // 15b: aload 0
      // 15c: aload 16
      // 15e: invokevirtual com/google/gson/JsonPrimitive.getAsNumber ()Ljava/lang/Number;
      // 161: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // 164: goto 29c
      // 167: aload 16
      // 169: invokevirtual com/google/gson/JsonPrimitive.isBoolean ()Z
      // 16c: ifeq 17a
      // 16f: aload 16
      // 171: invokevirtual com/google/gson/JsonPrimitive.getAsBoolean ()Z
      // 174: invokestatic net/minecraft/nbt/ByteTag.valueOf (Z)Lnet/minecraft/nbt/ByteTag;
      // 177: goto 29c
      // 17a: aload 16
      // 17c: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 17f: invokestatic net/minecraft/nbt/StringTag.valueOf (Ljava/lang/String;)Lnet/minecraft/nbt/StringTag;
      // 182: goto 29c
      // 185: aload 2
      // 186: checkcast java/util/Map
      // 189: astore 17
      // 18b: new dev/latvian/mods/kubejs/util/OrderedCompoundTag
      // 18e: dup
      // 18f: invokespecial dev/latvian/mods/kubejs/util/OrderedCompoundTag.<init> ()V
      // 192: astore 18
      // 194: aload 17
      // 196: invokeinterface java/util/Map.entrySet ()Ljava/util/Set; 1
      // 19b: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 1a0: astore 19
      // 1a2: aload 19
      // 1a4: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 1a9: ifeq 1df
      // 1ac: aload 19
      // 1ae: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 1b3: checkcast java/util/Map$Entry
      // 1b6: astore 20
      // 1b8: aload 0
      // 1b9: aload 20
      // 1bb: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 1c0: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // 1c3: astore 21
      // 1c5: aload 21
      // 1c7: ifnull 1dc
      // 1ca: aload 18
      // 1cc: aload 20
      // 1ce: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 1d3: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 1d6: aload 21
      // 1d8: invokevirtual net/minecraft/nbt/CompoundTag.put (Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;
      // 1db: pop
      // 1dc: goto 1a2
      // 1df: aload 18
      // 1e1: goto 29c
      // 1e4: aload 2
      // 1e5: checkcast com/google/gson/JsonObject
      // 1e8: astore 18
      // 1ea: new dev/latvian/mods/kubejs/util/OrderedCompoundTag
      // 1ed: dup
      // 1ee: invokespecial dev/latvian/mods/kubejs/util/OrderedCompoundTag.<init> ()V
      // 1f1: astore 19
      // 1f3: aload 18
      // 1f5: invokevirtual com/google/gson/JsonObject.entrySet ()Ljava/util/Set;
      // 1f8: invokeinterface java/util/Set.iterator ()Ljava/util/Iterator; 1
      // 1fd: astore 20
      // 1ff: aload 20
      // 201: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 206: ifeq 23c
      // 209: aload 20
      // 20b: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 210: checkcast java/util/Map$Entry
      // 213: astore 21
      // 215: aload 0
      // 216: aload 21
      // 218: invokeinterface java/util/Map$Entry.getValue ()Ljava/lang/Object; 1
      // 21d: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // 220: astore 22
      // 222: aload 22
      // 224: ifnull 239
      // 227: aload 19
      // 229: aload 21
      // 22b: invokeinterface java/util/Map$Entry.getKey ()Ljava/lang/Object; 1
      // 230: checkcast java/lang/String
      // 233: aload 22
      // 235: invokevirtual net/minecraft/nbt/CompoundTag.put (Ljava/lang/String;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;
      // 238: pop
      // 239: goto 1ff
      // 23c: aload 19
      // 23e: goto 29c
      // 241: aload 2
      // 242: checkcast java/util/Collection
      // 245: astore 19
      // 247: aload 0
      // 248: aload 19
      // 24a: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrapCollection0 (Ldev/latvian/mods/rhino/Context;Ljava/util/Collection;)Lnet/minecraft/nbt/CollectionTag;
      // 24d: goto 29c
      // 250: aload 2
      // 251: checkcast com/google/gson/JsonArray
      // 254: astore 20
      // 256: new java/util/ArrayList
      // 259: dup
      // 25a: aload 20
      // 25c: invokevirtual com/google/gson/JsonArray.size ()I
      // 25f: invokespecial java/util/ArrayList.<init> (I)V
      // 262: astore 21
      // 264: aload 20
      // 266: invokevirtual com/google/gson/JsonArray.iterator ()Ljava/util/Iterator;
      // 269: astore 22
      // 26b: aload 22
      // 26d: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 272: ifeq 292
      // 275: aload 22
      // 277: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 27c: checkcast com/google/gson/JsonElement
      // 27f: astore 23
      // 281: aload 21
      // 283: aload 0
      // 284: aload 23
      // 286: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // 289: invokeinterface java/util/List.add (Ljava/lang/Object;)Z 2
      // 28e: pop
      // 28f: goto 26b
      // 292: aload 0
      // 293: aload 21
      // 295: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrapCollection0 (Ldev/latvian/mods/rhino/Context;Ljava/util/Collection;)Lnet/minecraft/nbt/CollectionTag;
      // 298: goto 29c
      // 29b: aconst_null
      // 29c: areturn
   }

   @Nullable
   static CompoundTag wrapCompound(Context cx, @Nullable Object v) {
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
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/nbt/CompoundTag, java/lang/CharSequence, com/google/gson/JsonPrimitive, com/google/gson/JsonObject ]
      // 0b: tableswitch 145 -1 3 33 41 56 85 115
      // 2c: aconst_null
      // 2d: astore 4
      // 2f: aload 4
      // 31: goto bc
      // 34: aload 2
      // 35: checkcast net/minecraft/nbt/CompoundTag
      // 38: astore 5
      // 3a: aload 5
      // 3c: astore 4
      // 3e: aload 4
      // 40: goto bc
      // 43: aload 2
      // 44: checkcast java/lang/CharSequence
      // 47: astore 6
      // 49: aload 1
      // 4a: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 4d: invokestatic net/minecraft/nbt/TagParser.parseTag (Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;
      // 50: astore 4
      // 52: aload 4
      // 54: goto bc
      // 57: astore 7
      // 59: aload 7
      // 5b: aload 0
      // 5c: invokestatic dev/latvian/mods/rhino/Context.throwAsScriptRuntimeEx (Ljava/lang/Throwable;Ldev/latvian/mods/rhino/Context;)Ljava/lang/RuntimeException;
      // 5f: athrow
      // 60: aload 2
      // 61: checkcast com/google/gson/JsonPrimitive
      // 64: astore 7
      // 66: aload 7
      // 68: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 6b: invokestatic net/minecraft/nbt/TagParser.parseTag (Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;
      // 6e: astore 4
      // 70: aload 4
      // 72: goto bc
      // 75: astore 8
      // 77: aload 8
      // 79: aload 0
      // 7a: invokestatic dev/latvian/mods/rhino/Context.throwAsScriptRuntimeEx (Ljava/lang/Throwable;Ldev/latvian/mods/rhino/Context;)Ljava/lang/RuntimeException;
      // 7d: athrow
      // 7e: aload 2
      // 7f: checkcast com/google/gson/JsonObject
      // 82: astore 8
      // 84: aload 8
      // 86: invokevirtual com/google/gson/JsonObject.toString ()Ljava/lang/String;
      // 89: invokestatic net/minecraft/nbt/TagParser.parseTag (Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;
      // 8c: astore 4
      // 8e: aload 4
      // 90: goto bc
      // 93: astore 9
      // 95: aload 9
      // 97: aload 0
      // 98: invokestatic dev/latvian/mods/rhino/Context.throwAsScriptRuntimeEx (Ljava/lang/Throwable;Ldev/latvian/mods/rhino/Context;)Ljava/lang/RuntimeException;
      // 9b: athrow
      // 9c: aload 0
      // 9d: aload 1
      // 9e: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // a1: astore 10
      // a3: aload 10
      // a5: instanceof net/minecraft/nbt/CompoundTag
      // a8: ifeq b7
      // ab: aload 10
      // ad: checkcast net/minecraft/nbt/CompoundTag
      // b0: astore 9
      // b2: aload 9
      // b4: goto b8
      // b7: aconst_null
      // b8: astore 4
      // ba: aload 4
      // bc: areturn
   }

   @Nullable
   static CollectionTag<?> wrapCollection(Context cx, @Nullable Object v) {
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
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/nbt/CollectionTag, java/lang/CharSequence, com/google/gson/JsonArray ]
      // 0b: tableswitch 172 -1 2 29 37 52 95
      // 28: aconst_null
      // 29: astore 4
      // 2b: aload 4
      // 2d: goto c3
      // 30: aload 2
      // 31: checkcast net/minecraft/nbt/CollectionTag
      // 34: astore 5
      // 36: aload 5
      // 38: astore 4
      // 3a: aload 4
      // 3c: goto c3
      // 3f: aload 2
      // 40: checkcast java/lang/CharSequence
      // 43: astore 6
      // 45: aload 1
      // 46: invokestatic java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
      // 49: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "{a:\u0001}" ]
      // 4e: invokestatic net/minecraft/nbt/TagParser.parseTag (Ljava/lang/String;)Lnet/minecraft/nbt/CompoundTag;
      // 51: ldc_w "a"
      // 54: invokevirtual net/minecraft/nbt/CompoundTag.get (Ljava/lang/String;)Lnet/minecraft/nbt/Tag;
      // 57: checkcast net/minecraft/nbt/CollectionTag
      // 5a: astore 4
      // 5c: aload 4
      // 5e: goto c3
      // 61: astore 7
      // 63: aload 7
      // 65: aload 0
      // 66: invokestatic dev/latvian/mods/rhino/Context.throwAsScriptRuntimeEx (Ljava/lang/Throwable;Ldev/latvian/mods/rhino/Context;)Ljava/lang/RuntimeException;
      // 69: athrow
      // 6a: aload 2
      // 6b: checkcast com/google/gson/JsonArray
      // 6e: astore 7
      // 70: new java/util/ArrayList
      // 73: dup
      // 74: aload 7
      // 76: invokevirtual com/google/gson/JsonArray.size ()I
      // 79: invokespecial java/util/ArrayList.<init> (I)V
      // 7c: astore 8
      // 7e: aload 7
      // 80: invokevirtual com/google/gson/JsonArray.iterator ()Ljava/util/Iterator;
      // 83: astore 9
      // 85: aload 9
      // 87: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 8c: ifeq aa
      // 8f: aload 9
      // 91: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 96: checkcast com/google/gson/JsonElement
      // 99: astore 10
      // 9b: aload 8
      // 9d: aload 0
      // 9e: aload 10
      // a0: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/nbt/Tag;
      // a3: invokevirtual java/util/ArrayList.add (Ljava/lang/Object;)Z
      // a6: pop
      // a7: goto 85
      // aa: aload 0
      // ab: aload 8
      // ad: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrapCollection0 (Ldev/latvian/mods/rhino/Context;Ljava/util/Collection;)Lnet/minecraft/nbt/CollectionTag;
      // b0: astore 4
      // b2: aload 4
      // b4: goto c3
      // b7: aload 0
      // b8: aload 1
      // b9: checkcast java/util/Collection
      // bc: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/NBTWrapper.wrapCollection0 (Ldev/latvian/mods/rhino/Context;Ljava/util/Collection;)Lnet/minecraft/nbt/CollectionTag;
      // bf: astore 4
      // c1: aload 4
      // c3: areturn
   }

   @Nullable
   static ListTag wrapListTag(Context cx, @Nullable Object list) {
      return (ListTag)wrapCollection(cx, list);
   }

   private static CollectionTag<?> wrapCollection0(Context cx, Collection<?> c) {
      if (c.isEmpty()) {
         return new ListTag();
      } else {
         Tag[] values = new Tag[c.size()];
         int s = 0;
         byte commmonId = -1;

         for (Object o : c) {
            values[s] = wrap(cx, o);
            if (values[s] != null) {
               if (commmonId == -1) {
                  commmonId = values[s].getId();
               } else if (commmonId != values[s].getId()) {
                  commmonId = 0;
               }

               s++;
            }
         }

         if (commmonId == 3) {
            int[] array = new int[s];

            for (int i = 0; i < s; i++) {
               array[i] = ((NumericTag)values[i]).getAsInt();
            }

            return new IntArrayTag(array);
         } else if (commmonId == 1) {
            byte[] array = new byte[s];

            for (int i = 0; i < s; i++) {
               array[i] = ((NumericTag)values[i]).getAsByte();
            }

            return new ByteArrayTag(array);
         } else if (commmonId != 4) {
            if (commmonId != 0 && commmonId != -1) {
               ListTag nbt = new ListTag();

               for (Tag nbt1 : values) {
                  if (nbt1 == null) {
                     return nbt;
                  }

                  nbt.add(nbt1);
               }

               return nbt;
            } else {
               return new ListTag();
            }
         } else {
            long[] array = new long[s];

            for (int i = 0; i < s; i++) {
               array[i] = ((NumericTag)values[i]).getAsLong();
            }

            return new LongArrayTag(array);
         }
      }
   }

   static Tag compoundTag() {
      return new OrderedCompoundTag();
   }

   static Tag compoundTag(Context cx, Map<?, ?> map) {
      OrderedCompoundTag tag = new OrderedCompoundTag();

      for (Entry<?, ?> entry : map.entrySet()) {
         Tag tag1 = wrap(cx, entry.getValue());
         if (tag1 != null) {
            tag.put(String.valueOf(entry.getKey()), tag1);
         }
      }

      return tag;
   }

   static Tag listTag() {
      return new ListTag();
   }

   static Tag listTag(Context cx, List<?> list) {
      ListTag tag = new ListTag();

      for (Object v : list) {
         tag.add(wrap(cx, v));
      }

      return tag;
   }

   static Tag byteTag(byte v) {
      return ByteTag.valueOf(v);
   }

   static Tag b(byte v) {
      return ByteTag.valueOf(v);
   }

   static Tag shortTag(short v) {
      return ShortTag.valueOf(v);
   }

   static Tag s(short v) {
      return ShortTag.valueOf(v);
   }

   static Tag intTag(int v) {
      return IntTag.valueOf(v);
   }

   static Tag i(int v) {
      return IntTag.valueOf(v);
   }

   static Tag longTag(long v) {
      return LongTag.valueOf(v);
   }

   static Tag l(long v) {
      return LongTag.valueOf(v);
   }

   static Tag floatTag(float v) {
      return FloatTag.valueOf(v);
   }

   static Tag f(float v) {
      return FloatTag.valueOf(v);
   }

   static Tag doubleTag(double v) {
      return DoubleTag.valueOf(v);
   }

   static Tag d(double v) {
      return DoubleTag.valueOf(v);
   }

   static Tag stringTag(String v) {
      return StringTag.valueOf(v);
   }

   static Tag intArrayTag(int[] v) {
      return new IntArrayTag(v);
   }

   static Tag ia(int[] v) {
      return new IntArrayTag(v);
   }

   static Tag longArrayTag(long[] v) {
      return new LongArrayTag(v);
   }

   static Tag la(long[] v) {
      return new LongArrayTag(v);
   }

   static Tag byteArrayTag(byte[] v) {
      return new ByteArrayTag(v);
   }

   static Tag ba(byte[] v) {
      return new ByteArrayTag(v);
   }

   static JsonElement toJson(@Nullable Tag t) {
      return NBTUtils.toJson(t);
   }

   @Nullable
   static OrderedCompoundTag read(FriendlyByteBuf buf) {
      return NBTUtils.read(buf);
   }
}
