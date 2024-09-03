package org.example;

import org.example.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import static org.example.JavaSoundSynth.mouseClickLocation;

public class Oscillator extends SynthControlContrainer {
    private static final int TONE_OFFSET_LIMIT = 200;

    private Waveform waveform = Waveform.Sine;
    private double keyFrequency;
    private double frequency;
    private  final Random random = new Random();

    private int toneOffset;

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
        JLabel tone = new JLabel("x0.00");
        tone.setBounds(165,65,50,23);
        tone.setBorder(Utils.WindowDesign.LINE_BORDER);
        tone.addMouseListener(new MouseAdapter() {
              @Override
              public void mousePressed(MouseEvent e) {
                  final Cursor BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB),new Point(0,0),"blank cursor");
                setCursor(BLANK_CURSOR);
                mouseClickLocation = e.getLocationOnScreen();
              }

              @Override
              public void mouseReleased(MouseEvent e) {
                  setCursor(Cursor.getDefaultCursor());
              }
        });
        tone.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(mouseClickLocation.y != e.getYOnScreen()){
                    boolean mouseMovingUp = mouseClickLocation.y - e.getYOnScreen()>0;
                    if(mouseMovingUp && toneOffset < TONE_OFFSET_LIMIT){
                        ++toneOffset;
                    }
                    else if (!mouseMovingUp && toneOffset > -TONE_OFFSET_LIMIT){
                        --toneOffset;
                    }
                    applyToneOffset();
                    tone.setText("x"+ getToneOffset());
                }
                Utils.ParamHandle.PARAMETER_ROBOT.mouseMove(mouseClickLocation.x,mouseClickLocation.y);
            }
        });
        add(tone);
        JLabel toneLabel = new JLabel("Tone");
        toneLabel.setBounds(172,40,75,25);
        add(toneLabel);
        setSize(279,100);
        setBorder(Utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    private enum Waveform
    {
        Sine,Square,Saw,Triangle,Noise
    }

    public double getFrequency() {
        return frequency;
    }
    public void setFrequency(double frequency) {
        keyFrequency = this.frequency = frequency;
        //apply tone offsetw
        applyToneOffset();
    }
    private double getToneOffset() {
        return toneOffset/100.0;
    }

    public double nextSample (){
        double tDivP = (wavePos++ /(double) JavaSoundSynth.AudioInfo.SAMPLE_RATE / (1d / frequency));
        return switch (waveform) {
            case Sine ->
                    Math.sin(Utils.Math.frequencyToAngularFrequency(frequency) * wavePos / JavaSoundSynth.AudioInfo.SAMPLE_RATE);
            case Square ->
                    Math.signum(Math.sin(Utils.Math.frequencyToAngularFrequency(frequency) * wavePos / JavaSoundSynth.AudioInfo.SAMPLE_RATE));
            case Saw -> 2d * (tDivP - Math.floor(0.5 + tDivP));
            case Triangle -> 2d * Math.abs(2d * (tDivP - Math.floor(0.5 + tDivP))) - 1;
            case Noise -> random.nextDouble();
            default -> throw new RuntimeException("Unknown waveform");
        };

    }


    private void applyToneOffset(){
        frequency = keyFrequency * Math.pow(2,getToneOffset());
    }
}
