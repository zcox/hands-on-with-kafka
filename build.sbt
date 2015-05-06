name := "hands-on-with-kafka"

resolvers ++= Seq(
  "confluent" at "http://packages.confluent.io/maven/",
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "io.confluent" % "kafka-avro-serializer" % "2.0-SNAPSHOT" //need to `mvn install` https://github.com/confluentinc/schema-registry locally
)
