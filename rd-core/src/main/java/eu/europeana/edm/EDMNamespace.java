package eu.europeana.edm;

public interface EDMNamespace
{
    public static String DATA_NS              = "http://data.europeana.eu/";
    public static String DATA_PROVIDEDCHO     = DATA_NS + "item/";

    public static String EDM_NS               = "http://www.europeana.eu/schemas/edm/";
    //Classes
    public static String EDM_PROVIDEDCHO      = EDM_NS + "ProvidedCHO";
    public static String EDM_EAGGREGATION     = EDM_NS + "EuropeanaAggregation";
    public static String EDM_WEBRESOURCE      = EDM_NS + "WebResource";
    public static String EDM_AGENT            = EDM_NS + "Agent";
    public static String EDM_PLACE            = EDM_NS + "Place";
    public static String EDM_TIMESPAN         = EDM_NS + "TimeSpan";
    //Properties
    public static String EDM_ISRELATEDTO      = EDM_NS + "isRelatedTo";
    public static String EDM_END              = EDM_NS + "end";
    public static String EDM_YEAR             = EDM_NS + "year";

    public static String RDF_NS               = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static String RDF_TYPE             = RDF_NS + "type";

    public static String DC_NS                = "http://purl.org/dc/elements/1.1/";
    public static String DC_CONTRIBUTOR       = DC_NS + "contributor";
    public static String DC_COVERAGE          = DC_NS + "coverage";
    public static String DC_CREATOR           = DC_NS + "creator";
    public static String DC_DATE              = DC_NS + "date";
    public static String DC_IDENTIFIER        = DC_NS + "identifier";
    public static String DC_SUBJECT           = DC_NS + "subject";
    public static String DC_TYPE              = DC_NS + "type";
    public static String DC_TITLE             = DC_NS + "title";
    public static String DC_DESCRIPTION       = DC_NS + "description";

    public static String DCTERMS_NS           = "http://purl.org/dc/terms/";
    public static String DCTERMS_SPATIAL      = DCTERMS_NS + "spatial";
    public static String DCTERMS_TEMPORAL     = DCTERMS_NS + "temporal";

    public static String FOAF_NS              = "http://xmlns.com/foaf/0.1/";
    public static String FOAF_NAME            = FOAF_NS + "name";

    public static String ORE_NS               = "http://www.openarchives.org/ore/terms/";
    public static String ORE_AGGREGATION      = ORE_NS + "Aggregation";
    public static String ORE_PROXY            = ORE_NS + "Proxy";
    public static String ORE_PROXYFOR         = ORE_NS + "proxyFor";
    public static String ORE_PROXYIN          = ORE_NS + "proxyIn";

    public static String OWL_NS               = "http://www.w3.org/2002/07/owl#";
    public static String OWL_SAMEAS           = OWL_NS + "sameAs";

    public static String RDAGR2_NS               = "http://RDVocab.info/ElementsGr2/";
    public static String RDAGR2_BIBINFO          = RDAGR2_NS + "biographicalInformation";
    public static String RDAGR2_DATEOFBIRTH      = RDAGR2_NS + "dateOfBirth";
    public static String RDAGR2_DATEOFDEATH      = RDAGR2_NS + "dateOfDeath";
    public static String RDAGR2_PROFOROCCUPATION = RDAGR2_NS + "professionOrOccupation";

    public static String SKOS_NS             = "http://www.w3.org/2004/02/skos/core#";
    public static String SKOS_CONCEPT        = SKOS_NS + "Concept";
    public static String SKOS_CONCEPT_SCHEME = SKOS_NS + "ConceptScheme";
    public static String SKOS_CLOSE_MATCH = SKOS_NS + "closeMatch";
    public static String SKOS_PREF_LABEL  = SKOS_NS + "prefLabel";
    public static String SKOS_ALT_LABEL   = SKOS_NS + "altLabel";
    public static String SKOS_EXACT_MATCH = SKOS_NS + "exactMatch";
    public static String SKOS_NOTE        = SKOS_NS + "note";
    public static String SKOS_IN_SCHEME   = SKOS_NS + "inScheme";

    public static String[] NAMESPACES         = { RDF_NS, EDM_NS, DC_NS, DCTERMS_NS
                                                , FOAF_NS, ORE_NS, OWL_NS, RDAGR2_NS
                                                , SKOS_NS };

    public static String[] CONTEXTUAL_ENTITIES = { EDM_PLACE, EDM_AGENT, SKOS_CONCEPT, EDM_TIMESPAN };
}
