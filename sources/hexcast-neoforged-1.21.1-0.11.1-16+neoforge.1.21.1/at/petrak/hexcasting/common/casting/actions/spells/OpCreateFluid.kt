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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Position
import net.minecraft.core.Vec3i
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3

public class OpCreateFluid(cost: Long, bucket: Item, cauldron: BlockState, fluid: Fluid) : SpellAction {
   public final val cost: Long
   public final val bucket: Item
   public final val cauldron: BlockState
   public final val fluid: Fluid
   public open val argc: Int

   init {
      this.cost = cost;
      this.bucket = bucket;
      this.cauldron = cauldron;
      this.fluid = fluid;
      this.argc = 1;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val vecPos: Vec3 = OperatorUtils.getVec3(args, 0, this.getArgc());
      val pos: BlockPos = BlockPos.containing(vecPos as Position);
      if (env.canEditBlockAt(pos)
         && IXplatAbstractions.INSTANCE.isPlacingAllowed(env.getWorld(), pos, new ItemStack(this.bucket as ItemLike), env.getCaster() as Player)) {
         val var10002: RenderedSpell = new OpCreateFluid.Spell(pos, this.bucket, this.cauldron, this.fluid);
         val var10003: Long = this.cost;
         val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
         val var10005: Vec3 = Vec3.atCenterOf((new BlockPos(pos as Vec3i)) as Vec3i);
         return new SpellAction.Result(
            var10002, var10003, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
         );
      } else {
         throw new MishapBadLocation(vecPos, "forbidden");
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

   private data class Spell(pos: BlockPos, bucket: Item, cauldron: BlockState, fluid: Fluid) : RenderedSpell {
      public final val pos: BlockPos
      public final val bucket: Item
      public final val cauldron: BlockState
      public final val fluid: Fluid

      init {
         this.pos = pos;
         this.bucket = bucket;
         this.cauldron = cauldron;
         this.fluid = fluid;
      }

      public override fun cast(env: CastingEnvironment) {
         if (env.getWorld().getBlockState(this.pos).getBlock() == Blocks.CAULDRON) {
            env.getWorld().setBlock(this.pos, this.cauldron, 3);
         } else if (!IXplatAbstractions.INSTANCE.tryPlaceFluid(env.getWorld() as Level, env.getCastingHand(), this.pos, this.fluid)
            && this.bucket is BucketItem) {
            (this.bucket as BucketItem).emptyContents(null, env.getWorld() as Level, this.pos, null);
         }
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public operator fun component2(): Item {
         return this.bucket;
      }

      public operator fun component3(): BlockState {
         return this.cauldron;
      }

      public operator fun component4(): Fluid {
         return this.fluid;
      }

      public fun copy(pos: BlockPos = this.pos, bucket: Item = this.bucket, cauldron: BlockState = this.cauldron, fluid: Fluid = this.fluid): at.petrak.hexcasting.common.casting.actions.spells.OpCreateFluid.Spell {
         return new OpCreateFluid.Spell(pos, bucket, cauldron, fluid);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos}, bucket=${this.bucket}, cauldron=${this.cauldron}, fluid=${this.fluid})";
      }

      public override fun hashCode(): Int {
         return ((this.pos.hashCode() * 31 + this.bucket.hashCode()) * 31 + this.cauldron.hashCode()) * 31 + this.fluid.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpCreateFluid.Spell) {
            return false;
         } else {
            val var2: OpCreateFluid.Spell = other as OpCreateFluid.Spell;
            if (!(this.pos == (other as OpCreateFluid.Spell).pos)) {
               return false;
            } else if (!(this.bucket == var2.bucket)) {
               return false;
            } else if (!(this.cauldron == var2.cauldron)) {
               return false;
            } else {
               return this.fluid == var2.fluid;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
