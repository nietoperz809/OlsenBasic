package com.sixtyfour.basicshell;

import java.awt.*;

/**
 * Created by Administrator on 1/8/2017.
 */
public class C64Colors
{
    private static final Color[] COLORS = {
            new Color(0),
            new Color(0xffffff),
            new Color(0x880000),
            new Color(0xaaffee),
            new Color(0xcc44cc),
            new Color(0x00cc55),
            new Color(0x0000aa),
            new Color(0xeeee77),
            new Color(0xdd8855),
            new Color(0x664400),
            new Color(0xff7777),
            new Color(0x333333),
            new Color(0x777777),
            new Color(0xaaff66),
            new Color(0x0088ff),
            new Color(0xbbbbbb),
    };

    public static Color get(int idx)
    {
        try
        {
            return COLORS[idx % COLORS.length];
        }
        catch (Exception unused)
        {
            return Color.BLACK;
        }
    }
}
