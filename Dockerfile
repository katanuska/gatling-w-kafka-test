FROM openjdk:8-jdk-alpine
COPY . /gatling/
RUN chmod -R 777 /gatling
RUN apk update && apk add vim
ENV SIMULATION_NAME=KafkaSaveTelemetrySimulation

ENTRYPOINT ["/bin/sh", "-c", "--", "./gatling/gatling-charts-highcharts-bundle-3.0.3/bin/gatling.sh -s ${SIMULATION_NAME} -rf /gatling/results && while true; do sleep 30; done;"]



