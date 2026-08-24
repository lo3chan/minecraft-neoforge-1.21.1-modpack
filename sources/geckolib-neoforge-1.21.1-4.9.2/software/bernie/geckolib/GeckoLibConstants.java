package software.bernie.geckolib;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class GeckoLibConstants {
   public static final Logger LOGGER = LogManager.getLogger("GeckoLib");
   public static final String MODID = "geckolib";
   public static final Supplier<DataComponentType<Long>> STACK_ANIMATABLE_ID_COMPONENT = GeckoLibServices.PLATFORM
      .registerDataComponent("stack_animatable_id", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

   public static void init() {
   }

   public static ResourceLocation id(String path) {
      return ResourceLocation.fromNamespaceAndPath("geckolib", path);
   }

   public static RuntimeException exception(ResourceLocation resource, String message) {
      return new RuntimeException(resource + ": " + message);
   }

   public static RuntimeException exception(ResourceLocation resource, String message, Throwable exception) {
      return new RuntimeException(resource + ": " + message, exception);
   }
}
