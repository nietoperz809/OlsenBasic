package com.sixtyfour.basicshell;

import com.sixtyfour.resid.AudioDriverSE;
import com.sixtyfour.resid.ISIDDefs;
import com.sixtyfour.resid.SID;

import java.util.concurrent.Callable;

/**
 * Run the sound chip emulation
 */
@SuppressWarnings("InfiniteLoopStatement")
public class SidRunner
{
    private static AudioDriverSE audioDriver = null;
    private static SID sid = null;
    private static final int CPUFrq = 985248;
    private static final int SAMPLE_RATE = 22000;
    private static final int BUFFER_SIZE = 256;
    private static final byte[] buffer = new byte[BUFFER_SIZE * 2];
    private static int pos = 0;

    public static void reset()
    {
        setupSID();
    }

    private static void setupSID()
    {
        sid = new SID();
        sid.set_sampling_parameters (CPUFrq,
                ISIDDefs.sampling_method.SAMPLE_RESAMPLE_INTERPOLATE, //.SAMPLE_INTERPOLATE, //SAMPLE_FAST,
                SAMPLE_RATE,
                -1,
                0.97);
        sid.set_chip_model(ISIDDefs.chip_model.MOS8580);
    }
    /**
     * Main function that inits and starts the SID
     * @return false if SID is already running
     */
    static boolean start()
    {
        if (sid != null)
            return false;
        setupSID();
        audioDriver = new AudioDriverSE();
        audioDriver.init(SAMPLE_RATE, 22000);

        ShellFrame.executor.submit(new Callable<Object>()
        {
            final int clocksPerSample = CPUFrq / SAMPLE_RATE;
            final int temp = (int) ((CPUFrq * 1000L) / SAMPLE_RATE);
            final int clocksPerSampleRest = temp - clocksPerSample*1000;
            long nextSample = 0;
            long lastCycles = 0;
            int nextRest = 0;

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
            }

            @Override
            public Object call () throws Exception
            {
                long cycles = 1;
                //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                while (true)
                {
                    execute (cycles);
                    cycles += 33; //+= l1; //29; //add;
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

    private static synchronized void clock ()
    {
        sid.clock();
    }
}
