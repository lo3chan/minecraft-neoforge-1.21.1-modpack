package at.petrak.hexcasting.common.casting.actions.rw

import at.petrak.hexcasting.api.addldata.ADIotaHolder
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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

public object OpTheCoolerWrite : SpellAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      val datum: Iota = args.get(1) as Iota;
      env.assertEntityInRange(target);
      val var10000: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(target);
      if (var10000 == null) {
         throw MishapBadEntity.Companion.of(target, "iota.write");
      } else if (!var10000.writeIota(datum, true)) {
         throw MishapBadEntity.Companion.of(target, "iota.write");
      } else {
         val trueName: Player = MishapOthersName.Companion.getTrueNameFromDatum(datum, null);
         if (trueName != null) {
            throw new MishapOthersName(trueName);
         } else {
            val burstPos: Vec3 = if (target is ItemEntity) target.position().add(0.0, 0.375, 0.0) else target.position();
            val var10002: RenderedSpell = new OpTheCoolerWrite.Spell(datum, var10000);
            return new SpellAction.Result(var10002, 0L, CollectionsKt.listOf(new ParticleSpray(burstPos, new Vec3(1.0, 0.0, 0.0), 0.25, 3.14, 40)), 0L, 8, null);
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

   private data class Spell(datum: Iota, datumHolder: ADIotaHolder) : RenderedSpell {
      public final val datum: Iota
      public final val datumHolder: ADIotaHolder

      init {
         this.datum = datum;
         this.datumHolder = datumHolder;
      }

      public override fun cast(env: CastingEnvironment) {
         this.datumHolder.writeIota(this.datum, false);
      }

      public operator fun component1(): Iota {
         return this.datum;
      }

      public operator fun component2(): ADIotaHolder {
         return this.datumHolder;
      }

      public fun copy(datum: Iota = this.datum, datumHolder: ADIotaHolder = this.datumHolder): at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerWrite.Spell {
         return new OpTheCoolerWrite.Spell(datum, datumHolder);
      }

      public override fun toString(): String {
         return "Spell(datum=${this.datum}, datumHolder=${this.datumHolder})";
      }

      public override fun hashCode(): Int {
         return this.datum.hashCode() * 31 + this.datumHolder.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpTheCoolerWrite.Spell) {
            return false;
         } else {
            val var2: OpTheCoolerWrite.Spell = other as OpTheCoolerWrite.Spell;
            if (!(this.datum == (other as OpTheCoolerWrite.Spell).datum)) {
               return false;
            } else {
               return this.datumHolder == var2.datumHolder;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
