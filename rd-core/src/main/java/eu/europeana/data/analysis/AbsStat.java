package eu.europeana.data.analysis;

import java.io.PrintStream;

public class AbsStat {

    private int COLS = 80;

    protected void printSection(PrintStream ps, String sTitle)
    {
        ps.println();
        printSeparator(ps);
        printLine(ps, sTitle);
        printSeparator(ps);
    }

    protected void printLine(PrintStream ps, String... sLines)
    {
        for ( String s : sLines )
        {
            ps.print("* ");
            ps.print(s);
            int iL = s.length() + 4;
            for ( int i = iL; i < COLS; i++ ) { ps.print(' '); }
            ps.println(" *");
        }
    }

    protected void printSeparator(PrintStream ps)
    {
        for ( int i = 0; i < COLS; i++ ) { ps.print('*'); }
        ps.println();
    }
}
