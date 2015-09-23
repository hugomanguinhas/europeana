package eu.europeana.oai;

import ORG.oclc.oai.harvester2.verb.ListRecords;

public class TestOAI {

    public static void main(String[] args) throws Exception
    {
        String url = "http://digi.ub.uni-heidelberg.de/cgi-bin/digioai.cgi";
        ListRecords list = new ListRecords(url, null, null, "handschriften", "ese");
        String token = list.getResumptionToken();

        while ( token != null && !token.trim().isEmpty() )
        {
            list = new ListRecords(url, token);
            token = list.getResumptionToken();
        }
        //ListRecords list = new ListRecords(url, "{\"until\":null,\"urn\":\"\",\"from\":null,\"metadataPrefix\":\"ese\",\"start\":200,\"set\":\"handschriften\"}");
    }
}
