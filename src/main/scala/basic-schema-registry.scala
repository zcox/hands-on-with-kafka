package basic.schema.registry

import java.util.Properties
import utils.Utils
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import kafka.consumer.{Consumer, ConsumerConfig, KafkaStream}
import io.confluent.kafka.serializers.KafkaAvroDecoder
import kafka.utils.VerifiableProperties
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericRecord, GenericData}

case class BasicMessage(producerName: String, count: Int)

//TODO could probably make T => GenericRecord and GenericRecord => T much more reusable for any case class/Product type T
trait GenericRecordConverter[T] {
  def toGenericRecord(t: T): GenericRecord
  def fromGenericRecord(record: GenericRecord): T
}

object BasicMessageConverter extends GenericRecordConverter[BasicMessage] {
  val schemaString = """{"type":"record","name":"BasicMessage","fields":[{"name":"producerName","type":"string"}, {"name":"count","type":"int"}]}"""
  val parser = new Schema.Parser()
  val schema = parser.parse(schemaString)

  def toGenericRecord(message: BasicMessage): GenericRecord = {
    val record = new GenericData.Record(schema)
    record.put("producerName", message.producerName)
    record.put("count", message.count)
    record
  }

  def fromGenericRecord(record: GenericRecord): BasicMessage = 
    BasicMessage(record.get("producerName").toString, record.get("count").asInstanceOf[Int])
}

object BasicProducing extends App {
  //http://kafka.apache.org/documentation.html#newproducerconfigs
  val props = new Properties
  props.put("bootstrap.servers", "192.168.59.103:9092")
  props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("schema.registry.url", "http://192.168.59.103:8081")

  val producer = new KafkaProducer[Object, Object](props)
  val producerName = Utils.newRandomName()

  sys.addShutdownHook {
    println("Shutting down...")
    producer.close()
    println("Shut down")
  }

  val topic = "basic2"
  var count = 0
  while (true) {
    count += 1
    val message = BasicMessage(producerName, count)
    val record = new ProducerRecord[Object, Object](topic, BasicMessageConverter.toGenericRecord(message))
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
  props.put("schema.registry.url", "http://192.168.59.103:8081")

  val vProps = new VerifiableProperties(props)
  val keyDecoder = new KafkaAvroDecoder(vProps)
  val valueDecoder = new KafkaAvroDecoder(vProps)

  val topic = "basic2"
  val connector = Consumer.create(new ConsumerConfig(props))
  val stream = connector.createMessageStreams(Map(topic -> 1), keyDecoder, valueDecoder)(topic)(0)
  def consumeStream(): Unit = {
    val consumerName = Utils.newRandomName()
    println(s"$consumerName consuming from $topic...")
    val iterator = stream.iterator
    while (iterator.hasNext) {
      val messageAndMetadata = iterator.next
      val message = BasicMessageConverter.fromGenericRecord(messageAndMetadata.message.asInstanceOf[GenericRecord])
      println(s"$consumerName received from $topic: BasicMessage[producerName=${message.producerName}, count=${message.count}]")
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
