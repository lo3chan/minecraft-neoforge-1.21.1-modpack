package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.GLFWInputWrapper;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;

public class KeybindRegistryKubeEvent implements ClientKubeEvent {
   private final List<KeybindRegistryKubeEvent.Builder> builders = new ArrayList<>();

   public KeybindRegistryKubeEvent.Builder register(String id) {
      KeybindRegistryKubeEvent.Builder builder = new KeybindRegistryKubeEvent.Builder(id);
      this.builders.add(builder);
      return builder;
   }

   public KeybindRegistryKubeEvent.Builder register(String id, String defaultKey) {
      return this.register(id).defaultKey(defaultKey);
   }

   @HideFromJS
   public List<KubeJSKeybinds.KubeKey> build() {
      return this.builders.stream().map(KeybindRegistryKubeEvent.Builder::create).toList();
   }

   public static class Builder {
      private final String id;
      private KeyConflictContext keyConflictContext = KeyConflictContext.UNIVERSAL;
      private KeyModifier modifier = KeyModifier.NONE;
      private Type inputType = Type.KEYSYM;
      private int defaultKey = -1;
      private String category = "key.categories.kubejs";

      private Builder(String id) {
         this.id = id;
      }

      public KeybindRegistryKubeEvent.Builder conflictContext(KeyConflictContext keyConflictContext) {
         this.keyConflictContext = keyConflictContext;
         return this;
      }

      public KeybindRegistryKubeEvent.Builder gui() {
         return this.conflictContext(KeyConflictContext.GUI);
      }

      public KeybindRegistryKubeEvent.Builder inGame() {
         return this.conflictContext(KeyConflictContext.IN_GAME);
      }

      public KeybindRegistryKubeEvent.Builder modifier(KeyModifier modifier) {
         this.modifier = modifier;
         return this;
      }

      public KeybindRegistryKubeEvent.Builder inputType(Type inputType) {
         this.inputType = inputType;
         return this;
      }

      public KeybindRegistryKubeEvent.Builder scanCodeInputType() {
         return this.inputType(Type.SCANCODE);
      }

      public KeybindRegistryKubeEvent.Builder mouseInputType() {
         return this.inputType(Type.MOUSE);
      }

      public KeybindRegistryKubeEvent.Builder defaultKey(String keyName) {
         this.defaultKey = GLFWInputWrapper.get(keyName);
         return this;
      }

      public KeybindRegistryKubeEvent.Builder category(String category) {
         this.category = category;
         return this;
      }

      @HideFromJS
      public KubeJSKeybinds.KubeKey create() {
         KubeJSKeybinds.KubeKey key = KubeJSKeybinds.getOrCreate(this.id);
         key.mapping = new KeyMapping(
            "key.kubejs.%s".formatted(this.id), this.keyConflictContext, this.modifier, this.inputType, this.defaultKey, this.category
         );
         return key;
      }
   }
}
