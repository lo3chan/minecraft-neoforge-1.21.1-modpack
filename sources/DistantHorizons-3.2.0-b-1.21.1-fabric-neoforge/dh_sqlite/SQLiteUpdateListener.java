package dh_sqlite;

public interface SQLiteUpdateListener {
   void onUpdate(SQLiteUpdateListener.Type type, String string, String string2, long l);

   public static enum Type {
      INSERT,
      DELETE,
      UPDATE;
   }
}
