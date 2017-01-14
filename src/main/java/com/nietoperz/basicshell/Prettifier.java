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
    private final StringList list = new StringList();
    private final StringBuilder sb = new StringBuilder();

    public Prettifier (ProgramStore ps)
    {
        theStore = ps;
    }

    /**
     * Dynamic array for Strings
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
     * Helper function for tokenization
     */
    private void switchToNextToken ()
    {
        list.add (sb.toString());
        sb.setLength(0);
    }

    /**
     * Tokenize a string
     * @param in input string
     * @return ArrayList of tokens in same order as in input string
     */
    private StringList tokenize(String in)
    {
        sb.setLength(0);
        list.clear();

        char charBefore = 0;
        boolean quote = false;

        for (int s=0; s<in.length(); s++)
        {
            char charPresent = in.charAt(s);
            if (charPresent == '\"')
            {
                if (!quote)    // enter quote state
                {
                    switchToNextToken();
                    sb.append(charPresent);
                }
                else             // leave quote state
                {
                    sb.append(charPresent);
                    switchToNextToken();
                }
                quote = !quote;
                continue;
            }
            if (quote)   // leave out other stuff when in quote state
            {
                sb.append(charPresent);
                continue;
            }
            if (charBefore == 0)
            {

            }
            else if (Character.isWhitespace(charBefore))
            {
                switchToNextToken();
            }
            else if (Character.isDigit(charBefore))
            {
                if (!Character.isDigit(charPresent))
                {
                    switchToNextToken();
                }
            }
            else if (Character.isLetter(charBefore))
            {
                if (!Character.isLetter(charPresent) &&
                        !Character.isDigit(charPresent) &&
                        charPresent != '$' && charPresent != '%')
                {
                    switchToNextToken();
                }
            }
            else
            {
                switchToNextToken();
            }
            sb.append(charPresent);
            charBefore = charPresent;
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
        for (int n = 0; n<arr.size(); n++)
        {
            String s = arr.get(n);
            sb.append(s).append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * Adjust jump target
     * @param list tokenized basic line
     * @param keyword keyword (goto, gosub, ...)
     */
    private void adjust (StringList list, String keyword)
    {
        int state = 0;
        for (int s=0; s<list.size(); s++)
        {
            String token = list.get(s).toUpperCase();
            switch (state)
            {
                case 0:
                    if (token.equals(keyword.toUpperCase()))
                        state = 1;
                    break;

                case 1:
                    String newnum = theMap.get(token);
                    if (newnum != null)
                        list.set(s, newnum);
                    state = 2;
                    break;

                case 2:
                    if (token.equals(","))
                        state = 1;
                    else
                        state = 0;
                    break;
            }
        }
    }

    public void doRenumber ()
    {
        doRenumber(10,10);
    }

    public void doRenumber(int start, int stepWidth)
    {
        // Pass #1: renumber line numbers
        String[] prog = theStore.toArray();
        theStore.clear();
        for (String line : prog)
        {
            StringList sl = tokenize(line);
            System.out.println(Arrays.toString(sl.toArray()));
            theMap.put(sl.get(0), ""+start);
            sl.set(0, ""+start);
            start += stepWidth;
            theStore.insert(concat(sl));
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

    public void doPrettify()
    {
        String[] prog = theStore.toArray();
        for (String line : prog)
        {
            String s = concat(tokenize(line));
            theStore.insert(s);
        }
    }
}
