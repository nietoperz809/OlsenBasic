package com.sixtyfour.basicshell;

import com.sixtyfour.Basic;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 1/3/2017.
 */
public class ShellFrame
{
    static final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final ArrayBlockingQueue<String> fromTextArea = new ArrayBlockingQueue<>(20);
    private final ArrayBlockingQueue<String> toTextArea = new ArrayBlockingQueue<>(20);
    private JTextArea mainTextArea;
    private JPanel panel1;
    private JButton stopButton;
    private JButton clsButton;
    private Runner runner = null;
    private int[] lastStrLen = new int[2]; // Length of last output chunk

    /**
     * Returns length of the output string before the last one
     * Needed by some input statements
     * @return Lengh of penultimate output
     */
    int getPenultimateOutputSize ()
    {
        return lastStrLen[0];
    }

    private ShellFrame ()
    {
        setupUI();
        mainTextArea.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased (KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                {
                    int lp = getLinePos();
                    try
                    {
                        fromTextArea.put(getLineAt(lp - 2));
                    }
                    catch (InterruptedException e1)
                    {
                        e1.printStackTrace();
                    }
                }
                super.keyReleased(e);
            }
        });
        executor.execute(() ->
        {
            while (true)
            {
                try
                {
                    String s = toTextArea.take();
                    mainTextArea.append(s);
                    mainTextArea.setCaretPosition(mainTextArea.getDocument().getLength());
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        stopButton.addActionListener(e ->
        {
            if (runner != null)
            {
                Basic i = runner.getOlsenBasic();
                i.runStop();
            }
        });
        clsButton.addActionListener(e -> cls());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     */
    private void setupUI ()
    {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
        panel2.setBackground(Color.BLACK);
        panel2.setPreferredSize(new Dimension(600, 34));
        panel1.add(panel2, BorderLayout.SOUTH);
        stopButton = new JButton();
        stopButton.setText("Stop");
        stopButton.setPreferredSize(new Dimension(82, 30));
        stopButton.setText("Stop");
        panel2.add(stopButton);
        clsButton = new JButton();
        clsButton.setPreferredSize(new Dimension(82, 30));
        clsButton.setText("Cls");
        panel2.add(clsButton);
        mainTextArea = new JTextArea();
        mainTextArea.setBackground(new Color(-12679937));
        mainTextArea.setDoubleBuffered(true);
        mainTextArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        mainTextArea.setForeground(Color.YELLOW);
        mainTextArea.setToolTipText("<html>Typ one of:<br>" +
                "- cls<br>- list<br>- run<br>- new<br>" +
                "- save[file]<br>- load[file]<br>- dir<br>" +
                "or edit your BASIC code here</html>");
        //mainTextArea.setLineWrap(true);
        final JScrollPane scrollPane1 = new JScrollPane(mainTextArea);
        DefaultCaret caret = (DefaultCaret) mainTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel1.add(scrollPane1, BorderLayout.CENTER);
        panel1.setPreferredSize(new Dimension(600, 600));
    }

    /**
     * Get number of line where cursor is
     *
     * @return the line number
     */
    private int getLinePos ()
    {
        int caretPos = mainTextArea.getCaretPosition();
        int rowNum = 0;
        for (int offset = caretPos; offset > 0; )
        {
            try
            {
                offset = Utilities.getRowStart(mainTextArea, offset) - 1;
            }
            catch (BadLocationException e)
            {
                e.printStackTrace();
            }
            rowNum++;
        }
        return rowNum;
    }

    /**
     * Return line at specified position
     * @param linenum Line number
     * @return Line as String
     */
    private String getLineAt (int linenum)
    {
        try
        {
            int start = mainTextArea.getLineStartOffset(linenum);
            int end = mainTextArea.getLineEndOffset(linenum);
            return mainTextArea.getText(start, end - start);
        }
        catch (BadLocationException e)
        {
            return ("");
        }
    }

    /**
     * Wipe text area
     */
    private void cls ()
    {
        mainTextArea.setText("");
    }

    /**
     * Main thread entry point
     */
    public static void main (String[] unused)
    {
        JFrame frame = new JFrame("ShellFrame");
        ShellFrame shellFrame = new ShellFrame();
        frame.setContentPane(shellFrame.panel1);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        shellFrame.putString("BASIC V2 Ready.\n");
        shellFrame.commandLoop();
    }

    private void dir()
    {
        File[] filesInFolder = new File(".").listFiles();
        for (final File fileEntry : filesInFolder)
        {
            if (fileEntry.isFile())
            {
                putString(fileEntry.getName()+ " -- " + fileEntry.length()+'\n');
            }
        }
    }

    /**
     * Command loop that runs in main thread
     */
    private void commandLoop ()
    {
        ProgramStore store = new ProgramStore();
        while (true)
        {
            String s = getString();
            String[] split = s.split(" ");
            s = s.toLowerCase();
            if (s.equals("list"))
            {
                putString(store.toString());
            }
            else if (s.equals("new"))
            {
                store.clear();
            }
            else if (s.equals("cls"))
            {
                cls();
            }
            else if (s.equals("run"))
            {
                runner = new Runner(store.toArray(), this);
                runner.synchronousStart();
            }
            else if (s.equals("dir"))
            {
                dir();
            }
            else if (split[0].toLowerCase().equals("save"))
            {
                String msg = store.save(split[1]);
                putString(msg);
            }
            else if (split[0].toLowerCase().equals("load"))
            {
                String msg = store.load(split[1]);
                putString(msg);
            }
            else
            {
                if (!store.insert(s))
                    putString("?Syntax Error.\n");
            }
        }
    }

    /**
     * Get input from text area. Blocks the caller if there is none
     * @return
     */
    public String getString ()
    {
        try
        {
            return fromTextArea.take().trim();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean peek()
    {
        return fromTextArea.peek() != null;
    }

    /**
     * Send text to text area. Blocks thd caller if buffer is full
     * @param outText
     */
    public void putString (String outText)
    {
        try
        {
            toTextArea.put(outText);
            lastStrLen[0] = lastStrLen[1];
            lastStrLen[1] = outText.length();
            //System.out.println(lastStrLen);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

