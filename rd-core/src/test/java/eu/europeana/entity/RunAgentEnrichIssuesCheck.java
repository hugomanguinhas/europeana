package eu.europeana.entity;

import eu.europeana.entity.analysis.AgentEnrichIssuesAnalyser;

import static eu.europeana.TestingResources.*;

public class RunAgentEnrichIssuesCheck
{
    public static final void main(String[] args)
    {
        new AgentEnrichIssuesAnalyser().analyse(FILE_AGENTS_DBPEDIA);
    }
}
