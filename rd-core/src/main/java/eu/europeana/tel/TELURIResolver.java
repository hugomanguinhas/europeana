package eu.europeana.tel;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.europeana.api.RecordAPI;
import static eu.europeana.edm.EDMNamespace.*;

public class TELURIResolver {

    public static Pattern TEL_DOMAIN
        = Pattern.compile("^http[:]//data[.]theeuropeanlibrary[.]org/(BibliographicResource/\\d+)$");

    public RecordAPI _api = new RecordAPI();

    public String resolve(String uri)
    {
        String localID = getLocalID(uri);
        if (localID == null) { return uri; }

        localID = localID.replace('/', '_');
        try {
            return getIDFromResult(_api.searchByID(localID));
        } catch (IOException e) {
            System.err.println("Error resolving: " + uri + ", reason: " + e.getMessage());
        }
        return null;
    }

    private String getIDFromResult(Map result)
    {
        if ( result == null ) { return null; }

        List l = (List)result.get("items");
        if ( (l == null) || (l.size() < 1) ) { return null; }

        Map m = (Map)l.get(0);
        String id = (String)m.get("id");
        if ( (id == null) || (id.length() < 1) ) { return null; }
        return ( DATA_PROVIDEDCHO + id.substring(1) );
    }

    private String getLocalID(String id)
    {
        if (id == null  ) { return null; }

        Matcher m = TEL_DOMAIN.matcher(id);
        return (!m.matches() ? null : m.group(1));
    }

    public static void main(String[] args)
    {
        System.out.println(new TELURIResolver().resolve("http://data.theeuropeanlibrary.org/BibliographicResource/1000054930437"));
    }
}
