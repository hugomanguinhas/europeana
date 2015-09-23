package eu.europeana.edm;

import static eu.europeana.edm.EDMNamespace.*;

public class EuropeanaDataURI
{
    public String getURI(String localID) { return DATA_PROVIDEDCHO + localID; }
}
