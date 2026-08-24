package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.event.EventGetStarBrightness;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientLevel.class})
public abstract class ClientLevelMixin extends Level {
   protected ClientLevelMixin(
      WritableLevelData writableLevelData,
      ResourceKey<Level> levelResourceKey,
      RegistryAccess registryAccess,
      Holder<DimensionType> dimensionTypeHolder,
      Supplier<ProfilerFiller> filler,
      boolean b1,
      boolean b2,
      long seed,
      int i
   ) {
      super(writableLevelData, levelResourceKey, registryAccess, dimensionTypeHolder, filler, b1, b2, seed, i);
   }

   @Inject(
      at = {@At("RETURN")},
      remap = true,
      method = {"getStarBrightness"},
      cancellable = true
   )
   private void citadel_getStarBrightness(float partialTicks, CallbackInfoReturnable<Float> cir) {
      EventGetStarBrightness event = new EventGetStarBrightness((ClientLevel)this, (Float)cir.getReturnValue(), partialTicks);
      NeoForge.EVENT_BUS.post(event);
      if (event.getResult() == TriState.TRUE) {
         cir.setReturnValue(event.getBrightness());
      }
   }

   @ModifyConstant(
      method = {"tickTime"},
      remap = true,
      constant = {@Constant(
         longValue = 1L
      )},
      expect = 2
   )
   private long citadel_clientSetDayTime(long timeIn) {
      return ClientTickRateTracker.getForClient(Minecraft.getInstance()).getDayTimeIncrement(timeIn);
   }
}
