package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.castables.SpellAction.Result
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import java.util.UUID
import net.minecraft.Util
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

public object OpColorize : SpellAction {
   public open val argc: Int

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(IXplatAbstractions.INSTANCE::isPigment);
      if (var10000 == null) {
         val var7: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY;
         throw var7.of(var10001, null, "colorizer");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         if (!IXplatAbstractions.INSTANCE.isPigment(handStack)) {
            val var6: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var6.of(handStack, hand, "colorizer");
         } else {
            return new SpellAction.Result(new OpColorize.Spell(handStack), 10000L, CollectionsKt.emptyList(), 0L, 8, null);
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

   private data class Spell(stack: ItemStack) : RenderedSpell {
      public final val stack: ItemStack

      init {
         this.stack = stack;
      }

      public override fun cast(env: CastingEnvironment) {
         val copy: ItemStack = this.stack.copyWithCount(1);
         if (env.withdrawItem(OpColorize.Spell::cast$lambda$0, 1, true)) {
            var var3: UUID;
            var var10001: FrozenPigment;
            label13: {
               var10001 = new FrozenPigment;
               val var10004: ServerPlayer = env.getCaster();
               if (var10004 != null) {
                  var3 = var10004.getUUID();
                  if (var3 != null) {
                     break label13;
                  }
               }

               var3 = Util.NIL_UUID;
            }

            var10001./* $VF: Unable to resugar constructor */<init>(copy, var3);
            env.setPigment(var10001);
         }
      }

      public operator fun component1(): ItemStack {
         return this.stack;
      }

      public fun copy(stack: ItemStack = this.stack): at.petrak.hexcasting.common.casting.actions.spells.OpColorize.Spell {
         return new OpColorize.Spell(stack);
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
         } else if (other !is OpColorize.Spell) {
            return false;
         } else {
            return this.stack == (other as OpColorize.Spell).stack;
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }

      @JvmStatic
      fun `cast$lambda$0`(`$copy`: ItemStack, it: ItemStack): Boolean {
         return ItemStack.isSameItemSameComponents(`$copy`, it);
      }
   }
}
