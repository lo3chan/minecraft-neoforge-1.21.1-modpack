package io.github.razordevs.aeroblender.mixin;

import io.github.razordevs.aeroblender.aether.AetherRegionType;
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
import terrablender.api.RegionType;

@Mixin(
   value = {RegionType.class},
   remap = false
)
abstract class RegionTypeMixin {
   @Shadow
   @Final
   @Mutable
   private static RegionType[] $VALUES;

   @Invoker("<init>")
   private static RegionType newVariant(String internalName, int internalId) {
      throw new AssertionError();
   }

   @Inject(
      method = {"<clinit>"},
      at = {@At(
         value = "FIELD",
         opcode = 179,
         target = "Lterrablender/api/RegionType;$VALUES:[Lterrablender/api/RegionType;",
         shift = Shift.AFTER
      )}
   )
   private static void addCustomVariant(CallbackInfo ci) {
      ArrayList<RegionType> variants = new ArrayList<>(Arrays.asList(RegionType.values()));
      RegionType aether = newVariant("THE_AETHER", ((RegionType)variants.getLast()).ordinal() + 1);
      AetherRegionType.THE_AETHER = aether;
      variants.add(aether);
      $VALUES = variants.toArray(new RegionType[0]);
   }
}
