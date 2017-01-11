package com.nietoperz.basicshell;

import com.sixtyfour.Basic;
import com.sixtyfour.DelayTracer;

import javax.swing.*;
import java.util.concurrent.Future;

/**
 * Proxy class to instantiate an run the BASIC system
 */
public class BasicRunner implements Runnable
{
    private static volatile boolean running = false;
    private Basic olsenBasic;
    private ShellFrame shellFrame;

    public BasicRunner (String[] program, boolean slow, ShellFrame shellFrame)
    {
        if (running)
        {
            return;
        }
        this.shellFrame = shellFrame;
        olsenBasic = new Basic(program);
        if (slow)
        {
            DelayTracer t = new DelayTracer(100);
            olsenBasic.setTracer(t);
        }
        olsenBasic.getMachine().setMemoryListener(new PeekPokeHandler(shellFrame));
        olsenBasic.setOutputChannel(new ShellOutputChannel(shellFrame));
        olsenBasic.setInputProvider(new ShellInputProvider(shellFrame));
    }

    /**
     * Compile an run a single line
     *
     * @param in the BASIC line
     * @param sf reference to shell main window
     * @return textual representation of success/error
     */
    public static String runSingleLine (String in, ShellFrame sf)
    {
        try
        {
            Basic b = new Basic("0 " + in.toUpperCase());
            b.getMachine().setMemoryListener(new PeekPokeHandler(sf));
            b.compile();
            b.setOutputChannel(new ShellOutputChannel(sf));
            b.setInputProvider(new ShellInputProvider(sf));
            b.start();
            return "";
        }
        catch (Exception ex)
        {
            return ex.getMessage().toUpperCase()+"\n";
        }
    }

    /**
     * Start BASIC task
     *
     * @param synchronous if true the caller is blocked
     */
    public void start (boolean synchronous)
    {
        if (running)
        {
            System.out.println("already running ...");
            return;
        }
        Future f = ShellFrame.executor.submit(this);
        if (!synchronous)
        {
            return;
        }
        try
        {
            f.get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isRunning ()
    {
        return running;
    }

    public Basic getOlsenBasic ()
    {
        return olsenBasic;
    }

    @Override
    public void run ()
    {
        running = true;
        try
        {
            SidRunner.reset();
            SwingUtilities.invokeAndWait(() ->
                    shellFrame.runButton.setEnabled(false));
            olsenBasic.run();
            //SidRunner.reset();
            SwingUtilities.invokeAndWait(() ->
                    shellFrame.runButton.setEnabled(true)
            );
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            running = false;
        }
    }
}
