package org.example;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.example.utils.Utils;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

public class AudioThread extends Thread{
    static final int BUFFER_SIZE = 512;
    static final int BUFFER_COUNT = 8;

    private final Supplier<short[]> bufferSupplier;
    private final int[] buffers = new int[BUFFER_COUNT];
    private final long device= alcOpenDevice(ALC10.alcGetString(0,ALC_DEFAULT_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device,new int[1]);
    private final int source;

    private int bufferIndex;
    private boolean closed;
    private boolean running;

    AudioThread(Supplier<short[]> bufferSupplier){
        this.bufferSupplier = bufferSupplier;
        alcMakeContextCurrent(context); // makes current context of alc instance
        AL.createCapabilities(ALC.createCapabilities(device)); // creates what device is currently capable so AL knows what is possible
        source = alGenSources();
        for (int i = 0; i < BUFFER_COUNT; i++) {
            bufferSamples(new short[0]);
        }
        alSourcePlay(source);
        catchInternalException();
        start();
    }

    @Override
    public synchronized void run() {
        while (!closed) {
            while (!running) {
                Utils.handleProcedure(this::wait, true);
            }
            int processBuffers = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for (int i = 0; i < processBuffers; ++i)
            {
                short[] samples = bufferSupplier.get();
                if(samples == null){
                    running = false;
                    break;
                }

                alDeleteBuffers(alSourceUnqueueBuffers(source));//unque most recently procecessed buffer n delete it
                buffers[bufferIndex] = alGenBuffers(); //current index = new buffer
                bufferSamples(samples);
            }
            if (alGetSourcei(source, AL_SOURCE_STATE) != AL_PLAYING)
            {
                alSourcePlay(source);
            }
        }
        catchInternalException();
        alDeleteSources(source);
        alDeleteBuffers(buffers);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }


    synchronized void triggerPlayBack(){
        running = true;
        notify();
    }

    void close(){
        closed = true;
        triggerPlayBack();
    }



    boolean isRunning(){
        return running;
    }

    private  void bufferSamples(short[] samples){
        int buf = buffers[bufferIndex++];
        alBufferData(buf, AL_FORMAT_MONO16, samples, JavaSoundSynth.AudioInfo.SAMPLE_RATE);
        alSourceQueueBuffers(source, buf);
        bufferIndex %= BUFFER_COUNT; // 1%8=1 -> 8%8 = 0 counts up from 0 to 8 and equals when there is no remainder
    }

    private void catchInternalException(){
        int err = alcGetError(device);
        if(err != ALC_NO_ERROR){
            throw new OpenALException(err);
        }
    }
}
