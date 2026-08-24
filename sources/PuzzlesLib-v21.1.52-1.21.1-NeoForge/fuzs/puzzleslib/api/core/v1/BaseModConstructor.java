package fuzs.puzzleslib.api.core.v1;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

@Deprecated
public interface BaseModConstructor {
   @Deprecated
   @Nullable
   default ResourceLocation getPairingIdentifier() {
      throw new UnsupportedOperationException();
   }

   default ContentRegistrationFlags[] getContentRegistrationFlags() {
      return new ContentRegistrationFlags[0];
   }
}
