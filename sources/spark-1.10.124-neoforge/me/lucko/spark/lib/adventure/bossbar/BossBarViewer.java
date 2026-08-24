package me.lucko.spark.lib.adventure.bossbar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface BossBarViewer {
   @NotNull
   @UnmodifiableView
   Iterable<? extends BossBar> activeBossBars();
}
