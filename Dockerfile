FROM fftw-convolution-1d-example-base:latest as builder

FROM maven:3.6.3-jdk-8

WORKDIR /opt/fftw-convolution-1d-example

COPY --from=builder /opt/fftw-convolution-1d-example/target/fftw-convolution-1d-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar /opt/fftw-convolution-1d-example/target/fftw-convolution-1d-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar

CMD ["java",  "-jar", "/opt/fftw-convolution-1d-example/target/fftw-convolution-1d-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar"]
