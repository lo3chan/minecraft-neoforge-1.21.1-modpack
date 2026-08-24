package dh_sqlite;

public interface SQLiteCommitListener {
   void onCommit();

   void onRollback();
}
