package at.petrak.hexcasting.api.casting.eval.sideeffects

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.Mishap.Context
import at.petrak.hexcasting.api.mod.HexStatistics
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.HexUtils
import at.petrak.hexcasting.common.lib.HexItems
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.Util
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.DyeColor
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

public sealed class OperatorSideEffect protected constructor() {
   public abstract fun performEffect(harness: CastingVM): Boolean {
   }

   @SourceDebugExtension(["SMAP\nOperatorSideEffect.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OperatorSideEffect.kt\nat/petrak/hexcasting/api/casting/eval/sideeffects/OperatorSideEffect$AttemptSpell\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,82:1\n1#2:83\n*E\n"])
   public data class AttemptSpell(spell: RenderedSpell, hasCastingSound: Boolean = true, awardStat: Boolean = true) : OperatorSideEffect() {
      public final val spell: RenderedSpell
      public final val hasCastingSound: Boolean
      public final val awardStat: Boolean

      init {
         this.spell = spell;
         this.hasCastingSound = hasCastingSound;
         this.awardStat = awardStat;
      }

      public override fun performEffect(harness: CastingVM): Boolean {
         val var10000: CastingImage = this.spell.cast(harness.getEnv(), harness.getImage());
         if (var10000 != null) {
            harness.setImage(var10000);
         }

         if (this.awardStat) {
            val var4: ServerPlayer = harness.getEnv().getCaster();
            if (var4 != null) {
               var4.awardStat(HexStatistics.SPELLS_CAST);
            }
         }

         return false;
      }

      public operator fun component1(): RenderedSpell {
         return this.spell;
      }

      public operator fun component2(): Boolean {
         return this.hasCastingSound;
      }

      public operator fun component3(): Boolean {
         return this.awardStat;
      }

      public fun copy(spell: RenderedSpell = this.spell, hasCastingSound: Boolean = this.hasCastingSound, awardStat: Boolean = this.awardStat): at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.AttemptSpell {
         return new OperatorSideEffect.AttemptSpell(spell, hasCastingSound, awardStat);
      }

      public override fun toString(): String {
         return "AttemptSpell(spell=${this.spell}, hasCastingSound=${this.hasCastingSound}, awardStat=${this.awardStat})";
      }

