package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.ItemWrapper;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@RemapPrefixForJS("kjs$")
public interface ItemPredicate extends Predicate<ItemStack>, IngredientSupplierKJS {
   TypeInfo TYPE_INFO = TypeInfo.of(ItemPredicate.class);
   ItemPredicate NONE = stack -> false;
   ItemPredicate ALL = stack -> true;

   boolean test(ItemStack itemStack);

   private static ItemPredicate simplify(Ingredient in) {
      return (ItemPredicate)(in.isEmpty() ? NONE : (in.kjs$isWildcard() ? ALL : in));
   }

   static ItemPredicate wrap(Context cx, Object from) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Invalid switch case set: [[const("*")], [const(""), const(2)], [var5_1 instanceof var10_1], [null]] for selector of type Ljava/lang/String;
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.SwitchHeadExprent.checkExprTypeBounds(SwitchHeadExprent.java:66)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:140)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:302)
      //
      // Bytecode:
      // 00: aload 1
      // 01: astore 2
      // 02: bipush 0
      // 03: istore 3
      // 04: aload 2
      // 05: iload 3
      // 06: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ dev/latvian/mods/rhino/BaseFunction, java/lang/String ]
      // 0b: tableswitch 159 -1 1 25 31 52
      // 24: getstatic dev/latvian/mods/kubejs/item/ItemPredicate.NONE Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // 27: goto b2
      // 2a: aload 2
      // 2b: checkcast dev/latvian/mods/rhino/BaseFunction
      // 2e: astore 4
      // 30: aload 0
      // 31: getstatic dev/latvian/mods/kubejs/item/ItemPredicate.TYPE_INFO Ldev/latvian/mods/rhino/type/TypeInfo;
      // 34: aload 4
      // 36: invokevirtual dev/latvian/mods/rhino/Context.createInterfaceAdapter (Ldev/latvian/mods/rhino/type/TypeInfo;Ldev/latvian/mods/rhino/ScriptableObject;)Ljava/lang/Object;
      // 39: checkcast dev/latvian/mods/kubejs/item/ItemPredicate
      // 3c: goto b2
      // 3f: aload 2
      // 40: checkcast java/lang/String
      // 43: astore 5
      // 45: aload 5
      // 47: dup
      // 48: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 4b: pop
      // 4c: astore 7
      // 4e: bipush 0
      // 4f: istore 8
      // 51: aload 7
      // 53: iload 8
      // 55: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ "*", "", "-", java/lang/String ]
      // 5a: tableswitch 66 0 3 30 36 36 42
      // 78: getstatic dev/latvian/mods/kubejs/item/ItemPredicate.ALL Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // 7b: goto a7
      // 7e: getstatic dev/latvian/mods/kubejs/item/ItemPredicate.NONE Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // 81: goto a7
      // 84: aload 7
      // 86: astore 6
      // 88: aload 6
      // 8a: invokevirtual java/lang/String.isBlank ()Z
      // 8d: ifne 96
      // 90: bipush 4
      // 91: istore 8
      // 93: goto 51
      // 96: getstatic dev/latvian/mods/kubejs/item/ItemPredicate.NONE Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // 99: goto a7
      // 9c: aload 0
      // 9d: aload 1
      // 9e: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/world/item/crafting/Ingredient;
      // a1: invokestatic dev/latvian/mods/kubejs/item/ItemPredicate.simplify (Lnet/minecraft/world/item/crafting/Ingredient;)Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // a4: goto a7
      // a7: goto b2
      // aa: aload 0
      // ab: aload 1
      // ac: invokestatic dev/latvian/mods/kubejs/plugin/builtin/wrapper/IngredientWrapper.wrap (Ldev/latvian/mods/rhino/Context;Ljava/lang/Object;)Lnet/minecraft/world/item/crafting/Ingredient;
      // af: invokestatic dev/latvian/mods/kubejs/item/ItemPredicate.simplify (Lnet/minecraft/world/item/crafting/Ingredient;)Ldev/latvian/mods/kubejs/item/ItemPredicate;
      // b2: areturn
   }

   default boolean kjs$testItem(Item item) {
      return this.test(item.getDefaultInstance());
   }

   default ItemStack[] kjs$getStackArray() {
      return ItemWrapper.getList().stream().filter(this).toArray(ItemStack[]::new);
   }

   default ItemStackSet kjs$getStacks() {
      return new ItemStackSet(this.kjs$getStackArray());
   }

   default ItemStackSet kjs$getDisplayStacks() {
      ItemStackSet set = new ItemStackSet();

      for (ItemStack stack : ItemWrapper.getList()) {
         if (this.test(stack)) {
            set.add(stack);
         }
      }

      return set;
   }

   default boolean kjs$isWildcard() {
      return this == ALL;
   }

   default Stream<Item> kjs$getItemStream() {
      return Arrays.stream(this.kjs$getStackArray()).map(ItemStack::getItem);
   }

   default Set<Item> kjs$getItemTypes() {
      ItemStack[] items = this.kjs$getStackArray();
      if (items.length == 1 && !items[0].isEmpty()) {
         return Set.of(items[0].getItem());
      } else {
         LinkedHashSet<Item> set = new LinkedHashSet<>(items.length);

         for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
               set.add(stack.getItem());
            }
         }

         return set;
      }
   }

   default Set<String> kjs$getItemIds() {
      ItemStack[] items = this.kjs$getStackArray();
      if (items.length == 1 && !items[0].isEmpty()) {
         return Set.of(items[0].kjs$getId());
      } else {
         LinkedHashSet<String> ids = new LinkedHashSet<>(items.length);

         for (ItemStack item : items) {
            if (!item.isEmpty()) {
               ids.add(item.kjs$getId());
            }
         }

         return ids;
      }
   }

   default ItemStack kjs$getFirst() {
      for (ItemStack stack : this.kjs$getStackArray()) {
         if (!stack.isEmpty()) {
            return stack;
         }
      }

      return ItemStack.EMPTY;
   }

   default boolean kjs$canBeUsedForMatching() {
      return true;
   }

   @Override
   default Ingredient kjs$asIngredient() {
      return Ingredient.of(this.kjs$getStackArray());
   }
}
