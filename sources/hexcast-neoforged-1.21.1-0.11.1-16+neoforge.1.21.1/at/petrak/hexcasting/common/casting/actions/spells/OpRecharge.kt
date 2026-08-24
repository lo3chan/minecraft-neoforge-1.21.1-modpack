package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.addldata.ADMediaHolder
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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.utils.MediaHelper
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

public object OpRecharge : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val entity: ItemEntity = OperatorUtils.getItemEntity(args, 0, this.getArgc());
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpRecharge::execute$lambda$0);
      if (var10000 == null) {
         val var10: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY.copy();
         throw var10.of(var10001, null, "rechargable");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val media: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(handStack);
         if (media != null && media.canRecharge()) {
            env.assertEntityInRange(entity as Entity);
            val var9: ItemStack = entity.getItem();
            if (!MediaHelper.isMediaItem(var9)) {
               throw MishapBadItem.Companion.of(entity, "media");
            } else {
               val var10002: RenderedSpell = new OpRecharge.Spell(entity, handStack);
               val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
               val var10005: Vec3 = entity.position();
               return new SpellAction.Result(
                  var10002, 50000L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 0.5, 0, 4, null)), 0L, 8, null
               );
            }
         } else {
            val var8: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var8.of(handStack, hand, "rechargable");
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
      val media: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(it);
      return media != null && media.canRecharge() && media.insertMedia(-1L, true) != 0L;
   }

   private data class Spell(itemEntity: ItemEntity, stack: ItemStack) : RenderedSpell {
      public final val itemEntity: ItemEntity
      public final val stack: ItemStack

      init {
         this.itemEntity = itemEntity;
         this.stack = stack;
      }

      public override fun cast(env: CastingEnvironment) {
         val media: ADMediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(this.stack);
         if (media != null && this.itemEntity.isAlive()) {
            val entityStack: ItemStack = this.itemEntity.getItem().copy();
            val emptySpace: Long = media.insertMedia(-1L, true);
            media.insertMedia(MediaHelper.extractMedia$default(entityStack, emptySpace, false, false, 12, null), false);
            this.itemEntity.setItem(entityStack);
            if (entityStack.isEmpty()) {
               this.itemEntity.kill();
            }
         }
      }

      public operator fun component1(): ItemEntity {
         return this.itemEntity;
      }

      public operator fun component2(): ItemStack {
         return this.stack;
      }

      public fun copy(itemEntity: ItemEntity = this.itemEntity, stack: ItemStack = this.stack): at.petrak.hexcasting.common.casting.actions.spells.OpRecharge.Spell {
         return new OpRecharge.Spell(itemEntity, stack);
      }

      public override fun toString(): String {
         return "Spell(itemEntity=${this.itemEntity}, stack=${this.stack})";
      }

      public override fun hashCode(): Int {
         return this.itemEntity.hashCode() * 31 + this.stack.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpRecharge.Spell) {
            return false;
         } else {
            val var2: OpRecharge.Spell = other as OpRecharge.Spell;
            if (!(this.itemEntity == (other as OpRecharge.Spell).itemEntity)) {
               return false;
            } else {
               return this.stack == var2.stack;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
