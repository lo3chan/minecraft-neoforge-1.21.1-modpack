package vazkii.psi.common.spell.trick;

import net.minecraft.server.level.ServerPlayer;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.network.MessageRegister;
import vazkii.psi.common.network.message.MessageEidosSync;

public class PieceTrickEidosReversal extends PieceTrick {
   SpellParam<Number> time;

   public PieceTrickEidosReversal(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel("psi.spellparam.time", true).mul(11.0).add(20.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel("psi.spellparam.time", true).mul(40.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.time = new ParamNumber("psi.spellparam.time", 13773354, false, true));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      Double timeVal = this.getParamEvaluation(this.time);
      if (timeVal != null && !(timeVal <= 0.0) && timeVal == timeVal.intValue()) {
         meta.addStat(EnumSpellStat.POTENCY, (int)(timeVal * 11.0 + 20.0));
         meta.addStat(EnumSpellStat.COST, timeVal.intValue() * 40);
      } else {
         throw new SpellCompilationException("psi.spellerror.nonpositiveinteger", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      int timeVal = this.getParamValue(context, this.time).intValue();
      PlayerDataHandler.PlayerData data = PlayerDataHandler.get(context.caster);
      if (timeVal > 0 && !data.isReverting) {
         data.eidosReversionTime = timeVal * 10;
         data.isReverting = true;
         if (context.caster instanceof ServerPlayer) {
            MessageRegister.sendToPlayer((ServerPlayer)context.caster, new MessageEidosSync(data.eidosReversionTime));
         }
      }

      return null;
   }
}
