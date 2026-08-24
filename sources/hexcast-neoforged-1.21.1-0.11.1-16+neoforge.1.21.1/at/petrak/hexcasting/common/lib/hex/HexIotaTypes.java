package at.petrak.hexcasting.common.lib.hex;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.ContinuationIota;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
public class HexIotaTypes {
   public static final Registry<IotaType<?>> REGISTRY = IXplatAbstractions.INSTANCE.getIotaTypeRegistry();
   public static final String KEY_TYPE = "hexcasting:type";
   public static final String KEY_DATA = "hexcasting:data";
   public static final int MAX_SERIALIZATION_DEPTH = 256;
   public static final int MAX_SERIALIZATION_TOTAL = 1024;
   private static final Map<ResourceLocation, IotaType<?>> TYPES = new LinkedHashMap<>();
   public static final IotaType<NullIota> NULL = type("null", NullIota.TYPE);
   public static final IotaType<DoubleIota> DOUBLE = type("double", DoubleIota.TYPE);
   public static final IotaType<BooleanIota> BOOLEAN = type("boolean", BooleanIota.TYPE);
   public static final IotaType<EntityIota> ENTITY = type("entity", EntityIota.TYPE);
   public static final IotaType<ListIota> LIST = type("list", ListIota.TYPE);
   public static final IotaType<PatternIota> PATTERN = type("pattern", PatternIota.TYPE);
   public static final IotaType<GarbageIota> GARBAGE = type("garbage", GarbageIota.TYPE);
   public static final IotaType<Vec3Iota> VEC3 = type("vec3", Vec3Iota.TYPE);
   public static final IotaType<ContinuationIota> CONTINUATION = type("continuation", ContinuationIota.TYPE);

   public static void registerTypes(BiConsumer<IotaType<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, IotaType<?>> e : TYPES.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <U extends Iota, T extends IotaType<U>> T type(String name, T type) {
      IotaType<?> old = TYPES.put(HexAPI.modLoc(name), type);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return type;
      }
   }
}
