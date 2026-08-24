package fuzs.puzzleslib.impl.attachment;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface AttachmentTypeAdapter<T, V> {
   ResourceLocation id();

   boolean hasData(T var1);

   @Nullable
   V getData(T var1);

   @Nullable
   V setData(T var1, V var2);

   @Nullable
   V removeData(T var1);
}
