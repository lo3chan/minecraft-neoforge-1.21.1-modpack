package at.petrak.hexcasting.common.lib.hex;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

@ParametersAreNonnullByDefault
public class HexContinuationTypes {
   public static final Registry<ContinuationFrame.Type<?>> REGISTRY = IXplatAbstractions.INSTANCE.getContinuationTypeRegistry();
   public static final String KEY_TYPE = "hexcasting:type";
   public static final String KEY_DATA = "hexcasting:data";
   private static final Map<ResourceLocation, ContinuationFrame.Type<?>> CONTINUATIONS = new LinkedHashMap<>();
   public static final ContinuationFrame.Type<FrameEvaluate> EVALUATE = continuation("evaluate", FrameEvaluate.TYPE);
   public static final ContinuationFrame.Type<FrameForEach> FOREACH = continuation("foreach", FrameForEach.TYPE);
   public static final ContinuationFrame.Type<FrameFinishEval> END = continuation("end", FrameFinishEval.TYPE);

   public static void registerContinuations(BiConsumer<ContinuationFrame.Type<?>, ResourceLocation> r) {
      for (Entry<ResourceLocation, ContinuationFrame.Type<?>> e : CONTINUATIONS.entrySet()) {
         r.accept(e.getValue(), e.getKey());
      }
   }

   private static <U extends ContinuationFrame, T extends ContinuationFrame.Type<U>> T continuation(String name, T continuation) {
      ContinuationFrame.Type<?> old = CONTINUATIONS.put(HexAPI.modLoc(name), continuation);
      if (old != null) {
         throw new IllegalArgumentException("Typo? Duplicate id " + name);
      } else {
         return continuation;
      }
   }
}
