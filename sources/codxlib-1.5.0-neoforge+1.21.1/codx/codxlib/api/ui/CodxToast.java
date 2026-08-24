package codx.codxlib.api.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.SystemToast.SystemToastId;
import net.minecraft.network.chat.Component;

public final class CodxToast {
   private static final Map<String, SystemToastId> TOKENS = new ConcurrentHashMap<>();

   private CodxToast() {
   }

   public static void show(Component title, Component description) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && title != null) {
         addToast(mc, null, title, (Component)(description == null ? Component.empty() : description));
      }
   }

   public static void show(String title, String description) {
      show(title == null ? null : Component.literal(title), description == null ? null : Component.literal(description));
   }

   public static void show(String key, Component title, Component description) {
      Minecraft mc = Minecraft.getInstance();
      if (mc != null && key != null && title != null) {
         addToast(mc, key, title, (Component)(description == null ? Component.empty() : description));
      }
   }

   private static void addToast(Minecraft mc, String key, Component title, Component description) {
      if (key == null) {
         SystemToast.add(mc.getToasts(), new SystemToastId(), title, description);
      } else {
         SystemToast.addOrUpdate(mc.getToasts(), TOKENS.computeIfAbsent(key, k -> new SystemToastId()), title, description);
      }
   }
}
