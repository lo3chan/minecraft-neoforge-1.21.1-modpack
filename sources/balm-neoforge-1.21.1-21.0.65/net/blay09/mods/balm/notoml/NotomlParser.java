package net.blay09.mods.balm.notoml;

public class NotomlParser {
   public static Notoml parse(String input) {
      final Notoml result = new Notoml();
      NotomlStateMachine stateMachine = new NotomlStateMachine();
      NotomlParseBuffer parseBuffer = new NotomlParseBuffer(input);
      var tokenConsumer = new NotomlTokenConsumer() {
         @Override
         public void onPropertyParsed(String category, String property, Object value) {
            result.setProperty(category, property, value);
         }

         @Override
         public void onCommentParsed(String category, String property, String comment) {
            result.setComment(category, property, comment);
         }

         @Override
         public void onError(NotomlError error) {
            result.addError(error);
         }
      };

      while (parseBuffer.canRead() && stateMachine.next(parseBuffer, tokenConsumer)) {
      }

      return result;
   }
}
