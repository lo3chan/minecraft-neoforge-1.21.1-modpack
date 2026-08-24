package dh_sqlite.core;

import java.sql.SQLException;

public class SafeStmtPtr {
   private final DB db;
   private final long ptr;
   private volatile boolean closed = false;
   private int closedRC;
   private SQLException closeException;

   public SafeStmtPtr(DB db, long ptr) {
      this.db = db;
      this.ptr = ptr;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public int close() throws SQLException {
      synchronized (this.db) {
         return this.internalClose();
      }
   }

   private int internalClose() throws SQLException {
      int ex;
      try {
         if (!this.closed) {
            this.closedRC = this.db.finalize(this, this.ptr);
            return this.closedRC;
         }

         if (this.closeException != null) {
            throw this.closeException;
         }

         ex = this.closedRC;
      } catch (SQLException var5) {
         this.closeException = var5;
         throw var5;
      } finally {
         this.closed = true;
      }

      return ex;
   }

   public <E extends Throwable> int safeRunInt(SafeStmtPtr.SafePtrIntFunction<E> run) throws SQLException, E {
      synchronized (this.db) {
         this.ensureOpen();
         return run.run(this.db, this.ptr);
      }
   }

   public <E extends Throwable> long safeRunLong(SafeStmtPtr.SafePtrLongFunction<E> run) throws SQLException, E {
      synchronized (this.db) {
         this.ensureOpen();
         return run.run(this.db, this.ptr);
      }
   }

   public <E extends Throwable> double safeRunDouble(SafeStmtPtr.SafePtrDoubleFunction<E> run) throws SQLException, E {
      synchronized (this.db) {
         this.ensureOpen();
         return run.run(this.db, this.ptr);
      }
   }

   public <T, E extends Throwable> T safeRun(SafeStmtPtr.SafePtrFunction<T, E> run) throws SQLException, E {
      synchronized (this.db) {
         this.ensureOpen();
         return run.run(this.db, this.ptr);
      }
   }

   public <E extends Throwable> void safeRunConsume(SafeStmtPtr.SafePtrConsumer<E> run) throws SQLException, E {
      synchronized (this.db) {
         this.ensureOpen();
         run.run(this.db, this.ptr);
      }
   }

   private void ensureOpen() throws SQLException {
      if (this.closed) {
         throw new SQLException("stmt pointer is closed");
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SafeStmtPtr that = (SafeStmtPtr)o;
         return this.ptr == that.ptr;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Long.hashCode(this.ptr);
   }

   @FunctionalInterface
   public interface SafePtrConsumer<E extends Throwable> {
      void run(DB dB, long l) throws E;
   }

   @FunctionalInterface
   public interface SafePtrDoubleFunction<E extends Throwable> {
      double run(DB dB, long l) throws E;
   }

   @FunctionalInterface
   public interface SafePtrFunction<T, E extends Throwable> {
      T run(DB dB, long l) throws E;
   }

   @FunctionalInterface
   public interface SafePtrIntFunction<E extends Throwable> {
      int run(DB dB, long l) throws E;
   }

   @FunctionalInterface
   public interface SafePtrLongFunction<E extends Throwable> {
      long run(DB dB, long l) throws E;
   }
}
