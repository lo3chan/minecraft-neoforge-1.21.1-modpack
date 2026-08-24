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
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.xplat.IXplatAbstractions
import java.util.HashSet
import kotlin.jvm.functions.Function1
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.AbstractCauldronBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.BucketPickup
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3

public object OpDestroyFluid : SpellAction {
   public open val argc: Int = 1
   public const val MAX_DESTROY_COUNT: Int = 1024

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = BlockPos.containing(OperatorUtils.getVec3(args, 0, this.getArgc()) as Position);
      env.assertPosInRangeForEditing(pos);
      val var10002: RenderedSpell = new OpDestroyFluid.Spell(pos);
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = Vec3.atCenterOf(pos as Vec3i);
      return new SpellAction.Result(
         var10002, 200000L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 3.0, 0, 4, null)), 0L, 8, null
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

   private data class Spell(basePos: BlockPos) : RenderedSpell {
      public final val basePos: BlockPos

      init {
         this.basePos = basePos;
      }

      public override fun cast(env: CastingEnvironment) {
         if (!IXplatAbstractions.INSTANCE.drainAllFluid(env.getWorld() as Level, this.basePos)) {
            val todo: BlockState = env.getWorld().getBlockState(this.basePos);
            if (todo.getBlock() is AbstractCauldronBlock && !(todo.getBlock() == Blocks.CAULDRON)) {
               env.getWorld().setBlock(this.basePos, Blocks.CAULDRON.defaultBlockState(), 3);
            } else {
               val var13: ArrayDeque = new ArrayDeque();
               val seen: HashSet = new HashSet();

               for (int xShift = -2; xShift < 3; xShift++) {
                  for (int yShift = -2; yShift < 3; yShift++) {
                     for (int zShift = -2; zShift < 3; zShift++) {
                        val var10001: BlockPos = this.basePos.offset(successes, here, fluid);
                        var13.add(var10001);
                     }
                  }
               }

               var var14: Int = 0;

               while (!((java.util.Collection)todo).isEmpty() && successes <= 1024) {
                  val var15: BlockPos = var13.removeFirst() as BlockPos;
                  if (env.canEditBlockAt(var15) && seen.add(var15) && !(env.getWorld().getFluidState(var15) == Fluids.EMPTY.defaultFluidState())) {
                     val blockstate: BlockState = env.getWorld().getBlockState(var15);
                     if (IXplatAbstractions.INSTANCE.isBreakingAllowed(env.getWorld(), var15, blockstate, env.getCaster() as Player)) {
                        var var18: Boolean;
                        label80: {
                           if (blockstate.getBlock() is BucketPickup) {
                              val var10000: Block = blockstate.getBlock();
                              if (!(var10000 as BucketPickup)
                                 .pickupBlock(env.getCaster() as Player, env.getWorld() as LevelAccessor, var15, blockstate)
                                 .isEmpty()) {
                                 var18 = true;
                                 break label80;
                              }
                           }

                           if (blockstate.getBlock() is LiquidBlock) {
                              env.getWorld().setBlock(var15, Blocks.AIR.defaultBlockState(), 3);
                              var18 = true;
                           } else if (blockstate.getTags().anyMatch(OpDestroyFluid.Spell::cast$lambda$1)) {
                              Block.dropResources(
                                 blockstate,
                                 env.getWorld() as LevelAccessor,
                                 var15,
                                 if (blockstate.hasBlockEntity()) env.getWorld().getBlockEntity(var15) else null
                              );
                              env.getWorld().setBlock(var15, Blocks.AIR.defaultBlockState(), 3);
                              var18 = true;
                           } else {
                              var18 = false;
                           }
                        }

                        if (var18) {
                           env.getWorld()
                              .sendParticles(
                                 ParticleTypes.SMOKE as ParticleOptions,
                                 (double)var15.getX() + 0.5 + Math.random() * 0.4 - 0.2,
                                 (double)var15.getY() + 0.5 + Math.random() * 0.4 - 0.2,
                                 (double)var15.getZ() + 0.5 + Math.random() * 0.4 - 0.2,
                                 2,
                                 0.0,
                                 0.05,
                                 0.0,
                                 0.0
                              );
                           var14++;

                           for (Direction dir : Direction.values()) {
                              val var19: BlockPos = var15.relative(dir);
                              var13.add(var19);
                           }
                        }
                     }
                  }
               }

               if (var14 > 0) {
                  env.getWorld()
                     .playSound(
                        null,
                        (double)this.basePos.getX() + 0.5,
                        (double)this.basePos.getY() + 0.5,
                        (double)this.basePos.getZ() + 0.5,
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS,
                        1.0F,
                        0.95F
                     );
               }
            }
         }
      }

      public operator fun component1(): BlockPos {
         return this.basePos;
      }

      public fun copy(basePos: BlockPos = this.basePos): at.petrak.hexcasting.common.casting.actions.spells.OpDestroyFluid.Spell {
         return new OpDestroyFluid.Spell(basePos);
      }

      public override fun toString(): String {
         return "Spell(basePos=${this.basePos})";
      }

      public override fun hashCode(): Int {
         return this.basePos.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpDestroyFluid.Spell) {
            return false;
         } else {
            return this.basePos == (other as OpDestroyFluid.Spell).basePos;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }

      @JvmStatic
      fun `cast$lambda$0`(it: TagKey): Boolean {
         return it == HexTags.Blocks.WATER_PLANTS;
      }

      @JvmStatic
      fun `cast$lambda$1`(`$tmp0`: Function1, p0: Any): Boolean {
         return `$tmp0`.invoke(p0) as java.lang.Boolean;
      }
   }
}
