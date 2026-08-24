package net.raphimc.immediatelyfast.compat;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.function.BooleanSupplier;
import net.lenni0451.reflect.accessor.FieldAccessor;
import net.lenni0451.reflect.stream.RStream;
import net.raphimc.immediatelyfast.ImmediatelyFast;

public class IrisCompat {
   public static boolean IRIS_LOADED = false;
   public static BooleanSupplier isRenderingLevel;
   public static BooleanConsumer renderWithExtendedVertexFormat;
   public static ThreadLocal<Boolean> skipExtension;

   public static void init() {
      IRIS_LOADED = true;

      try {
         Class<?> immediateStateClass = Class.forName("net.irisshaders.iris.vertices.ImmediateState");
         isRenderingLevel = (BooleanSupplier)FieldAccessor.makeGetter(BooleanSupplier.class, null, immediateStateClass.getDeclaredField("isRenderingLevel"));
         renderWithExtendedVertexFormat = (BooleanConsumer)FieldAccessor.makeSetter(
            BooleanConsumer.class, null, immediateStateClass.getDeclaredField("renderWithExtendedVertexFormat")
         );
         skipExtension = (ThreadLocal<Boolean>)RStream.of(immediateStateClass).fields().by("skipExtension").get();
      } catch (Throwable var1) {
         ImmediatelyFast.LOGGER.error("Failed to initialize Iris compatibility. Try updating Iris and ImmediatelyFast before reporting this on GitHub", var1);
         System.exit(-1);
      }
   }
}
