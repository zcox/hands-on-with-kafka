// package producing

// object GardenHose extends App {
//   //produce a bunch of data to a bunch of topics
//   //http://kafka.apache.org/documentation.html#newproducerconfigs
//   val props = new Properties
//   props.put("bootstrap.servers", "192.168.59.103:9092")
//   props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
//   props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
//   props.put("schema.registry.url", "http://192.168.59.103:8081")

//   val producer = new KafkaProducer[Object, Object](props)
//   val producerName = Utils.newRandomName()

//   sys.addShutdownHook {
//     println("Shutting down...")
//     producer.close()
//     println("Shut down")
//   }

//   val topic = "basic2"
//   var count = 0
//   while (true) {
//     count += 1
//     val message = BasicMessage.newBuilder //generated from src/main/avro/BasicMessage.avsc
//       .setProducerName(producerName)
//       .setCount(count)
//       .build
//     val record = new ProducerRecord[Object, Object](topic, message) //message is a SpecificRecord, which KafkaAvroSerializer knows how to serialize
//     producer.send(record)
//     println(s"$producerName sent to $topic: $message")
//     Thread.sleep(1000)
//   }
// }