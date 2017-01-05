package com.sixtyfour.basicshell;

import com.sixtyfour.plugins.InputProvider;

/**
 * Created by Administrator on 1/4/2017.
 */
public class ShellInputProvider implements InputProvider
{
    private ShellFrame shellFrame;

    public ShellInputProvider (ShellFrame shellFrame)
    {
        this.shellFrame = shellFrame;
    }

    @Override
    public Character readKey ()
    {
        if (shellFrame.peek())
            return shellFrame.getString().charAt(0);
        return null;
    }

    @Override
    public String readString ()
    {
        return shellFrame.getString().substring(1);
    }
}
