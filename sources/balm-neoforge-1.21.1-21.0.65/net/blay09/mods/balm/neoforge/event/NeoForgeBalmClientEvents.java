package net.blay09.mods.balm.neoforge.event;

import com.mojang.blaze3d.platform.Window;
import net.blay09.mods.balm.api.event.TickPhase;
import net.blay09.mods.balm.api.event.TickType;
import net.blay09.mods.balm.api.event.client.BlockHighlightDrawEvent;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.balm.api.event.client.DisconnectedFromServerEvent;
import net.blay09.mods.balm.api.event.client.FovUpdateEvent;
import net.blay09.mods.balm.api.event.client.GuiDrawEvent;
import net.blay09.mods.balm.api.event.client.ItemTooltipEvent;
import net.blay09.mods.balm.api.event.client.KeyInputEvent;
import net.blay09.mods.balm.api.event.client.OpenScreenEvent;
import net.blay09.mods.balm.api.event.client.RecipesUpdatedEvent;
import net.blay09.mods.balm.api.event.client.RenderHandEvent;
import net.blay09.mods.balm.api.event.client.UseItemInputEvent;
import net.blay09.mods.balm.api.event.client.screen.ContainerScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenInitEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenKeyEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Post.Result;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

public class NeoForgeBalmClientEvents {
   public static void registerEvents(NeoForgeBalmEvents events) {
      events.registerTickEvent(TickType.Client, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(Minecraft.getInstance())));
      events.registerTickEvent(TickType.Client, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> handler.handle(Minecraft.getInstance())));
      events.registerTickEvent(TickType.ClientLevel, TickPhase.Start, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getLevel() instanceof ClientLevel clientLevel) {
            handler.handle(clientLevel);
         }
      }));
      events.registerTickEvent(TickType.ClientLevel, TickPhase.End, handler -> NeoForge.EVENT_BUS.addListener(orig -> {
         if (orig.getLevel() instanceof ClientLevel clientLevel) {
            handler.handle(clientLevel);
         }
      }));
      events.registerEvent(ConnectedToServerEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ConnectedToServerEvent event = new ConnectedToServerEvent(Minecraft.getInstance());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(DisconnectedFromServerEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         DisconnectedFromServerEvent event = new DisconnectedFromServerEvent(Minecraft.getInstance());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ScreenInitEvent.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenInitEvent.Pre event = new ScreenInitEvent.Pre(orig.getScreen());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenInitEvent.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenInitEvent.Post event = new ScreenInitEvent.Post(orig.getScreen());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(
         ScreenDrawEvent.Pre.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ScreenDrawEvent.Pre event = new ScreenDrawEvent.Pre(
                     orig.getScreen(), orig.getGuiGraphics(), orig.getMouseX(), orig.getMouseY(), orig.getPartialTick()
                  );
                  events.fireEventHandlers(priority, event);
                  if (event.isCanceled()) {
                     orig.setCanceled(true);
                  }
               }
            )
      );
      events.registerEvent(
         ContainerScreenDrawEvent.Background.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ContainerScreenDrawEvent.Background event = new ContainerScreenDrawEvent.Background(
                     orig.getContainerScreen(), orig.getGuiGraphics(), orig.getMouseX(), orig.getMouseY()
                  );
                  events.fireEventHandlers(priority, event);
               }
            )
      );
      events.registerEvent(
         ContainerScreenDrawEvent.Foreground.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ContainerScreenDrawEvent.Foreground event = new ContainerScreenDrawEvent.Foreground(
                     orig.getContainerScreen(), orig.getGuiGraphics(), orig.getMouseX(), orig.getMouseY()
                  );
                  events.fireEventHandlers(priority, event);
               }
            )
      );
      events.registerEvent(
         ScreenDrawEvent.Post.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ScreenDrawEvent.Post event = new ScreenDrawEvent.Post(
                     orig.getScreen(), orig.getGuiGraphics(), orig.getMouseX(), orig.getMouseY(), orig.getPartialTick()
                  );
                  events.fireEventHandlers(priority, event);
               }
            )
      );
      events.registerEvent(ScreenMouseEvent.Click.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenMouseEvent.Click.Pre event = new ScreenMouseEvent.Click.Pre(orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getButton());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenMouseEvent.Click.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenMouseEvent.Click.Post event = new ScreenMouseEvent.Click.Post(orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getButton());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setResult(Result.FORCE_HANDLED);
         }
      }));
      events.registerEvent(
         ScreenMouseEvent.Drag.Pre.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ScreenMouseEvent.Drag.Pre event = new ScreenMouseEvent.Drag.Pre(
                     orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getMouseButton(), orig.getDragX(), orig.getDragY()
                  );
                  events.fireEventHandlers(priority, event);
                  if (event.isCanceled()) {
                     orig.setCanceled(true);
                  }
               }
            )
      );
      events.registerEvent(
         ScreenMouseEvent.Drag.Post.class,
         priority -> NeoForge.EVENT_BUS
            .addListener(
               NeoForgeBalmEvents.toForge(priority),
               orig -> {
                  ScreenMouseEvent.Drag.Post event = new ScreenMouseEvent.Drag.Post(
                     orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getMouseButton(), orig.getDragX(), orig.getDragY()
                  );
                  events.fireEventHandlers(priority, event);
               }
            )
      );
      events.registerEvent(ScreenMouseEvent.Release.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenMouseEvent.Release.Pre event = new ScreenMouseEvent.Release.Pre(orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getButton());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenMouseEvent.Release.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenMouseEvent.Release.Post event = new ScreenMouseEvent.Release.Post(orig.getScreen(), orig.getMouseX(), orig.getMouseY(), orig.getButton());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setResult(net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Post.Result.FORCE_HANDLED);
         }
      }));
      events.registerEvent(ScreenKeyEvent.Press.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenKeyEvent.Press.Pre event = new ScreenKeyEvent.Press.Pre(orig.getScreen(), orig.getKeyCode(), orig.getScanCode(), orig.getModifiers());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenKeyEvent.Press.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenKeyEvent.Press.Post event = new ScreenKeyEvent.Press.Post(orig.getScreen(), orig.getKeyCode(), orig.getScanCode(), orig.getModifiers());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenKeyEvent.Release.Pre.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenKeyEvent.Release.Pre event = new ScreenKeyEvent.Release.Pre(orig.getScreen(), orig.getKeyCode(), orig.getScanCode(), orig.getModifiers());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(ScreenKeyEvent.Release.Post.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ScreenKeyEvent.Release.Post event = new ScreenKeyEvent.Release.Post(orig.getScreen(), orig.getKeyCode(), orig.getScanCode(), orig.getModifiers());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(FovUpdateEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         FovUpdateEvent event = new FovUpdateEvent(orig.getPlayer());
         events.fireEventHandlers(priority, event);
         if (event.getFov() != null) {
            orig.setNewFovModifier(event.getFov());
         }
      }));
      events.registerEvent(RecipesUpdatedEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
         RecipesUpdatedEvent event = new RecipesUpdatedEvent(orig.getRecipeManager(), registryAccess);
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(ItemTooltipEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         ItemTooltipEvent event = new ItemTooltipEvent(orig.getItemStack(), orig.getEntity(), orig.getToolTip(), orig.getFlags());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(UseItemInputEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         if (orig.isUseItem()) {
            UseItemInputEvent event = new UseItemInputEvent(orig.getHand());
            events.fireEventHandlers(priority, event);
            if (event.isCanceled()) {
               orig.setSwingHand(false);
               orig.setCanceled(true);
            }
         }
      }));
      events.registerEvent(RenderHandEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         RenderHandEvent event = new RenderHandEvent(orig.getHand(), orig.getItemStack(), orig.getSwingProgress());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(KeyInputEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         KeyInputEvent event = new KeyInputEvent(orig.getKey(), orig.getScanCode(), orig.getAction(), orig.getModifiers());
         events.fireEventHandlers(priority, event);
      }));
      events.registerEvent(BlockHighlightDrawEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         BlockHighlightDrawEvent event = new BlockHighlightDrawEvent(orig.getTarget(), orig.getPoseStack(), orig.getMultiBufferSource(), orig.getCamera());
         events.fireEventHandlers(priority, event);
         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(OpenScreenEvent.class, priority -> NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
         OpenScreenEvent event = new OpenScreenEvent(orig.getScreen(), orig.getNewScreen());
         events.fireEventHandlers(priority, event);
         if (event.getNewScreen() != null) {
            orig.setNewScreen(event.getNewScreen());
         }

         if (event.isCanceled()) {
            orig.setCanceled(true);
         }
      }));
      events.registerEvent(GuiDrawEvent.Pre.class, priority -> {
         NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
            Window window = Minecraft.getInstance().getWindow();
            GuiDrawEvent.Pre event = new GuiDrawEvent.Pre(window, orig.getGuiGraphics(), GuiDrawEvent.Element.ALL);
            events.fireEventHandlers(priority, event);
            if (event.isCanceled()) {
               orig.setCanceled(true);
            }
         });
         NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
            Window window = Minecraft.getInstance().getWindow();
            GuiDrawEvent.Element element = getGuiDrawEventElement(orig.getName());
            if (element != null) {
               GuiDrawEvent.Pre event = new GuiDrawEvent.Pre(window, orig.getGuiGraphics(), element);
               events.fireEventHandlers(priority, event);
               if (event.isCanceled()) {
                  orig.setCanceled(true);
               }
            }
         });
      });
      events.registerEvent(GuiDrawEvent.Post.class, priority -> {
         NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
            Window window = Minecraft.getInstance().getWindow();
            GuiDrawEvent.Post event = new GuiDrawEvent.Post(window, orig.getGuiGraphics(), GuiDrawEvent.Element.ALL);
            events.fireEventHandlers(priority, event);
         });
         NeoForge.EVENT_BUS.addListener(NeoForgeBalmEvents.toForge(priority), orig -> {
            Window window = Minecraft.getInstance().getWindow();
            GuiDrawEvent.Element element = getGuiDrawEventElement(orig.getName());
            if (element != null) {
               GuiDrawEvent.Post event = new GuiDrawEvent.Post(window, orig.getGuiGraphics(), element);
               events.fireEventHandlers(priority, event);
            }
         });
      });
   }

   @Nullable
   private static GuiDrawEvent.Element getGuiDrawEventElement(ResourceLocation id) {
      GuiDrawEvent.Element type = null;
      if (id.equals(VanillaGuiLayers.PLAYER_HEALTH)) {
         type = GuiDrawEvent.Element.HEALTH;
      } else if (id.equals(VanillaGuiLayers.CHAT)) {
         type = GuiDrawEvent.Element.CHAT;
      } else if (id.equals(VanillaGuiLayers.DEBUG_OVERLAY)) {
         type = GuiDrawEvent.Element.DEBUG;
      } else if (id.equals(VanillaGuiLayers.BOSS_OVERLAY)) {
         type = GuiDrawEvent.Element.BOSS_INFO;
      } else if (id.equals(VanillaGuiLayers.TAB_LIST)) {
         type = GuiDrawEvent.Element.PLAYER_LIST;
      }

      return type;
   }
}
