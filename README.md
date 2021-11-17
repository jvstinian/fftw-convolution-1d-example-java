# FFTW Convolution 1D Example

This is a simple translation of 
the one-dimensional convolution example using FFTW in C++ 
found at [https://github.com/mosco/fftw-convolution-example-1D](https://github.com/mosco/fftw-convolution-example-1D) 
to Java.

# Compilation notes

To build and run locally, you will need to install java, mvn, and fftw.  Once the necessary packages are installed, 
build with `make build` and run with `make run`.  

To use docker instead, run 
```
make build-docker
make run-docker
```

# Linting

```
mvn com.coveo:fmt-maven-plugin:check
```

can be used to check the format and

```
mvn com.coveo:fmt-maven-plugin:format
```

can be used to fix formatting. These are called when running `make lint` and `make fix`, respectively.

[checkstyle](https://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html) is also installed, though is not being explicitly used
at the moment. It can be called using

```
mvn checkstyle::check
```