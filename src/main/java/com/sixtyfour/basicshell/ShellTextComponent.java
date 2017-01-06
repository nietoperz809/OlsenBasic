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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.awt.datatransfer.DataFlavor.stringFlavor;

/**
 * Created by Administrator on 1/6/2017.
 */
class ShellTextComponent extends JTextArea
{
    ShellFrame parent;

    private DropTarget dropTarget = new DropTarget(this, new DropTargetAdapter()
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
                        for (File file : files)
                        {
                            parent.getStore().clear();
                            parent.getStore().load(file.getPath());
                            parent.putString("Loaded: "+file.getName()+"\n"+ProgramStore.OK);
                            return; // only one file
                        }
                    }
                }
                catch (Exception e)
                {
                    parent.putString(ProgramStore.ERROR);
                }
            }
        }
    });

    public ShellTextComponent (ShellFrame sf)
    {
        parent = sf;
        setBackground(new Color(-12679937));
        setDoubleBuffered(true);
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        setForeground(Color.YELLOW);
        setToolTipText("<html>Type one of:<br>" +
                "- cls<br>- list<br>- run<br>- new<br>" +
                "- save[file]<br>- load[file]<br>- dir<br>" +
                "or edit your BASIC code here</html>");
        BlockCaret mc = new BlockCaret();
        mc.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setCaret(mc);
        setFont (ResourceLoader.getFont());
    }

    @Override
    public void paste ()
    {
        try
        {
            String[] lines = getClipBoardString().split("[\r\n]+");
            for (String s : lines)
            {
                parent.getStore().insert(s.trim());
            }
            parent.putString("" + lines.length + " lines pasted\n" + ProgramStore.OK);
        }
        catch (Exception e)
        {
            parent.putString(ProgramStore.ERROR);
            e.printStackTrace();
        }
    }

    public static String getClipBoardString () throws Exception
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
        throw new Exception("no clpboard data");
    }
}
