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
import at.petrak.hexcasting.ktxt.AccessorWrappers
import at.petrak.hexcasting.xplat.IXplatAbstractions
import java.util.HashSet
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.AbstractCandleBlock
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CampfireBlock
import net.minecraft.world.level.block.NetherPortalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

public object OpExtinguish : SpellAction {
   public open val argc: Int = 1
   public const val MAX_DESTROY_COUNT: Int = 1024

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = BlockPos.containing(OperatorUtils.getVec3(args, 0, this.getArgc()) as Position);
      env.assertPosInRangeForEditing(pos);
      val var10002: RenderedSpell = new OpExtinguish.Spell(pos);
      val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
      val var10005: Vec3 = Vec3.atCenterOf(pos as Vec3i);
      return new SpellAction.Result(
         var10002, 60000L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
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

   private data class Spell(target: BlockPos) : RenderedSpell {
      public final val target: BlockPos

      init {
         this.target = target;
      }

      public override fun cast(env: CastingEnvironment) {
         val todo: ArrayDeque = new ArrayDeque();
         val seen: HashSet = new HashSet();
         todo.add(this.target);
         var successes: Int = 0;

         while (!((java.util.Collection)todo).isEmpty() && successes <= 1024) {
            val here: BlockPos = todo.removeFirst() as BlockPos;
            if (env.canEditBlockAt(here) && this.target.distSqr(here as Vec3i) < 100.0 && seen.add(here)) {
               val blockstate: BlockState = env.getWorld().getBlockState(here);
               if (IXplatAbstractions.INSTANCE.isBreakingAllowed(env.getWorld(), here, blockstate, env.getCaster() as Player)) {
                  val var10: Block = blockstate.getBlock();
                  val var10000: Boolean;
                  if (var10 is BaseFireBlock) {
                     env.getWorld().setBlock(here, Blocks.AIR.defaultBlockState(), 3);
                     var10000 = true;
                  } else if (var10 is CampfireBlock) {
                     if (blockstate.getValue(CampfireBlock.LIT as Property) as java.lang.Boolean) {
                        val wilson: Item = Items.WOODEN_SHOVEL;
                        val hereVec: Vec3 = Vec3.atCenterOf(here as Vec3i);
                        val var10001: ServerLevel = env.getWorld();
                        wilson.useOn(
                           AccessorWrappers.UseOnContext(
                              var10001 as Level,
                              null,
                              InteractionHand.MAIN_HAND,
                              new ItemStack(wilson as ItemLike),
                              new BlockHitResult(hereVec, Direction.UP, here, false)
                           )
                        );
                        var10000 = true;
                     } else {
                        var10000 = false;
                     }
                  } else if (var10 is AbstractCandleBlock) {
                     if (blockstate.getValue(AbstractCandleBlock.LIT as Property) as java.lang.Boolean) {
                        AbstractCandleBlock.extinguish(null, blockstate, env.getWorld() as LevelAccessor, here);
                        var10000 = true;
                     } else {
                        var10000 = false;
                     }
                  } else if (var10 is NetherPortalBlock) {
                     env.getWorld().setBlock(here, Blocks.AIR.defaultBlockState(), 3);
                     var10000 = true;
                  } else {
                     var10000 = false;
                  }

                  if (var10000) {
                     env.getWorld()
                        .sendParticles(
                           ParticleTypes.SMOKE as ParticleOptions,
                           (double)here.getX() + 0.5 + Math.random() * 0.4 - 0.2,
                           (double)here.getY() + 0.5 + Math.random() * 0.4 - 0.2,
                           (double)here.getZ() + 0.5 + Math.random() * 0.4 - 0.2,
                           2,
                           0.0,
                           0.05,
                           0.0,
                           0.0
                        );
                     successes++;
                  }

                  for (Direction dir : Direction.values()) {
                     val var17: BlockPos = here.relative(dir);
                     todo.add(var17);
                  }
               }
            }
         }

         if (successes > 0) {
            env.getWorld()
               .playSound(
                  null,
                  (double)this.target.getX(),
                  (double)this.target.getY(),
                  (double)this.target.getZ(),
                  SoundEvents.FIRE_EXTINGUISH,
                  SoundSource.BLOCKS,
                  1.0F,
                  0.95F
               );
         }
      }

      public operator fun component1(): BlockPos {
         return this.target;
      }

      public fun copy(target: BlockPos = this.target): at.petrak.hexcasting.common.casting.actions.spells.OpExtinguish.Spell {
         return new OpExtinguish.Spell(target);
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
         } else if (other !is OpExtinguish.Spell) {
            return false;
         } else {
            return this.target == (other as OpExtinguish.Spell).target;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
