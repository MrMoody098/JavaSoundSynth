package org.example;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JavaSoundSynth
{

    private boolean shouldGenerate;

    private final Oscillator[] oscillators = new Oscillator[1];
    private final JFrame frame = new JFrame("JavaSoundSynth");
    private final AudioThread audioThread = new AudioThread(() ->

    {
        if(!shouldGenerate){
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i< AudioThread.BUFFER_SIZE;i++){
           double d = 0;
           for(Oscillator oscillator : oscillators){
               d += oscillator.nextSample();
           }
           s[i] =  (short)(Short.MAX_VALUE * d);
        }
        return s;
    });

    private final KeyAdapter keyAdapter = new KeyAdapter() {
    @Override
    public void keyPressed(KeyEvent e) {
        if(!audioThread.isRunning()){
            shouldGenerate = true;
            audioThread.triggerPlayBack();
            System.out.println("playing audiod");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        shouldGenerate = false;
        System.out.println("stopping audio");
    }
};
    JavaSoundSynth()
    {
        int y = 0;
        for (int i = 0; i <oscillators.length; ++i)
        {
            oscillators[i] = new Oscillator(this);
            oscillators[i].setLocation(5,y);
            frame.add(oscillators[i]);
            y += 105;
        }

        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                audioThread.close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(613,357);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null); // appears centered
        frame.setVisible(true);

        Oscillator osc = new Oscillator(this);
        osc.setLocation(5,0);
        frame.add(osc);

    }
    public KeyAdapter getKeyAdapter () {return keyAdapter;}

    public static class AudioInfo{
        public static final int SAMPLE_RATE = 44100;
    }
}
