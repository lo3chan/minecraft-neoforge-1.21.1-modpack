package io.wispforest.owo.mixin.ext;

import io.wispforest.owo.ext.DerivedComponentMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemStack.class})
public class ItemStackMixin {
   @Shadow
   @Final
   PatchedDataComponentMap components;
   @Unique
   private DerivedComponentMap owo$derivedMap;

   @Inject(
      method = {"<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V"
      )}
   )
   private void injectDerivedComponentMap(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
      DataComponentMap base = ((ComponentMapImplAccessor)this.components).owo$getBaseComponents();
      if (base instanceof DerivedComponentMap derived) {
         this.owo$derivedMap = derived;
      } else {
         this.owo$derivedMap = new DerivedComponentMap(base);
         ((ComponentMapImplAccessor)this.components).owo$setBaseComponents(this.owo$derivedMap);
      }
   }

   @Inject(
      method = {"<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V",
         shift = Shift.AFTER
      )}
   )
   private void deriveComponents1(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo ci) {
      if (this.owo$derivedMap != null) {
         this.owo$derivedMap.derive((ItemStack)this);
      }
   }

   @Inject(
      method = {"applyComponentsAndValidate(Lnet/minecraft/core/component/DataComponentPatch;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V",
         shift = Shift.AFTER
      )}
   )
   private void deriveComponents2(DataComponentPatch changes, CallbackInfo ci) {
      if (this.owo$derivedMap != null) {
         this.owo$derivedMap.derive((ItemStack)this);
      }
   }

   @Inject(
      method = {"applyComponents(Lnet/minecraft/core/component/DataComponentPatch;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V",
         shift = Shift.AFTER
      )}
   )
   private void deriveComponents3(DataComponentPatch changes, CallbackInfo ci) {
      if (this.owo$derivedMap != null) {
         this.owo$derivedMap.derive((ItemStack)this);
      }
   }

   @Inject(
      method = {"applyComponents(Lnet/minecraft/core/component/DataComponentMap;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/Item;verifyComponentsAfterLoad(Lnet/minecraft/world/item/ItemStack;)V",
         shift = Shift.AFTER
      )}
   )
   private void deriveComponents4(DataComponentMap components, CallbackInfo ci) {
      if (this.owo$derivedMap != null) {
         this.owo$derivedMap.derive((ItemStack)this);
      }
   }
}
