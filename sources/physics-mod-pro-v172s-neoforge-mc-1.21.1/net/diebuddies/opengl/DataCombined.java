package net.diebuddies.opengl;

import java.util.Arrays;

public class DataCombined {
   public Data[] values;

   public DataCombined(Data... values) {
      this.values = values;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      return 31 * result + Arrays.hashCode((Object[])this.values);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         DataCombined other = (DataCombined)obj;
         return Arrays.equals((Object[])this.values, (Object[])other.values);
      }
   }
}
