package amp_libs.org.bouncycastle.pqc.crypto.sphincsplus;

interface SPHINCSPlusEngineProvider {
   int getN();

   SPHINCSPlusEngine get();
}
