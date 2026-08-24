package at.petrak.hexcasting.api.casting.arithmetic.engine;

import at.petrak.hexcasting.api.casting.arithmetic.Arithmetic;
import at.petrak.hexcasting.api.casting.arithmetic.operator.Operator;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ArithmeticEngine {
   public final Arithmetic[] arithmetics;
   private final Map<HexPattern, ArithmeticEngine.OpCandidates> operators = new HashMap<>();
   private final Map<HashCons, Operator> cache = new HashMap<>();

   public ArithmeticEngine(List<Arithmetic> arithmetics) {
      this.arithmetics = arithmetics.toArray(new Arithmetic[0]);

      for (Arithmetic arith : arithmetics) {
         for (HexPattern op : arith.opTypes()) {
            this.operators.compute(op, ($, info) -> {
               Operator operator = arith.getOperator(op);
               if (info == null) {
                  info = new ArithmeticEngine.OpCandidates(op, operator.arity, new ArrayList<>());
               }

               info.addOp(operator);
               return (ArithmeticEngine.OpCandidates)info;
            });
         }
      }
   }

   public Iterable<HexPattern> operatorSyms() {
      return this.operators.keySet();
   }

   public OperationResult run(HexPattern pattern, CastingEnvironment env, CastingImage image, SpellContinuation continuation) throws Mishap {
      List<Iota> stackList = image.getStack();
      Stack<Iota> stack = new Stack<>();
      stack.addAll(stackList);
      int startingLength = stackList.size();
      ArithmeticEngine.OpCandidates candidates = this.operators.get(pattern);
      if (candidates == null) {
         throw new InvalidOperatorException("the pattern " + pattern + " is not an operator.");
      } else {
         HashCons hash = new HashCons.Pattern(pattern);
         ArrayList<Iota> args = new ArrayList<>(candidates.arity());

         for (int i = 0; i < candidates.arity(); i++) {
            if (stack.isEmpty()) {
               throw new MishapNotEnoughArgs(candidates.arity, startingLength);
            }

            Iota iota = stack.pop();
            hash = new HashCons.Pair(iota.getType(), hash);
            args.add(iota);
         }

         Collections.reverse(args);
         Operator op = this.resolveCandidates(args, hash, candidates);
         return op.operate(env, image, continuation);
      }
   }

   private Operator resolveCandidates(List<Iota> args, HashCons hash, ArithmeticEngine.OpCandidates candidates) {
      return this.cache
         .computeIfAbsent(
            hash,
            $ -> {
               for (Operator op : candidates.operators()) {
                  if (op.accepts.test(args)) {
                     return op;
                  }
               }

               throw new NoOperatorCandidatesException(
                  candidates.pattern(), args, "No implementation candidates for op " + candidates.pattern() + " on args: " + args
               );
            }
         );
   }

   private record OpCandidates(HexPattern pattern, int arity, List<Operator> operators) {
      public void addOp(Operator next) {
         if (next.arity != this.arity) {
            throw new IllegalArgumentException(
               "Operators exist of differing arity! The pattern "
                  + this.pattern
                  + " already had arity "
                  + this.arity
                  + " when the operator with arity "
                  + next.arity
                  + ", "
                  + next
                  + " was added."
            );
         } else {
            this.operators.add(next);
         }
      }
   }
}
