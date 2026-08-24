package dev.latvian.mods.kubejs.util;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DataResult;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface ID {
   ResourceLocation UNKNOWN = ResourceLocation.fromNamespaceAndPath("unknown", "unknown");
   ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");
   UnaryOperator<String> BLOCKSTATE = s -> "blockstates/" + s;
   UnaryOperator<String> BLOCK = s -> "block/" + s;
   UnaryOperator<String> ITEM = s -> "item/" + s;
   UnaryOperator<String> MODEL = s -> "models/" + s;
   UnaryOperator<String> BLOCK_MODEL = s -> "models/block/" + s;
   UnaryOperator<String> ITEM_MODEL = s -> "models/item/" + s;
   UnaryOperator<String> BLOCK_LOOT_TABLE = s -> "loot_table/blocks/" + s;
   UnaryOperator<String> PNG_TEXTURE = s -> "textures/" + s + ".png";
   UnaryOperator<String> PNG_TEXTURE_MCMETA = s -> "textures/" + s + ".png.mcmeta";
   UnaryOperator<String> PARTICLE = s -> "particles/" + s;

   static String string(@Nullable String id) {
      if (id != null && !id.isEmpty()) {
         return id.indexOf(58) == -1 ? "minecraft:" + id : id;
      } else {
         return "";
      }
   }

   static String kjsString(String id) {
      if (id != null && !id.isEmpty()) {
         return id.indexOf(58) == -1 ? "kubejs:" + id : id;
      } else {
         return "";
      }
   }

   static String namespace(@Nullable String s) {
      if (s != null && !s.isEmpty()) {
         int i = s.indexOf(58);
         return i == -1 ? "minecraft" : s.substring(0, i);
      } else {
         return "minecraft";
      }
   }

   static String path(@Nullable String s) {
      if (s != null && !s.isEmpty()) {
         int i = s.indexOf(58);
         return i == -1 ? s : s.substring(i + 1);
      } else {
         return "air";
      }
   }

   static ResourceLocation of(@Nullable Object o, boolean preferKJS) {
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
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ net/minecraft/resources/ResourceLocation, net/minecraft/resources/ResourceKey, net/minecraft/core/Holder, dev/latvian/mods/kubejs/core/RegistryObjectKJS ]
      // 0b: tableswitch 117 -1 3 33 41 56 74 97
      // 2c: aconst_null
      // 2d: astore 4
      // 2f: aload 4
      // 31: goto d7
      // 34: aload 2
      // 35: checkcast net/minecraft/resources/ResourceLocation
      // 38: astore 5
      // 3a: aload 5
      // 3c: astore 4
      // 3e: aload 4
      // 40: goto d7
      // 43: aload 2
      // 44: checkcast net/minecraft/resources/ResourceKey
      // 47: astore 6
      // 49: aload 6
      // 4b: invokevirtual net/minecraft/resources/ResourceKey.location ()Lnet/minecraft/resources/ResourceLocation;
      // 4e: astore 4
      // 50: aload 4
      // 52: goto d7
      // 55: aload 2
      // 56: checkcast net/minecraft/core/Holder
      // 59: astore 7
      // 5b: aload 7
      // 5d: invokeinterface net/minecraft/core/Holder.getKey ()Lnet/minecraft/resources/ResourceKey; 1
      // 62: invokevirtual net/minecraft/resources/ResourceKey.location ()Lnet/minecraft/resources/ResourceLocation;
      // 65: astore 4
      // 67: aload 4
      // 69: goto d7
      // 6c: aload 2
      // 6d: checkcast dev/latvian/mods/kubejs/core/RegistryObjectKJS
      // 70: astore 8
      // 72: aload 8
      // 74: invokeinterface dev/latvian/mods/kubejs/core/RegistryObjectKJS.kjs$getIdLocation ()Lnet/minecraft/resources/ResourceLocation; 1
      // 79: astore 4
      // 7b: aload 4
      // 7d: goto d7
      // 80: aload 0
      // 81: instanceof com/google/gson/JsonPrimitive
      // 84: ifeq 95
      // 87: aload 0
      // 88: checkcast com/google/gson/JsonPrimitive
      // 8b: astore 10
      // 8d: aload 10
      // 8f: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 92: goto 99
      // 95: aload 0
      // 96: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 99: astore 9
      // 9b: aload 9
      // 9d: bipush 58
      // 9f: invokevirtual java/lang/String.indexOf (I)I
      // a2: bipush -1
      // a3: if_icmpne b3
      // a6: iload 1
      // a7: ifeq b3
      // aa: aload 9
      // ac: invokedynamic makeConcatWithConstants (Ljava/lang/String;)Ljava/lang/String; bsm=java/lang/invoke/StringConcatFactory.makeConcatWithConstants (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "kubejs:\u0001" ]
      // b1: astore 9
      // b3: aload 9
      // b5: invokestatic net/minecraft/resources/ResourceLocation.parse (Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;
      // b8: astore 4
      // ba: aload 4
      // bc: goto d7
      // bf: astore 10
      // c1: new dev/latvian/mods/kubejs/error/KubeRuntimeException
      // c4: dup
      // c5: ldc "Could not create ID from '%s'!"
      // c7: bipush 1
      // c8: anewarray 59
      // cb: dup
      // cc: bipush 0
      // cd: aload 9
      // cf: aastore
      // d0: invokevirtual java/lang/String.formatted ([Ljava/lang/Object;)Ljava/lang/String;
      // d3: invokespecial dev/latvian/mods/kubejs/error/KubeRuntimeException.<init> (Ljava/lang/String;)V
      // d6: athrow
      // d7: areturn
   }

   static ResourceLocation mc(@Nullable Object o) {
      return of(o, false);
   }

   static ResourceLocation kjs(@Nullable Object o) {
      return of(o, true);
   }

   static boolean isKey(Object from) {
      return from instanceof CharSequence || from instanceof ResourceLocation || from instanceof ResourceKey;
   }

   static String url(ResourceLocation id) {
      return URLEncoder.encode(id.getNamespace(), StandardCharsets.UTF_8) + "/" + URLEncoder.encode(id.getPath(), StandardCharsets.UTF_8);
   }

   static String reduce(ResourceLocation id) {
      return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
   }

   static String reduceKjs(ResourceLocation id) {
      return id.getNamespace().equals("kubejs") ? id.getPath() : id.toString();
   }

   static String resourcePath(ResourceLocation id) {
      return id.getNamespace().equals("minecraft") ? id.getPath() : id.getNamespace() + "/" + id.getPath();
   }

   static DataResult<ResourceLocation> read(StringReader reader) {
      return ResourceLocation.read(readGreedy(reader));
   }

   private static String readGreedy(StringReader reader) {
      int i = reader.getCursor();

      while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
         reader.skip();
      }

      return reader.getString().substring(i, reader.getCursor());
   }
}
