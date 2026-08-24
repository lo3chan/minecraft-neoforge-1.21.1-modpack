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
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.player.FlightAbility
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.enums.EnumEntries
import kotlin.math.MathKt
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Abilities
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.phys.Vec3

public class OpFlight(type: at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type) : SpellAction {
   public final val type: at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type
   public open val argc: Int

   init {
      this.type = type;
      this.argc = 2;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val target: ServerPlayer = OperatorUtils.getPlayer(args, 0, this.getArgc());
      val theArg: Double = OperatorUtils.getPositiveDouble(args, 1, this.getArgc());
      env.assertEntityInRange(target as Entity);
      var var10000: Double;
      switch (OpFlight.WhenMappings.$EnumSwitchMapping$0[this.type.ordinal()]) {
         case 1:
            var10000 = theArg * 10000L;
            break;
         case 2:
            var10000 = theArg * 50000L;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      val cost: Long = MathKt.roundToLong(var10000);
      val var10002: RenderedSpell = new OpFlight.Spell(this.type, target, theArg);
      val var10006: Vec3 = target.position();
      return new SpellAction.Result(
         var10002, cost, CollectionsKt.listOf(new ParticleSpray(var10006, new Vec3(0.0, 2.0, 0.0), 0.0, 0.1, 0, 16, null)), 0L, 8, null
      );
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

   public companion object {
      private final val DIST_DANGER_THRESHOLD: Double
      private final val TIME_DANGER_THRESHOLD: Double

      public fun tickAllPlayers(world: ServerLevel) {
         for (ServerPlayer player : world.players()) {
            this.tickDownFlight(player);
         }
      }

      public fun tickDownFlight(player: ServerPlayer) {
         val flight: FlightAbility = IXplatAbstractions.INSTANCE.getFlight(player);
         if (flight != null) {
            val danger: Double = this.getDanger(player, flight);
            if (danger >= 1.0) {
               IXplatAbstractions.INSTANCE.setFlight(player, null);
               if (!player.isCreative() && !player.isSpectator()) {
                  val time2: Abilities = player.getAbilities();
                  time2.flying = false;
                  time2.mayfly = false;
                  player.onUpdateAbilities();
               }

               player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HexSounds.FLIGHT_FINISH, SoundSource.PLAYERS, 2.0F, 1.0F);
               val var10002: Vec3 = player.position();
               val var13: ParticleSpray = new ParticleSpray(var10002, new Vec3(0.0, 1.0, 0.0), 3.141592653589793, 0.4, 20);
               var var10001: ServerLevel = player.serverLevel();
               var var10006: Any = HexItems.DYE_PIGMENTS.get(DyeColor.RED);
               var13.sprayParticles(var10001, new FrozenPigment(new ItemStack(var10006 as ItemLike), Util.NIL_UUID));
               var10001 = player.serverLevel();
               var10006 = HexItems.DYE_PIGMENTS.get(DyeColor.BLACK);
               var13.sprayParticles(var10001, new FrozenPigment(new ItemStack(var10006 as ItemLike), Util.NIL_UUID));
            } else {
               if (!player.getAbilities().mayfly) {
                  player.getAbilities().mayfly = true;
                  player.onUpdateAbilities();
               }

               IXplatAbstractions.INSTANCE
                  .setFlight(
                     player,
                     new FlightAbility(
                        if (flight.timeLeft() >= 0) flight.timeLeft() - 1 else flight.timeLeft(), flight.dimension(), flight.origin(), flight.radius()
                     )
                  );
               val dangerParticleCount: Int = MathKt.roundToInt((double)5 * danger);
               val okParticleCount: Int = 5 - dangerParticleCount;
               val oneDangerParticleCount: Int = Mth.ceil((double)dangerParticleCount / 2.0);
               val color: FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(player as Player);
               var var25: Vec3 = player.position();
               var var10000: ParticleSpray = new ParticleSpray(var25, new Vec3(0.0, -0.6, 0.0), 0.6, 0.9424777960769379, okParticleCount);
               var var20: ServerLevel = player.serverLevel();
               var10000.sprayParticles(var20, color);
               var25 = player.position();
               val dangerSpray: ParticleSpray = new ParticleSpray(var25, new Vec3(0.0, 1.0, 0.0), 0.3, 2.356194490192345, 0);
               var10000 = ParticleSpray.copy$default(dangerSpray, null, null, 0.0, 0.0, oneDangerParticleCount, 15, null);
               var20 = player.serverLevel();
               var var28: Any = HexItems.DYE_PIGMENTS.get(DyeColor.BLACK);
               var10000.sprayParticles(var20, new FrozenPigment(new ItemStack(var28 as ItemLike), Util.NIL_UUID));
               var10000 = ParticleSpray.copy$default(dangerSpray, null, null, 0.0, 0.0, oneDangerParticleCount, 15, null);
               var20 = player.serverLevel();
               var28 = HexItems.DYE_PIGMENTS.get(DyeColor.RED);
               var10000.sprayParticles(var20, new FrozenPigment(new ItemStack(var28 as ItemLike), Util.NIL_UUID));
               if (player.level().random.nextFloat() < 0.02) {
                  player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HexSounds.FLIGHT_AMBIENCE, SoundSource.PLAYERS, 0.2F, 1.0F);
               }

               if (flight.radius() >= 0.0) {
                  val spoofedOrigin: Vec3 = flight.origin().add(0.0, 1.0, 0.0);
                  var10000 = new ParticleSpray(spoofedOrigin, new Vec3(0.0, 1.0, 0.0), 0.5, 0.3141592653589793, 5);
                  var20 = player.serverLevel();
                  var10000.sprayParticles(var20, color);
                  var10000 = new ParticleSpray(spoofedOrigin, new Vec3(0.0, -1.0, 0.0), 1.5, 0.7853981633974483, 5);
                  var20 = player.serverLevel();
                  var10000.sprayParticles(var20, color);
               }
            }
         }
      }

      private fun getDanger(player: ServerPlayer, flight: FlightAbility): Double {
         val var10000: Double;
         if (flight.radius() >= 0.0) {
            if (!(player.level().dimension() == flight.dimension())) {
               var10000 = 1.0;
            } else {
               val timeDanger: Double = new Vec3(player.getX(), 0.0, player.getZ()).distanceTo(new Vec3(flight.origin().x, 0.0, flight.origin().z));
               val distFromEdge: Double = flight.radius() - timeDanger;
               var10000 = if (distFromEdge >= OpFlight.access$getDIST_DANGER_THRESHOLD$cp())
                  0.0
                  else
                  (if (timeDanger > flight.radius()) 1.0 else 1.0 - distFromEdge / OpFlight.access$getDIST_DANGER_THRESHOLD$cp());
            }
         } else {
            var10000 = 0.0;
         }

         return Math.max(
            var10000,
            if (flight.timeLeft() >= 0)
               (
                  if ((double)flight.timeLeft() >= OpFlight.access$getTIME_DANGER_THRESHOLD$cp())
                     0.0
                     else
                     (OpFlight.access$getTIME_DANGER_THRESHOLD$cp() - (double)flight.timeLeft()) / OpFlight.access$getTIME_DANGER_THRESHOLD$cp()
               )
               else
               0.0
         );
      }
   }

