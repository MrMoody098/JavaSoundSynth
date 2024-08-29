package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI
{
    private int wavePos;

    private boolean shouldGenerate;
    private final JFrame frame = new JFrame("GUI");
    private final AudioThread audioThread = new AudioThread(() ->
    {
        if(shouldGenerate){
            return null;
        }
        short[] s = new short[AudioThread.BUFFER_SIZE];
        for (int i = 0; i< AudioThread.BUFFER_SIZE;i++){
            s[i] = (short) (Short.MAX_VALUE * Math.sin((2 * Math.PI * 440 * wavePos) / AudioInfo.SAMPLE_RATE));
            wavePos ++;
        }
        return s;
    });

    GUI()
    {
        frame.addKeyListener(new KeyAdapter() {
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
        });
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


    }

    public static class AudioInfo{
        public static final int SAMPLE_RATE = 44100;
    }
}
