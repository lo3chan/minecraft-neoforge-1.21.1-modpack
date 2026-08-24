package vazkii.psi.api.gui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderPsiHudEvent extends Event implements ICancellableEvent {
   @NotNull
   private final PsiHudElementType type;

   public RenderPsiHudEvent(@NotNull PsiHudElementType type) {
      this.type = type;
   }

   @NotNull
   public PsiHudElementType getType() {
      return this.type;
   }
}
