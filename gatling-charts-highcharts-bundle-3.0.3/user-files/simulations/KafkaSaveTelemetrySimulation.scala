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

  val scn = scenario("Kafka Test")
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
      .inject(rampUsersPerSec(5000) to 10000 during (10 seconds)))//.inject(constantUsersPerSec(10) during(10 seconds))) 
    .protocols(kafkaConf)
}