package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AutoRegisterMobEffect extends AutoRegisterEntry<MobEffect> {
   private Holder<MobEffect> holder;

   public static AutoRegisterMobEffect of(Supplier<MobEffect> mobEffectSupplier) {
      return new AutoRegisterMobEffect(mobEffectSupplier);
   }

   public Holder<MobEffect> getHolder() {
      if (this.holder == null) {
         throw new IllegalStateException("MobEffect holder is not set. Ensure the MobEffect is registered before accessing the holder.");
      } else {
         return this.holder;
      }
   }

   @Internal
   public void setHolder(Holder<MobEffect> holder) {
      this.holder = holder;
   }

   private AutoRegisterMobEffect(Supplier<MobEffect> mobEffectSupplier) {
      super(mobEffectSupplier);
   }
}
