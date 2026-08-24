package net.joefoxe.hexerei.util;

import net.joefoxe.hexerei.command.CofferListCommand;
import net.joefoxe.hexerei.command.ToggleBookShadersCommand;
import net.joefoxe.hexerei.data.books.BookReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(
   modid = "hexerei"
)
public class ServerProxy implements SidedProxy {
   @Override
   public Player getPlayer() {
      return null;
   }

   @Override
   public Level getLevel() {
      return null;
   }

   @Override
   public void init() {
   }

   @Override
   public void openCodexGui() {
   }

   @SubscribeEvent
   public static void onAddReloadListeners(AddReloadListenerEvent event) {
      event.addListener(new BookReloadListener());
   }

   @SubscribeEvent
   public static void commandRegister(RegisterCommandsEvent event) {
      CofferListCommand.register(event.getDispatcher());
      ToggleBookShadersCommand.register(event.getDispatcher());
   }
}
