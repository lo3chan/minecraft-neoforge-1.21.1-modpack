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
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.player.AltioraAbility
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

public object OpAltiora : SpellAction {
   public open val argc: Int = 1
   private final val GRACE_PERIOD: Int = 20

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: ServerPlayer = OperatorUtils.getPlayer(args, 0, this.getArgc());
      env.assertEntityInRange(target as Entity);
      val var10002: RenderedSpell = new OpAltiora.Spell(target);
      val var4: Array<ParticleSpray> = new ParticleSpray[2];
      val var10006: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10007: Vec3 = target.position();
      var4[0] = ParticleSpray.Companion.burst$default(var10006, var10007, 0.5, 0, 4, null);
      val var10008: Vec3 = target.position();
      var4[1] = new ParticleSpray(var10008, new Vec3(0.0, 2.0, 0.0), 0.0, 0.1, 0, 16, null);
      return new SpellAction.Result(var10002, 100000L, CollectionsKt.listOf(var4), 0L, 8, null);
   }

   @JvmStatic
   public fun checkPlayerCollision(player: ServerPlayer) {
      val altiora: AltioraAbility = IXplatAbstractions.INSTANCE.getAltiora(player as Player);
      if (altiora != null) {
         if (altiora.gracePeriod() != 0 || !player.onGround() && !player.horizontalCollision) {
            IXplatAbstractions.INSTANCE.setAltiora(player as Player, new AltioraAbility(Math.max(altiora.gracePeriod() - 1, 0)));
            if (player.level().random.nextFloat() < 0.02) {
               player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HexSounds.FLIGHT_AMBIENCE, SoundSource.PLAYERS, 0.2F, 1.0F);
            }

            val color: FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(player as Player);
            val var10002: Vec3 = player.position();
            val var10000: ParticleSpray = new ParticleSpray(var10002, new Vec3(0.0, -0.2, 0.0), 0.4, 1.5707963267948966, 3);
            val var10001: ServerLevel = player.serverLevel();
            var10000.sprayParticles(var10001, color);
         } else {
            IXplatAbstractions.INSTANCE.setAltiora(player as Player, null);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HexSounds.FLIGHT_FINISH, SoundSource.PLAYERS, 2.0F, 1.0F);
         }
      }
   }

   public fun checkAllPlayers(world: ServerLevel) {
      for (ServerPlayer player : world.players()) {
         checkPlayerCollision(player);
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

   private data class Spell(target: ServerPlayer) : RenderedSpell {
      public final val target: ServerPlayer

      init {
         this.target = target;
      }

      public override fun cast(env: CastingEnvironment) {
         this.target.push(0.0, 1.5, 0.0);
         this.target.hurtMarked = true;
         IXplatAbstractions.INSTANCE.setAltiora(this.target as Player, new AltioraAbility(OpAltiora.access$getGRACE_PERIOD$p()));
      }

      public operator fun component1(): ServerPlayer {
         return this.target;
      }

      public fun copy(target: ServerPlayer = this.target): at.petrak.hexcasting.common.casting.actions.spells.great.OpAltiora.Spell {
         return new OpAltiora.Spell(target);
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
         } else if (other !is OpAltiora.Spell) {
            return false;
         } else {
            return this.target == (other as OpAltiora.Spell).target;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
