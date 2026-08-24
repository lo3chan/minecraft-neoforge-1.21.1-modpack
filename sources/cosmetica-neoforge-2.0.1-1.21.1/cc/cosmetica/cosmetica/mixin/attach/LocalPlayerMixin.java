package cc.cosmetica.cosmetica.mixin.attach;

import cc.cosmetica.core.api.Cosmetics;
import cc.cosmetica.cosmetica.StateHolder;
import cc.cosmetica.kupe.api.State;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({LocalPlayer.class})
public class LocalPlayerMixin implements StateHolder {
   @Unique
   private final State<Cosmetics> cosmetica$cosmeticsState = new State(null);

   @Override
   public State<Cosmetics> cosmetica$getCosmeticState() {
      return this.cosmetica$cosmeticsState;
   }

   @Override
   public void cosmetica$setCosmeticState(@Nullable Cosmetics cosmetics) {
      this.cosmetica$cosmeticsState.set(cosmetics);
   }
}
