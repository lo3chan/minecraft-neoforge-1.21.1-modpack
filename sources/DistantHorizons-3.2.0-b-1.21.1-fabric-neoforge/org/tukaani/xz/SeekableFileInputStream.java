package org.tukaani.xz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SeekableFileInputStream extends SeekableInputStream {
   protected RandomAccessFile randomAccessFile;

   public SeekableFileInputStream(File file) throws FileNotFoundException {
      this.randomAccessFile = new RandomAccessFile(file, "r");
   }

   public SeekableFileInputStream(String string) throws FileNotFoundException {
      this.randomAccessFile = new RandomAccessFile(string, "r");
   }

   public SeekableFileInputStream(RandomAccessFile randomAccessFile) {
      this.randomAccessFile = randomAccessFile;
   }

   @Override
   public int read() throws IOException {
      return this.randomAccessFile.read();
   }

   @Override
   public int read(byte[] bs) throws IOException {
      return this.randomAccessFile.read(bs);
   }

   @Override
   public int read(byte[] bs, int i, int j) throws IOException {
      return this.randomAccessFile.read(bs, i, j);
   }

   @Override
   public void close() throws IOException {
      this.randomAccessFile.close();
   }

   @Override
   public long length() throws IOException {
      return this.randomAccessFile.length();
   }

   @Override
   public long position() throws IOException {
      return this.randomAccessFile.getFilePointer();
   }

   @Override
   public void seek(long l) throws IOException {
      this.randomAccessFile.seek(l);
   }
}
