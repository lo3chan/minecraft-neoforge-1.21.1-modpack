package net.bettercombat.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Excluded;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData;

@Config(
   name = "bettercombat"
)
public class ServerConfigWrapper extends GlobalData {
   @Category("server")
   @Excluded
   public ServerConfig server = new ServerConfig();
}
