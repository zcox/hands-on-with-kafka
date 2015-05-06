package basic

import java.util.Properties
import utils.Utils
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}

object BasicProducing extends App {
  //http://kafka.apache.org/documentation.html#newproducerconfigs
  val props = new Properties
  props.put("bootstrap.servers", "192.168.59.103:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

  val producer = new KafkaProducer[Array[Byte], Array[Byte]](props)
  val producerName = Utils.newRandomName()

  sys.addShutdownHook {
    println("Shutting down...")
    producer.close()
    println("Shut down")
  }

  val topic = "basic"
  var count = 0
  while (true) {
    count += 1
    val message = s"${producerName}-${count.toString}"
    val record = new ProducerRecord[Array[Byte], Array[Byte]](topic, message.getBytes("UTF-8"))
    producer.send(record)
    println(s"$producerName sent to $topic: $message")
    Thread.sleep(1000)
  }
}

object BasicConsuming extends App {
  //http://kafka.apache.org/documentation.html#consumerconfigs
  val props = new Properties
  props.put("zookeeper.connect", "192.168.59.103:2181")
  props.put("group.id", "BasicConsumer")
  props.put("offsets.storage", "kafka")
  props.put("dual.commit.enabled", "false")

  val topic = "basic"
  val connector = Consumer.create(new ConsumerConfig(props))
  val stream = connector.createMessageStreams(Map(topic -> 1))(topic)(0)
  def consumeStream(): Unit = {
    val consumerName = Utils.newRandomName()
    println(s"$consumerName consuming from $topic...")
    val iterator = stream.iterator
    while (iterator.hasNext) {
      val messageAndMetadata = iterator.next
      val message = new String(messageAndMetadata.message, "UTF-8")
      println(s"$consumerName received from $topic: $message")
    }
    println(s"$consumerName finished consuming from $topic")
  }

  new Thread(new Runnable() { override def run(): Unit = consumeStream }).start()

  scala.sys.addShutdownHook {
    println("Shutting down...")
    connector.shutdown()
    println("Shut down")
  }
}
