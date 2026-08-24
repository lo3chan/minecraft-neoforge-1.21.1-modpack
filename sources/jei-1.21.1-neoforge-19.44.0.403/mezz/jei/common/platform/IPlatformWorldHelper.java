package mezz.jei.common.platform;

import java.util.Optional;
import net.minecraft.server.MinecraftServer;

public interface IPlatformWorldHelper {
   Optional<String> getLevelId(MinecraftServer var1);
}
