package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.common.msgs.MsgBeepS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.Holder
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3

public object OpBeep : SpellAction {
   public open val argc: Int = 3

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val instrument: Int = OperatorUtils.getPositiveIntUnder(args, 1, NoteBlockInstrument.values().length, this.getArgc());
      val note: Int = OperatorUtils.getPositiveIntUnderInclusive(args, 2, 24, this.getArgc());
      env.assertVecInRange(target);
      return new SpellAction.Result(
         new OpBeep.Spell(target, note, NoteBlockInstrument.values()[instrument]),
         1000L,
         CollectionsKt.listOf(ParticleSpray.Companion.cloud$default(ParticleSpray.Companion, target, 1.0, 0, 4, null)),
         0L,
         8,
         null
      );
   }

   public override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return false;
   }

   override fun awardsCastingStat(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
   }

   override fun executeWithUserdata(args: MutableList<Iota>, env: CastingEnvironment, userData: CompoundTag): SpellAction.Result {
      return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return SpellAction.DefaultImpls.operate(this, env, image, continuation);
   }

   private data class Spell(target: Vec3, note: Int, instrument: NoteBlockInstrument) : RenderedSpell {
      public final val target: Vec3
      public final val note: Int
      public final val instrument: NoteBlockInstrument

      init {
         this.target = target;
         this.note = note;
         this.instrument = instrument;
      }

      public override fun cast(env: CastingEnvironment) {
         IXplatAbstractions.INSTANCE.sendPacketNear(this.target, 128.0, env.getWorld(), new MsgBeepS2C(this.target, this.note, this.instrument));
         env.getWorld().gameEvent(null, GameEvent.NOTE_BLOCK_PLAY as Holder, this.target);
      }

      public operator fun component1(): Vec3 {
         return this.target;
      }

      public operator fun component2(): Int {
         return this.note;
      }

      public operator fun component3(): NoteBlockInstrument {
         return this.instrument;
      }

      public fun copy(target: Vec3 = this.target, note: Int = this.note, instrument: NoteBlockInstrument = this.instrument): at.petrak.hexcasting.common.casting.actions.spells.OpBeep.Spell {
         return new OpBeep.Spell(target, note, instrument);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target}, note=${this.note}, instrument=${this.instrument})";
      }

      public override fun hashCode(): Int {
         return (this.target.hashCode() * 31 + Integer.hashCode(this.note)) * 31 + this.instrument.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpBeep.Spell) {
            return false;
         } else {
            val var2: OpBeep.Spell = other as OpBeep.Spell;
            if (!(this.target == (other as OpBeep.Spell).target)) {
               return false;
            } else if (this.note != var2.note) {
               return false;
            } else {
               return this.instrument === var2.instrument;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
