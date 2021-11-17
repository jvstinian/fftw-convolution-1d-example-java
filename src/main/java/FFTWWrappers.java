package main.java;

import org.bytedeco.fftw.global.fftw3;
import org.bytedeco.javacpp.DoublePointer;
import java.util.Arrays;
import org.apache.commons.math3.complex.Complex;

public class FFTWWrappers {
    public static class FFTW_R2C_1D_Executor {
        public int input_size;
        private DoublePointer input_buffer;
        public int output_size;
        private DoublePointer output_buffer;
        private fftw3.fftw_plan plan;

        public FFTW_R2C_1D_Executor(int n_real_samples) {
            this.input_size = n_real_samples;
            this.input_buffer = fftw3.fftw_alloc_real(this.input_size);
            this.output_size = n_real_samples/2 + 1;
            this.output_buffer = fftw3.fftw_alloc_complex(this.output_size);
            this.plan = fftw3.fftw_plan_dft_r2c_1d(this.input_size, this.input_buffer, this.output_buffer, fftw3.FFTW_ESTIMATE);
        }

        public void free() {
            fftw3.fftw_destroy_plan(this.plan);
            fftw3.fftw_free(this.input_buffer);
            fftw3.fftw_free(this.output_buffer);
        }

        public void set_input_zeropadded(double[] buffer) {
            int size = buffer.length;
            assert(size <= this.input_size);
            /*
            double[] tempbuffer = new double[this.input_size];
            System.arraycopy(buffer, 0, tempbuffer, 0, buffer.length);
            Arrays.fill(tempbuffer, size, this.input_size, 0.0);
            this.input_buffer.put(tempbuffer);
            */
            DoublePointer.memcpy(this.input_buffer, new DoublePointer(buffer), this.input_buffer.sizeof()*size);
            DoublePointer.memset(this.input_buffer.getPointer(size), 0, this.input_buffer.sizeof()*(this.input_size - size));
        }

        public void execute() {
            fftw3.fftw_execute(plan);
        }
        
        public DoublePointer get_input_pointer() {
            return this.input_buffer;
        }

        public double[] get_output() {
            // return this.output_buffer.asBuffer().array();
            double[] result = new double[2*this.output_size];
            this.output_buffer.get(result);
            return result;
        }
        
        public DoublePointer get_output_pointer() {
            return this.output_buffer;
        }
        
        public Complex[] get_output_as_complex_array() {
            Complex[] result = new Complex[this.output_size];
            double[] ds = new double[2*this.output_size];
            this.output_buffer.get(ds);
            for (int i = 0; i < result.length; i++) {
                result[i] = new Complex(ds[2*i], ds[2*i+1]);
            }
            return result;
        }
    };

    public static class FFTW_C2R_1D_Executor {
        public int input_size;
        private DoublePointer input_buffer;
        public int output_size;
        private DoublePointer output_buffer;
        private fftw3.fftw_plan plan;

