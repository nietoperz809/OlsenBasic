package com.sixtyfour.basicshell;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;

/**
 * Subclassed JTextArea
 */
@SuppressWarnings("unchecked")
class ShellTextComponent extends JTextArea
{
    private final ShellFrame parent;

    public ShellTextComponent (ShellFrame sf)
    {
        parent = sf;
        Color fg = Settings.loadForegroundColor();
        Color bg = Settings.loadBackgroundColor();
        setBackground(bg);
        setForeground(fg);
        setCaretColor(new Color(0x7C70DA));
        setDoubleBuffered(true);
        setToolTipText("<html>Type one of:<br>" +
                "- cls<br>- list<br>- run<br>- new<br>" +
                "- save[file]<br>- load[file]<br>- dir<br>" +
                "or edit your BASIC code here</html>");
        BlockCaret mc = new BlockCaret();
        mc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setCaret(mc);
        setFont(ResourceLoader.getFont());
        addKeyListener(new KeyAdapter()
        {
            public void keyTyped (KeyEvent e)
            {
                char keyChar = e.getKeyChar();
                if (Character.isLowerCase(keyChar))
                {
                    e.setKeyChar(Character.toUpperCase(keyChar));
                }
            }
        });
        new DropTarget(this, new DropTargetAdapter()
        {
            @Override
            public void drop (DropTargetDropEvent event)
            {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = event.getTransferable();
                DataFlavor[] flavors = transferable.getTransferDataFlavors();
                for (DataFlavor flavor : flavors)
                {
                    try
                    {
                        if (flavor.isFlavorJavaFileListType())
                        {
                            List<File> files = (List<File>) transferable.getTransferData(flavor);
                            File f = files.get(0);
                            parent.getStore().load(f.getPath());
                            parent.putStringUCase("Loaded: " + f.getName() + "\n" + ProgramStore.OK);
                            return; // only one file
                        }
                    }
                    catch (Exception e)
                    {
                        parent.putString(ProgramStore.ERROR);
                    }
                }
            }
        });
    }

    /**
     * Called if types ctrl+v
     */
    @Override
    public void paste ()
    {
        try
        {
            String[] lines = getClipBoardString().split("[\r\n]+");
            for (String s : lines)
            {
                s = s.trim();
                if (Character.isDigit(s.charAt(0))) // program line?
                {
                    parent.getStore().insert(s.trim());
                }
                else
                {
                    BasicRunner.runLine(s, parent);
                }
            }
            parent.putStringUCase("" + lines.length + " lines pasted\n" + ProgramStore.OK);
        }
        catch (Exception e)
        {
            parent.putString(ProgramStore.ERROR);
            e.printStackTrace();
        }
    }

    private static String getClipBoardString () throws Exception
    {
        Clipboard clipboard = getDefaultToolkit().getSystemClipboard();
        Transferable clipData = clipboard.getContents(clipboard);
        if (clipData != null)
        {
            if (clipData.isDataFlavorSupported(stringFlavor))
            {
                return (String) (clipData.getTransferData(stringFlavor));
            }
        }
        throw new Exception("no clipboard data");
    }
}
