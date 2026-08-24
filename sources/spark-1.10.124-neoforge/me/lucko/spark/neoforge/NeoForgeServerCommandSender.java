package me.lucko.spark.neoforge;

import java.util.Objects;
import java.util.UUID;
import me.lucko.spark.common.command.sender.AbstractCommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import me.lucko.spark.neoforge.plugin.NeoForgeServerSparkPlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.world.entity.Entity;

public class NeoForgeServerCommandSender extends AbstractCommandSender<CommandSourceStack> {
   private final NeoForgeServerSparkPlugin plugin;

   public NeoForgeServerCommandSender(CommandSourceStack commandSource, NeoForgeServerSparkPlugin plugin) {
      super(commandSource);
      this.plugin = plugin;
   }

   @Override
   public String getName() {
      String name = this.delegate.getTextName();
      return this.delegate.getEntity() != null && name.equals("Server") ? "Console" : name;
   }

   @Override
   public UUID getUniqueId() {
      Entity entity = this.delegate.getEntity();
      return entity != null ? entity.getUUID() : null;
   }

   @Override
   public void sendMessage(Component message) {
      MutableComponent component = Serializer.fromJson(GsonComponentSerializer.gson().serializeToTree(message), RegistryAccess.EMPTY);
      Objects.requireNonNull(component, "component");
      ((CommandSourceStack)super.delegate).sendSystemMessage(component);
   }

   @Override
   public boolean hasPermission(String permission) {
      return this.plugin.hasPermission(this.delegate, permission);
   }

   @Override
   protected Object getObjectForComparison() {
      UUID uniqueId = this.getUniqueId();
      if (uniqueId != null) {
         return uniqueId;
      } else {
         Entity entity = this.delegate.getEntity();
         return entity != null ? entity : this.getName();
      }
   }
}
