package codx.codxlib.api.command;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface CodxCommandSource {
   void reply(Component var1);
}
