package eu.europeana.tools;

import java.io.InputStream;
import java.io.InputStreamReader;

public class TranslatorCmdInfo
{
    private static String HEADER = loadFile("header.txt");
    private static String USAGE  = loadFile("usage.txt");
    private static String FOOTER = loadFile("footer.txt");
    private static String SEP
        = "\n===============================================================================\n";


    /**************************************************************************/
    /* Public Methods
    /**************************************************************************/
    public static void printError(String message)
    {
        System.out.println(HEADER);
        System.out.println("  Error: " + message);
        System.out.println(SEP);
        System.out.println(USAGE);
        System.out.println(FOOTER);
    }

    public static void printHeader()
    {
        System.out.println(HEADER);
    }

    public static void printHelp()
    {
        System.out.println(HEADER);
        System.out.println(FOOTER);
    }


    /**************************************************************************/
    /* Private Methods
    /**************************************************************************/
    private static String loadFile(String filename)
    {
        InputStream is = TranslatorCmdInfo.class.getResourceAsStream(filename);
        if ( is == null ) { return ""; }

        StringBuilder builder = new StringBuilder(1024);
        InputStreamReader reader = new InputStreamReader(is);
        char[] buffer = new char[1024];

        try {
            while ( reader.ready() )
            {
                builder.append(buffer, 0, reader.read(buffer));
            }
        }
        catch (Throwable t) { return ""; }

        return builder.toString();
    }
}
