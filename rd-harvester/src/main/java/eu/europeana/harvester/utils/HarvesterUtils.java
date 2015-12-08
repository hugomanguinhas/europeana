/**
 * 
 */
package eu.europeana.harvester.utils;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 7 Dec 2015
 */
public class HarvesterUtils
{
    private static String URI_PREFIX = "http://data.europeana.eu/";
    private static String PREFIX     = "http://data.europeana.eu/item/";

    public static String getDataset(String urn)
    {
        int i = urn.lastIndexOf('/');
        if ( i <= 0 || !urn.startsWith(PREFIX) ) { return null; }
        return urn.substring(PREFIX.length(), i);
    }

    public static String getLocalID(String urn)
    {
        if ( !urn.startsWith(PREFIX) ) { return null; }
        return urn.substring(PREFIX.length());
    }

    public static boolean isEuropeanaURI(String url)
    {
        return url.startsWith(URI_PREFIX);
    }
}
