package coolbeevip.playgroud.statemachine.chatroom.util;

import akka.event.LoggingAdapter;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyUtils {

    public static void memAndLogChatMessage(String message, String fileName, Logger log){
        log.info(message);

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy  HH:mm:ss");
        message = "["+format.format(new Date()) + "]: " + message;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(message);
            writer.append("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