   public data class Spell(type: at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type, target: ServerPlayer, theArg: Double) : RenderedSpell {
      public final val type: at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type
      public final val target: ServerPlayer
      public final val theArg: Double

      init {
         this.type = type;
         this.target = target;
         this.theArg = theArg;
      }

      public override fun cast(env: CastingEnvironment) {
         if (!this.target.getAbilities().mayfly) {
            val dim: ResourceKey = this.target.level().dimension();
            val origin: Vec3 = this.target.position();
            var var10000: FlightAbility;
            switch (OpFlight.Spell.WhenMappings.$EnumSwitchMapping$0[this.type.ordinal()]) {
               case 1:
                  var10000 = new FlightAbility(-1, dim, origin, this.theArg);
                  break;
               case 2:
                  var10000 = new FlightAbility(MathKt.roundToInt(this.theArg * 20.0), dim, origin, -1.0);
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            IXplatAbstractions.INSTANCE.setFlight(this.target, var10000);
            this.target.getAbilities().mayfly = true;
            this.target.onUpdateAbilities();
         }
      }

      public operator fun component1(): at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type {
         return this.type;
      }

      public operator fun component2(): ServerPlayer {
         return this.target;
      }

      public operator fun component3(): Double {
         return this.theArg;
      }

      public fun copy(
         type: at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Type = this.type,
         target: ServerPlayer = this.target,
         theArg: Double = this.theArg
      ): at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Spell {
         return new OpFlight.Spell(type, target, theArg);
      }

      public override fun toString(): String {
         return "Spell(type=${this.type}, target=${this.target}, theArg=${this.theArg})";
      }

      public override fun hashCode(): Int {
         return (this.type.hashCode() * 31 + this.target.hashCode()) * 31 + java.lang.Double.hashCode(this.theArg);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpFlight.Spell) {
            return false;
         } else {
            val var2: OpFlight.Spell = other as OpFlight.Spell;
            if (this.type != (other as OpFlight.Spell).type) {
               return false;
            } else if (!(this.target == var2.target)) {
               return false;
            } else {
               return java.lang.Double.compare(this.theArg, var2.theArg) == 0;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }

   public enum class Type {
      LimitRange,
      LimitTime
      @JvmStatic
      fun getEntries(): EnumEntries<OpFlight.Type> {
         return $ENTRIES;
      }
   }
}
