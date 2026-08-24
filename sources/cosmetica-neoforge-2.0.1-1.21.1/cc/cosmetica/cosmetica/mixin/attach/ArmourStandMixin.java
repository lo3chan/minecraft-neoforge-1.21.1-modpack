package cc.cosmetica.cosmetica.mixin.attach;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.cosmetica.StateHolder;
import cc.cosmetica.kupe.api.State;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ArmorStand.class})
public class ArmourStandMixin implements StateHolder {
   @Override
   public State<Cosmetics> cosmetica$getCosmeticState() {
      return new State((Cosmetics)Cosmetics.getCosmetics((LivingEntity)this).orElse(null));
   }

   @Override
   public void cosmetica$setCosmeticState(@Nullable Cosmetics cosmetics) {
   }
}
