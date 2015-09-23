package eu.europeana.entity;

import java.io.IOException;

import eu.europeana.entity.ConceptAnalysis;
import static eu.europeana.TestingResources.*;

public class RunConceptStat
{
    public static final void main(String... args) throws IOException
    {
        new ConceptAnalysis().analyse(FILE_CONCEPTS_DBPEDIA, null);
    }
}