      public override fun hashCode(): Int {
         return (this.spell.hashCode() * 31 + java.lang.Boolean.hashCode(this.hasCastingSound)) * 31 + java.lang.Boolean.hashCode(this.awardStat);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OperatorSideEffect.AttemptSpell) {
            return false;
         } else {
            val var2: OperatorSideEffect.AttemptSpell = other as OperatorSideEffect.AttemptSpell;
            if (!(this.spell == (other as OperatorSideEffect.AttemptSpell).spell)) {
               return false;
            } else if (this.hasCastingSound != var2.hasCastingSound) {
               return false;
            } else {
               return this.awardStat == var2.awardStat;
            }
         }
      }
   }

   public data class ConsumeMedia(amount: Long) : OperatorSideEffect() {
      public final val amount: Long

      init {
         this.amount = amount;
      }

      public override fun performEffect(harness: CastingVM): Boolean {
         return harness.getEnv().extractMedia(this.amount) > 0L;
      }

      public operator fun component1(): Long {
         return this.amount;
      }

      public fun copy(amount: Long = this.amount): at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.ConsumeMedia {
         return new OperatorSideEffect.ConsumeMedia(amount);
      }

      public override fun toString(): String {
         return "ConsumeMedia(amount=${this.amount})";
      }

      public override fun hashCode(): Int {
         return java.lang.Long.hashCode(this.amount);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OperatorSideEffect.ConsumeMedia) {
            return false;
         } else {
            return this.amount == (other as OperatorSideEffect.ConsumeMedia).amount;
         }
      }
   }

   public data class DoMishap(mishap: Mishap, errorCtx: Context) : OperatorSideEffect() {
      public final val mishap: Mishap
      public final val errorCtx: Context

      init {
         this.mishap = mishap;
         this.errorCtx = errorCtx;
      }

      public override fun performEffect(harness: CastingVM): Boolean {
         val spray: ParticleSpray = this.mishap.particleSpray(harness.getEnv());
         val color: FrozenPigment = this.mishap.accentColor(harness.getEnv(), this.errorCtx);
         var var10001: ServerLevel = harness.getEnv().getWorld();
         spray.sprayParticles(var10001, color);
         var10001 = harness.getEnv().getWorld();
         val var10006: Any = HexItems.DYE_PIGMENTS.get(DyeColor.RED);
         spray.sprayParticles(var10001, new FrozenPigment(new ItemStack(var10006 as ItemLike), Util.NIL_UUID));
         harness.setImage(
            CastingImage.copy$default(
               harness.getImage(),
               this.mishap.executeReturnStack(harness.getEnv(), this.errorCtx, CollectionsKt.toMutableList(harness.getImage().getStack())),
               0,
               null,
               false,
               0L,
               null,
               62,
               null
            )
         );
         return true;
      }

      public operator fun component1(): Mishap {
         return this.mishap;
      }

      public operator fun component2(): Context {
         return this.errorCtx;
      }

      public fun copy(mishap: Mishap = this.mishap, errorCtx: Context = this.errorCtx): at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.DoMishap {
         return new OperatorSideEffect.DoMishap(mishap, errorCtx);
      }

      public override fun toString(): String {
         return "DoMishap(mishap=${this.mishap}, errorCtx=${this.errorCtx})";
      }

      public override fun hashCode(): Int {
         return this.mishap.hashCode() * 31 + this.errorCtx.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OperatorSideEffect.DoMishap) {
            return false;
         } else {
            val var2: OperatorSideEffect.DoMishap = other as OperatorSideEffect.DoMishap;
            if (!(this.mishap == (other as OperatorSideEffect.DoMishap).mishap)) {
               return false;
            } else {
               return this.errorCtx == var2.errorCtx;
            }
         }
      }
   }

   public data class Particles(spray: ParticleSpray) : OperatorSideEffect() {
      public final val spray: ParticleSpray

      init {
         this.spray = spray;
      }

      public override fun performEffect(harness: CastingVM): Boolean {
         harness.getEnv().produceParticles(this.spray, harness.getEnv().getPigment());
         return false;
      }

      public operator fun component1(): ParticleSpray {
         return this.spray;
      }

      public fun copy(spray: ParticleSpray = this.spray): at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.Particles {
         return new OperatorSideEffect.Particles(spray);
      }

      public override fun toString(): String {
         return "Particles(spray=${this.spray})";
      }

      public override fun hashCode(): Int {
         return this.spray.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OperatorSideEffect.Particles) {
            return false;
         } else {
            return this.spray == (other as OperatorSideEffect.Particles).spray;
         }
      }
   }

   public data class RequiredEnlightenment(awardStat: Boolean) : OperatorSideEffect() {
      public final val awardStat: Boolean

      init {
         this.awardStat = awardStat;
      }

      public override fun performEffect(harness: CastingVM): Boolean {
         val var10000: ServerPlayer = harness.getEnv().getCaster();
         if (var10000 != null) {
            var10000.sendSystemMessage(HexUtils.getAsTranslatedComponent("hexcasting.message.cant_great_spell") as Component);
         }

         return true;
      }

      public operator fun component1(): Boolean {
         return this.awardStat;
      }

      public fun copy(awardStat: Boolean = this.awardStat): at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.RequiredEnlightenment {
         return new OperatorSideEffect.RequiredEnlightenment(awardStat);
      }

      public override fun toString(): String {
         return "RequiredEnlightenment(awardStat=${this.awardStat})";
      }

      public override fun hashCode(): Int {
         return java.lang.Boolean.hashCode(this.awardStat);
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is OperatorSideEffect.RequiredEnlightenment) {
            return false;
         } else {
            return this.awardStat == (other as OperatorSideEffect.RequiredEnlightenment).awardStat;
         }
      }
   }
}
