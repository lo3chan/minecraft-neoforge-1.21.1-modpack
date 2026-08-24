package vazkii.psi.data;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.ICondition.IContext;
import org.jetbrains.annotations.NotNull;
import vazkii.psi.common.Psi;

public final class MagicalPsiCondition implements ICondition {
   public static final MagicalPsiCondition INSTANCE = new MagicalPsiCondition();
   public static final MapCodec<MagicalPsiCondition> CODEC = MapCodec.unit(INSTANCE).stable();

   private MagicalPsiCondition() {
   }

   public boolean test(IContext condition) {
      return Psi.magical;
   }

   @NotNull
   public MapCodec<? extends ICondition> codec() {
      return CODEC;
   }

   @Override
   public String toString() {
      return "magipsi_enabled";
   }
}
