/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.library.load;

import java.time.Duration;
import mezz.jei.common.util.TimeUtil;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PluginCallerTimerRunnable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final long startReportDurationMs = 10L;
    private static final long longReportDurationInterval = Duration.ofSeconds(5L).toMillis();
    private final String title;
    private final ResourceLocation pluginUid;
    private final long startTime;
    private long nextLongReportDurationMs = longReportDurationInterval;

    public PluginCallerTimerRunnable(String title, ResourceLocation pluginUid) {
        this.title = title;
        this.pluginUid = pluginUid;
        this.startTime = System.nanoTime();
        LOGGER.debug("{}: {}...", (Object)title, (Object)pluginUid);
    }

    public void check() {
        Duration elapsed = Duration.ofNanos(System.nanoTime() - this.startTime);
        long elapsedMs = elapsed.toMillis();
        if (elapsedMs > this.nextLongReportDurationMs) {
            LOGGER.error("{}: {} is running and has taken {} so far", (Object)this.title, (Object)this.pluginUid, (Object)TimeUtil.toHumanString(elapsed));
            this.nextLongReportDurationMs += longReportDurationInterval;
        }
    }

    public void stop() {
        Duration elapsed = Duration.ofNanos(System.nanoTime() - this.startTime);
        if (elapsed.toMillis() > 10L) {
            LOGGER.info("{}: {} took {}", (Object)this.title, (Object)this.pluginUid, (Object)TimeUtil.toHumanString(elapsed));
        }
    }
}

