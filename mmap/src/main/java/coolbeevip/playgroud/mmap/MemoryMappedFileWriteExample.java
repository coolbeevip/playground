package coolbeevip.playgroud.mmap;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class MemoryMappedFileWriteExample {
  private static String bigTextFile = "bigfile.txt";
  public static final int PAGE_SIZE = 1024 * 4;
  public static final long FILE_SIZE = PAGE_SIZE * 2000L * 1000L;

  public static void main(String[] args) throws Exception {
    // Create file object
    File file = new File(bigTextFile);

    //Delete the file; we will create a new file
    file.delete();

    long s = System.currentTimeMillis();
    try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw")) {
      FileChannel fileChannel = randomAccessFile.getChannel();
      MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, Math.min(fileChannel.size(), Integer.MAX_VALUE));

      int checkSum = 0;
      for (long i = 0; i < FILE_SIZE; i++) {
        if (!buffer.hasRemaining()) {
          buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, i, Math.min(fileChannel.size() - i, Integer.MAX_VALUE));
        }
        byte b = (byte) i;
        checkSum += b;
        buffer.put(b);
      }
      long e = System.currentTimeMillis();
      log.info("Finished writing use {} ms", (e - s));
    }
  }
}