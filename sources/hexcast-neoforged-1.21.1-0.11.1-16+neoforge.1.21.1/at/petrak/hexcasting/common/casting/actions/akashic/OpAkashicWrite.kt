package at.petrak.hexcasting.common.casting.actions.akashic

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapNoAkashicRecord
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicRecord
import at.petrak.hexcasting.common.lib.HexSounds
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block

public object OpAkashicWrite : SpellAction {
   public open val argc: Int = 3

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = OperatorUtils.getBlockPos(args, 0, this.getArgc());
      val key: HexPattern = OperatorUtils.getPattern(args, 1, this.getArgc());
      val datum: Iota = args.get(2) as Iota;
      env.assertPosInRange(pos);
      val record: Block = env.getWorld().getBlockState(pos).getBlock();
      if (record !is BlockAkashicRecord) {
         throw new MishapNoAkashicRecord(pos);
      } else {
         val trueName: Player = MishapOthersName.Companion.getTrueNameFromDatum(datum, env.getCaster() as Player);
         if (trueName != null) {
            throw new MishapOthersName(trueName);
         } else {
            return new SpellAction.Result(
               new OpAkashicWrite.Spell(record as BlockAkashicRecord, pos, key, datum), 10000L, CollectionsKt.emptyList(), 0L, 8, null
            );
         }
      }
   }

   override fun hasCastingSound(ctx: CastingEnvironment): Boolean {
      return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
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

   private data class Spell(record: BlockAkashicRecord, recordPos: BlockPos, key: HexPattern, datum: Iota) : RenderedSpell {
      public final val record: BlockAkashicRecord
      public final val recordPos: BlockPos
      public final val key: HexPattern
      public final val datum: Iota

      init {
         this.record = record;
         this.recordPos = recordPos;
         this.key = key;
         this.datum = datum;
      }

      public override fun cast(env: CastingEnvironment) {
         this.record.addNewDatum(this.recordPos, env.getWorld() as Level, this.key, this.datum);
         env.getWorld().playSound(null, this.recordPos, HexSounds.SCROLL_SCRIBBLE, SoundSource.BLOCKS, 1.0F, 0.8F);
      }

      public operator fun component1(): BlockAkashicRecord {
         return this.record;
      }

      public operator fun component2(): BlockPos {
         return this.recordPos;
      }

      public operator fun component3(): HexPattern {
         return this.key;
      }

      public operator fun component4(): Iota {
         return this.datum;
      }

      public fun copy(record: BlockAkashicRecord = this.record, recordPos: BlockPos = this.recordPos, key: HexPattern = this.key, datum: Iota = this.datum): at.petrak.hexcasting.common.casting.actions.akashic.OpAkashicWrite.Spell {
         return new OpAkashicWrite.Spell(record, recordPos, key, datum);
      }

      public override fun toString(): String {
         return "Spell(record=${this.record}, recordPos=${this.recordPos}, key=${this.key}, datum=${this.datum})";
      }

      public override fun hashCode(): Int {
         return ((this.record.hashCode() * 31 + this.recordPos.hashCode()) * 31 + this.key.hashCode()) * 31 + this.datum.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpAkashicWrite.Spell) {
            return false;
         } else {
            val var2: OpAkashicWrite.Spell = other as OpAkashicWrite.Spell;
            if (!(this.record == (other as OpAkashicWrite.Spell).record)) {
               return false;
            } else if (!(this.recordPos == var2.recordPos)) {
               return false;
            } else if (!(this.key == var2.key)) {
               return false;
            } else {
               return this.datum == var2.datum;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
