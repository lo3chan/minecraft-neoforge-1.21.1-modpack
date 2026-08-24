package me.lucko.spark.neoforge;

import java.util.Objects;
import java.util.UUID;
import me.lucko.spark.common.command.sender.AbstractCommandSender;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;

public class NeoForgeClientCommandSender extends AbstractCommandSender<CommandSourceStack> {
   public NeoForgeClientCommandSender(CommandSourceStack source) {
      super(source);
   }

   @Override
   public String getName() {
      return this.delegate.getTextName();
   }

   @Override
   public UUID getUniqueId() {
      return this.delegate.getEntity() instanceof LocalPlayer player ? player.getUUID() : null;
   }

   @Override
   public void sendMessage(Component message) {
      MutableComponent component = Serializer.fromJson(GsonComponentSerializer.gson().serializeToTree(message), RegistryAccess.EMPTY);
      Objects.requireNonNull(component, "component");
      ((CommandSourceStack)super.delegate).sendSystemMessage(component);
   }

   @Override
   public boolean hasPermission(String permission) {
      return true;
   }

   @Override
   protected Object getObjectForComparison() {
      return this.delegate.getEntity();
   }
}
