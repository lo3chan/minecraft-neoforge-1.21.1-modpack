package dev.isxander.yacl3.gui.controllers.string;

import dev.isxander.yacl3.api.Option;

public class StringController implements IStringController<String> {
   private final Option<String> option;

   public StringController(Option<String> option) {
      this.option = option;
   }

   @Override
   public Option<String> option() {
      return this.option;
   }

   @Override
   public String getString() {
      return this.option().pendingValue();
   }

   @Override
   public void setFromString(String value) {
      this.option().requestSet(value);
   }
}
