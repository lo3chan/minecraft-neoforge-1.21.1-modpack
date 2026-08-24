package net.raphimc.immediatelyfast.service;

import java.nio.file.Path;
import java.util.Optional;
import net.raphimc.immediatelyfast.util.ServiceUtil;

public interface PlatformService {
   PlatformService INSTANCE = ServiceUtil.load(PlatformService.class);

   Path getConfigDirectory();

   Optional<String> getModVersion(String var1);
}
