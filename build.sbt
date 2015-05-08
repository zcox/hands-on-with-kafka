name := "hands-on-with-kafka"

scalaVersion := "2.11.6"

seq( sbtavro.SbtAvro.avroSettings : _*) //https://github.com/cavorite/sbt-avro

stringType in avroConfig := "String"

version in avroConfig := "1.7.7"

resolvers ++= Seq(
  "confluent" at "http://packages.confluent.io/maven/",
  Resolver.mavenLocal //so we can use local build of kafka-avro-serializer
)

libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "0.8.2.1",
  "io.confluent" % "kafka-avro-serializer" % "2.0-SNAPSHOT" //need to `mvn install` https://github.com/confluentinc/schema-registry locally
)
