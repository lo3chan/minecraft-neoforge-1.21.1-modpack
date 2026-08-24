package at.petrak.hexcasting.common.casting.actions.spells

import at.petrak.hexcasting.api.addldata.ADHexHolder
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
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.api.utils.MediaHelper
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

public class OpMakePackagedSpell<T extends ItemPackagedHex>(itemType: Any, cost: Long) : SpellAction {
   public final val itemType: Any
   public final val cost: Long
   public open val argc: Int

   init {
      this.itemType = (T)itemType;
      this.cost = cost;
      this.argc = 2;
   }

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val entity: ItemEntity = OperatorUtils.getItemEntity(args, 0, this.getArgc());
      val patterns: java.util.List = CollectionsKt.toList(OperatorUtils.getList(args, 1, this.getArgc()));
      val var10000: CastingEnvironment.HeldItemInfo = env.getHeldItemToOperateOn(OpMakePackagedSpell::execute$lambda$0);
      if (var10000 == null) {
         val var13: ItemStack = ItemStack.EMPTY.copy();
         val var15: Component = this.itemType.getDescription();
         throw new MishapBadOffhandItem(var13, null, var15);
      } else {
         val handStack: ItemStack = var10000.component1();
         val hand: InteractionHand = var10000.component2();
         val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(handStack);
         if (!handStack.is(this.itemType)) {
            val var14: Component = this.itemType.getDescription();
            throw new MishapBadOffhandItem(handStack, hand, var14);
         } else if (hexHolder != null && !hexHolder.hasHex()) {
            env.assertEntityInRange(entity as Entity);
            val var11: ItemStack = entity.getItem();
            if (MediaHelper.isMediaItem(var11)) {
               val var12: ItemStack = entity.getItem();
               if (MediaHelper.extractMedia$default(var12, 0L, true, true, 2, null) > 0L) {
                  val trueName: Player = MishapOthersName.Companion.getTrueNameFromArgs(patterns, env.getCaster() as Player);
                  if (trueName != null) {
                     throw new MishapOthersName(trueName);
                  }

                  val var10002: RenderedSpell = new OpMakePackagedSpell.Spell(this, entity, patterns, handStack);
                  val var10003: Long = this.cost;
                  val var10004: ParticleSpray.Companion = ParticleSpray.Companion;
                  val var10005: Vec3 = entity.position();
                  return new SpellAction.Result(
                     var10002, var10003, CollectionsKt.listOf(ParticleSpray.Companion.burst$default(var10004, var10005, 0.5, 0, 4, null)), 0L, 8, null
                  );
               }
            }

            throw MishapBadItem.Companion.of(entity, "media_for_battery");
         } else {
            val var10: MishapBadOffhandItem.Companion = MishapBadOffhandItem.Companion;
            throw var10.of(handStack, hand, "iota.write");
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
   fun `execute$lambda$0`(`this$0`: OpMakePackagedSpell, it: ItemStack): Boolean {
      val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(it);
      return it.is(`this$0`.itemType) && hexHolder != null && !hexHolder.hasHex();
   }

   private inner class Spell(itemEntity: ItemEntity, patterns: List<Iota>, stack: ItemStack) : RenderedSpell {
      public final val itemEntity: ItemEntity
      public final val patterns: List<Iota>
      public final val stack: ItemStack

      init {
         this.this$0 = `this$0`;
         this.itemEntity = itemEntity;
         this.patterns = patterns;
         this.stack = stack;
      }

      public override fun cast(env: CastingEnvironment) {
         val hexHolder: ADHexHolder = IXplatAbstractions.INSTANCE.findHexHolder(this.stack);
         if (hexHolder != null && !hexHolder.hasHex() && this.itemEntity.isAlive()) {
            val entityStack: ItemStack = this.itemEntity.getItem().copy();
            val mediamount: Long = MediaHelper.extractMedia$default(entityStack, 0L, true, false, 10, null);
            if (mediamount > 0L) {
               hexHolder.writeHex(this.patterns, env.getPigment(), mediamount);
            }

            this.itemEntity.setItem(entityStack);
            if (entityStack.isEmpty()) {
               this.itemEntity.kill();
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
