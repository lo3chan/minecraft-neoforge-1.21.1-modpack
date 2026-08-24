package snownee.jade.api.ui;

public enum MessageType {
   NORMAL,
   INFO,
   TITLE,
   SUCCESS,
   WARNING,
   DANGER,
   FAILURE;

   public static MessageType parse(String s) {
      try {
         return valueOf(s);
      } catch (Exception var2) {
         return NORMAL;
      }
   }
}
