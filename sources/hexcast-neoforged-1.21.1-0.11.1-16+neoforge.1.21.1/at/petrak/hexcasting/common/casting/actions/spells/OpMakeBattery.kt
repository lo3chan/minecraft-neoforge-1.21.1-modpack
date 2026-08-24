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
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.api.utils.MediaHelper
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import at.petrak.hexcasting.common.lib.HexItems
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.phys.Vec3

public object OpMakeBattery : SpellAction {
   public open val argc: Int = 1

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val entity: ItemEntity = OperatorUtils.getItemEntity(args, 0, this.getArgc());
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpMakeBattery::execute$lambda$0);
      if (var10000 == null) {
         val var11: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
         val var10001: ItemStack = ItemStack.EMPTY.copy();
         throw var11.of(var10001, null, "bottle");
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         if (!handStack.is(HexTags.Items.PHIAL_BASE)) {
            val var10: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var10.of(handStack, hand, "bottle");
         } else if (handStack.getCount() != 1) {
            val var9: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var9.of(handStack, hand, "only_one");
         } else {
            env.assertEntityInRange(entity as Entity);
            val var7: ItemStack = entity.getItem();
            if (MediaHelper.isMediaItem(var7)) {
               val var8: ItemStack = entity.getItem();
               if (MediaHelper.extractMedia$default(var8, 0L, true, true, 2, null) > 0L) {
                  val var10002: RenderedSpell = new OpMakeBattery.Spell(entity, handStack, hand);
                  val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
                  val var10005: Vec3 = entity.position();
                  return new SpellAction.Result(
                     var10002, 100000L, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 0.5, 0, 4, null)), 0L, 8, null
                  );
               }
            }

            throw MishapBadItem.Companion.of(entity, "media_for_battery");
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
      return it.is(HexTags.Items.PHIAL_BASE);
   }

   private data class Spell(itemEntity: ItemEntity, handStack: ItemStack, hand: InteractionHand?) : RenderedSpell {
      public final val itemEntity: ItemEntity
      public final val handStack: ItemStack
      public final val hand: InteractionHand?

      init {
         this.itemEntity = itemEntity;
         this.handStack = handStack;
         this.hand = hand;
      }

      public override fun cast(env: CastingEnvironment) {
         if (this.itemEntity.isAlive()) {
            val entityStack: ItemStack = this.itemEntity.getItem().copy();
            val mediamount: Long = MediaHelper.extractMedia$default(entityStack, 0L, true, false, 10, null);
            if (mediamount <= 0L
               || env.replaceItem(
                  OpMakeBattery.Spell::cast$lambda$0, ItemMediaHolder.withMedia(new ItemStack(HexItems.BATTERY as ItemLike), mediamount, mediamount), this.hand
               )) {
               this.itemEntity.setItem(entityStack);
               if (entityStack.isEmpty()) {
                  this.itemEntity.kill();
               }
            }
         }
      }

      public operator fun component1(): ItemEntity {
         return this.itemEntity;
      }

      public operator fun component2(): ItemStack {
         return this.handStack;
      }

      public operator fun component3(): InteractionHand? {
         return this.hand;
      }

      public fun copy(itemEntity: ItemEntity = this.itemEntity, handStack: ItemStack = this.handStack, hand: InteractionHand? = this.hand): at.petrak.hexcasting.common.casting.actions.spells.OpMakeBattery.Spell {
         return new OpMakeBattery.Spell(itemEntity, handStack, hand);
      }

      public override fun toString(): String {
         return "Spell(itemEntity=${this.itemEntity}, handStack=${this.handStack}, hand=${this.hand})";
      }

      public override fun hashCode(): Int {
         return (this.itemEntity.hashCode() * 31 + this.handStack.hashCode()) * 31 + (if (this.hand == null) 0 else this.hand.hashCode());
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpMakeBattery.Spell) {
            return false;
         } else {
            val var2: OpMakeBattery.Spell = other as OpMakeBattery.Spell;
            if (!(this.itemEntity == (other as OpMakeBattery.Spell).itemEntity)) {
               return false;
            } else if (!(this.handStack == var2.handStack)) {
               return false;
            } else {
               return this.hand === var2.hand;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }

      @JvmStatic
      fun `cast$lambda$0`(`this$0`: OpMakeBattery.Spell, it: ItemStack): Boolean {
         return it == `this$0`.handStack;
      }
   }
}
