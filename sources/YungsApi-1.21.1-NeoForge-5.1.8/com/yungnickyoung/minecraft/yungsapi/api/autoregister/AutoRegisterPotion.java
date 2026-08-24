package com.yungnickyoung.minecraft.yungsapi.api.autoregister;

import com.yungnickyoung.minecraft.yungsapi.autoregister.AutoRegisterEntry;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AutoRegisterPotion extends AutoRegisterEntry<Potion> {
   private Holder<Potion> holder;

   public static AutoRegisterPotion of(Supplier<Potion> potionSupplier) {
      return new AutoRegisterPotion(potionSupplier);
   }

   private AutoRegisterPotion(Supplier<Potion> potionSupplier) {
      super(potionSupplier);
   }

   public Holder<Potion> getHolder() {
      if (this.holder == null) {
         throw new IllegalStateException("Potion holder is not set. Ensure the Potion is registered before accessing the holder.");
      } else {
         return this.holder;
      }
   }

   @Internal
   public void setHolder(Holder<Potion> holder) {
      this.holder = holder;
   }
}
