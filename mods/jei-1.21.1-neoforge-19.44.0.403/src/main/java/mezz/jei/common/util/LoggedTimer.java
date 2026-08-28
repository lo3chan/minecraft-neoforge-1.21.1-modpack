/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package mezz.jei.common.util;

import com.google.common.base.Stopwatch;
import mezz.jei.common.util.TimeUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggedTimer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch stopWatch = Stopwatch.createUnstarted();
    private String message = "";

    public void start(String message) {
        this.message = message;
        LOGGER.info("{}...", (Object)message);
        this.stopWatch.reset();
        this.stopWatch.start();
    }

    public void stop() {
        this.stopWatch.stop();
        LOGGER.info("{} took {}", (Object)this.message, (Object)TimeUtil.toHumanString(this.stopWatch.elapsed()));
    }
}

