package dhcomgithubluben.zstd;

public interface SequenceProducer {
   long getFunctionPointer();

   long createState();

   void freeState(long l);
}
