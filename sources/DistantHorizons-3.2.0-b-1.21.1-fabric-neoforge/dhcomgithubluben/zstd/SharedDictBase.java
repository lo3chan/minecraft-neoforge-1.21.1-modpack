package dhcomgithubluben.zstd;

abstract class SharedDictBase extends AutoCloseBase {
   @Override
   protected void finalize() {
      this.close();
   }
}
