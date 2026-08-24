package at.petrak.hexcasting.common.casting.actions.spells.great

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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

public object OpLightning : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      env.assertVecInRange(target);
      if (!env.canEditBlockAt(BlockPos.containing(target as Position))) {
         throw new MishapBadLocation(target, "forbidden");
      } else {
         val var10002: RenderedSpell = new OpLightning.Spell(target);
         val var10006: Vec3 = target.add(0.0, 2.0, 0.0);
         return new SpellAction.Result(
            var10002, 150000L, CollectionsKt.listOf(new ParticleSpray(var10006, new Vec3(0.0, -1.0, 0.0), 0.5, 0.1, 0, 16, null)), 0L, 8, null
         );
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

   private data class Spell(target: Vec3) : RenderedSpell {
      public final val target: Vec3

      init {
         this.target = target;
      }

      public override fun cast(env: CastingEnvironment) {
         val lightning: LightningBolt = new LightningBolt(EntityType.LIGHTNING_BOLT, env.getWorld() as Level);
         lightning.setPosRaw(this.target.x, this.target.y, this.target.z);
         env.getWorld().addWithUUID(lightning as Entity);
      }

      public operator fun component1(): Vec3 {
         return this.target;
      }

      public fun copy(target: Vec3 = this.target): at.petrak.hexcasting.common.casting.actions.spells.great.OpLightning.Spell {
         return new OpLightning.Spell(target);
      }

      public override fun toString(): String {
         return "Spell(target=${this.target})";
      }

      public override fun hashCode(): Int {
         return this.target.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpLightning.Spell) {
            return false;
         } else {
            return this.target == (other as OpLightning.Spell).target;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
