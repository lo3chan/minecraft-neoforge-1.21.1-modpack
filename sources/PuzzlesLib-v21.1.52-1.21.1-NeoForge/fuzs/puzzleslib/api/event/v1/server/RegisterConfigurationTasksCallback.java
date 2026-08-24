package fuzs.puzzleslib.api.event.v1.server;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

@FunctionalInterface
public interface RegisterConfigurationTasksCallback {
   EventInvoker<RegisterConfigurationTasksCallback> EVENT = EventInvoker.lookup(RegisterConfigurationTasksCallback.class);

   void onRegisterConfigurationTasks(MinecraftServer var1, ServerConfigurationPacketListenerImpl var2, Consumer<ConfigurationTask> var3);
}
