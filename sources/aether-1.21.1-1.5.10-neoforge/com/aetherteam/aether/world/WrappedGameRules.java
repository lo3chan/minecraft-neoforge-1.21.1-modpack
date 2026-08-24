package com.aetherteam.aether.world;

import java.util.Set;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Value;

public class WrappedGameRules extends GameRules {
   private final GameRules gameRules;
   private final Set<Key<BooleanValue>> blacklist;

   public WrappedGameRules(GameRules gameRules, Set<Key<BooleanValue>> keys) {
      this.gameRules = gameRules;
      this.blacklist = keys;
   }

   public <T extends Value<T>> T getRule(Key<T> key) {
      return (T)this.gameRules.getRule(key);
   }

   public boolean getBoolean(Key<BooleanValue> key) {
      return !this.blacklist.contains(key) && ((BooleanValue)this.getRule(key)).get();
   }
}
