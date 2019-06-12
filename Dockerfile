FROM openjdk:8-jdk-alpine
COPY . /gatling/
RUN chmod -R 777 /gatling
RUN apk update && apk add vim
ENV SIMULATION-NAME=KafkaSaveTelemetrySimulation

ENTRYPOINT ["/bin/sh", "-c", "--", "./gatling/gatling-charts-highcharts-bundle-3.0.3/bin/gatling.sh -s ${SIMULATION-NAME} && while true; do sleep 30; done;"]



