package at.petrak.hexcasting.common.casting.actions.rw

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
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

public object OpWrite : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val datum: Iota = args.get(0) as Iota;
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpWrite::execute$lambda$0);
      if (var10000 == null) {
         val var13: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY.copy();
         throw var13.of(var10001, null, "iota.write");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val var10: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(handStack);
         if (var10 == null) {
            val var12: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var12.of(handStack, hand, "iota.write");
         } else if (!var10.writeIota(datum, true)) {
            val var11: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var11.of(handStack, hand, "iota.readonly", datum.display());
         } else {
            val trueName: Player = MishapOthersName.Companion.getTrueNameFromDatum(datum, env.getCaster() as Player);
            if (trueName != null) {
               throw new MishapOthersName(trueName);
            } else {
               return new SpellAction.Result(new OpWrite.Spell(datum, var10), 0L, CollectionsKt.emptyList(), 0L, 8, null);
            }
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
   fun `execute$lambda$0`(`$datum`: Iota, it: ItemStack): Boolean {
      val datumHolder: ADIotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(it);
      return datumHolder != null && datumHolder.writeIota(`$datum`, true);
   }

   private data class Spell(datum: Iota, datumHolder: ADIotaHolder) : RenderedSpell {
      public final val datum: Iota
      public final val datumHolder: ADIotaHolder

      init {
         this.datum = datum;
         this.datumHolder = datumHolder;
      }

      public override fun cast(env: CastingEnvironment) {
         this.datumHolder.writeIota(this.datum, false);
      }

      public operator fun component1(): Iota {
         return this.datum;
      }

      public operator fun component2(): ADIotaHolder {
         return this.datumHolder;
      }

      public fun copy(datum: Iota = this.datum, datumHolder: ADIotaHolder = this.datumHolder): at.petrak.hexcasting.common.casting.actions.rw.OpWrite.Spell {
         return new OpWrite.Spell(datum, datumHolder);
      }

      public override fun toString(): String {
         return "Spell(datum=${this.datum}, datumHolder=${this.datumHolder})";
      }

      public override fun hashCode(): Int {
         return this.datum.hashCode() * 31 + this.datumHolder.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpWrite.Spell) {
            return false;
         } else {
            val var2: OpWrite.Spell = other as OpWrite.Spell;
            if (!(this.datum == (other as OpWrite.Spell).datum)) {
               return false;
            } else {
               return this.datumHolder == var2.datumHolder;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
