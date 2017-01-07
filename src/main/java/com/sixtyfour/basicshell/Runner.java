package com.sixtyfour.basicshell;

import com.sixtyfour.Basic;

import java.util.concurrent.Future;

/**
 * Proxy class to instantiate an run the BASIC system
 */
public class Runner implements Runnable
{
    private final Basic olsenBasic;
    private boolean running = false;

    public Runner (String[] program, ShellFrame shellFrame)
    {
        this.olsenBasic = new Basic(program);
        olsenBasic.setOutputChannel(new ShellOutputChannel(shellFrame));
        olsenBasic.setInputProvider(new ShellInputProvider(shellFrame));
    }

    /**
     * Compile an run a single line
     * @param in the BASIC line
     * @param sf reference to shell main window
     * @return textual representation of success/error
     */
    public static String runLine (String in, ShellFrame sf)
    {
        try
        {
            Basic b = new Basic("0 " + in.toUpperCase());
            b.compile();
            b.setOutputChannel(new ShellOutputChannel(sf));
            b.setInputProvider(new ShellInputProvider(sf));
            b.start();
            return "";
        }
        catch (Exception ex)
        {
            return ProgramStore.ERROR;
        }
    }

    /**
     * Start BASIC task
     * @param synchronous if true the caller is blocked
     */
    public void start (boolean synchronous)
    {
        Future f = ShellFrame.executor.submit (this);
        if (!synchronous)
            return;
        try
        {
            f.get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isRunning()
    {
        return running;
    }

    public Basic getOlsenBasic ()
    {
        return olsenBasic;
    }

    @Override
    public void run()
    {
        running = true;
        olsenBasic.run();
        running = false;
    }
}
