package at.petrak.hexcasting.api.casting.eval.vm

import at.petrak.hexcasting.api.utils.NBTBuilder
import at.petrak.hexcasting.api.utils.NBTHelper
import at.petrak.hexcasting.api.utils.NbtCompoundBuilder
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel

public sealed interface SpellContinuation {
   public open fun pushFrame(frame: ContinuationFrame): SpellContinuation {
   }

   public open fun serializeToNBT(): CompoundTag {
   }

   public open fun getNBTFrames(): List<CompoundTag> {
   }

   public companion object {
      public const val TAG_FRAME: String = "frame"

      public fun fromNBT(nbt: CompoundTag, world: ServerLevel): SpellContinuation {
         val frames: ListTag = NBTHelper.getListByByte(nbt, "frame", (byte)10);
         var result: SpellContinuation = SpellContinuation.Done.INSTANCE;

         for (Tag frame : CollectionsKt.asReversedMutable((java.util.List)frames)) {
            if (frame is CompoundTag) {
               result = result.pushFrame(ContinuationFrame.Companion.fromNBT(frame as CompoundTag, world));
            }
         }

         return result;
      }
   }

   // $VF: Class flags could not be determined
   @SourceDebugExtension(["SMAP\nSpellContinuation.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpellContinuation.kt\nat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation$DefaultImpls\n+ 2 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NBTBuilder\n+ 3 NBTDsl.kt\nat/petrak/hexcasting/api/utils/NbtCompoundBuilder\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,47:1\n14#2,8:48\n105#3:56\n63#3,2:58\n1#4:57\n*S KotlinDebug\n*F\n+ 1 SpellContinuation.kt\nat/petrak/hexcasting/api/casting/eval/vm/SpellContinuation$DefaultImpls\n*L\n19#1:48,8\n20#1:56\n20#1:58,2\n20#1:57\n*E\n"])
   internal class DefaultImpls {
      @JvmStatic
      fun pushFrame(`$this`: SpellContinuation, frame: ContinuationFrame): SpellContinuation {
         return new SpellContinuation.NotDone(frame, `$this`);
      }

      @JvmStatic
      fun serializeToNBT(`$this`: SpellContinuation): CompoundTag {
         val `this_$iv`: NBTBuilder = NBTBuilder.INSTANCE;
         val var5: CompoundTag = NbtCompoundBuilder.constructor-impl(new CompoundTag());
         val `nbt$iv`: java.util.Collection = `$this`.getNBTFrames();
         val var11: ListTag = new ListTag();
         var11.addAll(`nbt$iv`);
         var5.put("frame", var11 as Tag);
         return var5;
      }

      @JvmStatic
      fun getNBTFrames(`$this`: SpellContinuation): MutableList<CompoundTag> {
         var self: SpellContinuation = `$this`;

         val frames: java.util.List;
         for (frames = new ArrayList(); self instanceof SpellContinuation.NotDone; self = ((SpellContinuation.NotDone)self).getNext()) {
            frames.add(ContinuationFrame.Companion.toNBT((self as SpellContinuation.NotDone).getFrame()));
         }

         return frames;
      }
   }

   public object Done : SpellContinuation {
      override fun pushFrame(frame: ContinuationFrame): SpellContinuation {
         return SpellContinuation.DefaultImpls.pushFrame(this, frame);
      }

      override fun serializeToNBT(): CompoundTag {
         return SpellContinuation.DefaultImpls.serializeToNBT(this);
      }

      override fun getNBTFrames(): MutableList<CompoundTag> {
         return SpellContinuation.DefaultImpls.getNBTFrames(this);
      }
   }

   public data class NotDone(frame: ContinuationFrame, next: SpellContinuation) : SpellContinuation {
      public final val frame: ContinuationFrame
      public final val next: SpellContinuation

      init {
         this.frame = frame;
         this.next = next;
      }

      public operator fun component1(): ContinuationFrame {
         return this.frame;
      }

      public operator fun component2(): SpellContinuation {
         return this.next;
      }

      public fun copy(frame: ContinuationFrame = this.frame, next: SpellContinuation = this.next): at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation.NotDone {
         return new SpellContinuation.NotDone(frame, next);
      }

      public override fun toString(): String {
         return "NotDone(frame=${this.frame}, next=${this.next})";
      }

      public override fun hashCode(): Int {
         return this.frame.hashCode() * 31 + this.next.hashCode();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is SpellContinuation.NotDone) {
            return false;
         } else {
            val var2: SpellContinuation.NotDone = other as SpellContinuation.NotDone;
            if (!(this.frame == (other as SpellContinuation.NotDone).frame)) {
               return false;
            } else {
               return this.next == var2.next;
            }
         }
      }

      override fun pushFrame(frame: ContinuationFrame): SpellContinuation {
         return SpellContinuation.DefaultImpls.pushFrame(this, frame);
      }

      override fun serializeToNBT(): CompoundTag {
         return SpellContinuation.DefaultImpls.serializeToNBT(this);
      }

      override fun getNBTFrames(): MutableList<CompoundTag> {
         return SpellContinuation.DefaultImpls.getNBTFrames(this);
      }
   }
}
