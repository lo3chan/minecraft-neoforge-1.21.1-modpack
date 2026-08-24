package dev.latvian.mods.kubejs.util.registrypredicate;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface RegistryPredicate<T> extends Predicate<Holder<T>> {
   static RegistryPredicate<?> of(Context cx, Object from, TypeInfo target) {
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
      // 01: astore 3
      // 02: bipush 0
      // 03: istore 4
      // 05: aload 3
      // 06: iload 4
      // 08: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate, java/util/regex/Pattern, dev/latvian/mods/rhino/regexp/NativeRegExp, java/lang/CharSequence, com/google/gson/JsonPrimitive, dev/latvian/mods/rhino/BaseFunction ]
      // 0d: tableswitch 211 -1 5 43 49 60 78 99 119 137
      // 38: getstatic dev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate.FALSE Ldev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate;
      // 3b: goto ef
      // 3e: aload 3
      // 3f: checkcast dev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate
      // 42: astore 5
      // 44: aload 5
      // 46: goto ef
      // 49: aload 3
      // 4a: checkcast java/util/regex/Pattern
      // 4d: astore 6
      // 4f: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate
      // 52: dup
      // 53: aload 6
      // 55: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate.<init> (Ljava/util/regex/Pattern;)V
      // 58: goto ef
      // 5b: aload 3
      // 5c: checkcast dev/latvian/mods/rhino/regexp/NativeRegExp
      // 5f: astore 7
      // 61: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate
      // 64: dup
      // 65: aload 7
      // 67: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.wrap (Ljava/lang/Object;)Ljava/util/regex/Pattern;
      // 6a: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate.<init> (Ljava/util/regex/Pattern;)V
      // 6d: goto ef
      // 70: aload 3
      // 71: checkcast java/lang/CharSequence
      // 74: astore 8
      // 76: aload 2
      // 77: aload 8
      // 79: invokeinterface java/lang/CharSequence.toString ()Ljava/lang/String; 1
      // 7e: invokestatic dev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate.ofString (Ldev/latvian/mods/rhino/type/TypeInfo;Ljava/lang/String;)Ldev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate;
      // 81: goto ef
      // 84: aload 3
      // 85: checkcast com/google/gson/JsonPrimitive
      // 88: astore 9
      // 8a: aload 2
      // 8b: aload 9
      // 8d: invokevirtual com/google/gson/JsonPrimitive.getAsString ()Ljava/lang/String;
      // 90: invokestatic dev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate.ofString (Ldev/latvian/mods/rhino/type/TypeInfo;Ljava/lang/String;)Ldev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate;
      // 93: goto ef
      // 96: aload 3
      // 97: checkcast dev/latvian/mods/rhino/BaseFunction
      // 9a: astore 10
      // 9c: aload 2
      // 9d: bipush 0
      // 9e: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.param (I)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // a3: astore 11
      // a5: aload 0
      // a6: aload 10
      // a8: aload 11
      // aa: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.shouldConvert ()Z 1
      // af: ifeq c6
      // b2: getstatic dev/latvian/mods/rhino/type/TypeInfo.RAW_PREDICATE Ldev/latvian/mods/rhino/type/TypeInfo;
      // b5: bipush 1
      // b6: anewarray 47
      // b9: dup
      // ba: bipush 0
      // bb: aload 11
      // bd: aastore
      // be: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.withParams ([Ldev/latvian/mods/rhino/type/TypeInfo;)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // c3: goto c9
      // c6: getstatic dev/latvian/mods/rhino/type/TypeInfo.RAW_PREDICATE Ldev/latvian/mods/rhino/type/TypeInfo;
      // c9: invokevirtual dev/latvian/mods/rhino/Context.jsToJava (Ljava/lang/Object;Ldev/latvian/mods/rhino/type/TypeInfo;)Ljava/lang/Object;
      // cc: checkcast java/util/function/Predicate
      // cf: astore 12
      // d1: aload 12
      // d3: dup
      // d4: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // d7: pop
      // d8: invokedynamic test (Ljava/util/function/Predicate;)Ldev/latvian/mods/kubejs/util/registrypredicate/RegistryPredicate; bsm=java/lang/invoke/LambdaMetafactory.metafactory (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite; args=[ (Ljava/lang/Object;)Z, java/util/function/Predicate.test (Ljava/lang/Object;)Z, (Lnet/minecraft/core/Holder;)Z ]
      // dd: goto ef
      // e0: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryHolderPredicate
      // e3: dup
      // e4: new net/minecraft/core/Holder$Direct
      // e7: dup
      // e8: aload 1
      // e9: invokespecial net/minecraft/core/Holder$Direct.<init> (Ljava/lang/Object;)V
      // ec: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryHolderPredicate.<init> (Lnet/minecraft/core/Holder;)V
      // ef: areturn
   }

   @NotNull
   private static RegistryPredicate<?> ofString(TypeInfo target, String s) {
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
      // 001: dup
      // 002: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 005: pop
      // 006: astore 2
      // 007: bipush 0
      // 008: istore 3
      // 009: aload 2
      // 00a: iload 3
      // 00b: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "*", "-", java/lang/String, java/lang/String ]
      // 010: tableswitch 158 0 3 32 38 44 123
      // 030: getstatic dev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate.TRUE Ldev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate;
      // 033: goto 124
      // 036: getstatic dev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate.FALSE Ldev/latvian/mods/kubejs/util/registrypredicate/EntireRegistryPredicate;
      // 039: goto 124
      // 03c: aload 2
      // 03d: astore 4
      // 03f: aload 4
      // 041: bipush 0
      // 042: invokevirtual java/lang/String.charAt (I)C
      // 045: bipush 35
      // 047: if_icmpeq 04f
      // 04a: bipush 3
      // 04b: istore 3
      // 04c: goto 009
      // 04f: aload 0
      // 050: bipush 0
      // 051: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.param (I)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 056: invokestatic dev/latvian/mods/kubejs/registry/RegistryType.ofType (Ldev/latvian/mods/rhino/type/TypeInfo;)Ldev/latvian/mods/kubejs/registry/RegistryType;
      // 059: astore 5
      // 05b: aload 4
      // 05d: bipush 1
      // 05e: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 061: invokestatic dev/latvian/mods/kubejs/util/ID.mc (Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;
      // 064: astore 6
      // 066: aload 5
      // 068: ifnull 07f
      // 06b: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryTagKeyPredicate
      // 06e: dup
      // 06f: aload 5
      // 071: invokevirtual dev/latvian/mods/kubejs/registry/RegistryType.key ()Lnet/minecraft/resources/ResourceKey;
      // 074: aload 6
      // 076: invokestatic net/minecraft/tags/TagKey.create (Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/tags/TagKey;
      // 079: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryTagKeyPredicate.<init> (Lnet/minecraft/tags/TagKey;)V
      // 07c: goto 124
      // 07f: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryTagIDPredicate
      // 082: dup
      // 083: aload 6
      // 085: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryTagIDPredicate.<init> (Lnet/minecraft/resources/ResourceLocation;)V
      // 088: goto 124
      // 08b: aload 2
      // 08c: astore 5
      // 08e: aload 5
      // 090: bipush 0
      // 091: invokevirtual java/lang/String.charAt (I)C
      // 094: bipush 64
      // 096: if_icmpeq 09e
      // 099: bipush 4
      // 09a: istore 3
      // 09b: goto 009
      // 09e: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryNamespacePredicate
      // 0a1: dup
      // 0a2: aload 5
      // 0a4: bipush 1
      // 0a5: invokevirtual java/lang/String.substring (I)Ljava/lang/String;
      // 0a8: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryNamespacePredicate.<init> (Ljava/lang/String;)V
      // 0ab: goto 124
      // 0ae: aload 1
      // 0af: invokestatic dev/latvian/mods/kubejs/util/RegExpKJS.wrap (Ljava/lang/Object;)Ljava/util/regex/Pattern;
      // 0b2: astore 6
      // 0b4: aload 6
      // 0b6: ifnull 0c5
      // 0b9: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate
      // 0bc: dup
      // 0bd: aload 6
      // 0bf: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryRegExpPredicate.<init> (Ljava/util/regex/Pattern;)V
      // 0c2: goto 124
      // 0c5: aload 0
      // 0c6: bipush 0
      // 0c7: invokeinterface dev/latvian/mods/rhino/type/TypeInfo.param (I)Ldev/latvian/mods/rhino/type/TypeInfo; 2
      // 0cc: invokestatic dev/latvian/mods/kubejs/registry/RegistryType.ofType (Ldev/latvian/mods/rhino/type/TypeInfo;)Ldev/latvian/mods/kubejs/registry/RegistryType;
      // 0cf: astore 7
      // 0d1: aload 1
      // 0d2: invokestatic dev/latvian/mods/kubejs/util/ID.mc (Ljava/lang/Object;)Lnet/minecraft/resources/ResourceLocation;
      // 0d5: astore 8
      // 0d7: aload 7
      // 0d9: ifnull 118
      // 0dc: getstatic net/minecraft/core/registries/BuiltInRegistries.REGISTRY Lnet/minecraft/core/Registry;
      // 0df: aload 7
      // 0e1: invokevirtual dev/latvian/mods/kubejs/registry/RegistryType.key ()Lnet/minecraft/resources/ResourceKey;
      // 0e4: invokeinterface net/minecraft/core/Registry.get (Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object; 2
      // 0e9: checkcast net/minecraft/core/Registry
      // 0ec: astore 9
      // 0ee: aload 9
      // 0f0: ifnull 118
      // 0f3: aload 9
      // 0f5: aload 8
      // 0f7: invokeinterface net/minecraft/core/Registry.getHolder (Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional; 2
      // 0fc: astore 10
      // 0fe: aload 10
      // 100: invokevirtual java/util/Optional.isPresent ()Z
      // 103: ifeq 118
      // 106: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryHolderPredicate
      // 109: dup
      // 10a: aload 10
      // 10c: invokevirtual java/util/Optional.get ()Ljava/lang/Object;
      // 10f: checkcast net/minecraft/core/Holder
      // 112: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryHolderPredicate.<init> (Lnet/minecraft/core/Holder;)V
      // 115: goto 124
      // 118: new dev/latvian/mods/kubejs/util/registrypredicate/RegistryIDPredicate
      // 11b: dup
      // 11c: aload 8
      // 11e: invokespecial dev/latvian/mods/kubejs/util/registrypredicate/RegistryIDPredicate.<init> (Lnet/minecraft/resources/ResourceLocation;)V
      // 121: goto 124
      // 124: areturn
   }

   default List<Reference<T>> getHolders(Registry<T> registry) {
      return registry.holders().filter(this).toList();
   }

   default List<T> getValues(Registry<T> registry) {
      return registry.holders().filter(this).<T>map(Holder::value).toList();
   }
}
