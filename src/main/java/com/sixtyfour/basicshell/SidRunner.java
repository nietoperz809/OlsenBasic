package com.sixtyfour.basicshell;

import com.sixtyfour.resid.AudioDriverSE;
import com.sixtyfour.resid.ISIDDefs;
import com.sixtyfour.resid.SID;

import java.util.concurrent.Callable;

/**
 * Created by Administrator on 1/8/2017.
 */
public class SidRunner
{
    private static AudioDriverSE audioDriver = null;
    private static SID sid = null;
    static final int CPUFrq = 985248;
    static final int SAMPLE_RATE = 22000;
    static final int BUFFER_SIZE = 256;
    static final byte[] buffer = new byte[BUFFER_SIZE * 2];
    static int pos = 0;

    static boolean start()
    {
        if (sid != null)
            return false;
        audioDriver = new AudioDriverSE();
        sid = new SID();
        audioDriver.init(SAMPLE_RATE, 22000);
        audioDriver.setMasterVolume(100);
        sid.set_sampling_parameters (CPUFrq,
                ISIDDefs.sampling_method.SAMPLE_FAST,
                SAMPLE_RATE,
                -1,
                0.97);
        sid.set_chip_model(ISIDDefs.chip_model.MOS6581);

        ShellFrame.executor.submit(new Callable<Object>()
        {
            int clocksPerSample = CPUFrq / SAMPLE_RATE;
            int temp = (int) ((CPUFrq * 1000L) / SAMPLE_RATE);
            int clocksPerSampleRest = temp - clocksPerSample*1000;
            long nextSample = 0;
            long lastCycles = 0;
            int nextRest = 0;
            long time;

            public void execute (long cycles)
            {
                nextSample += clocksPerSample;
                nextRest += clocksPerSampleRest;
                if (nextRest > 1000)
                {
                    nextRest -= 1000;
                    nextSample++;
                }
                // Clock resid!
                while (lastCycles < cycles)
                {
                    SidRunner.clock();
                    lastCycles++;
                }
                int sample = sid.output();
                buffer[pos++] = (byte) (sample & 0xff);
                buffer[pos++] = (byte) ((sample >> 8));
                if (pos == buffer.length)
                {
                    audioDriver.write(buffer);
                    pos = 0;
                }
                time = nextSample;
            }

            @Override
            public Object call () throws Exception
            {
                long cycles = 0;
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                while (true)
                {
                    cycles += 29; //add;
                    execute (cycles);
                }
            }
        });
        return true;
    }

    static synchronized int read (int reg)
    {
        return sid.read(reg);
    }

    static synchronized void write (int reg, int val)
    {
        sid.write(reg, val);
    }

    static synchronized void clock()
    {
        sid.clock();
    }
}
