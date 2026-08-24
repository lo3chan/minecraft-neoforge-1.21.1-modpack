package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.GameRulesFactory;
import java.util.function.BiConsumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameRules.BooleanValue;
import net.minecraft.world.level.GameRules.Category;
import net.minecraft.world.level.GameRules.IntegerValue;
import net.minecraft.world.level.GameRules.Key;
import net.minecraft.world.level.GameRules.Type;
import net.minecraft.world.level.GameRules.Value;

public final class NeoForgeGameRulesFactory implements GameRulesFactory {
   @Override
   public <T extends Value<T>> Key<T> register(String name, Category category, Type<T> type) {
      return GameRules.register(name, category, type);
   }

   @Override
   public Type<BooleanValue> createBooleanRule(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> callback) {
      return BooleanValue.create(defaultValue, callback);
   }

   @Override
   public Type<IntegerValue> createIntRule(int defaultValue, int minimumValue, int maximumValue, BiConsumer<MinecraftServer, IntegerValue> callback) {
      return IntegerValue.create(defaultValue, callback);
   }
}
