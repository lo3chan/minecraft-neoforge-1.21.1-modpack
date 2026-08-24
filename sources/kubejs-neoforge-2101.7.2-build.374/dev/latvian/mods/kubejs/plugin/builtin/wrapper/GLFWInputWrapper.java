package dev.latvian.mods.kubejs.plugin.builtin.wrapper;

import dev.latvian.mods.kubejs.util.Lazy;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.lwjgl.glfw.GLFW;

public interface GLFWInputWrapper {
   Lazy<Map<String, Integer>> MAP = Lazy.map(map -> {
      try {
         for (Field field : GLFW.class.getDeclaredFields()) {
            int mod = field.getModifiers();
            if (field.getType() == int.class && Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
               String n = field.getName();
               if (n.startsWith("GLFW_KEY_") || n.startsWith("GLFW_MOUSE_") || n.startsWith("GLFW_GAMEPAD_") || n.startsWith("GLFW_CURSOR_")) {
                  map.put(n.substring(5), field.getInt(null));
               }
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   });

   static int get(String name) {
      return MAP.get().getOrDefault(name, -1);
   }
}
