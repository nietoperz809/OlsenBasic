package com.nietoperz.basicshell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Administrator on 1/11/2017.
 */
public class Prettifier
{
    private final ProgramStore theStore;
    private final HashMap<String, String> theMap = new HashMap<>();

    public Prettifier (ProgramStore ps)
    {
        theStore = ps;
    }

    /**
     * Dyn array for Strings
     */
    class StringList extends ArrayList<String>
    {
        /**
         * Discard whitespace only strings
         * @param s input
         * @return result from superclass method
         */
        public boolean add (String s)
        {
            s = s.trim();
            return s.isEmpty() || super.add(s);
        }
    }

    /**
     * Tokenizes a string
     * @param in input string
     * @return ArrayList of tokens in same order as in input string
     */
    private StringList tokenize(String in)
    {
        StringList list = new StringList();
        StringBuilder sb = new StringBuilder();
        char lastc = 0;
        boolean inquote = false;

        for (int s=0; s<in.length(); s++)
        {
            char c = in.charAt(s);
            if (c == '\"')
            {
                if (!inquote)
                {
                    list.add (sb.toString());
                    sb.setLength(0);
                    inquote = true;
                    sb.append(c);
                }
                else
                {
                    sb.append(c);
                    list.add (sb.toString());
                    sb.setLength(0);
                    inquote = false;
                }
                continue;
            }
            if (inquote)
            {
                sb.append(c);
                continue;
            }
            if (lastc == 0)
            {

            }
            else if (Character.isWhitespace(lastc))
            {
                list.add (sb.toString());
                sb.setLength(0);
            }
            else if (Character.isDigit(lastc))
            {
                if (!Character.isDigit(c))
                {
                    list.add (sb.toString());
                    sb.setLength(0);
                }
            }
            else if (Character.isLetter(lastc))
            {
                if (!Character.isLetter(c))
                {
                    list.add (sb.toString());
                    sb.setLength(0);
                }
            }
            else
            {
                if (Character.isDigit(c) || Character.isLetter(c))
                {
                    list.add (sb.toString());
                    sb.setLength(0);
                }
            }
            sb.append(c);
            lastc = c;
        }
        list.add (sb.toString());
        return list;
    }

    /**
     * Stick stringlist together ...
     * @param arr input list
     * @return all put together
     */
    private String concat (StringList arr)
    {
        StringBuilder sb = new StringBuilder();
        for (String s : arr)
        {
            sb.append(s).append(' ');
        }
        return sb.toString().trim();
    }

    public void doPrettify()
    {
        String[] prog = theStore.toArray();
        for (String line : prog)
        {
            String s = concat(tokenize(line));
            theStore.insert(s);
        }
    }

    /**
     * Adjust jump target
     * @param list tokenized basic line
     * @param keyword keyword (goto, gosub, ...)
     */
    private void adjust (StringList list, String keyword)
    {
        boolean flag = false;
        for (int s=0; s<list.size(); s++)
        {
            String token = list.get(s).toUpperCase();
            if (flag)
            {
                String newnum = theMap.get(token);
                if (newnum != null)
                    list.set(s, newnum);
                flag = false;
            }
            else if (token.equals(keyword.toUpperCase()))
            {
                flag = true;
            }
        }
    }

    public void doRenumber()
    {
        // Pass #1: renumber line numbers
        String[] prog = theStore.toArray();
        theStore.clear();
        int start = 10;
        for (String line : prog)
        {
            StringList sl = tokenize(line);
            System.out.println(Arrays.toString(sl.toArray()));
            theMap.put(sl.get(0), ""+start);
            sl.set(0, ""+start);
            start += 10;
            String s = concat(sl);
            theStore.insert(s);
        }
        // Pass #2: adjust goto, etc
        prog = theStore.toArray();
        theStore.clear();
        for (String line : prog)
        {
            StringList sl = tokenize(line);
            adjust(sl,"goto");
            adjust(sl,"gosub");
            adjust(sl,"then");
            String s = concat(sl);
            theStore.insert(s);
        }
    }

//    public static void main (String[] args)
//    {
//        String s = "if a = 10 then 100:gosub300";
//        ArrayList<String> ar = tokenize(s);
//        System.out.println(Arrays.toString(ar.toArray()));
//    }
}
