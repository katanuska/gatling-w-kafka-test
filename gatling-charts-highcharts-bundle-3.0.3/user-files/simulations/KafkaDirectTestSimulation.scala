import io.gatling.core.Predef._
import org.apache.kafka.clients.producer.ProducerConfig
import scala.concurrent.duration._

import com.github.mnogu.gatling.kafka.Predef._

class KafkaDirectTestSimulation extends Simulation {
  val kafkaConf = kafka
    .topic("telemetry")
    .properties(
      Map(
        ProducerConfig.ACKS_CONFIG -> "1",
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> "my-cluster-kafka-brokers.om-strimzi-0100:9092",
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer",
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG ->
          "org.apache.kafka.common.serialization.StringSerializer"))

  val scn = scenario("Kafka Test")
    // You can also use feeder
    .exec {
        session => session.set("tsd", System.currentTimeMillis - 100000).set("status_tamperingd", System.currentTimeMillis % 2)
                    .set("status_batteryd", System.currentTimeMillis % 100).set("status_leakd", System.currentTimeMillis % 2)
                    .set("status_counter_errord", System.currentTimeMillis % 10).set("status_resetd", System.currentTimeMillis % 2)
                    .set("status_gen_ad", System.currentTimeMillis % 2).set("status_gen_bd", System.currentTimeMillis % 2)
                    .set("status_gen_cd", System.currentTimeMillis % 2).set("tlm_temperatured", System.currentTimeMillis % 100)
                    .set("tlm_counter_totald", System.currentTimeMillis % 100000).set("tlm_gen_ad", System.currentTimeMillis % 1000)
                    .set("tlm_gen_bd", System.currentTimeMillis % 1000).set("tlm_gen_cd", System.currentTimeMillis % 1000)
                    .set("tlm_gen_dd", System.currentTimeMillis % 1000).set("tlm_gen_ed", System.currentTimeMillis % 1000)
                    .set("tlm_gen_fd", System.currentTimeMillis % 1000).set("tlm_gen_gd", System.currentTimeMillis % 1000)
    }
    .feed(csv("kafkaDirectTestDevices.csv").random)
    .exec(kafka("request").send[String](
      "{" + 
        "\"deviceId\": ${deviceId}," +
        "\"status_tampering\": ${status_tamperingd}," +
        "\"status_battery\": ${status_batteryd}," +
        "\"status_leak\": ${status_leakd}," +
        "\"status_counter_error\": ${status_counter_errord}," +
        "\"status_reset\": ${status_resetd}," +
        "\"status_gen_a\": ${status_gen_ad}," +
        "\"status_gen_b\": ${status_gen_bd}," +
        "\"status_gen_c\": ${status_gen_cd}," +
        "\"tlm_temperature\": ${tlm_temperatured}," +
        "\"tlm_counter_total\": ${tlm_counter_totald}," +
        "\"tlm_gen_a\": ${tlm_gen_ad}," +
        "\"tlm_gen_b\": ${tlm_gen_bd}," +
        "\"tlm_gen_c\": ${tlm_gen_cd}," +
        "\"tlm_gen_d\": ${tlm_gen_dd}," +
        "\"tlm_gen_e\": ${tlm_gen_ed}," +
        "\"tlm_gen_f\": ${tlm_gen_fd}," +
        "\"tlm_gen_g\": ${tlm_gen_gd}" +
      "}"))

  setUp(
    scn
      .inject(rampUsersPerSec(5000) to 10000 during (10 seconds)))//.inject(constantUsersPerSec(10) during(10 seconds))) 
    .protocols(kafkaConf)
}