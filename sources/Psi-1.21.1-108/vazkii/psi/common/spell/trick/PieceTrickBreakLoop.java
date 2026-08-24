package vazkii.psi.common.spell.trick;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity.RemovalReason;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.cad.ISocketable;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.piece.PieceTrick;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.entity.EntitySpellCircle;

public class PieceTrickBreakLoop extends PieceTrick {
   SpellParam<Number> valueParam;

   public PieceTrickBreakLoop(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.valueParam = new ParamNumber("psi.spellparam.number", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) {
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      double value = this.getParamValue(context, this.valueParam).doubleValue();
      if (Math.abs(value) < 1.0) {
         if (context.focalPoint != context.caster) {
            if (context.focalPoint instanceof EntitySpellCircle circle) {
               CompoundTag circleNBT = new CompoundTag();
               circle.addAdditionalSaveData(circleNBT);
               circleNBT.putInt("timesCast", 20);
               circleNBT.putInt("timesAlive", 100);
               circle.load(circleNBT);
            } else {
               context.focalPoint.remove(RemovalReason.DISCARDED);
            }
         } else {
            if (!context.tool.isEmpty()) {
               ISocketable socketableCap = (ISocketable)context.tool.getCapability(PsiAPI.SOCKETABLE_CAPABILITY);
               if (socketableCap != null) {
                  socketableCap.setSelectedSlot(socketableCap.getLastSlot() + 1);
               }
            }

            PlayerDataHandler.PlayerData data = PlayerDataHandler.get(context.caster);
            data.stopLoopcast();
         }
      }

      return null;
   }
}
