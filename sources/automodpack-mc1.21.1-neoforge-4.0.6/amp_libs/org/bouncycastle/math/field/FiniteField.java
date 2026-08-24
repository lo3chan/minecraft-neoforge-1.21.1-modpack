package amp_libs.org.bouncycastle.math.field;

import java.math.BigInteger;

public interface FiniteField {
   BigInteger getCharacteristic();

   int getDimension();
}
