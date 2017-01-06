package com.sixtyfour.basicshell;

import com.sixtyfour.Basic;

import java.util.concurrent.Future;

/**
 * Created by Administrator on 1/4/2017.
 */
public class Runner implements Runnable
{
    private final Basic olsenBasic;
    private boolean running;

    public Runner (String[] program, ShellFrame shellFrame)
    {
        this.olsenBasic = new Basic(program);
        olsenBasic.setOutputChannel(new ShellOutputChannel(shellFrame));
        olsenBasic.setInputProvider(new ShellInputProvider(shellFrame));
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
