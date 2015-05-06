# Hands-On With Kafka

[Zach Cox](http://theza.ch)
[Iowa Code Camp Spring 2015](http://iowacodecamp.com)

## Running Examples

To run all of the infrastructure:

```
docker-compose up -d
```

To run one of the code examples:

```
./sbt run
```

To kill the infrastructure:

```
docker-compose kill && docker-compose rm --force
```

## Basic Kafka: Producers and Consumers

![](img/kafka0.png)

- producer sends messages to kafka topic
- consumer receives messages from kafka topic
- messages in topic
  - ordered
  - persistent
  - each message has an offset
- consumer remembers offset of last received message
- consumer can start receiving messages from any offset
- each message sent to one consumer in same group

## Running Kafka

- examine docker-compose.yml

## Serialization/Deserialization

- Kafka only knows bytes: `ProducerRecord[Array[Byte], Array[Byte]]`
- Best practice: [Avro](http://avro.apache.org/) + [Schema Registry](http://confluent.io/docs/current/schema-registry/docs/intro.html)

To view Avro schema in Registry: `curl http://192.168.59.103:8081/subjects/basic2-value/versions/1`

## Partitions

- topic split into 1 or more partitions
- partition is unit of parallelism and allows scale
- messages ordered within partition
- message key determines partition
- messages in partition go to same consumer

![](img/consumer1.png)
![](img/consumer2.png)
![](img/consumer3.png)
![](img/consumer4.png)
![](img/consumer5.png)
![](img/consumer6.png)
![](img/consumer6b.png)
![](img/consumer7.png)
![](img/consumer7b.png)

## Replicas

## Example: Application Events

## Example: Logging

## Example: Change Data Capture

## Fault Tolerance

## Performance Testing

## References

- [Kafka docs](http://kafka.apache.org/documentation.html)
- [I ♡ Logs](http://shop.oreilly.com/product/0636920034339.do)
- [Confluent Platform docs](http://confluent.io/docs/current/index.html)
