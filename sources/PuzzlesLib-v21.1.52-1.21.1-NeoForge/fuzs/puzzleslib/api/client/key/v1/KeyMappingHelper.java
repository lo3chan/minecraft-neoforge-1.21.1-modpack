package fuzs.puzzleslib.api.client.key.v1;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import fuzs.puzzleslib.impl.client.core.proxy.ClientProxyImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public interface KeyMappingHelper {
   KeyMappingHelper INSTANCE = ClientProxyImpl.get().getKeyMappingActivationHelper();

   KeyActivationContext getKeyActivationContext(KeyMapping var1);

   default boolean isConflictingWith(KeyMapping keyMapping, KeyMapping otherKeyMapping) {
      return this.getKeyActivationContext(keyMapping).isConflictingWith(this.getKeyActivationContext(otherKeyMapping));
   }

   static KeyMapping registerUnboundKeyMapping(ResourceLocation resourceLocation) {
      return registerKeyMapping(resourceLocation, InputConstants.UNKNOWN.getValue());
   }

   static KeyMapping registerKeyMapping(ResourceLocation resourceLocation, int keyCode) {
      return new KeyMapping("key." + resourceLocation.getPath(), keyCode, "key.categories." + resourceLocation.getNamespace());
   }

   static boolean isKeyActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
      return ClientProxyImpl.get().isKeyActiveAndMatches(keyMapping, keyCode, scanCode);
   }

   static boolean matchesCodePoint(KeyMapping keyMapping, int codePoint) {
      if (keyMapping.key.getType() == Type.KEYSYM && !keyMapping.isUnbound()) {
         String string = new String(Character.toChars(codePoint));
         String keyName = GLFW.glfwGetKeyName(keyMapping.key.getValue(), -1);
         return keyName != null && keyName.equalsIgnoreCase(string);
      } else {
         return false;
      }
   }
}
