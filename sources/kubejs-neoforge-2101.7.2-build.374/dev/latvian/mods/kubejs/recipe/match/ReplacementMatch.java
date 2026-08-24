package dev.latvian.mods.kubejs.recipe.match;

import dev.latvian.mods.rhino.Context;

public interface ReplacementMatch {
   ReplacementMatch NONE = new ReplacementMatch() {
      @Override
      public String toString() {
         return "NONE";
      }
   };

   static ReplacementMatch wrap(Context cx, Object o) {
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
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/kubejs/recipe/match/ReplacementMatch ]
      // 0b: lookupswitch 42 2 -1 25 0 31
      // 24: getstatic dev/latvian/mods/kubejs/recipe/match/ReplacementMatch.NONE Ldev/latvian/mods/kubejs/recipe/match/ReplacementMatch;
      // 27: goto 4f
      // 2a: aload 2
      // 2b: checkcast dev/latvian/mods/kubejs/recipe/match/ReplacementMatch
      // 2e: astore 4
      // 30: aload 4
      // 32: goto 4f
      // 35: aload 0
      // 36: aload 1
      // 37: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/world/item/crafting/Ingredient;
      // 3a: astore 5
      // 3c: aload 5
      // 3e: invokevirtual net/minecraft/world/item/crafting/Ingredient.isEmpty ()Z
      // 41: ifeq 4a
      // 44: getstatic dev/latvian/mods/kubejs/recipe/match/ReplacementMatch.NONE Ldev/latvian/mods/kubejs/recipe/match/ReplacementMatch;
      // 47: goto 4f
      // 4a: aload 5
      // 4c: goto 4f
      // 4f: areturn
   }
}
