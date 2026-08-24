package at.petrak.hexcasting.api.casting.eval.env;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.circles.CircleExecutionState;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CircleCastEnv extends CastingEnvironment {
   protected final CircleExecutionState execState;

   public CircleCastEnv(ServerLevel world, CircleExecutionState execState) {
      super(world);
      this.execState = execState;
   }

   @Nullable
   @Override
   public ServerPlayer getCaster() {
      return this.execState.getCaster(this.world);
   }

   @Nullable
   public BlockEntityAbstractImpetus getImpetus() {
      BlockEntity entity = this.world.getBlockEntity(this.execState.impetusPos);
      return entity instanceof BlockEntityAbstractImpetus ? (BlockEntityAbstractImpetus)entity : null;
   }

   public CircleExecutionState circleState() {
      return this.execState;
   }

   @Override
   public MishapEnvironment getMishapEnvironment() {
      return new CircleMishapEnv(this.world, this.execState);
   }

   @Override
   public void precheckAction(PatternShapeMatch match) throws Mishap {
      super.precheckAction(match);
      ResourceLocation key = this.actionKey(match);
      if (!HexConfig.server().isActionAllowedInCircles(key)) {
         throw new MishapDisallowedSpell("disallowed_circle");
      }
   }

   @Override
   public void postExecution(CastResult result) {
      super.postExecution(result);
      SoundEvent sound = result.getSound().sound();
      if (sound != null) {
         BlockPos soundPos = this.execState.currentPos;
         this.world.playSound(null, soundPos, sound, SoundSource.PLAYERS, 1.0F, 1.0F);
      }

      BlockEntityAbstractImpetus imp = this.getImpetus();
      if (imp != null) {
         for (OperatorSideEffect sideEffect : result.getSideEffects()) {
            if (sideEffect instanceof OperatorSideEffect.DoMishap doMishap) {
               Component msg = doMishap.getMishap().errorMessageWithName(this, doMishap.getErrorCtx());
               if (msg != null) {
                  imp.postMishap(msg);
               }
            }
         }
      }
   }

   @Override
   public Vec3 mishapSprayPos() {
      return Vec3.atCenterOf(this.execState.currentPos);
   }

   @Override
   public long extractMediaEnvironment(long cost) {
      BlockEntityAbstractImpetus entity = this.getImpetus();
      if (entity == null) {
         return cost;
      } else {
         long mediaAvailable = entity.getMedia();
         if (mediaAvailable < 0L) {
            return 0L;
         } else {
            long mediaToTake = Math.min(cost, mediaAvailable);
            cost -= mediaToTake;
            entity.setMedia(mediaAvailable - mediaToTake);
            return cost;
         }
      }
   }

   @Override
   public boolean isVecInRangeEnvironment(Vec3 vec) {
      ServerPlayer caster = this.execState.getCaster(this.world);
      if (caster != null) {
         Sentinel sentinel = HexAPI.instance().getSentinel(caster);
         if (sentinel != null
            && sentinel.extendsRange()
            && caster.level().dimension() == sentinel.dimension()
            && vec.distanceToSqr(sentinel.position()) <= 256.0) {
            return true;
         }
      }

      return this.execState.bounds.contains(vec);
   }

   @Override
   public boolean hasEditPermissionsAtEnvironment(BlockPos pos) {
      return true;
   }

   @Override
   public InteractionHand getCastingHand() {
      return InteractionHand.MAIN_HAND;
   }

   @Override
   protected List<ItemStack> getUsableStacks(CastingEnvironment.StackDiscoveryMode mode) {
      return new ArrayList<>();
   }

   @Override
   protected List<CastingEnvironment.HeldItemInfo> getPrimaryStacks() {
      return List.of();
   }

   @Override
   public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable InteractionHand hand) {
      return false;
   }

   @Override
   public FrozenPigment getPigment() {
      BlockEntityAbstractImpetus impetus = this.getImpetus();
      return impetus == null ? FrozenPigment.DEFAULT.get() : impetus.getPigment();
   }

   @Nullable
   @Override
   public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
      BlockEntityAbstractImpetus impetus = this.getImpetus();
      return impetus == null ? null : impetus.setPigment(pigment);
   }

   @Override
   public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {
      particles.sprayParticles(this.world, pigment);
   }

   @Override
   public void printMessage(Component message) {
      BlockEntityAbstractImpetus impetus = this.getImpetus();
      if (impetus != null) {
         impetus.postPrint(message);
      }
   }
}
