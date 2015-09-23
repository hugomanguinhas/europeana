package eu.europeana.enrich;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.jena.atlas.json.JsonParseException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.utils.JsonUtils;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import static eu.europeana.enrich.YorgosUtils.*;
import static eu.europeana.edm.EDMNamespace.*;
import static eu.europeana.utils.JenaUtils.*;

public class EnrichmentAPI
{
    public static String URL = "http://testenv-solr.eanadev.org:9191/enrichment-framework-rest-0.1-SNAPSHOT";
    private static Map<String,String[]> _fields = new TreeMap();

    static {
        addFields("CONCEPT" , DC_SUBJECT, DC_TYPE);
        addFields("PLACE"   , DCTERMS_SPATIAL, DC_COVERAGE);
        addFields("AGENT"   , DC_CONTRIBUTOR, DC_CREATOR);
        addFields("TIMESPAN", DC_DATE, EDM_YEAR, DCTERMS_TEMPORAL);
    }

    private static void addFields(String type, String... args)
    {
        String[] atype = new String[] { type };
        for (String arg : args) { _fields.put(arg, atype); }
    }

    private String     _apiURL;
    private HttpClient _client = new HttpClient();

    public EnrichmentAPI() { this(URL); }

    public EnrichmentAPI(String url) { _apiURL = url; }


    /*
    protected PostMethod newGet(String url)
    {
        
        //method.setRequestBody("7|0|10|http://testenv-solr.eanadev.org:9191/enrichment-framework-gui-0.1-SNAPSHOT/MainPage/|B00F9FC9CD85EC4C57B0C095AD8A3889|eu.europeana.enrichment.gui.client.EnrichmentService|enrich|java.util.List|Z|java.util.ArrayList/4159755760|eu.europeana.enrichment.gui.shared.InputValueDTO/2551344374|paris|PLACE|1|2|3|4|2|5|6|7|1|8|9|9|10|0|");
        //method.setRequestHeader("Content-Type", "text/x-gwt-rpc; charset=UTF-8");
        //method.setRequestHeader("Accept", "application/json");
        
        method.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");


        method.setParameter("input", "{\"inputValueList\":[{\"originalField\":\"proxy_dc_subject\",\"value\":\"Music\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dc_subject\",\"value\":\"Ivory\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dc_subject\",\"value\":\"Steel\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dcterms_spatial\",\"value\":\"Paris\",\"vocabularies\":[\"PLACE\"]},{\"originalField\":\"proxy_dc_date\",\"value\":\"1918\",\"vocabularies\":[\"TIMESPAN\"]},{\"originalField\":\"proxy_dc_creator\",\"value\":\"Rembrandt\",\"vocabularies\":[\"AGENT\"]}]}");
        method.setParameter("toXML", "false");

        HttpMethodParams params = new HttpMethodParams();
        params.setParameter(HttpMethodParams.RETRY_HANDLER, 5);
        method.setParams(params);

        return method;
    }
    */

    public List enrich(Resource pcho)
    {
        return enrich(getRequest(pcho));
    }

    public List enrich(List inputs)
    {
        try {
            if ( (inputs == null) || (inputs.size() == 0) ) { return null; }

            Map mInput  = Collections.singletonMap("inputValueList", inputs);
            Map mOutput = (Map)JsonUtils.fromString(enrich(JsonUtils.toString(mInput)));
            return processResponse((List)mOutput.get("wrapperList"));
        } catch (JsonParseException e) {
            System.err.println("Error parsing output: ");
        } catch (JsonGenerationException e) {
            System.err.println("Error serializing input: " + inputs);
        } catch (IOException e) {
            System.err.println("Error serializing input: " + inputs);
        }
        return null;
    }

