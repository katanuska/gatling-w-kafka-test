FROM openjdk:8-jdk-alpine
COPY . /gatling/
RUN chmod -R 777 /gatling
RUN apk update && apk add vim

ENTRYPOINT ["/bin/sh", "-c", "--", " while true; do sleep 30; done;"]



