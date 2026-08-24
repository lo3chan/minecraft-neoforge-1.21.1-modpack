package dhcomgithubluben.zstd;

public class ZstdException extends RuntimeException {
   private long code;

   public ZstdException(long l) {
      this(Zstd.getErrorCode(l), Zstd.getErrorName(l));
   }

   public ZstdException(long l, String string) {
      super(string);
      this.code = l;
   }

   public long getErrorCode() {
      return this.code;
   }
}
