import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class KafkaSaveTelemetrySimulation extends Simulation {
  val kafkaConf = kafka
    .topic("save-telemetry")
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "my-cluster-kafka-brokers.strimzi0113:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Test telemetry service and Cassandra")
    .exec {
        session => session.set("messageAsJson", """{ \"consumption\": 3.0, \"time\": """ + (System.currentTimeMillis - 100000) + "}")
    }
    .feed(jsonFile("saveTelemetry.json").circular)
    .exec(kafka("request").send[String](
      "{" + 
        "\"messageAsJson\": ${messageAsJson.jsonStringify()}," +
        "\"deviceId\": ${deviceId}," +
        "\"applicationId\": ${applicationId}," +
        "\"assetUnitIds\": ${assetUnitIds} " +
      "}"))

  setUp(
    scn
      //.inject(rampUsersPerSec(5000) to 10000 during (10 seconds)))
      .inject(
        constantUsersPerSec(1000) during (10 seconds)
      ))
    .protocols(kafkaConf)
}