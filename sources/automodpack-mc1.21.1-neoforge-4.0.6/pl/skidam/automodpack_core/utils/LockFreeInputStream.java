package pl.skidam.automodpack_core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class LockFreeInputStream extends InputStream {
   private final InputStream delegate;

   public LockFreeInputStream(Path path) throws IOException {
      if (PlatformUtils.IS_WIN && CustomFileUtils.isFilePhysical(path)) {
         RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r");
         this.delegate = Channels.newInputStream(raf.getChannel());
      } else {
         this.delegate = Files.newInputStream(path, StandardOpenOption.READ);
      }
   }

   public static ReadableByteChannel openChannel(Path path) throws IOException {
      return (ReadableByteChannel)(PlatformUtils.IS_WIN && CustomFileUtils.isFilePhysical(path)
         ? new RandomAccessFile(path.toFile(), "r").getChannel()
         : Files.newByteChannel(path, StandardOpenOption.READ));
   }

   @Override
   public int read() throws IOException {
      return this.delegate.read();
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException {
      return this.delegate.read(b, off, len);
   }

   @Override
   public int available() throws IOException {
      return this.delegate.available();
   }

   @Override
   public void close() throws IOException {
      this.delegate.close();
   }

   @Override
   public synchronized void mark(int readlimit) {
      this.delegate.mark(readlimit);
   }

   @Override
   public synchronized void reset() throws IOException {
      this.delegate.reset();
   }

   @Override
   public boolean markSupported() {
      return this.delegate.markSupported();
   }
}
