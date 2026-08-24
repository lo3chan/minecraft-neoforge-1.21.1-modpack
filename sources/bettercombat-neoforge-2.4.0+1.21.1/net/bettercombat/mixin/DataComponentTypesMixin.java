package net.bettercombat.mixin;

import net.bettercombat.api.component.BetterCombatDataComponents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DataComponents.class})
public class DataComponentTypesMixin {
   @Inject(
      method = {"<clinit>()V"},
      at = {@At("TAIL")}
   )
   private static void static_init_TAIL_SpellEngine(CallbackInfo ci) {
      DataComponentType<ResourceLocation> asd = BetterCombatDataComponents.WEAPON_PRESET_ID;
   }
}
