package vazkii.psi.common.spell.operator.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamEntity;
import vazkii.psi.api.spell.piece.PieceOperator;
import vazkii.psi.common.core.handler.PlayerDataHandler;

public class PieceOperatorEntityMotion extends PieceOperator {
   SpellParam<Entity> target;

   public PieceOperatorEntityMotion(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.target = new ParamEntity("psi.spellparam.target", 13814826, false, false));
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Entity e = this.getParamValue(context, this.target);
      if (e == null) {
         throw new SpellRuntimeException("psi.spellerror.nulltarget");
      } else {
         if (e instanceof Player player) {
            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
            if (data.eidosChangelog.size() >= 2) {
               Vector3 last = data.eidosChangelog.get(data.eidosChangelog.size() - 2);
               Vector3 vec = Vector3.fromEntity(e).sub(last).multiply(3.3333333333333335);
               if (vec.mag() < 10.0) {
                  return vec;
               }
            }
         }

         return new Vector3(e.getDeltaMovement()).multiply(3.3333333333333335);
      }
   }

   @Override
   public Class<?> getEvaluationType() {
      return Vector3.class;
   }
}
