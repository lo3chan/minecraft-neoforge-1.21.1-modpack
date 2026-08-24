package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.addldata.ADHexHolder
import at.petrak.hexcasting.api.addldata.ADIotaHolder
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

public object OpErase : SpellAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpErase::execute$lambda$0);
      if (var10000 == null) {
         val var9: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY.copy();
         throw var9.of(var10001, null, "eraseable");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(handStack);
         val datumHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(handStack);
         if ((hexHolder == null || !hexHolder.hasHex()) && (datumHolder == null || !datumHolder.writeIota(null, true))) {
            val var8: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var8.of(handStack, hand, "eraseable");
         } else {
            return new SpellAction.Result(new OpErase.Spell(handStack), 10000L, CollectionsKt.emptyList(), 0L, 8, null);
         }
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

   @JvmStatic
   fun `execute$lambda$0`(it: ItemStack): Boolean {
      val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(it);
      val datumHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(it);
      return hexHolder != null && hexHolder.hasHex() || datumHolder != null && datumHolder.writeIota(null, true);
   }

   private data class Spell(stack: ItemStack) : RenderedSpell {
      public final val stack: ItemStack

      init {
         this.stack = stack;
      }

      public override fun cast(env: CastingEnvironment) {
         val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(this.stack);
         val datumHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(this.stack);
         if (hexHolder != null && hexHolder.hasHex()) {
            hexHolder.clearHex();
         }

         if (datumHolder != null && datumHolder.writeIota(null, true)) {
            datumHolder.writeIota(null, false);
         }
      }

      public operator fun component1(): ItemStack {
         return this.stack;
      }

      public fun copy(stack: ItemStack = this.stack): at.petrak.hexcasting.common.casting.actions.spells.OpErase.Spell {
         return new OpErase.Spell(stack);
      }

      public override fun toString(): String {
         return "Spell(stack=${this.stack})";
      }

      public override fun hashCode(): Int {
         return this.stack.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpErase.Spell) {
            return false;
         } else {
            return this.stack == (other as OpErase.Spell).stack;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
