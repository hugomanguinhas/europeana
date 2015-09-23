/**
 * 
 */
package eu.europeana.ld.negotiation;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Aug 2015
 */
public class NegotiationUtils
{
    public static boolean isRspOK(int code)       { return ((code/100) == 2); }
    public static boolean isRspRedirect(int code) { return ((code/100) == 3); }
}
