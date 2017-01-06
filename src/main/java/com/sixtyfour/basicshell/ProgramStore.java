package com.sixtyfour.basicshell;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * Created by Administrator on 1/4/2017.
 */
public class ProgramStore
{
    public static final String ERROR = "ERROR.\n";
    public static final String OK = "READY.\n";
    private final TreeSet<String> store = new TreeSet<>(new LineComparator());

    public String[] toArray ()
    {
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        String[] arr = new String[clone.size()];
        int n = 0;
        for (String s : clone)
        {
            arr[n++] = s;
        }
        return arr;
    }

    public void insert (String codeLine) throws NumberFormatException
    {
        if (codeLine.trim().isEmpty())
            return;
        int num = getLineNumber(codeLine);
        try
        {
            int num2 = Integer.parseInt(codeLine); // Number only?
            removeLine(num);
        }
        catch (NumberFormatException ex)
        {
            addLine(codeLine);
        }
    }

    private int getLineNumber (String in) throws NumberFormatException
    {
        StringBuilder buff = new StringBuilder();
        for (int s = 0; s < in.length(); s++)
        {
            char c = in.charAt(s);
            if (!Character.isDigit(c))
            {
                break;
            }
            buff.append(c);
        }
        return Integer.parseInt(buff.toString());
    }

    private void removeLine (int num)
    {
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        for (String s : clone)
        {
            if (getLineNumber(s) == num)
            {
                store.remove(s);
            }
        }
    }

    private void addLine (String s)
    {
        removeLine(getLineNumber(s));
        store.add(s);
    }

    public void clear ()
    {
        store.clear();
    }

    public String load (String path)
    {
        try
        {
            try (Stream<String> stream = Files.lines(Paths.get(path)))
            {
                stream.forEach(this::insert);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return ERROR;
        }
        return OK;
    }

    public String save (String path)
    {
        boolean ok = true;
        String txt = this.toString();
        FileWriter outFile = null;
        try
        {
            outFile = new FileWriter(path);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ok = false;
        }
        try
        {
            PrintWriter out1 = new PrintWriter(outFile);
            try
            {
                out1.append(txt);
            }
            finally
            {
                out1.close();
            }
        }
        finally
        {
            try
            {
                outFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ok = false;
            }
        }
        return ok ? OK : ERROR;
    }

    @Override
    public String toString ()
    {
        TreeSet<String> clone = (TreeSet<String>) store.clone();  // avoid java.util.ConcurrentModificationException
        StringBuilder sb = new StringBuilder();
        for (String s : clone)
        {
            sb.append(s).append('\n');
        }
        return sb.toString();
    }

    class LineComparator implements Comparator<String>
    {
        @Override
        public int compare (String s1, String s2)
        {
            return getLineNumber(s1) - getLineNumber(s2);
        }
    }
}
