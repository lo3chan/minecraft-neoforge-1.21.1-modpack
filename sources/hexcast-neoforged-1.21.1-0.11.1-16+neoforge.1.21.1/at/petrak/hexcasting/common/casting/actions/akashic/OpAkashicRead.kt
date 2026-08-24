package at.petrak.hexcasting.common.casting.actions.akashic

import at.petrak.hexcasting.api.casting.OperatorUtils
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapNoAkashicRecord
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicRecord
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block

public object OpAkashicRead : ConstMediaAction {
   public open val argc: Int = 2
   public open val mediaCost: Long = 10000L

   public override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
      val pos: BlockPos = OperatorUtils.getBlockPos(args, 0, this.getArgc());
      val key: HexPattern = OperatorUtils.getPattern(args, 1, this.getArgc());
      val record: Block = env.getWorld().getBlockState(pos).getBlock();
      if (record !is BlockAkashicRecord) {
         throw new MishapNoAkashicRecord(pos);
      } else {
         val datum: Iota = (record as BlockAkashicRecord).lookupPattern(pos, key, env.getWorld());
         var var10000: Iota = datum;
         if (datum == null) {
            var10000 = new NullIota();
         }

         return CollectionsKt.listOf(var10000);
      }
   }

   override fun executeWithOpCount(args: MutableList<Iota>, env: CastingEnvironment): ConstMediaAction.CostMediaActionResult {
      return ConstMediaAction.DefaultImpls.executeWithOpCount(this, args, env);
   }

   override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }
}
