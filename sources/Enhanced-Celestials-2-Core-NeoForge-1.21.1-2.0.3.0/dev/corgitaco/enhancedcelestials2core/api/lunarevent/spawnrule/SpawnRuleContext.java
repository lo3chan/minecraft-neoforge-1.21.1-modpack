package dev.corgitaco.enhancedcelestials2core.api.lunarevent.spawnrule;

public record SpawnRuleContext(long day, long dayLength, int moonPhase, long lastDayOfThisEvent, long lastScheduledEventDay) {
}
