package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.addldata.ADVariantItem
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

public object OpCycleVariant : SpellAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpCycleVariant::execute$lambda$0);
      if (var10000 == null) {
         val var9: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY.copy();
         throw var9.of(var10001, null, "variant");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val var7: ADVariantItem = IXplatAbstractions.INSTANCE.findVariantHolder(handStack);
         if (var7 == null) {
            val var8: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var8.of(handStack, hand, "variant");
         } else {
            return new SpellAction.Result(new OpCycleVariant.Spell(var7), 0L, CollectionsKt.emptyList(), 0L, 8, null);
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
      return IXplatAbstractions.INSTANCE.findVariantHolder(it) != null;
   }

   private data class Spell(variantHolder: ADVariantItem) : RenderedSpell {
      public final val variantHolder: ADVariantItem

      init {
         this.variantHolder = variantHolder;
      }

      public override fun cast(env: CastingEnvironment) {
         this.variantHolder.setVariant((this.variantHolder.getVariant() + 1) % this.variantHolder.numVariants());
      }

      public operator fun component1(): ADVariantItem {
         return this.variantHolder;
      }

      public fun copy(variantHolder: ADVariantItem = this.variantHolder): at.petrak.hexcasting.common.casting.actions.spells.OpCycleVariant.Spell {
         return new OpCycleVariant.Spell(variantHolder);
      }

      public override fun toString(): String {
         return "Spell(variantHolder=${this.variantHolder})";
      }

      public override fun hashCode(): Int {
         return this.variantHolder.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpCycleVariant.Spell) {
            return false;
         } else {
            return this.variantHolder == (other as OpCycleVariant.Spell).variantHolder;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
