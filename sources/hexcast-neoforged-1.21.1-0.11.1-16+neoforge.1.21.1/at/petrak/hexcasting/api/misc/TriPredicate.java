package at.petrak.hexcasting.api.misc;

@FunctionalInterface
public interface TriPredicate<A, B, C> {
   boolean test(A var1, B var2, C var3);
}
