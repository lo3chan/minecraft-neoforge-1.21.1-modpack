package at.petrak.hexcasting.common.casting.actions.spells.great

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
import at.petrak.hexcasting.api.casting.mishaps.MishapImmuneEntity
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.api.mod.HexTags
import at.petrak.hexcasting.common.msgs.MsgBlinkS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.Position
import net.minecraft.core.Holder.Reference
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.TicketType
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.phys.Vec3

@SourceDebugExtension(["SMAP\nOpTeleport.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpTeleport.kt\nat/petrak/hexcasting/common/casting/actions/spells/great/OpTeleport\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,148:1\n1761#2,3:149\n808#2,11:152\n1869#2,2:163\n1869#2,2:165\n*S KotlinDebug\n*F\n+ 1 OpTeleport.kt\nat/petrak/hexcasting/common/casting/actions/spells/great/OpTeleport\n*L\n111#1:149,3\n122#1:152,11\n122#1:163,2\n128#1:165,2\n*E\n"])
public object OpTeleport : SpellAction {
   public open val argc: Int = 2

   public override fun execute(args: List<Iota>, env: CastingEnvironment): Result {
      val teleportee: Entity = OperatorUtils.getEntity(args, 0, this.getArgc());
      val delta: Vec3 = OperatorUtils.getVec3(args, 1, this.getArgc());
      env.assertEntityInRange(teleportee);
      if (teleportee.canUsePortal(false) && !teleportee.getType().is(HexTags.Entities.CANNOT_TELEPORT)) {
         val targetPos: Vec3 = teleportee.position().add(delta);
         if (!HexConfig.server().canTeleportInThisDimension(env.getWorld().dimension())) {
            throw new MishapBadLocation(targetPos, "bad_dimension");
         } else {
            env.assertVecInWorld(targetPos);
            if (!env.isVecInWorld(targetPos.subtract(0.0, 1.0, 0.0))) {
               throw new MishapBadLocation(targetPos, "too_close_to_out");
            } else {
               val targetMiddlePos: Vec3 = teleportee.position().add(0.0, (double)teleportee.getEyeHeight() / 2.0, 0.0);
               val var10002: RenderedSpell = new OpTeleport.Spell(teleportee, delta);
               val var7: Array<ParticleSpray> = new ParticleSpray[2];
               var var10006: ParticleSpray.Companion = ParticleSpray.Companion;
               var7[0] = ParticleSpray.Companion.cloud$default(var10006, targetMiddlePos, 2.0, 0, 4, null);
               var10006 = ParticleSpray.Companion;
               val var10007: Vec3 = targetMiddlePos.add(delta);
               var7[1] = ParticleSpray.Companion.burst$default(var10006, var10007, 2.0, 0, 4, null);
               return new SpellAction.Result(var10002, 1000000L, CollectionsKt.listOf(var7), 0L, 8, null);
            }
         }
      } else {
         throw new MishapImmuneEntity(teleportee);
      }
   }

