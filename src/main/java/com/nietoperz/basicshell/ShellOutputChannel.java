package com.nietoperz.basicshell;

import com.sixtyfour.plugins.impl.ConsoleOutputChannel;


/**
 * Created by Administrator on 1/4/2017.
 */
public class ShellOutputChannel extends ConsoleOutputChannel
{
    private ShellFrame shellFrame;

    public ShellOutputChannel (ShellFrame sf)
    {
        this.shellFrame = sf;
    }

    @Override
    public void print (int id, String txt)
    {
        shellFrame.putString(txt);
    }

    @Override
    public void println (int id, String txt)
    {
        shellFrame.putString(txt+'\n');
    }
}
