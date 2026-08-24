package vazkii.psi.api.spell;

import net.minecraft.world.entity.Entity;
import vazkii.psi.api.PsiAPI;

public interface ISpellImmune {
   static boolean isImmune(Entity e) {
      if (!e.canUsePortal(false)) {
         return true;
      } else {
         ISpellImmune cap = (ISpellImmune)e.getCapability(PsiAPI.SPELL_IMMUNE_CAPABILITY);
         return cap != null && cap.isImmune();
      }
   }

   boolean isImmune();
}
