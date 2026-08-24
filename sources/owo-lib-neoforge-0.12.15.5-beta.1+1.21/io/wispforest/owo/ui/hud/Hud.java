package io.wispforest.owo.ui.hud;

import com.mojang.blaze3d.platform.Window;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.OwoUIAdapter;
import io.wispforest.owo.ui.event.ClientRenderCallback;
import io.wispforest.owo.ui.event.WindowResizeCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class Hud {
   @Nullable
   static OwoUIAdapter<FlowLayout> adapter = null;
   static boolean suppress = false;
   private static final Map<ResourceLocation, Component> activeComponents = new HashMap<>();
   private static final List<Consumer<FlowLayout>> pendingActions = new ArrayList<>();

   public static void add(ResourceLocation id, Supplier<Component> component) {
      pendingActions.add(flowLayout -> {
         Component instance = component.get();
         flowLayout.child(instance);
         activeComponents.put(id, instance);
      });
   }

   public static void remove(ResourceLocation id) {
      pendingActions.add(flowLayout -> {
         Component component = activeComponents.get(id);
         if (component != null) {
            flowLayout.removeChild(component);
            activeComponents.remove(id);
         }
      });
   }

   @Nullable
   public static Component getComponent(ResourceLocation id) {
      return activeComponents.get(id);
   }

   public static boolean hasComponent(ResourceLocation id) {
      return activeComponents.containsKey(id);
   }

   private static void initializeAdapter() {
      Window window = Minecraft.getInstance().getWindow();
      adapter = OwoUIAdapter.createWithoutScreen(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), HudContainer::new);
      adapter.inflateAndMount();
   }

   static {
      WindowResizeCallback.EVENT.register((WindowResizeCallback)(client, window) -> {
         if (adapter != null) {
            adapter.moveAndResize(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight());
         }
      });
      ClientRenderCallback.BEFORE.register((ClientRenderCallback)client -> {
         if (client.level != null) {
            if (!pendingActions.isEmpty()) {
               if (adapter == null) {
                  initializeAdapter();
               }

               pendingActions.forEach(action -> action.accept(adapter.rootComponent));
               pendingActions.clear();
            }
         }
      });
      NeoForge.EVENT_BUS.addListener(event -> {
         GuiGraphics context = event.getGuiGraphics();
         DeltaTracker tickDelta = event.getPartialTick();
         if (adapter != null && !suppress && !Minecraft.getInstance().options.hideGui) {
            context.push().translate(0.0F, 0.0F, 100.0F);
            adapter.render(context, -69, -69, tickDelta.getGameTimeDeltaPartialTick(false));
            context.pop();
         }
      });
   }
}
