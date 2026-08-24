package dhcomgithubluben.zstd;

final class Objects {
   static void checkFromIndexSize(int i, int j, int k) {
      if ((k | i | j) < 0 || j > k - i) {
         throw new IndexOutOfBoundsException(String.format("Range [%s, %<s + %s) out of bounds for length %s", i, j, k));
      }
   }
}
