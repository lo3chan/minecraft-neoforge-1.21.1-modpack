package software.bernie.geckolib.network.packet;

import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface MultiloaderPacket extends CustomPacketPayload {
   void receiveMessage(@Nullable Player var1, Consumer<Runnable> var2);
}