   public fun teleportRespectSticky(teleportee: Entity, delta: Vec3, world: ServerLevel) {
      if (HexConfig.server().canTeleportInThisDimension(world.dimension())) {
         val playersToUpdate: java.util.List = new ArrayList();
         val target: Vec3 = teleportee.position().add(delta);
         val var10000: java.util.List = teleportee.getPassengers();
         val sticky: java.lang.Iterable = var10000;
         var var36: Boolean;
         if (var10000 is java.util.Collection && (var10000 as java.util.Collection).isEmpty()) {
            var36 = false;
         } else {
            val player: java.util.Iterator = sticky.iterator();

            while (true) {
               if (!player.hasNext()) {
                  var36 = false;
                  break;
               }

               if ((player.next() as Entity).getType().is(HexTags.Entities.CANNOT_TELEPORT)) {
                  var36 = true;
                  break;
               }
            }
         }

         if (!var36) {
            if (teleportee.getType().is(HexTags.Entities.STICKY_TELEPORTERS)) {
               teleportee.stopRiding();
               val var27: java.util.Collection = new ArrayList();

               val var16: java.lang.Iterable;
               for (Object element$iv$iv : var16) {
                  if (var14 is ServerPlayer) {
                     var27.add(var14);
                  }
               }

               for (Object element$iv : var16) {
                  playersToUpdate.add(var28 as ServerPlayer);
               }

               teleportee.teleportTo(target.x, target.y, target.z);
            } else {
               teleportee.stopRiding();

               val var18: java.lang.Iterable;
               for (Object element$iv : var18) {
                  (var29 as Entity).stopRiding();
               }

               if (teleportee is ServerPlayer) {
                  playersToUpdate.add(teleportee);
               } else {
                  teleportee.setPos(teleportee.position().add(delta));
               }
            }

            for (ServerPlayer player : playersToUpdate) {
               world.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(BlockPos.containing(target as Position)), 1, var23.getId());
               var23.connection.resetPosition();
               var23.setPos(target);
               IXplatAbstractions.INSTANCE.sendPacketToPlayer(var23, new MsgBlinkS2C(delta));
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

   private data class Spell(teleportee: Entity, delta: Vec3) : RenderedSpell {
      public final val teleportee: Entity
      public final val delta: Vec3

      init {
         this.teleportee = teleportee;
         this.delta = delta;
      }

      public override fun cast(env: CastingEnvironment) {
         val distance: Double = this.delta.length();
         val var10000: OpTeleport = OpTeleport.INSTANCE;
         val var10001: Entity = this.teleportee;
         val var10002: Vec3 = this.delta;
         val var10003: ServerLevel = env.getWorld();
         var10000.teleportRespectSticky(var10001, var10002, var10003);
         if (this.teleportee is ServerPlayer && this.teleportee == env.getCaster()) {
            val baseDropChance: Double = distance / 10000.0;
            val bindingCurse: Reference = this.teleportee.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.BINDING_CURSE);
            val var15: java.util.Iterator = (this.teleportee as ServerPlayer).getInventory().armor.iterator();
            var var7: java.util.Iterator = var15;

            while (var7.hasNext()) {
               val armorItem: ItemStack = var7.next() as ItemStack;
               if (EnchantmentHelper.getItemEnchantmentLevel(bindingCurse as Holder, armorItem) <= 0 && Math.random() < baseDropChance * 0.25) {
                  (this.teleportee as ServerPlayer).drop(armorItem.copy(), true, false);
                  armorItem.shrink(armorItem.getCount());
               }
            }

            var7 = ((this.teleportee as ServerPlayer).getInventory().items as java.lang.Iterable).iterator();
            var var14: Int = 0;

            while (var7.hasNext()) {
               val pos: Int = var14++;
               val invItem: ItemStack = var7.next() as ItemStack;
               if (!(invItem == (this.teleportee as ServerPlayer).getMainHandItem()) && Math.random() < (if (pos < 9) baseDropChance * 0.5 else baseDropChance)
                  )
                {
                  (this.teleportee as ServerPlayer).drop(invItem.copy(), true, false);
                  invItem.shrink(invItem.getCount());
               }
            }
         }
      }

      public operator fun component1(): Entity {
         return this.teleportee;
      }

      public operator fun component2(): Vec3 {
         return this.delta;
      }

      public fun copy(teleportee: Entity = this.teleportee, delta: Vec3 = this.delta): at.petrak.hexcasting.common.casting.actions.spells.great.OpTeleport.Spell {
         return new OpTeleport.Spell(teleportee, delta);
      }

      public override fun toString(): String {
         return "Spell(teleportee=${this.teleportee}, delta=${this.delta})";
      }

      public override fun hashCode(): Int {
         return this.teleportee.hashCode() * 31 + this.delta.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OpTeleport.Spell) {
            return false;
         } else {
            val var2: OpTeleport.Spell = other as OpTeleport.Spell;
            if (!(this.teleportee == (other as OpTeleport.Spell).teleportee)) {
               return false;
            } else {
               return this.delta == var2.delta;
            }
         }
      }

      override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
         return RenderedSpell.DefaultImpls.cast(this, env, image);
      }
   }
}
