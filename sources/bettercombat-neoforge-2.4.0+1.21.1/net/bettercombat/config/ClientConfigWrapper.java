package net.bettercombat.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Category;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.TransitiveObject;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer.GlobalData;

@Config(
   name = "bettercombat"
)
public class ClientConfigWrapper extends GlobalData {
   @Category("client")
   @TransitiveObject
   public ClientConfig client = new ClientConfig();
}
