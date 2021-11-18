FROM maven:3.6.3-jdk-8

WORKDIR /opt/fftw-convolution-1d-example

# Add and Install Application Code.
COPY pom.xml /opt/fftw-convolution-1d-example
COPY src /opt/fftw-convolution-1d-example/src

RUN mvn package