        public FFTW_C2R_1D_Executor(int n_real_samples) {
            this.input_size = n_real_samples/2 + 1;
            this.input_buffer = fftw3.fftw_alloc_complex(this.input_size);
            this.output_size = n_real_samples;
            this.output_buffer = fftw3.fftw_alloc_real(this.output_size);
            this.plan = fftw3.fftw_plan_dft_c2r_1d(this.output_size, this.input_buffer, this.output_buffer, fftw3.FFTW_ESTIMATE);
        }
        public void free() {
            fftw3.fftw_destroy_plan(this.plan);
            fftw3.fftw_free(this.input_buffer);
            fftw3.fftw_free(this.output_buffer);
        }
        public void set_input(DoublePointer ptr, int size) {
            assert(size == this.input_size);
            DoublePointer.memcpy(this.input_buffer, ptr, 2*ptr.sizeof()*size); // 2 for sizeof(complex)/sizeof(double)
            DoublePointer.memset(this.input_buffer.getPointer(size), 0, ptr.sizeof()*(this.input_size - size));
        }
        public void set_input(double[] buffer) {
            assert((buffer.length/2) == this.input_size);
            DoublePointer.memcpy(this.input_buffer, new DoublePointer(buffer), this.input_buffer.sizeof()*buffer.length);
            DoublePointer.memset(this.input_buffer.getPointer(buffer.length), 0, this.input_buffer.sizeof()*(2*input_size - buffer.length));
        }
        public void set_input(Complex[] buffer) {
            assert(buffer.length == this.input_size);
            double[] buffer_reals = new double[2*buffer.length];
            for (int i = 0; i < buffer.length; i++) {
                buffer_reals[2*i] = buffer[i].getReal();
                buffer_reals[2*i + 1] = buffer[i].getImaginary();
            }
            this.set_input(buffer_reals);
        }
        public void execute()
        {
            fftw3.fftw_execute(plan);
        }
        public DoublePointer get_output_ponter()
        {
            return this.output_buffer;
        }
        public double[] get_output() {
            // return this.output_buffer.asBuffer().array();
            double[] result = new double[this.output_size];
            this.output_buffer.get(result);
            return result;
        }

    };
};
/*
#include <vector>
#include <complex.h>
#include <fftw3.h>

// Usage: (after initializing the class)
// 1. Fill input_buffer with input containing n_real_samples double numbers
//    (note, set_input_zeropadded will copy your buffer with optional zero padding)
// 2. Run execute().
// 3. Extract output by calling get_output() or directly access output_buffer[0], ..., output_buffer[output_size-1].
//    Note that the output is composed of n_real_samples/2 + 1 complex numbers.
// 
// These 3 steps can be repeated many times.
class FFTW_R2C_1D_Executor {
public:
    FFTW_R2C_1D_Executor(int n_real_samples);
    ~FFTW_R2C_1D_Executor();
    void set_input_zeropadded(const double* buffer, int size);
    void set_input_zeropadded(const std::vector<double>& vec);
    void execute();
    std::vector<double complex> get_output();

    const int input_size;
    double* const input_buffer;

    const int output_size;
    double complex* const output_buffer;

private:
    fftw_plan plan;
};

// Usage of this class is similar to that of FFTW_R2C_1D_Executor, only the input is n_real_samples/2+1 complex samples.
class FFTW_C2R_1D_Executor {
public:
    FFTW_C2R_1D_Executor(int n_real_samples);
    ~FFTW_C2R_1D_Executor();
    void set_input(const double complex* buffer, int size);
    void set_input(const std::vector<double complex>& vec);
    void execute();
    std::vector<double> get_output();

    const int input_size;
    double complex* const input_buffer;

    const int output_size;
    double* const output_buffer;

private:
    fftw_plan plan;
};


#endif

#include <cassert>
#include <cstring>
#include "fftw_wrappers.hh"

using namespace std;

FFTW_R2C_1D_Executor::FFTW_R2C_1D_Executor(int n_real_samples) :
    input_size(n_real_samples),
    input_buffer(fftw_alloc_real(n_real_samples)),
    output_size(n_real_samples/2 + 1),
    output_buffer(fftw_alloc_complex(n_real_samples/2 + 1))
{
    plan = fftw_plan_dft_r2c_1d(n_real_samples, input_buffer, output_buffer, FFTW_ESTIMATE);
}

FFTW_R2C_1D_Executor::~FFTW_R2C_1D_Executor()
{
    fftw_destroy_plan(plan);
    fftw_free(input_buffer);
    fftw_free(output_buffer);
}

void FFTW_R2C_1D_Executor::set_input_zeropadded(const double* buffer, int size)
{
    assert(size <= input_size);
    memcpy(input_buffer, buffer, sizeof(double)*size);
    memset(&input_buffer[size], 0, sizeof(double)*(input_size - size));
}

void FFTW_R2C_1D_Executor::set_input_zeropadded(const vector<double>& vec)
{
    set_input_zeropadded(&vec[0], vec.size());
}

void FFTW_R2C_1D_Executor::execute()
{
    fftw_execute(plan);
}

vector<double complex> FFTW_R2C_1D_Executor::get_output()
{
    return vector<double complex>(output_buffer, output_buffer + output_size);
}

FFTW_C2R_1D_Executor::FFTW_C2R_1D_Executor(int n_real_samples) : 
    input_size(n_real_samples/2 + 1),
    input_buffer(fftw_alloc_complex(n_real_samples/2 + 1)),
    output_size(n_real_samples),
    output_buffer(fftw_alloc_real(n_real_samples))
{
    plan = fftw_plan_dft_c2r_1d(n_real_samples, input_buffer, output_buffer, FFTW_ESTIMATE);
}

FFTW_C2R_1D_Executor::~FFTW_C2R_1D_Executor()
{
    fftw_destroy_plan(plan);
    fftw_free(input_buffer);
    fftw_free(output_buffer);
}

void FFTW_C2R_1D_Executor::set_input(const double complex* buffer, int size)
{
    assert(size == input_size);
    memcpy(input_buffer, buffer, sizeof(double complex)*size);
    memset(&input_buffer[size], 0, sizeof(double complex)*(input_size - size));
}

void FFTW_C2R_1D_Executor::set_input(const vector<double complex>& vec)
{
    set_input(&vec[0], vec.size());
}

void FFTW_C2R_1D_Executor::execute()
{
    fftw_execute(plan);
}

vector<double> FFTW_C2R_1D_Executor::get_output()
{
    return vector<double>(output_buffer, output_buffer + output_size);
}
*/