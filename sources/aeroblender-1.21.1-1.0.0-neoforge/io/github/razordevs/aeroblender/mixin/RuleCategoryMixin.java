package io.github.razordevs.aeroblender.mixin;

import io.github.razordevs.aeroblender.aether.AetherRuleCategory;
import java.util.ArrayList;
import java.util.Arrays;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import terrablender.api.SurfaceRuleManager.RuleCategory;

@Mixin(
   value = {RuleCategory.class},
   remap = false
)
abstract class RuleCategoryMixin {
   @Shadow
   @Final
   @Mutable
   private static RuleCategory[] $VALUES;

   @Invoker("<init>")
   private static RuleCategory newVariant(String internalName, int internalId) {
      throw new AssertionError();
   }

   @Inject(
      method = {"<clinit>"},
      at = {@At(
         value = "FIELD",
         opcode = 179,
         target = "Lterrablender/api/SurfaceRuleManager$RuleCategory;$VALUES:[Lterrablender/api/SurfaceRuleManager$RuleCategory;",
         shift = Shift.AFTER
      )}
   )
   private static void addCustomVariant(CallbackInfo ci) {
      ArrayList<RuleCategory> variants = new ArrayList<>(Arrays.asList($VALUES));
      RuleCategory aether = newVariant("THE_AETHER", ((RuleCategory)variants.getLast()).ordinal() + 1);
      AetherRuleCategory.THE_AETHER = aether;
      variants.add(aether);
      $VALUES = variants.toArray(new RuleCategory[0]);
   }
}
