package at.petrak.hexcasting.common.lib.hex;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.arithmetic.engine.ArithmeticEngine;
import at.petrak.hexcasting.common.casting.arithmetic.BitwiseSetArithmetic;
import at.petrak.hexcasting.common.casting.arithmetic.BoolArithmetic;
import at.petrak.hexcasting.common.casting.arithmetic.DoubleArithmetic;
import at.petrak.hexcasting.common.casting.arithmetic.ListArithmetic;
import at.petrak.hexcasting.common.casting.arithmetic.ListSetArithmetic;
import at.petrak.hexcasting.common.casting.arithmetic.Vec3Arithmetic;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.Holder.Reference;
import net.minecraft.resources.ResourceLocation;

public class HexArithmetics {
   private static ArithmeticEngine ENGINE;
   public static final Registry<Arithmetic> REGISTRY = IXplatAbstractions.INSTANCE.getArithmeticRegistry();
   private static final Map<ResourceLocation, Arithmetic> ARITHMETICS = new LinkedHashMap<>();
   public static DoubleArithmetic DOUBLE = make("double", DoubleArithmetic.INSTANCE);
   public static Vec3Arithmetic VEC3 = make("vec3", Vec3Arithmetic.INSTANCE);
   public static ListArithmetic LIST = make("list", ListArithmetic.INSTANCE);
   public static BoolArithmetic BOOL = make("bool", BoolArithmetic.INSTANCE);
   public static ListSetArithmetic LIST_SET = make("list_set", ListSetArithmetic.INSTANCE);
   public static BitwiseSetArithmetic BITWISE_SET = make("bitwise_set", BitwiseSetArithmetic.INSTANCE);

   public static ArithmeticEngine getEngine() {
      if (ENGINE == null) {
         ENGINE = new ArithmeticEngine(REGISTRY.holders().<Arithmetic>map(Reference::value).collect(Collectors.toList()));
      }

      return ENGINE;
   }

   public static void register(BiConsumer<Arithmetic, ResourceLocation> r) {
      for (Entry<ResourceLocation, Arithmetic> e : ARITHMETICS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <T extends Arithmetic> T make(String name, T arithmetic) {
      Arithmetic old = ARITHMETICS.put(HexAPI.modLoc(name), arithmetic);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return arithmetic;
      }
   }
}
