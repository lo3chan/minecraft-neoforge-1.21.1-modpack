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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.PublicUseOnContext
import at.petrak.hexcasting.xplat.IXplatAbstractions
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpPlaceBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpPlaceBlock.kt\nat/petrak/hexcasting/common/casting/actions/spells/OpPlaceBlock\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,106:1\n1#2:107\n*E\n"])
public object OpPlaceBlock : SpellAction {
   public open val argc: Int
      public open get() {
         return 1;
      }


   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      var pos: BlockPos;
      var var14: Direction;
      var var10000: BlockHitResult;
      var var10002: Vec3;
      label22: {
         pos = OperatorUtils.getBlockPos(args, 0, this.getArgc());
         env.assertPosInRangeForEditing(pos);
         var10000 = new BlockHitResult;
         var10002 = Vec3.atCenterOf(pos as Vec3i);
         val var10003: ServerPlayer = env.getCaster();
         if (var10003 != null) {
            var14 = var10003.getDirection();
            if (var14 != null) {
               break label22;
            }
         }

         var14 = Direction.NORTH;
      }

      var10000./* $VF: Unable to resugar constructor */<init>(var10002, var14, pos, false);
      val placeContext: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpPlaceBlock::execute$lambda$0);
      if (placeContext != null) {
         val worldState: ItemStack = placeContext.stack();
         if (worldState != null) {
            if (!env.getWorld()
               .getBlockState(pos)
               .canBeReplaced(
                  new BlockPlaceContext(new PublicUseOnContext(env.getWorld() as Level, env.getCaster() as Player, env.getCastingHand(), worldState, var10000))
               )) {
               throw MishapBadBlock.Companion.of(pos, "replaceable");
            }

            val var13: RenderedSpell = new OpPlaceBlock.Spell(pos);
            val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
            val var10005: Vec3 = Vec3.atCenterOf(pos as Vec3i);
            return new SpellAction.Result(
               var13, 1250L, CollectionsKt.listOf(ParticleSpray.Companion.cloud$default(var10004, var10005, 1.0, 0, 4, null)), 0L, 8, null
            );
         }
      }

      val var12: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
      val var10001: ItemStack = ItemStack.EMPTY;
      throw var12.of(var10001, env.getCastingHand(), "placeable");
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

   @JvmStatic
   fun `execute$lambda$0`(it: ItemStack): Boolean {
      return it.getItem() is BlockItem;
   }

   private data class Spell(pos: BlockPos) : RenderedSpell {
      public final val pos: BlockPos

      init {
         this.pos = pos;
      }

      public override fun cast(env: CastingEnvironment) {
         var caster: ServerPlayer;
         var var10000: BlockHitResult;
         var var10002: Vec3;
         var var10003: Direction;
         label32: {
            caster = env.getCaster();
            var10000 = new BlockHitResult;
            var10002 = Vec3.atCenterOf(this.pos as Vec3i);
            if (caster != null) {
               var10003 = caster.getDirection();
               if (var10003 != null) {
                  break label32;
               }
            }

            var10003 = Direction.NORTH;
         }

         var10000./* $VF: Unable to resugar constructor */<init>(var10002, var10003, this.pos, false);
         val bstate: BlockState = env.getWorld().getBlockState(this.pos);
         val var11: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpPlaceBlock.Spell::cast$lambda$0);
         val placeeStack: ItemStack = if (var11 != null) var11.stack() else null;
         if (placeeStack != null) {
            if (!IXplatAbstractions.INSTANCE.isPlacingAllowed(env.getWorld(), this.pos, placeeStack, env.getCaster() as Player)) {
               return;
            }

            if (!placeeStack.isEmpty()) {
               val spoofedStack: ItemStack = placeeStack.copy();
               spoofedStack.setCount(1);
               val placeContext: BlockPlaceContext = new BlockPlaceContext(
                  new PublicUseOnContext(env.getWorld() as Level, caster as Player, env.getCastingHand(), spoofedStack, var10000)
               );
               if (bstate.canBeReplaced(placeContext)
                  && env.withdrawItem(OpPlaceBlock.Spell::cast$lambda$1, 1, false)
                  && spoofedStack.useOn(placeContext as UseOnContext) != InteractionResult.FAIL) {
                  env.withdrawItem(OpPlaceBlock.Spell::cast$lambda$2, 1, true);
                  env.getWorld()
                     .playSound(
                        env.getCaster() as Player,
                        (double)this.pos.getX(),
                        (double)this.pos.getY(),
                        (double)this.pos.getZ(),
                        bstate.getSoundType().getPlaceSound(),
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F + (float)(Math.random() * 0.5 - 0.25)
                     );
                  env.getWorld()
                     .sendParticles(
                        (new BlockParticleOption(ParticleTypes.BLOCK, bstate)) as ParticleOptions,
                        (double)this.pos.getX(),
                        (double)this.pos.getY(),
                        (double)this.pos.getZ(),
                        4,
                        0.1,
                        0.2,
                        0.1,
                        0.1
                     );
               }
            }
         }
      }

      public operator fun component1(): BlockPos {
         return this.pos;
      }

      public fun copy(pos: BlockPos = this.pos): at.petrak.hexcasting.common.casting.actions.spells.OpPlaceBlock.Spell {
         return new OpPlaceBlock.Spell(pos);
      }

      public override fun toString(): String {
         return "Spell(pos=${this.pos})";
      }

      public override fun hashCode(): Int {
         return this.pos.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpPlaceBlock.Spell) {
            return false;
         } else {
            return this.pos == (other as OpPlaceBlock.Spell).pos;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }

      @JvmStatic
      fun `cast$lambda$0`(it: ItemStack): Boolean {
         return it.getItem() is BlockItem;
      }

      @JvmStatic
      fun `cast$lambda$1`(`$placeeStack`: ItemStack, it: ItemStack): Boolean {
         return it == `$placeeStack`;
      }

      @JvmStatic
      fun `cast$lambda$2`(`$placeeStack`: ItemStack, it: ItemStack): Boolean {
         return it == `$placeeStack`;
      }
   }
}
