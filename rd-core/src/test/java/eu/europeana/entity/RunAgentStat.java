package eu.europeana.entity;

import java.io.IOException;

import eu.europeana.entity.AgentAnalysis;
import static eu.europeana.TestingResources.*;

public class RunAgentStat
{
    public static final void main(String... args) throws IOException
    {
        new AgentAnalysis().analyse(FILE_AGENTS_DBPEDIA, null);
    }
}
