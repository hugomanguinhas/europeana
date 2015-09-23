package eu.europeana.tools;

public class TranslatorTest {

    public static void main(String[] args) throws Exception
    {
        TranslatorCmd.main("D:/work/incoming/APEX/data3/CZ-00000001092_fa_CZ-NA_ACK.xml"
                         , "D:/work/incoming/APEX/xsl/Europeana_Extended_v2_new.xsl"
                         , "D:/work/incoming/APEX/data3/output/");
    }
}
