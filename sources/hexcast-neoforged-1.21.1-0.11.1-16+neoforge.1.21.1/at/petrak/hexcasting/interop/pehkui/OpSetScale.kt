package at.petrak.hexcasting.interop.pehkui

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
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

public object OpSetScale : SpellAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      val scale: Double = OperatorUtils.getDoubleBetween(args, 1, 0.03125, 8.0, this.getArgc());
      val var10002: RenderedSpell = new OpSetScale.Spell(target, scale);
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = target.position();
      return new SpellAction.Result(var10002, 50000L, CollectionsKt.listOf(var10004.burst(var10005, scale, 40)), 0L, 8, null);
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

   private data class Spell(target: Entity, scale: Double) : RenderedSpell {
      public final val target: Entity
      public final val scale: Double

      init {
         this.target = target;
         this.scale = scale;
      }

      public override fun cast(env: CastingEnvironment) {
         IXplatAbstractions.INSTANCE.getPehkuiApi().setScale(this.target, (float)this.scale);
      }

      public operator fun component1(): Entity {
         return this.target;
      }

      public operator fun component2(): Double {
         return this.scale;
      }

      public fun copy(target: Entity = this.target, scale: Double = this.scale): at.petrak.hexcasting.interop.pehkui.OpSetScale.Spell {
         return new OpSetScale.Spell(target, scale);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target}, scale=${this.scale})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode() * 31 + java.lang.Double.hashCode(this.scale);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpSetScale.Spell) {
            return false;
         } else {
            val var2: OpSetScale.Spell = other as OpSetScale.Spell;
            if (!(this.target == (other as OpSetScale.Spell).target)) {
               return false;
            } else {
               return java.lang.Double.compare(this.scale, var2.scale) == 0;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
