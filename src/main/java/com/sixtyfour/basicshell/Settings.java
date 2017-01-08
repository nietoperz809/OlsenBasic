package com.sixtyfour.basicshell;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Created by Administrator on 1/8/2017.
 */
public class Settings
{
    static final Preferences prefs = Preferences.userNodeForPackage(ShellFrame.class);

    private static Color loadColor (String key, String def)
    {
        String s = prefs.get (key, def);
        return new Color(Integer.parseInt(s, 16));
    }

    private static void saveColor (String key, Color c)
    {
        prefs.put (key, Integer.toHexString(c.getRGB()).substring(2));
    }

    static Color loadForegroundColor()
    {
        try
        {
            return loadColor ("FgColor", "7C70DA");
        }
        catch (Exception unused)
        {
            return new Color(0x7C70DA);
        }
    }

    static Color loadBackgroundColor()
    {
        try
        {
            return loadColor ("BgColor", "3E31A2");
        }
        catch (Exception unused)
        {
            return new Color(0x3E31A2);
        }
    }

    static void saveForegroundColor (Color c)
    {
        saveColor("FgColor", c);
    }

    static void saveBackgroundColor (Color c)
    {
        saveColor("BgColor", c);
    }
}
