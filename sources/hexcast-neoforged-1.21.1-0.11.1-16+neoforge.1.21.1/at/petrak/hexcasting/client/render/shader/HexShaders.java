package at.petrak.hexcasting.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.util.function.Consumer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;

public class HexShaders {
   private static ShaderInstance grayscale;

   public static void init(ResourceProvider resourceProvider, Consumer<Pair<ShaderInstance, Consumer<ShaderInstance>>> registrations) throws IOException {
      registrations.accept(
         Pair.of(
            new ShaderInstance(resourceProvider, "hexcasting__grayscale", DefaultVertexFormat.NEW_ENTITY), (Consumer<ShaderInstance>)inst -> grayscale = inst
         )
      );
   }

   public static ShaderInstance grayscale() {
      return grayscale;
   }
}
