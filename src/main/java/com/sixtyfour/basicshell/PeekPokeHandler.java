package com.sixtyfour.basicshell;

import com.sixtyfour.plugins.impl.NullMemoryListener;

/**
 * Created by Administrator on 1/8/2017.
 */
public class PeekPokeHandler extends NullMemoryListener
{
    private ShellFrame shell;

    public PeekPokeHandler (ShellFrame f)
    {
        shell = f;
    }

    @Override
    public void poke (int addr, int value)
    {
        if (addr == 53281)
        {
            shell.setBkColor(C64Colors.get(value));
        }
        else if (addr >= 0xd400 && addr <= 0xd41c)   // SID
        {
            SidRunner.write(addr-0xd400, value);
        }
    }

    @Override
    public Integer peek (int addr)
    {
        if (addr >= 0xd400 && addr <= 0xd41c)   // SID
        {
            return SidRunner.read(addr-0xd400);
        }
        return 0;
    }
}
