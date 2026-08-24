package dhcomgithubluben.zstd;

public enum EndDirective {
   CONTINUE(0),
   FLUSH(1),
   END(2);

   private final int value;

   private EndDirective(int j) {
      this.value = j;
   }

   int value() {
      return this.value;
   }
}
