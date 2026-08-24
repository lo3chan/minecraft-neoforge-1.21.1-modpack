package org.tukaani.xz.lzma;

final class Optimum {
   private static final int INFINITY_PRICE = 1073741824;
   final State state = new State();
   final int[] reps = new int[4];
   int price;
   int optPrev;
   int backPrev;
   boolean prev1IsLiteral;
   boolean hasPrev2;
   int optPrev2;
   int backPrev2;

   void reset() {
      this.price = 1073741824;
   }

   void set1(int i, int j, int k) {
      this.price = i;
      this.optPrev = j;
      this.backPrev = k;
      this.prev1IsLiteral = false;
   }

   void set2(int i, int j, int k) {
      this.price = i;
      this.optPrev = j + 1;
      this.backPrev = k;
      this.prev1IsLiteral = true;
      this.hasPrev2 = false;
   }

   void set3(int i, int j, int k, int l, int m) {
      this.price = i;
      this.optPrev = j + l + 1;
      this.backPrev = m;
      this.prev1IsLiteral = true;
      this.hasPrev2 = true;
      this.optPrev2 = j;
      this.backPrev2 = k;
   }
}
