package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.misc.AMCompat;
import com.mojang.serialization.MapCodec;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class AMIconSpecialRenderer {
   private static final AMItemstackRenderer RENDERER = new AMItemstackRenderer();
   private static final Map<String, ItemStack> CANONICAL_ARGS = new ConcurrentHashMap<>();
   private static final Set<Object> CANONICAL_IDENTITIES = Collections.synchronizedSet(Collections.newSetFromMap(new IdentityHashMap<>()));
   private static final int CANONICAL_CAP = 512;
   private static final ArrayDeque<ItemDisplayContext> CONTEXTS = new ArrayDeque<>();

   public ItemStack extractArgument(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         String key = BuiltInRegistries.ITEM.getKey(stack.getItem()) + "|" + AMCompat.getTag(stack);
         ItemStack canonical = CANONICAL_ARGS.get(key);
         if (canonical == null) {
            if (CANONICAL_ARGS.size() >= 512) {
               CANONICAL_ARGS.clear();
               CANONICAL_IDENTITIES.clear();
            }

            canonical = stack.copy();
            CANONICAL_ARGS.put(key, canonical);
            CANONICAL_IDENTITIES.add(canonical);
         }

         return canonical;
      } else {
         return stack;
      }
   }

   public static boolean isCanonicalArgument(Object element) {
      return element instanceof ItemStack && CANONICAL_IDENTITIES.contains(element);
   }

   public static void pushDisplayContext(ItemDisplayContext context) {
      if (CONTEXTS.size() > 32) {
         CONTEXTS.clear();
      }

      CONTEXTS.push(context == null ? ItemDisplayContext.GUI : context);
   }

   public static void popDisplayContext() {
      CONTEXTS.poll();
   }

   private static ItemDisplayContext currentDisplayContext() {
      ItemDisplayContext context = CONTEXTS.peek();
      return context == null ? ItemDisplayContext.GUI : context;
   }

   private static void addExtents(Consumer<Vector3f> out) {
      for (int x = 0; x <= 1; x++) {
         for (int y = 0; y <= 1; y++) {
            for (int z = 0; z <= 1; z++) {
               out.accept(new Vector3f(x, y, z));
            }
         }
      }
   }

   public static final class Unbaked {
      public static final MapCodec<AMIconSpecialRenderer.Unbaked> MAP_CODEC = MapCodec.unit(new AMIconSpecialRenderer.Unbaked());

      public MapCodec<AMIconSpecialRenderer.Unbaked> type() {
         return MAP_CODEC;
      }
   }
}
