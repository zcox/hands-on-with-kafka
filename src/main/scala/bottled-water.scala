package bottled.water

import java.util.Properties
import utils.Utils
import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}
import io.confluent.kafka.serializers.KafkaAvroDecoder
import kafka.utils.VerifiableProperties
import org.apache.avro.generic.GenericRecord

object PrintlnConsumer extends App {
  val props = new Properties
  props.put("zookeeper.connect", "192.168.59.103:2181")
  props.put("group.id", "PrintlnConsumer")
  props.put("offsets.storage", "kafka")
  props.put("dual.commit.enabled", "false")
  props.put("schema.registry.url", "http://192.168.59.103:8081")

  val vProps = new VerifiableProperties(props)
  val keyDecoder = new KafkaAvroDecoder(vProps)
  val valueDecoder = new KafkaAvroDecoder(vProps)

  val topic = "users" //postgres table name
  val connector = Consumer.create(new ConsumerConfig(props))
  val stream = connector.createMessageStreams(Map(topic -> 1), keyDecoder, valueDecoder)(topic)(0)
  def consumeStream(): Unit = {
    val consumerName = Utils.newRandomName()
    println(s"$consumerName consuming from $topic...")
    val iterator = stream.iterator
    while (iterator.hasNext) {
      val messageAndMetadata = iterator.next
      val key = messageAndMetadata.key.asInstanceOf[GenericRecord]
      val message = messageAndMetadata.message.asInstanceOf[GenericRecord]
      println(s"$consumerName received from $topic: ($key, $message)")
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