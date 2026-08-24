package jeresources.platform;

import java.util.List;
import net.minecraft.server.packs.PackResources;

public interface IModInfo {
   String getName();

   List<? extends PackResources> getPackResources();
}
