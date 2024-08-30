package org.example;

import org.example.utils.Utils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.Random;

public class Oscillator extends SynthControlContrainer {
    private Waveform waveform = Waveform.Sine;

    public static double FREQUENCY= 440;

    private  final Random random = new Random();

    private int wavePos;
    public Oscillator(JavaSoundSynth synth) {
        super(synth);
        JComboBox<Waveform> comboBox = new JComboBox<Waveform>(new Waveform[] {Waveform.Sine,Waveform.Triangle,Waveform.Square,Waveform.Saw,Waveform.Noise});
        comboBox.setSelectedItem(Waveform.Sine);
        comboBox.setBounds(10,10,75,25);
        comboBox.addItemListener(l -> {
            if(l.getStateChange() == ItemEvent.SELECTED)
            {
                waveform = (Waveform)l.getItem();
            }
        });

        add(comboBox);
        setSize(279,100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    private enum Waveform
    {
        Sine,Square,Saw,Triangle,Noise
    }

    public double nextSample (){
        double tDivP = (wavePos++ /(double) JavaSoundSynth.AudioInfo.SAMPLE_RATE / (1d / FREQUENCY));
        switch (waveform){
            case Sine:
                return Math.sin(Utils.Math.frequencyToAngularFrequency(FREQUENCY)*wavePos/ JavaSoundSynth.AudioInfo.SAMPLE_RATE);
            case Square:
                return Math.signum(Math.sin(Utils.Math.frequencyToAngularFrequency(FREQUENCY)*wavePos/ JavaSoundSynth.AudioInfo.SAMPLE_RATE));
            case Saw:
                return 2d * (tDivP- Math.floor(0.5 + tDivP));
            case Triangle:
                return 2d * Math.abs( 2d * (tDivP- Math.floor(0.5 + tDivP)))-1;
            case Noise:
                return random.nextDouble();
                default:
                    throw new RuntimeException("Unknown waveform");
        }

    }
}
