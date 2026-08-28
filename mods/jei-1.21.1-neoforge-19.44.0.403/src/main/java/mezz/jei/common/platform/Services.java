/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.platform;

import java.util.ServiceLoader;
import mezz.jei.common.platform.IPlatformHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Services {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final IPlatformHelper PLATFORM = Services.load(IPlatformHelper.class);

    public static <T> T load(Class<T> serviceClass) {
        T loadedService = ServiceLoader.load(serviceClass).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + serviceClass.getName()));
        LOGGER.debug("Loaded {} for service {}", loadedService, serviceClass);
        return loadedService;
    }
}

