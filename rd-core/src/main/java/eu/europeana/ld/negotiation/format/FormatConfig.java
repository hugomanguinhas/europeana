/**
 * 
 */
package eu.europeana.ld.negotiation.format;

/**
 * @author Hugo Manguinhas <hugo.manguinhas@europeana.eu>
 * @since 11 Aug 2015
 */
public class FormatConfig
{
    private String[]        _prefMimes;
    private String[]        _mimetypes;
    private FormatValidator _validator;

    public FormatConfig(FormatValidator val, int numPref, String... mimetypes)
    {
        _validator = val;
        _prefMimes = new String[numPref];
        int iL = mimetypes.length;
        for ( int i = 0; (i < numPref) && (i < iL); i++ )
        {
            _prefMimes[i] = mimetypes[i];
        }
        _mimetypes = mimetypes;
    }

    public String[]        getPreferredMimetypes() { return _prefMimes; }
    public String[]        getMimetypes()          { return _mimetypes; }
    public FormatValidator getValidator()          { return _validator; }
}