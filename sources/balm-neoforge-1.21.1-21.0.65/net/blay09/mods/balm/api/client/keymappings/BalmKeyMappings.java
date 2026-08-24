package net.blay09.mods.balm.api.client.keymappings;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.Optional;
import net.minecraft.client.KeyMapping;

@Deprecated
public interface BalmKeyMappings {
   default KeyMapping registerKeyMapping(String name, int keyCode, String category) {
      return this.registerKeyMapping(name, Type.KEYSYM, keyCode, category);
   }

   KeyMapping registerKeyMapping(String var1, Type var2, int var3, String var4);

   @Deprecated
   KeyMapping registerKeyMapping(String var1, KeyConflictContext var2, KeyModifier var3, int var4, String var5);

   @Deprecated
   KeyMapping registerKeyMapping(String var1, KeyConflictContext var2, KeyModifiers var3, int var4, String var5);

   @Deprecated
   KeyMapping registerKeyMapping(String var1, KeyConflictContext var2, KeyModifier var3, Type var4, int var5, String var6);

   @Deprecated
   KeyMapping registerKeyMapping(String var1, KeyConflictContext var2, KeyModifiers var3, Type var4, int var5, String var6);

   @Deprecated
   default boolean isActiveAndMatches(KeyMapping keyMapping, int keyCode, int scanCode) {
      return this.isActiveAndMatches(keyMapping, InputConstants.getKey(keyCode, scanCode));
   }

   @Deprecated
   default boolean isActiveAndMatches(KeyMapping keyMapping, Type type, int keyCode, int scanCode) {
      return this.isActiveAndMatches(keyMapping, type.getOrCreate(type == Type.SCANCODE ? scanCode : keyCode));
   }

   @Deprecated
   boolean isActiveAndMatches(KeyMapping var1, Key var2);

   @Deprecated
   boolean isActiveAndWasPressed(KeyMapping var1);

   @Deprecated
   boolean isKeyDownIgnoreContext(KeyMapping var1);

   @Deprecated
   boolean isActiveAndKeyDown(KeyMapping var1);

   @Deprecated
   Optional<Boolean> conflictsWith(KeyMapping var1, KeyMapping var2);

   @Deprecated
   void ignoreConflicts(KeyMapping var1);

   @Deprecated
   boolean shouldIgnoreConflicts(KeyMapping var1);

   BalmKeyMappings scoped(String var1);
}
