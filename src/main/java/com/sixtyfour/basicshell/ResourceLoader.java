package com.sixtyfour.basicshell;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to load resources
 */
public class ResourceLoader
{
    private final static String ICON = "commodore.png";
    private final static String FONT = "CommodoreServer.ttf";

    /**
     * Converts path to Inputstream
     * to proide a unified way to load resources
     * @param name
     * @return Inputstream that can be used to access the file
     */
    private static InputStream resourceAsStream (String name)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResourceAsStream(name);
    }

    /**
     * Load the C64 TTF font
     * @return the loaded and resized font
     */
    public static Font getFont()
    {
        Font nt = null;
        try
        {
            nt = Font.createFont(Font.TRUETYPE_FONT, resourceAsStream(FONT));
            return nt.deriveFont(16f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Load the icon
     * @return an Image that can be used as icon
     */
    public static BufferedImage getIcon()
    {
        try
        {
            return ImageIO.read (resourceAsStream(ICON));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
