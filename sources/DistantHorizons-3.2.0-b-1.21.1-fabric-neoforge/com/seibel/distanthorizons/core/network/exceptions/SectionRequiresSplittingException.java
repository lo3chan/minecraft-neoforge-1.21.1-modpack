package com.seibel.distanthorizons.core.network.exceptions;

public class SectionRequiresSplittingException extends Exception {
   public SectionRequiresSplittingException() {
      this("Section requires splitting");
   }

   public SectionRequiresSplittingException(String message) {
      super(message);
   }
}
