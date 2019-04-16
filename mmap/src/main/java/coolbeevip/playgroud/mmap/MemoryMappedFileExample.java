package coolbeevip.playgroud.mmap;

import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class MemoryMappedFileExample {
  static int length = 128 * 1024 * 1024;

  public static void main(String[] args) throws Exception {
    long s = System.currentTimeMillis();
    try (RandomAccessFile file = new RandomAccessFile("mmap.dat", "rw")) {
      MappedByteBuffer out = file.getChannel()
              .map(FileChannel.MapMode.READ_WRITE, 0, length);

      for (int i = 0; i < length; i++) {
        out.put((byte) 'x');
      }
      long e = System.currentTimeMillis();
      log.info("Finished writing use {} ms",(e-s));
    }
  }
}