package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.plugin.builtin.event.KeyBindEvents;
import dev.latvian.mods.kubejs.plugin.builtin.wrapper.StringUtilsWrapper;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.type.TypeInfo;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class KubeJSKeybinds {
   private static final Map<String, KubeJSKeybinds.KubeKey> REGISTERED = new LinkedHashMap<>();
   public static final EventTargetType<KubeJSKeybinds.KubeKey> TARGET = EventTargetType.create(KubeJSKeybinds.KubeKey.class)
      .identity()
      .transformer(KubeJSKeybinds::get0)
      .describeType(TypeInfo.STRING);

   public static void triggerReload() {
      for (KubeJSKeybinds.KubeKey key : REGISTERED.values()) {
         key.shouldTick = false;
      }

      KeyBindEvents.TICK.forEachListener(ScriptType.CLIENT, container -> ((KubeJSKeybinds.KubeKey)container.target).shouldTick = true);
   }

   public static void triggerKeyEvents(Minecraft client) {
      for (KubeJSKeybinds.KubeKey key : REGISTERED.values()) {
         if (key.mapping != null) {
            if (client.kjs$isKeyMappingDown(key.mapping)) {
               if (!key.down) {
                  key.down = true;
                  KeyBindEvents.PRESSED.post(ScriptType.CLIENT, key, new KubeJSKeybinds.KeyEvent(client.player, key));
               }

               if (key.shouldTick) {
                  KeyBindEvents.TICK.post(ScriptType.CLIENT, key, new KubeJSKeybinds.TickingKeyEvent(client.player, key));
               }

               key.ticksPressed++;
            } else if (key.down) {
               key.down = false;
               KeyBindEvents.RELEASED.post(ScriptType.CLIENT, key, new KubeJSKeybinds.TickingKeyEvent(client.player, key));
               key.ticksPressed = 0;
            }
         }
      }
   }

   @Nullable
   private static KubeJSKeybinds.KubeKey get0(Object o) {
      return o == null ? null : getOrCreate(o.toString());
   }

   @Nullable
   public static KubeJSKeybinds.KubeKey get(String id) {
      return REGISTERED.get(id);
   }

   public static KubeJSKeybinds.KubeKey getOrCreate(String id) {
      return REGISTERED.computeIfAbsent(id, KubeJSKeybinds.KubeKey::new);
   }

   public static void generateLang(LangKubeEvent event) {
      for (KubeJSKeybinds.KubeKey key : REGISTERED.values()) {
         event.add("kubejs", "key.kubejs." + key.id, StringUtilsWrapper.toTitleCase(key.id));
      }
   }

   public static class KeyEvent extends ClientPlayerKubeEvent {
      protected final KubeJSKeybinds.KubeKey key;

      public KeyEvent(LocalPlayer player, KubeJSKeybinds.KubeKey key) {
         super(player);
         this.key = key;
      }
   }

   public static class KubeKey {
      public final String id;
      public transient KeyMapping mapping;
      public transient boolean down = false;
      private boolean shouldTick = false;
      public transient int ticksPressed = 0;

      public KubeKey(String id) {
         this.id = id;
      }

      @Override
      public boolean equals(Object obj) {
         return this == obj || obj instanceof KubeJSKeybinds.KubeKey o && Objects.equals(this.id, o.id);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(this.id);
      }
   }

   public static class TickingKeyEvent extends KubeJSKeybinds.KeyEvent {
      public TickingKeyEvent(LocalPlayer player, KubeJSKeybinds.KubeKey key) {
         super(player, key);
      }

      public int getTicks() {
         return this.key.ticksPressed;
      }
   }
}