    public String enrich(String input)
    {
        PostMethod method = new PostMethod(_apiURL + "/enrich");
        method.setParameter("input", input);
        method.setParameter("toXML", "false");
        try {
            int iRet = _client.executeMethod(method);
            if ( iRet == 200 ) { return method.getResponseBodyAsString(); }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private List getRequest(Resource pcho)
    {
        List l = new ArrayList();
        StmtIterator iter = pcho.listProperties();
        while ( iter.hasNext() )
        {
            Statement stmt = iter.nextStatement();
            RDFNode   node = stmt.getObject();
            String    uri  = stmt.getPredicate().getURI();
            if ( node.isResource() || !_fields.containsKey(uri) ) { continue; }

            String    prop = getQName(stmt.getPredicate());
            String    value = node.asLiteral().getString();
            String[]  vocs  = _fields.get(uri);
            for ( String key : normalizeInternal(value) )
            {
                Map m = new HashMap(3);
                m.put("originalField", prop + ";" + value);
                m.put("value", key);
                m.put("vocabularies",  vocs);
                l.add(m);
            }
        }
        return l;
    }

    private List<Map> processResponse(List<Map> rsp)
    {
        List<Map> ret = new ArrayList(rsp.size());
        for ( Map m : rsp )
        {
            String field = (String)m.get("originalField");
            if ( field == null ) { continue; }

            Map mNew = new HashMap(5);
            decodeField(field, mNew);
            mNew.put("enrichment", m.get("url"));
            ret.add(mNew);
        }
        return ret;
    }

    private void decodeField(String field, Map m)
    {
        int index = field.indexOf(";");
        m.put("field", field.substring(0, index));
        m.put("value", field.substring(index+1));
    }

    public static void main(String... args)
    {
        System.out.println(new EnrichmentAPI().enrich("{\"inputValueList\":[{\"originalField\":\"proxy_dc_subject\",\"value\":\"Music\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dc_subject\",\"value\":\"Ivory\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dc_subject\",\"value\":\"Steel\",\"vocabularies\":[\"CONCEPT\"]},{\"originalField\":\"proxy_dcterms_spatial\",\"value\":\"Paris\",\"vocabularies\":[\"PLACE\"]},{\"originalField\":\"proxy_dc_date\",\"value\":\"1918\",\"vocabularies\":[\"TIMESPAN\"]},{\"originalField\":\"proxy_dc_creator\",\"value\":\"Rembrandt\",\"vocabularies\":[\"AGENT\"]}]}"));

        //{"wrapperList":[{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":"proxy_dc_subject","contextualEntity":"{\"about\":\"http://dbpedia.org/resource/Music\",\"id\":\"5473111f2cdc57856f4a952f\",\"prefLabel\":{\"de\":[\"Musik\"],\"zh\":[\"é³ä¹\"],\"it\":[\"Musica\"],\"pt\":[\"MÃºsica\"],\"pl\":[\"Muzyka\"],\"fr\":[\"Musique\"],\"sv\":[\"Musik\"],\"en\":[\"Music\"],\"ru\":[\"ÐÑÐ·ÑÐºÐ°\"],\"es\":[\"MÃºsica\"],\"ja\":[\"é³æ¥½\"],\"nl\":[\"Muziek\"]}}","url":"http://dbpedia.org/resource/Music","originalValue":"music"},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":"proxy_dc_subject","contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/4524\",\"id\":\"547311222cdc57856f4c00d9\",\"prefLabel\":{\"ro\":[\"fildeÅ\"],\"tr\":[\"fildiÅi\"],\"no\":[\"elfenben\"],\"hu\":[\"elefÃ¡ntcsont\"],\"lv\":[\"ziloÅkauls\"],\"lt\":[\"dramblio kaulas\"],\"de\":[\"Elfenbein\"],\"def\":[\"ivory\",\"è±¡ç\"],\"fi\":[\"norsunluu\"],\"bg\":[\"Ð¡Ð»Ð¾Ð½Ð¾Ð²Ð° ÐºÐ¾ÑÑ\"],\"sv\":[\"elfenben\"],\"fr\":[\"ivoire\"],\"sl\":[\"slonovina\"],\"sk\":[\"slonovina\"],\"da\":[\"elfenben\"],\"eu\":[\"boli; marfil\"],\"it\":[\"avorio\"],\"mt\":[\"avorju\"],\"ar\":[\"Ø§ÙØ¹Ø§Ø¬\"],\"cs\":[\"slonovina\"],\"el\":[\"ÎµÎ»ÎµÏÎ±Î½ÏÏÎ´Î¿Î½ÏÎ¿/ÎµÎ»ÎµÏÎ±Î½ÏÎ¿ÏÏÏ\"],\"pl\":[\"koÅÄ sÅoniowa\"],\"pt\":[\"marfim\"],\"en\":[\"ivory\"],\"ru\":[\"ÑÐ»Ð¾Ð½Ð¾Ð²Ð°Ñ ÐºÐ¾ÑÑÑ\"],\"et\":[\"elevandiluu, vandel\"],\"es\":[\"marfil\"],\"nl\":[\"ivoor\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/442\"]}","url":"http://www.eionet.europa.eu/gemet/concept/4524","originalValue":"ivory"},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/442\",\"id\":\"5473111f2cdc57856f4adf3b\",\"prefLabel\":{\"ro\":[\"produs de origine animalÄ\"],\"tr\":[\"hayvan Ã¼rÃ¼nÃ¼\"],\"no\":[\"animalsk produkt\"],\"hu\":[\"Ã¡llati termÃ©k\"],\"lv\":[\"dzÄ«vnieku izcelsmes izstrÄdÄjumi\"],\"lt\":[\"gyvÅ«ninÄs kilmÄs produktas\"],\"de\":[\"Tierisches Produkt\"],\"def\":[\"animal product\",\"å¨ç©äº§å\"],\"fi\":[\"elÃ¤intuote\"],\"bg\":[\"ÐÐ¸Ð²Ð¾ÑÐ¸Ð½ÑÐºÐ¸ Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"sv\":[\"animalisk produkt\"],\"fr\":[\"produit animal\"],\"sl\":[\"proizvod Å¾ivalskega izvora\"],\"sk\":[\"Å¾ivoÄÃ­Å¡ny produkt\"],\"da\":[\"animalsk produkt\"],\"eu\":[\"animalia-produktu\"],\"it\":[\"prodotto animale\"],\"mt\":[\"prodotti tal-annimali\"],\"ar\":[\"ÙÙØªØ¬ Ø­ÙÙØ§ÙÙ\"],\"cs\":[\"vÃ½robek Å¾ivoÄiÅ¡nÃ½\"],\"el\":[\"Î¶ÏÎ¹ÎºÏ ÏÏÎ¿ÏÏÎ½\"],\"pl\":[\"produkty zwierzÄce\"],\"pt\":[\"produtos de origem animal\"],\"en\":[\"animal product\"],\"ru\":[\"Ð¶Ð¸Ð²Ð¾ÑÐ½Ð¾Ð²Ð¾Ð´ÑÐµÑÐºÐ°Ñ Ð¿ÑÐ¾Ð´ÑÐºÑÐ¸Ñ\"],\"et\":[\"loomakasvatussaadus\"],\"es\":[\"productos animales\"],\"nl\":[\"dierlijk product\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/5510\"]}","url":"http://www.eionet.europa.eu/gemet/concept/442","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/5510\",\"id\":\"5473111e2cdc57856f4a94b0\",\"prefLabel\":{\"ro\":[\"material natural\"],\"tr\":[\"doÄal malzeme\"],\"no\":[\"naturmateriale\"],\"hu\":[\"termÃ©szetes anyag\"],\"lv\":[\"dabisks materiÄls\"],\"lt\":[\"natÅ«rali medÅ¾iaga\"],\"de\":[\"Naturstoffe\"],\"def\":[\"natural material\",\"å¤©ç¶ææ\"],\"fi\":[\"luonnonmateriaali\"],\"bg\":[\"ÐÑÑÐµÑÑÐ²ÐµÐ½ Ð¼Ð°ÑÐµÑÐ¸Ð°Ð»\"],\"fr\":[\"matÃ©riaux naturels\"],\"sv\":[\"naturligt Ã¤mne\"],\"sl\":[\"naravna snov, naravni material\"],\"sk\":[\"prÃ­rodnÃ½ materiÃ¡l\"],\"eu\":[\"material natural; gai natural\"],\"da\":[\"naturlige materialer\"],\"it\":[\"materiale naturale\"],\"mt\":[\"materjal naturali\"],\"ar\":[\"ÙØ§Ø¯Ø© Ø·Ø¨ÙØ¹ÙØ©\"],\"cs\":[\"materiÃ¡l pÅÃ­rodnÃ­\"],\"el\":[\"ÏÏÏÎ¹ÎºÏ ÏÎ»Î¹ÎºÏ/ÏÏÏÎ¹ÎºÎ® ÏÎ»Î·\"],\"pl\":[\"materiaÅ pochodzenia naturalnego\"],\"pt\":[\"produtos naturais\"],\"en\":[\"natural material\"],\"ru\":[\"Ð¿ÑÐ¸ÑÐ¾Ð´Ð½ÑÐ¹ Ð¼Ð°ÑÐµÑÐ¸Ð°Ð»\"],\"et\":[\"looduslik materjal\"],\"es\":[\"materiales naturales\"],\"nl\":[\"natuurlijke materiaal\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/5086\"]}","url":"http://www.eionet.europa.eu/gemet/concept/5510","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/5086\",\"id\":\"547311212cdc57856f4b837b\",\"prefLabel\":{\"ro\":[\"material\"],\"tr\":[\"materyal, malzeme\"],\"no\":[\"materiale\"],\"hu\":[\"anyag\"],\"lv\":[\"materiÄls\"],\"lt\":[\"medÅ¾iaga\"],\"de\":[\"Werkstoff\"],\"def\":[\"material\",\"ææ\"],\"fi\":[\"materiaali\"],\"bg\":[\"ÐÐ°ÑÐµÑÐ¸Ð°Ð»\"],\"sv\":[\"material\"],\"fr\":[\"matÃ©riau\"],\"sl\":[\"snov, material\"],\"sk\":[\"materiÃ¡l\"],\"da\":[\"materialer\"],\"eu\":[\"material; gai\"],\"it\":[\"materiali\"],\"mt\":[\"materjal\"],\"ar\":[\"ÙØ§Ø¯Ø©\"],\"cs\":[\"materiÃ¡l\"],\"el\":[\"ÏÎ»Î¹ÎºÎ¬\"],\"pt\":[\"materiais\"],\"pl\":[\"materiaÅ\"],\"en\":[\"material\"],\"ru\":[\"Ð¼Ð°ÑÐµÑÐ¸Ð°Ð»\"],\"et\":[\"materjal\"],\"es\":[\"materiales\"],\"nl\":[\"materialen\"]}}","url":"http://www.eionet.europa.eu/gemet/concept/5086","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":"proxy_dc_subject","contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/8088\",\"id\":\"547311242cdc57856f4c7ea8\",\"prefLabel\":{\"ro\":[\"oÅ£el\"],\"tr\":[\"Ã§elik\"],\"no\":[\"stÃ¥l\"],\"hu\":[\"acÃ©l\"],\"lv\":[\"tÄrauds\"],\"lt\":[\"plienas\"],\"de\":[\"Stahl\"],\"def\":[\"steel\",\"é¢\"],\"fi\":[\"terÃ¤s\"],\"bg\":[\"Ð¡ÑÐ¾Ð¼Ð°Ð½Ð°\"],\"fr\":[\"acier\"],\"sv\":[\"stÃ¥l\"],\"sl\":[\"jeklo\"],\"sk\":[\"oceÄ¾\"],\"eu\":[\"altzairu\"],\"da\":[\"stÃ¥l\"],\"it\":[\"acciaio\"],\"mt\":[\"azzar\"],\"ar\":[\"ÙÙÙØ§Ø° - ØµÙÙØ¨\"],\"cs\":[\"ocel\"],\"el\":[\"ÏÎ¬Î»ÏÎ²Î±Ï\"],\"pt\":[\"aÃ§o\"],\"pl\":[\"stal\"],\"en\":[\"steel\"],\"ru\":[\"ÑÑÐ°Ð»Ñ\"],\"et\":[\"teras\"],\"es\":[\"acero\"],\"nl\":[\"staal\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/5180\"]}","url":"http://www.eionet.europa.eu/gemet/concept/8088","originalValue":"steel"},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/5180\",\"id\":\"547311222cdc57856f4bbbce\",\"prefLabel\":{\"ro\":[\"produs metalic\"],\"tr\":[\"metal Ã¼rÃ¼n\"],\"no\":[\"metallprodukt\"],\"hu\":[\"fÃ©mtermÃ©k\"],\"lv\":[\"metÄla izstrÄdÄjums\"],\"lt\":[\"metalinis gaminys\"],\"de\":[\"Metallwaren\"],\"def\":[\"metal product\",\"éå±å¶å\"],\"fi\":[\"metallituote\"],\"bg\":[\"ÐÐµÑÐ°Ð»ÐµÐ½ Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"sv\":[\"metallvara; metallprodukt\"],\"fr\":[\"produit mÃ©tallique\"],\"sl\":[\"kovinski izdelek\"],\"sk\":[\"kovovÃ© vÃ½robky\"],\"da\":[\"metalprodukt\"],\"eu\":[\"produktu metaliko; metalezko produktu\"],\"it\":[\"prodotto metallico\"],\"mt\":[\"prodott tal-metall\"],\"ar\":[\"ÙÙØªØ¬ ÙØ¹Ø¯ÙÙ\"],\"cs\":[\"vÃ½robek kovovÃ½\"],\"el\":[\"Î¼ÎµÏÎ±Î»Î»Î¹ÎºÏ ÏÏÎ¿ÏÏÎ½\"],\"pl\":[\"produkt metalowy\"],\"pt\":[\"produtos metÃ¡licos\"],\"en\":[\"metal product\"],\"ru\":[\"Ð¼ÐµÑÐ°Ð»Ð»Ð¸ÑÐµÑÐºÐ¸Ð¹ Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"et\":[\"metallsaadus\"],\"es\":[\"producto metÃ¡lico\"],\"nl\":[\"metaalproduct\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/4260\"]}","url":"http://www.eionet.europa.eu/gemet/concept/5180","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/4260\",\"id\":\"547311232cdc57856f4c5d98\",\"prefLabel\":{\"ro\":[\"produs industrial\"],\"tr\":[\"endÃ¼striyel Ã¼rÃ¼n\"],\"no\":[\"industriprodukt\"],\"hu\":[\"ipari termÃ©k\"],\"lv\":[\"rÅ«pniecÄ«bas izstrÄdÄjums\"],\"lt\":[\"pramoninis gaminys\"],\"de\":[\"Industrieprodukt\"],\"def\":[\"industrial product\",\"å·¥ä¸äº§å\"],\"fi\":[\"tehdasvalmiste, teollisuustuote\"],\"bg\":[\"ÐÑÐ¾Ð¼Ð¸ÑÐ»ÐµÐ½ Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"sv\":[\"industriprodukt\"],\"fr\":[\"produit industriel\"],\"sl\":[\"industrijski izdelek, industrijski proizvod\"],\"sk\":[\"priemyselnÃ½ produkt\"],\"da\":[\"industriprodukt\"],\"eu\":[\"industria produktu; produktu industrial\"],\"it\":[\"prodotti industriali\"],\"mt\":[\"prodott industrijali\"],\"ar\":[\"ÙÙØªØ¬ ØµÙØ§Ø¹Ù\"],\"cs\":[\"vÃ½robek prÅ¯myslovÃ½\"],\"el\":[\"Î²Î¹Î¿Î¼Î·ÏÎ±Î½Î¹ÎºÏ ÏÏÎ¿ÏÏÎ½\"],\"pl\":[\"produkt przemysÅowy\"],\"pt\":[\"produtos industriais\"],\"en\":[\"industrial product\"],\"ru\":[\"Ð¿ÑÐ¾Ð¼ÑÑÐ»ÐµÐ½Ð½ÑÐ¹ Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"et\":[\"tÃ¶Ã¶stustoode\"],\"es\":[\"productos industriales\"],\"nl\":[\"industrieel product\"]},\"broader\":[\"http://www.eionet.europa.eu/gemet/concept/6660\"]}","url":"http://www.eionet.europa.eu/gemet/concept/4260","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.ConceptImpl","originalField":null,"contextualEntity":"{\"about\":\"http://www.eionet.europa.eu/gemet/concept/6660\",\"id\":\"547311222cdc57856f4bba57\",\"prefLabel\":{\"ro\":[\"produs\"],\"tr\":[\"Ã¼rÃ¼n\"],\"no\":[\"produkt\"],\"hu\":[\"termÃ©k\"],\"lv\":[\"produkts, izstrÄdÄjums\"],\"lt\":[\"produktas, gaminys\"],\"de\":[\"Produkt\"],\"def\":[\"product\",\"äº§å\"],\"fi\":[\"tuotteet\"],\"bg\":[\"ÐÑÐ¾Ð´ÑÐºÑ\"],\"fr\":[\"produit\"],\"sv\":[\"produkter; varor\"],\"sl\":[\"izdelek, proizvod\"],\"sk\":[\"produkt\"],\"da\":[\"produkter\"],\"eu\":[\"produktu\"],\"it\":[\"prodotti\"],\"mt\":[\"prodott\"],\"ar\":[\"ÙÙØªÙØ¬\"],\"cs\":[\"vÃ½robek\"],\"el\":[\"ÏÏÎ¿ÏÏÎ½ÏÎ±\"],\"pl\":[\"produkt\"],\"pt\":[\"produtos\"],\"en\":[\"product\"],\"ru\":[\"Ð¿ÑÐ¾Ð´ÑÐºÑ\"],\"et\":[\"saadus, produkt\"],\"es\":[\"producto\"],\"nl\":[\"producten\"]}}","url":"http://www.eionet.europa.eu/gemet/concept/6660","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.PlaceImpl","originalField":"proxy_dcterms_spatial","contextualEntity":"{\"about\":\"http://sws.geonames.org/2988507/\",\"id\":\"5473110f2cdc57856f44c761\",\"prefLabel\":{\"tl\":[\"Lungsod ng Paris\",\"Paris\"],\"ca\":[\"ParÃ­s\"],\"tr\":[\"Paris\"],\"no\":[\"Paris\"],\"nn\":[\"Paris\"],\"fy\":[\"Parys\"],\"tg\":[\"ÐÐ°ÑÐ¸Ð¶\"],\"gd\":[\"Paris\"],\"br\":[\"Pariz\"],\"ga\":[\"PÃ¡ras\"],\"th\":[\"à¸à¸²à¸£à¸µà¸ª\"],\"oc\":[\"ParÃ­s\"],\"fi\":[\"Pariisi\"],\"ta\":[\"à®ªà®¾à®°à®¿à®¸à¯\"],\"ka\":[\"ááá ááá\"],\"bg\":[\"ÐÐ°ÑÐ¸Ð¶\"],\"fr\":[\"Lutece\",\"Paname\",\"Pantruche\",\"Paris\",\"Ville-LumiÃ¨re\"],\"sv\":[\"Paris\"],\"sw\":[\"Paris\"],\"be\":[\"ÐÐ°ÑÑÐ¶\"],\"kw\":[\"Paris\"],\"sl\":[\"Pariz\"],\"os\":[\"ÐÐ°ÑÐ¸Ð¶\"],\"sk\":[\"ParÃ­Å¾\"],\"da\":[\"Paris\"],\"sr\":[\"ÐÐ°ÑÐ¸Ð·\"],\"ku\":[\"ParÃ®s\"],\"sq\":[\"Paris\",\"Parisi\"],\"ko\":[\"íë¦¬ ì\"],\"cy\":[\"Paris\"],\"he\":[\"×¤×¨××\"],\"kn\":[\"à²ªà³à²¯à²¾à²°à²¿à²¸à³\"],\"cs\":[\"PaÅÃ­Å¾\"],\"li\":[\"Paries\"],\"gl\":[\"ParÃ­s - Paris\"],\"la\":[\"Lutetia Parisorum\"],\"gv\":[\"Paarys\"],\"pl\":[\"ParyÅ¼\"],\"ru\":[\"ÐÐ°ÑÐ¸Ð¶\"],\"lb\":[\"ParÃ¤is\"],\"hr\":[\"Pariz\"],\"ln\":[\"Pari\"],\"zh\":[\"å·´é»\"],\"ro\":[\"Paris\"],\"vi\":[\"Pa-ri\",\"Paris\"],\"hu\":[\"PÃ¡rizs\"],\"lv\":[\"ParÄ«ze\"],\"lt\":[\"ParyÅ¾ius\"],\"id\":[\"Paris\"],\"de\":[\"Paris\"],\"ia\":[\"Paris\"],\"def\":[\"75091\",\"Paris\"],\"mg\":[\"Paris\"],\"hy\":[\"ÕÕ¡ÖÕ«Õ¦\"],\"mk\":[\"ÐÐ°ÑÐ¸Ð·\"],\"uk\":[\"ÐÐ°ÑÐ¸Ð¶\"],\"eu\":[\"Paris\"],\"is\":[\"ParÃ­s\"],\"it\":[\"Parigi\"],\"mr\":[\"à¤ªà¥à¤°à¤¿à¤¸\"],\"ug\":[\"Ù¾Ø§Ø±ÙÚ\"],\"ms\":[\"Paris\"],\"ur\":[\"Ù¾ÛØ±Ø³\"],\"fa\":[\"Ù¾Ø§Ø±ÛØ³\"],\"ar\":[\"Ø¨Ø§Ø±ÙØ³\"],\"io\":[\"Paris\"],\"ty\":[\"Paris\"],\"na\":[\"Paris\"],\"el\":[\"Î Î±ÏÎ¯ÏÎ¹\"],\"am\":[\"ááªáµ\"],\"an\":[\"ParÃ­s\"],\"pt\":[\"Paris\"],\"eo\":[\"Parizo\"],\"en\":[\"Paris\"],\"et\":[\"Pariis\"],\"es\":[\"ParÃ­s\"],\"ja\":[\"ããª\"],\"nl\":[\"Parijs\"],\"af\":[\"Parys\"]},\"isPartOf\":{\"def\":[\"http://sws.geonames.org/3017382/\"]},\"latitude\":48.85341,\"longitude\":2.3488}","url":"http://sws.geonames.org/2988507/","originalValue":"paris"},{"className":"eu.europeana.corelib.solr.entity.PlaceImpl","originalField":null,"contextualEntity":"{\"about\":\"http://sws.geonames.org/3017382/\",\"id\":\"5473110e2cdc57856f444535\",\"prefLabel\":{\"no\":[\"Frankrike\"],\"fy\":[\"Frankryk\"],\"nn\":[\"Frankrike\"],\"gd\":[\"An Fhraing\"],\"ga\":[\"An Fhrainc\"],\"oc\":[\"FranÃ§a\"],\"fi\":[\"Ranska\"],\"om\":[\"France\"],\"fr\":[\"France\",\"RÃ©publique FranÃ§aise\"],\"fo\":[\"Frakland\"],\"os\":[\"Ð¤ÑÐ°Ð½Ñ\"],\"he\":[\"×¦×¨×¤×ª\"],\"gl\":[\"Francia\"],\"pl\":[\"Francja\"],\"gv\":[\"Yn Rank\"],\"gu\":[\"àª«à«àª°àª¾àªàª¸\"],\"lo\":[\"àºàº¥àº±à»àº\"],\"ln\":[\"Falansia\"],\"dv\":[\"ÞÞ¦ÞÞ¦ÞÞ°ÞÞ­ÞÞ¨ÞÞ¨ÞÞ§ÞÞ°\"],\"vi\":[\"PhÃ¡p\"],\"dz\":[\"à½à½¢à½±à½à½¦à½²\"],\"lv\":[\"Francija\"],\"lt\":[\"PrancÅ«zija\"],\"vo\":[\"FransÃ¤n\"],\"de\":[\"Frankreich\"],\"uz\":[\"Ð¤ÑÐ°Ð½ÑÐ¸Ñ\"],\"def\":[\"FR\",\"France\",\"Republic of France\"],\"mg\":[\"Frantsa\"],\"mk\":[\"Ð¤ÑÐ°Ð½ÑÐ¸ÑÐ°\"],\"ml\":[\"à´«àµà´°à´¾à´¨àµâà´¸àµ\"],\"mn\":[\"Ð¤ÑÐ°Ð½Ñ\",\"Ð¤ÑÐ°Ð½Ñ ÑÐ»Ñ\"],\"eu\":[\"Frantzia\"],\"uk\":[\"Ð¤ÑÐ°Ð½ÑÑÑ\"],\"mr\":[\"à¤«à¥à¤°à¤¾à¤¨à¥à¤¸\"],\"mt\":[\"Franza\"],\"ug\":[\"ÙØ±Ø§ÙØ³ÙÙÛ\"],\"ms\":[\"Perancis\"],\"ur\":[\"ÙØ±Ø§ÙØ³\"],\"fa\":[\"ÙØ±Ø§ÙØ³Ù\"],\"ty\":[\"FarÄni\"],\"na\":[\"France\"],\"nb\":[\"Frankrike\"],\"el\":[\"ÎÎ±Î»Î»Î¯Î±\"],\"ne\":[\"à¤«à¥à¤°à¤¾à¤¨à¥à¤¸\"],\"eo\":[\"Francio\",\"Francujo\"],\"en\":[\"France\"],\"et\":[\"Prantsusmaa\"],\"es\":[\"Francia\"],\"nl\":[\"Frankrijk\"],\"to\":[\"FalanisÄ\"],\"ca\":[\"FranÃ§a\"],\"tl\":[\"Pransya\"],\"tr\":[\"Fransa\"],\"tg\":[\"Ð¤Ð°ÑÐ¾Ð½ÑÐ°\"],\"te\":[\"à°«à±à°°à°¾à°¨à±à°¸à±â\"],\"bs\":[\"Francuska\"],\"br\":[\"Bro-C'hall\"],\"th\":[\"à¸à¸£à¸°à¹à¸à¸¨à¸à¸£à¸±à¹à¸à¹à¸¨à¸ª\",\"à¸à¸£à¸±à¹à¸à¹à¸¨à¸ª\"],\"bn\":[\"à¦«à§à¦°à¦¾à¦¨à§à¦¸\"],\"bo\":[\"à½à¼à½¢à½±à½à¼à½¦à½²à¼\"],\"ta\":[\"à®ªà®¿à®°à®¾à®©à¯à®¸à¯\"],\"ka\":[\"á¡áá¤á áááááá\"],\"sv\":[\"Frankrike\"],\"bg\":[\"Ð¤ÑÐ°Ð½ÑÐ¸Ñ\"],\"st\":[\"France\"],\"sw\":[\"Ufaransa\"],\"be\":[\"Ð¤ÑÐ°Ð½ÑÑÑ\"],\"sl\":[\"Francija\"],\"kw\":[\"Pow Frynk\"],\"sk\":[\"FrancÃºzsko\"],\"da\":[\"Frankrig\"],\"ks\":[\"à¤«à¥à¤°à¤¾à¤à¤¸\"],\"so\":[\"Faransiis\",\"Faransiiska\"],\"ku\":[\"Fransa\"],\"sr\":[\"Ð¤ÑÐ°Ð½ÑÑÑÐºÐ°\"],\"sq\":[\"Franca\",\"FrancÃ«\"],\"ko\":[\"íëì¤\"],\"cy\":[\"Ffrainc\"],\"sc\":[\"Frantza\"],\"se\":[\"FrÃ¡nkriika\"],\"sh\":[\"Francuska\"],\"cv\":[\"Ð¤ÑÐ°Ð½ÑÐ¸\"],\"km\":[\"áá¶áá¶áá\"],\"kn\":[\"à²«à³à²°à²¾à²¨à³à²¸à³\"],\"cs\":[\"Francie\"],\"li\":[\"Frankriek\"],\"co\":[\"Francia\"],\"sa\":[\"à¤«à¥à¤°à¤¾à¤à¤¸\"],\"la\":[\"Francia\",\"Francogallia\"],\"lb\":[\"FrankrÃ¤ich\"],\"ru\":[\"Ð¤ÑÐ°Ð½ÑÐ¸Ñ\"],\"hr\":[\"Francuska\"],\"zh\":[\"æ³å½\"],\"ro\":[\"FranÅ£a\"],\"rm\":[\"Frantscha\"],\"ht\":[\"Frans\"],\"hu\":[\"FranciaorszÃ¡g\"],\"hi\":[\"à¤«à¤¼à¥à¤°à¤¾à¤à¤¸\",\"à¤«à¤¼à¥à¤°à¤¾à¤à¤¸\",\"à¤«à¥à¤°à¤¾à¤à¤¸\"],\"id\":[\"Perancis\",\"Prancis\"],\"ia\":[\"Francia\"],\"qu\":[\"Fransiya\",\"Fransya\"],\"hy\":[\"ÕÖÕ¡Õ¶Õ½Õ«Õ¡\"],\"az\":[\"Fransa\"],\"is\":[\"Frakkland\"],\"it\":[\"Francia\"],\"ii\":[\"êê©\"],\"ar\":[\"ÙØ±ÙØ³Ø§\"],\"io\":[\"Francia\"],\"am\":[\"áá¨áá£á­\",\"áá¨áá³á­\"],\"an\":[\"Franzia\"],\"pt\":[\"FranÃ§a\"],\"aa\":[\"France\"],\"ja\":[\"ãã©ã³ã¹\",\"ãã©ã³ã¹å±åå½\"],\"af\":[\"Frankryk\"],\"ps\":[\"ÙØ±Ø§ÙØ³Ù\"]}}","url":"http://sws.geonames.org/3017382/","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":"proxy_dc_date","contextualEntity":"{\"about\":\"http://semium.org/time/1918\",\"id\":\"5473111b2cdc57856f4a5166\",\"prefLabel\":{\"def\":[\"1918\"]},\"begin\":{\"def\":[\"Tue Jan 01 00:19:32 CET 1918\"]},\"end\":{\"def\":[\"Tue Dec 31 00:19:32 CET 1918\"]},\"isPartOf\":{\"def\":[\"http://semium.org/time/19xx_1_third\"]}}","url":"http://semium.org/time/1918","originalValue":"1918"},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":null,"contextualEntity":"{\"about\":\"http://semium.org/time/19xx_1_third\",\"id\":\"5473111b2cdc57856f4a40f3\",\"prefLabel\":{\"en\":[\"Early 20th century\"],\"ru\":[\"ÐÐ°ÑÐ°Ð»Ð¾ 20-Ð³Ð¾ Ð²ÐµÐºÐ°\"]},\"begin\":{\"def\":[\"Tue Jan 01 00:19:32 CET 1901\"]},\"end\":{\"def\":[\"Sun Dec 31 00:19:32 CET 1933\"]},\"isPartOf\":{\"def\":[\"http://semium.org/time/19xx\"]}}","url":"http://semium.org/time/19xx_1_third","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":null,"contextualEntity":"{\"about\":\"http://semium.org/time/19xx\",\"id\":\"5473111b2cdc57856f4a4ccd\",\"prefLabel\":{\"def\":[\"20..\",\"20??\",\"20e\"],\"fr\":[\"20e siÃ¨cle\"],\"en\":[\"20-th\",\"20th\",\"20th century\"],\"ru\":[\"20Ð¹ Ð²ÐµÐº\"],\"nl\":[\"20de eeuw\"]},\"begin\":{\"def\":[\"Tue Jan 01 00:19:32 CET 1901\"]},\"end\":{\"def\":[\"Sun Dec 31 01:00:00 CET 2000\"]},\"isPartOf\":{\"def\":[\"http://semium.org/time/AD2xxx\"]}}","url":"http://semium.org/time/19xx","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":null,"contextualEntity":"{\"about\":\"http://semium.org/time/AD2xxx\",\"id\":\"5473111b2cdc57856f4a4be8\",\"prefLabel\":{\"fr\":[\"2e millÃ©naire aprÃ¨s J.-C.\"],\"en\":[\"Second millenium AD\",\"Second millenium AD, years 1001-2000\"]},\"isPartOf\":{\"def\":[\"http://semium.org/time/ChronologicalPeriod\"]}}","url":"http://semium.org/time/AD2xxx","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":null,"contextualEntity":"{\"about\":\"http://semium.org/time/ChronologicalPeriod\",\"id\":\"5473111b2cdc57856f4a4add\",\"prefLabel\":{\"en\":[\"Chronological period\"]},\"isPartOf\":{\"def\":[\"http://semium.org/time/Time\"]}}","url":"http://semium.org/time/ChronologicalPeriod","originalValue":null},{"className":"eu.europeana.corelib.solr.entity.TimespanImpl","originalField":null,"contextualEntity":"{\"about\":\"http://semium.org/time/Time\",\"id\":\"5473111a2cdc57856f4a373c\",\"prefLabel\":{\"en\":[\"Time\"]}}","url":"http://semium.org/time/Time","originalValue":null}]}
    }
}
