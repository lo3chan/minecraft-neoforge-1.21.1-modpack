package net.bettercombat.api.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.world.entity.player.Player;

public class AttackRangeExtensions {
   private static final ArrayList<Function<AttackRangeExtensions.Context, AttackRangeExtensions.Modifier>> sources = new ArrayList<>();

   public static void register(Function<AttackRangeExtensions.Context, AttackRangeExtensions.Modifier> source) {
      sources.add(source);
   }

   public static List<Function<AttackRangeExtensions.Context, AttackRangeExtensions.Modifier>> sources() {
      return sources;
   }

   public record Context(Player player, double attackRange) {
   }

   public record Modifier(double value, AttackRangeExtensions.Operation operation) {
      public int operationOrder() {
         return this.operation.order;
      }
   }

   public static enum Operation {
      ADD(0),
      MULTIPLY(1);

      public final int order;

      private Operation(int order) {
         this.order = order;
      }

      public int getOrder() {
         return this.order;
      }
   }
}
