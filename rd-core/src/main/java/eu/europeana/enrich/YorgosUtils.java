package eu.europeana.enrich;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class YorgosUtils
{
    public static String cleanExcess(String str)
    {
        if (str.contains("[")) { str = StringUtils.substringBefore(str, "[").trim(); }
        if (str.contains("(")) { str = StringUtils.substringBefore(str, "(").trim(); }
        if (str.contains(";")) { str = StringUtils.substringBefore(str, ";").trim(); }
        return str;
    }

    public static List<String> normalizeInternal(String input)
    {
        List<String> normalized = new ArrayList<String>();
        String str = cleanExcess(input);
        if (str.contains(",")) {
            String[] split = str.split(",");
            if (split.length > 1) {
                normalized.add(split[0].trim() + " " + split[1].trim());
                normalized.add(split[1].trim() + " " + split[0].trim());
            } else {
                normalized.add(str);
            }
        } else {
            normalized.add(str);
        }
        return normalized;
    }
}
