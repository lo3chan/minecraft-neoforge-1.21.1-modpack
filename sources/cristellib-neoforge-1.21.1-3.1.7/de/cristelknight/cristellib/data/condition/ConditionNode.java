package de.cristelknight.cristellib.data.condition;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;

public record ConditionNode(Either<List<ICondition<?>>, ICondition<?>> either) {
   public static final Codec<Either<List<ICondition<?>>, ICondition<?>>> EITHER_CODEC = Codec.either(Codec.list(ICondition.FULL_CODEC), ICondition.FULL_CODEC);
   public static final Codec<ConditionNode> CODEC = EITHER_CODEC.xmap(ConditionNode::new, conditionNode -> conditionNode.either);

   public ConditionNode(ICondition<?> condition) {
      this(Either.right(condition));
   }

   public ConditionNode(List<ICondition<?>> conditions) {
      this(Either.left(conditions));
   }

   public static boolean testConditionNode(Optional<ConditionNode> condition) {
      return condition.isEmpty() || condition.get().test();
   }

   public boolean test() {
      if (this.either.left().isPresent()) {
         return this.evaluateConditions((List<ICondition<?>>)this.either.left().get());
      } else {
         return this.either.right().isPresent() ? this.evaluateConditions(List.of((ICondition<?>)this.either.right().get())) : false;
      }
   }

   private boolean evaluateConditions(List<ICondition<?>> jsonElements) {
      for (ICondition<?> e : jsonElements) {
         if (!e.test()) {
            return false;
         }
      }

      return true;
   }
}
