/**
 * This file is a part of JaC64 - a Java C64 Emulator
 * Main Developer: Joakim Eriksson (JaC64.com Dreamfabric.com)
 * Contact: joakime@sics.se
 * Web: http://www.jac64.com/
 * http://www.dreamfabric.com/c64
 * ---------------------------------------------------
 */
package com.sixtyfour.resid;

import javax.sound.sampled.*;

public class AudioDriverSE
{
    private SourceDataLine dataLine;
    private FloatControl volume;
    private boolean soundOn = true;
    private boolean fullSpeed = false;

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public int available ()
//    {
//        if (dataLine == null)
//        {
//            return 0;
//        }
//        return dataLine.available();
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public int getMasterVolume ()
//    {
//        return vol;
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

    public void setMasterVolume (int v)
    {
        if (volume != null)
        {
            volume.setValue(-10.0f + 0.1f * v);
        }
    }

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public long getMicros ()
//    {
//        if (dataLine == null)
//        {
//            return 0;
//        }
//        return dataLine.getMicrosecondPosition();
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public boolean hasSound ()
//    {
//        return dataLine != null;
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

    public void init (int sampleRate, int bufferSize)
    {
//  Allocate Audio resources
        AudioFormat af = new AudioFormat(sampleRate,
                16, 1, true, false);
        DataLine.Info dli = new DataLine.Info(SourceDataLine.class, af, bufferSize);
        try
        {
            dataLine = (SourceDataLine) AudioSystem.getLine(dli);
            if (dataLine == null)
            {
                System.out.println("DataLine: not existing...");
            }
            else
            {
                //System.out.println("DataLine allocated: " + dataLine);
                dataLine.open(dataLine.getFormat(), bufferSize);
                volume = (FloatControl)
                        dataLine.getControl(FloatControl.Type.MASTER_GAIN);
                setMasterVolume(100);
                dataLine.start();
            }
        }
        catch (Exception e)
        {
            System.out.println("Problem while getting data line ");
            e.printStackTrace();
            dataLine = null;
        }
    }

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public void shutdown ()
//    {
//        dataLine.close();
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

    public void write (byte[] buffer)
    {
        if (dataLine == null)
        {
            return;
        }
        int bsize = buffer.length;
            while (dataLine.available() < bsize)
            {
                Thread.yield();
            }
//        else if (dataLine.available() < bsize)
//        {
//            return;
//        }
//        if (!soundOn)
//        {
//            // Kill sound!!!
//            for (int i = 0; i < buffer.length; i++)
//            {
//                buffer[i] = 0;
//            }
//        }
        dataLine.write(buffer, 0, bsize);
    }

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public void setSoundOn (boolean on)
//    {
//        soundOn = on;
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public void setFullSpeed (boolean full)
//    {
//        fullSpeed = full;
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

// --Commented out by Inspection START (1/9/2017 7:19 AM):
//    public boolean fullSpeed ()
//    {
//        return fullSpeed;
//    }
// --Commented out by Inspection STOP (1/9/2017 7:19 AM)

//    /**
//     * Test audio
//     * @param args
//     */
//    @SuppressWarnings("InfiniteLoopStatement")
//    public static void main (String[] args)
//    {
//        AudioDriverSE audioDriver = new AudioDriverSE();
//        audioDriver.init(44000, 22000);
//        audioDriver.setMasterVolume(100);
//
//        byte[] buff = new byte[256];
//        Random rand = new Random();
//        while (true)
//        {
//            rand.nextBytes(buff);
//            audioDriver.write(buff);    // make some noise
//        }
//    }
}
