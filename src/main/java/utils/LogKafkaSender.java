package utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * 向kafka发送log日志的模拟程序
 */
public class LogKafkaSender {
    private static final String TOPIC = "monitor_realtime_javaxy";
    private static List<String> logs = new ArrayList<>();
    private static Random random = new Random();

    static {
        logs.add("1$$$$$error java.lang.AritheticException");
        logs.add("1$$$$$error java.lang.ArrayIndexOutofException");
        logs.add("2$$$$$error java.lang.AritheticException");
        logs.add("1$$$$$error java.lang.ClassNotFoundException");
        logs.add("1$$$$$error error code 123");
        logs.add("1$$$$$error java.lang.ClassNotFoundException");
        logs.add("4$$$$$error java.lang.AritheticException");
        logs.add("1$$$$$error java.lang.ArrayIndexOutofException");
        logs.add("5$$$$$error unexcepted error");
        logs.add("1$$$$$error unkonw host");
        logs.add("1$$$$$error unkonw cause");
        logs.add("2$$$$$error java.lang.AritheticException");
        logs.add("1$$$$$error java.lang.AritheticException");
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "slave1:9092, slave2:9092, enno-host02:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);


        for(int i = 0; i < 100; i++) {
            producer.send(new ProducerRecord<String, String>(
                    TOPIC,
                    String.valueOf(i),
                    logs.get(random.nextInt(logs.size()))));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
