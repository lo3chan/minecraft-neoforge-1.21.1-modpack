package vazkii.psi.common.spell.trick;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.EnumSpellStat;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.StatLabel;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickSmite extends PieceTrick {
   SpellParam<Vector3> position;

   public PieceTrickSmite(Spell spell) {
      super(spell);
      this.setStatLabel(EnumSpellStat.POTENCY, new StatLabel(100.0));
      this.setStatLabel(EnumSpellStat.COST, new StatLabel(400.0));
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      meta.addStat(EnumSpellStat.POTENCY, 100);
      meta.addStat(EnumSpellStat.COST, 400);
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      Vector3 positionVal = this.getParamValue(context, this.position);
      if (positionVal == null) {
         throw new SpellRuntimeException("psi.spellerror.nullvector");
      } else if (!context.isInRadius(positionVal)) {
         throw new SpellRuntimeException("psi.spellerror.outsideradius");
      } else {
         EntityPlaceEvent placeEvent = new EntityPlaceEvent(
            BlockSnapshot.create(context.focalPoint.getCommandSenderWorld().dimension(), context.focalPoint.getCommandSenderWorld(), positionVal.toBlockPos()),
            context.focalPoint.getCommandSenderWorld().getBlockState(positionVal.toBlockPos().relative(Direction.UP)),
            context.caster
         );
         NeoForge.EVENT_BUS.post(placeEvent);
         if (placeEvent.isCanceled()) {
            return null;
         } else {
            if (context.focalPoint.getCommandSenderWorld() instanceof ServerLevel) {
               LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, context.focalPoint.level());
               lightning.setPosRaw(positionVal.x, positionVal.y, positionVal.z);
               context.focalPoint.getCommandSenderWorld().addFreshEntity(lightning);
            }

            return null;
         }
      }
   }
}
