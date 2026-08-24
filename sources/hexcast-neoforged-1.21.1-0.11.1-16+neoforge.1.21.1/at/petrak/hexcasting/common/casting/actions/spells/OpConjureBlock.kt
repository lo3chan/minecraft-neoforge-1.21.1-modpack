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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.blocks.BlockConjured
import at.petrak.hexcasting.common.lib.HexBlocks
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.DirectionalPlaceContext
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

public class OpConjureBlock(light: Boolean) : SpellAction {
   public final val light: Boolean
   public open val argc: Int

   init {
      this.light = light;
      this.argc = 1;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val pos: BlockPos = BlockPos.containing(OperatorUtils.getVec3(args, 0, this.getArgc()) as Position);
      env.assertPosInRangeForEditing(pos);
      if (!env.getWorld()
         .getBlockState(pos)
         .canBeReplaced((new DirectionalPlaceContext(env.getWorld() as Level, pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP)) as BlockPlaceContext)) {
         val var10000: MishapBadBlock.Companion = MishapBadBlock.Companion;
         throw var10000.of(pos, "replaceable");
      } else {
         val var10002: RenderedSpell = new OpConjureBlock.Spell(pos, this.light);
         val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
         val var10005: Vec3 = Vec3.atCenterOf(pos as Vec3i);
         return new SpellAction.Result(
            var10002, 10000L, CollectionsKt.listOf(ParticleSpray.Companion.cloud$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
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

   private data class Spell(pos: BlockPos, light: Boolean) : RenderedSpell {
      public final val pos: BlockPos
      public final val light: Boolean

      init {
         this.pos = pos;
         this.light = light;
      }

      public override fun cast(env: CastingEnvironment) {
         if (env.canEditBlockAt(this.pos)) {
            val placeContext: DirectionalPlaceContext = new DirectionalPlaceContext(
               env.getWorld() as Level, this.pos, Direction.DOWN, ItemStack.EMPTY, Direction.UP
            );
            if (env.getWorld().getBlockState(this.pos).canBeReplaced(placeContext as BlockPlaceContext)) {
               val block: Block = if (this.light) HexBlocks.CONJURED_LIGHT else HexBlocks.CONJURED_BLOCK;
               if (!IXplatAbstractions.INSTANCE.isPlacingAllowed(env.getWorld(), this.pos, new ItemStack(block as ItemLike), env.getCaster() as Player)) {
                  return;
               }

               val state: BlockState = block.getStateForPlacement(placeContext as BlockPlaceContext);
               if (state != null) {
                  env.getWorld().setBlock(this.pos, state, 5);
                  val pigment: FrozenPigment = env.getPigment();
                  if (env.getWorld().getBlockState(this.pos).getBlock() is BlockConjured) {
                     BlockConjured.setColor(env.getWorld() as LevelAccessor, this.pos, pigment);
                  }
               }
            }
         }
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public operator fun component2(): Boolean {
         return this.light;
      }

      public fun copy(pos: BlockPos = this.pos, light: Boolean = this.light): at.petrak.hexcasting.common.casting.actions.spells.OpConjureBlock.Spell {
         return new OpConjureBlock.Spell(pos, light);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos}, light=${this.light})";
      }

      public override fun hashCode(): Int {
         return this.pos.hashCode() * 31 + java.lang.Boolean.hashCode(this.light);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpConjureBlock.Spell) {
            return false;
         } else {
            val var2: OpConjureBlock.Spell = other as OpConjureBlock.Spell;
            if (!(this.pos == (other as OpConjureBlock.Spell).pos)) {
               return false;
            } else {
               return this.light == var2.light;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
