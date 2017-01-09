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
            addr -= 0xd400;
            SidRunner.write(addr, value);
            //System.out.println("sid write"+addr +":"+value);
        }
    }

    @Override
    public Integer peek (int addr)
    {
        if (addr >= 0xd400 && addr <= 0xd41c)   // SID
        {
            addr -= 0xd400;
            return SidRunner.read(addr);
            //System.out.println("sid read"+addr);
        }
        return 0;
    }
}
