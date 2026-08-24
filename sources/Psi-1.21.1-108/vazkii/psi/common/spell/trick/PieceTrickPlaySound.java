package vazkii.psi.common.spell.trick;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Instrument;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.Spell;
import vazkii.psi.api.spell.SpellCompilationException;
import vazkii.psi.api.spell.SpellContext;
import vazkii.psi.api.spell.SpellHelpers;
import vazkii.psi.api.spell.SpellMetadata;
import vazkii.psi.api.spell.SpellParam;
import vazkii.psi.api.spell.SpellRuntimeException;
import vazkii.psi.api.spell.param.ParamNumber;
import vazkii.psi.api.spell.param.ParamVector;
import vazkii.psi.api.spell.piece.PieceTrick;

public class PieceTrickPlaySound extends PieceTrick {
   SpellParam<Vector3> position;
   SpellParam<Number> pitch;
   SpellParam<Number> volume;
   SpellParam<Number> instrument;

   public PieceTrickPlaySound(Spell spell) {
      super(spell);
   }

   @Override
   public void initParams() {
      this.addParam(this.position = new ParamVector("psi.spellparam.position", 2774482, false, false));
      this.addParam(this.instrument = new ParamNumber("psi.spellparam.instrument", 13773354, false, false));
      this.addParam(this.pitch = new ParamNumber("psi.spellparam.pitch", 4117034, true, false));
      this.addParam(this.volume = new ParamNumber("psi.spellparam.volume", 13814826, true, false));
   }

   @Override
   public void addToMetadata(SpellMetadata meta) throws SpellCompilationException {
      super.addToMetadata(meta);
      double dVol = SpellHelpers.ensurePositiveOrZero(this, this.volume, 1.0);
      double dPit = SpellHelpers.ensurePositiveOrZero(this, this.pitch, 0.0);
      if (dPit > 24.0) {
         throw new SpellCompilationException("psi.spellerror.pitch", this.x, this.y);
      } else if (dVol > 1.0) {
         throw new SpellCompilationException("psi.spellerror.volume", this.x, this.y);
      }
   }

   @Override
   public Object execute(SpellContext context) throws SpellRuntimeException {
      BlockPos pos = SpellHelpers.getBlockPos(this, context, this.position, true, false);
      double instrumentVal = this.getParamValue(context, this.instrument).doubleValue();
      double volVal = this.getParamValueOrDefault(context, this.volume, 1).doubleValue();
      double pitchVal = this.getParamValueOrDefault(context, this.pitch, 0).doubleValue();
      int instrumentId = Mth.clamp((int)instrumentVal, 0, BuiltInRegistries.INSTRUMENT.size() - 1);
      float f = (float)Math.pow(2.0, (pitchVal - 12.0) / 12.0);
      context.focalPoint
         .level()
         .playSound(
            null,
            pos,
            (SoundEvent)((Instrument)BuiltInRegistries.INSTRUMENT.stream().toList().get(instrumentId)).soundEvent().value(),
            SoundSource.RECORDS,
            (float)volVal,
            f
         );
      return null;
   }
}
