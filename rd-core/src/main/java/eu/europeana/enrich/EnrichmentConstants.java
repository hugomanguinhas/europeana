package eu.europeana.enrich;

import java.io.File;

public interface EnrichmentConstants
{
    public static File DIR_AGENT_COLLECTION
        = new File("C:/Users/Hugo/Google Drive/Europeana/Entity Collection/entities/agents");

    public static File DIR_CONCEPT_COLLECTION
        = new File("C:/Users/Hugo/Google Drive/Europeana/Entity Collection/entities/concepts");

    public static File FILE_AGENT_ENRICHMENTS_HITS
       = new File(DIR_AGENT_COLLECTION, "agents_enrichment_hits.csv");

    public static File FILE_AGENT_PORTAL_HITS
       = new File(DIR_AGENT_COLLECTION, "agents_portal_hits.csv");
}
