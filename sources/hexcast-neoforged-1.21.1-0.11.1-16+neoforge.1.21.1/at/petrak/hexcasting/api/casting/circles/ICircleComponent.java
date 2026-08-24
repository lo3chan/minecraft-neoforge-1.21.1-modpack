package at.petrak.hexcasting.api.casting.circles;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexSounds;
import com.mojang.datafixers.util.Pair;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;

public interface ICircleComponent {
   ICircleComponent.ControlFlow acceptControlFlow(CastingImage var1, CircleCastEnv var2, Direction var3, BlockPos var4, BlockState var5, ServerLevel var6);

   @Contract(
      pure = true
   )
   boolean canEnterFromDirection(Direction var1, BlockPos var2, BlockState var3, ServerLevel var4);

   @Contract(
      pure = true
   )
   EnumSet<Direction> possibleExitDirections(BlockPos var1, BlockState var2, Level var3);

   @Contract(
      pure = true
   )
   default Pair<BlockPos, Direction> exitPositionFromDirection(BlockPos pos, Direction dir) {
      return Pair.of(pos.offset(dir.getStepX(), dir.getStepY(), dir.getStepZ()), dir);
   }

   BlockState startEnergized(BlockPos var1, BlockState var2, Level var3);

   boolean isEnergized(BlockPos var1, BlockState var2, Level var3);

   BlockState endEnergized(BlockPos var1, BlockState var2, Level var3);

   static void sfx(BlockPos pos, BlockState bs, Level world, BlockEntityAbstractImpetus impetus, boolean success) {
      UUID activator = Util.NIL_UUID;
      if (impetus != null && impetus.getExecutionState() != null && impetus.getExecutionState().caster != null) {
         activator = impetus.getExecutionState().caster;
      }

      FrozenPigment colorizer;
      if (impetus != null && impetus.getExecutionState() != null) {
         colorizer = impetus.getPigment();
      } else {
         colorizer = new FrozenPigment(new ItemStack((ItemLike)HexItems.DYE_PIGMENTS.get(DyeColor.RED)), activator);
      }

      Vec3 vpos;
      Vec3 vecOutDir;
      if (bs.getBlock() instanceof BlockCircleComponent bcc) {
         Direction outDir = bcc.normalDir(pos, bs, world);
         float height = bcc.particleHeight(pos, bs, world);
         vecOutDir = new Vec3(outDir.step());
         vpos = Vec3.atCenterOf(pos).add(vecOutDir.scale(height));
      } else {
         vpos = Vec3.atCenterOf(pos);
         vecOutDir = new Vec3(0.0, 0.0, 0.0);
      }

      if (world instanceof ServerLevel serverLevel) {
         ParticleSpray spray = new ParticleSpray(
            vpos, vecOutDir.scale(success ? 1.0 : 1.5), success ? 0.1 : 0.5, 3.1415927F / (success ? 4 : 2), success ? 30 : 100
         );
         spray.sprayParticles(serverLevel, success ? colorizer : new FrozenPigment(new ItemStack((ItemLike)HexItems.DYE_PIGMENTS.get(DyeColor.RED)), activator));
      }

      float pitch = 1.0F;
      SoundEvent sound = HexSounds.SPELL_CIRCLE_FAIL;
      if (success && impetus != null) {
         sound = HexSounds.SPELL_CIRCLE_FIND_BLOCK;
         CircleExecutionState state = impetus.getExecutionState();
         int note = state.reachedPositions.size() - 1;
         int semitone = impetus.semitoneFromScale(note);
         pitch = (float)Math.pow(2.0, (semitone - 8) / 12.0);
      }

      world.playSound(null, vpos.x, vpos.y, vpos.z, sound, SoundSource.BLOCKS, 1.0F, pitch);
   }

   default void fakeThrowMishap(BlockPos pos, BlockState bs, CastingImage image, CircleCastEnv env, Mishap mishap) {
      Mishap.Context errorCtx = new Mishap.Context(null, bs.getBlock().getName().append(" (").append(Component.literal(pos.toShortString())).append(")"));
      OperatorSideEffect.DoMishap sideEffect = new OperatorSideEffect.DoMishap(mishap, errorCtx);
      CastingVM vm = new CastingVM(image, env);
      sideEffect.performEffect(vm);
   }

   public abstract static sealed class ControlFlow permits ICircleComponent.ControlFlow.Continue, ICircleComponent.ControlFlow.Stop {
      public static final class Continue extends ICircleComponent.ControlFlow {
         public final CastingImage update;
         public final List<Pair<BlockPos, Direction>> exits;

         public Continue(CastingImage update, List<Pair<BlockPos, Direction>> exits) {
            this.update = update;
            this.exits = exits;
         }
      }

      public static final class Stop extends ICircleComponent.ControlFlow {
      }
   }
}
