package at.petrak.hexcasting.common.casting.arithmetic.operator.vec;

import at.petrak.hexcasting.api.casting.arithmetic.operator.OperatorBasic;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaMultiPredicate;
import at.petrak.hexcasting.api.casting.arithmetic.predicates.IotaPredicate;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class OperatorUnpack extends OperatorBasic {
   public static OperatorUnpack INSTANCE = new OperatorUnpack();

   private OperatorUnpack() {
      super(1, IotaMultiPredicate.all(IotaPredicate.ofType(HexIotaTypes.VEC3)));
   }

   @NotNull
   @Override
   public Iterable<Iota> apply(Iterable<? extends Iota> iotas, @NotNull CastingEnvironment env) {
      Iterator<? extends Iota> it = iotas.iterator();
      Vec3 vec = downcast(it.next(), HexIotaTypes.VEC3).getVec3();
      return List.of(new DoubleIota(vec.x), new DoubleIota(vec.y), new DoubleIota(vec.z));
   }
}
