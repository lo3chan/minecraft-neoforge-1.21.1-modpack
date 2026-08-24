package at.petrak.hexcasting.api.casting.iota;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnescapedValue;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.List;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Iota {
   @NotNull
   protected final Object payload;
   @NotNull
   protected final IotaType<?> type;

   protected Iota(@NotNull IotaType<?> type, @NotNull Object payload) {
      this.type = type;
      this.payload = payload;
   }

   @NotNull
   public IotaType<?> getType() {
      return this.type;
   }

   public abstract boolean isTruthy();

   protected abstract boolean toleratesOther(Iota var1);

   @NotNull
   public abstract Tag serialize();

   @NotNull
   public CastResult execute(CastingVM vm, ServerLevel world, SpellContinuation continuation) {
      return new CastResult(
         this,
         continuation,
         null,
         List.of(new OperatorSideEffect.DoMishap(new MishapUnescapedValue(this), new Mishap.Context(new HexPattern(HexDir.WEST, List.of()), null))),
         ResolvedPatternType.INVALID,
         HexEvalSounds.MISHAP
      );
   }

   public boolean executable() {
      return false;
   }

   @Nullable
   public Iterable<Iota> subIotas() {
      return null;
   }

   public int size() {
      return 1;
   }

   public Component display() {
      return this.type.display(this.serialize());
   }

   public static boolean typesMatch(Iota a, Iota b) {
      ResourceLocation resA = HexIotaTypes.REGISTRY.getKey(a.getType());
      ResourceLocation resB = HexIotaTypes.REGISTRY.getKey(b.getType());
      return resA != null && resA.equals(resB);
   }

   public static boolean tolerates(Iota a, Iota b) {
      return a.toleratesOther(b) || b.toleratesOther(a);
   }

   @Override
   public int hashCode() {
      return this.payload.hashCode();
   }
}
