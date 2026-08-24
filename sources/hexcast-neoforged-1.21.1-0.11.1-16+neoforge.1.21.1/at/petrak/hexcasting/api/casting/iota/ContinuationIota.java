package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

public class ContinuationIota extends Iota {
   public static final Component DISPLAY = Component.translatable("hexcasting.tooltip.jump_iota").withStyle(ChatFormatting.RED);
   public static IotaType<ContinuationIota> TYPE = new IotaType<ContinuationIota>() {
      @NotNull
      public ContinuationIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
         CompoundTag compoundTag = HexUtils.downcast(tag, CompoundTag.TYPE);
         return new ContinuationIota(SpellContinuation.fromNBT(compoundTag, world));
      }

      @Override
      public Component display(Tag tag) {
         return ContinuationIota.DISPLAY;
      }

      @Override
      public int color() {
         return -3407872;
      }
   };

   public ContinuationIota(SpellContinuation cont) {
      super(HexIotaTypes.CONTINUATION, cont);
   }

   public SpellContinuation getContinuation() {
      return (SpellContinuation)this.payload;
   }

   @Override
   public boolean isTruthy() {
      return true;
   }

   @Override
   public boolean toleratesOther(Iota that) {
      return typesMatch(this, that) && that instanceof ContinuationIota cont && cont.getContinuation().equals(this.getContinuation());
   }

   @NotNull
   @Override
   public Tag serialize() {
      return this.getContinuation().serializeToNBT();
   }

   @NotNull
   @Override
   public CastResult execute(CastingVM vm, ServerLevel world, SpellContinuation continuation) {
      return new CastResult(this, this.getContinuation(), vm.getImage(), List.of(), ResolvedPatternType.EVALUATED, HexEvalSounds.HERMES);
   }

   @Override
   public boolean executable() {
      return true;
   }

   @Override
   public int size() {
      SpellContinuation continuation = this.getContinuation();
      int size = 0;

      while (continuation instanceof SpellContinuation.NotDone) {
         SpellContinuation.NotDone notDone = (SpellContinuation.NotDone)continuation;
         size = ++size + notDone.component1().size();
         continuation = notDone.component2();
      }

      return Math.min(size, 1);
   }
}
