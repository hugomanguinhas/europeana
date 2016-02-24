# The 20th Century Black Hole

## Introduction

As cultural heritage institutions across Europe digitize more and more of their collections and
make them available online, an alarming pattern is starting to emerge. Collections that consist
of works dating from the 20th century or that contain large proportions of works from that
period are available online to a much lesser degree than collections from the periods before
or after the 20th century. This effect has been called "*the 20th century black hole*" and can be
attributed to the way copyright interacts with the digitization of cultural heritage collections.

*The purpose of this work was to use the Europeana dataset to explore this claim.*

If you wish to know more information about this investigation and its findings, see: 
* [Europeana BlogPost on Pro] (http://pro.europeana.eu/blogpost/the-missing-decades-the-20th-century-black-hole-in-europeana)
* [Europeana Factsheet on the 20th Century Black Hole]
(http://pro.europeana.eu/files/Europeana_Professional/Advocacy/Twentieth%20Century%20Black%20Hole/copy-of-europeana-policy-illustrating-the-20th-century-black-hole-in-the-europeana-dataset.pdf)

This github project contains the source code that was developed to process and collect 
the necessary information to run the 20th Century Black Hole investigation. 

Contact: Hugo Manguinhas (hugo.manguinhas@europeana.eu)

## Methodology

The following major steps were performed for this experiment:

**1. Obtain values for dcterms:issued from the Europeana API**

For this investigation, it was decided to use only the values from 
dcterms:issued (please see the report for more information on the reasons behind this decision).
To get the data we needed, we queried the Europeana API. Since we wanted to
query only for the values of dcterms:issued, we chose to use the faceted search
with a limit of 350.000 items which significantly reduced the size of the output 
(values come clustered, order by frequency and with hit counts) and time needed 
to get all the necessary values. This is the query that we have used:
```
http://www.europeana.eu/api/v2/search.json?query=*:*&profile=facets&facet=proxy_dcterms_issued&rows=0&f.proxy_dcterms_issued.facet.limit=350000&wskey=api2demo
```
The result of this query is stored in [dctermsissued350000.xml]
(../src/test/resources/etc/blackhole/dctermsissued350000.xml) on the maven test directory.
There you will also find another file [dctermscreated350000.xml]
(../src/test/resources/etc/blackhole/dctermscreated350000.xml) containing the results
for property "dcterms:created" which ended up not being used for this work.

**2. Analyse date patterns within dcterms:issued**

When analysing the dates that we obtained by querying the Europeana API, we understood
that dates are represented in various ways: 
* different formats (e.g. YYYY-MM-DD, DD-MM-YYYY)
* ordinal/cardinal forms 
* date ranges
* additions/annotations stating the actual meaning of that date

This meant that in order to perform this investigation we needed to find ways to extract
the correct date (more precisely the year, see next step) from the value in dcterms:issued. 
For this we developed a XSLT (see [DatePatterns.xsl]
(../src/main/resources/etc/blackhole/DatePatterns.xsl)) to apply a set of patterns 
(expressed as regular expression, see next section) over the XML output of the API 
and return as result the dates clustered by pattern, and 
separating the dates for which no pattern had been identified. The idea was to make sure
that the dates where correctly matching the pattern, and understand if there we patterns
that had not been identified yet. This was repeated until we obtained a significant coverage
of the dates to make this investigation.

**3. Obtain statistics of records per year**

After analysing the date patterns found in dcterms:issued, we had all the necessary information
to, with some level of accuracy, generate the statistics of records per each year. For this,
we developed a second XSLT (see 
[DateResults.xsl](../src/main/resources/etc/blackhole/DateResults.xsl)) which extracts the year using
the patterns within the XSLT and performs the counts for each year. The years were bounded from 
1800 to 2015 reflecting the years that we were interested on focusing our analysis.

## Patterns

This sections shows the patterns we have identified with this work. They can most likely be expanded
to cover more date patterns.

**Note:** The patterns are flexible in a way that they can cope with whitespace and 
in some cases ignore some "irrelevant" punctuation.

| Pattern | Regular Expression | Example | Matching Values | 
| --- | --- | --- | --- |
| `[YYYY]` | `^\s*[\[(]YEAR[\])][^?\w]*$` | `[1980]` | |
| `YYYY` `YYYY-MM` `YYYY-MM-DD` | `^\s*YEAR(\s*[-/.]\d{1,2}){0,2}[^?\w]*$` | `1999-01-28` | |
| `DD-MM-YYYY` | `^\s*\d{1,2}\s*[-=/.]\s*\d{1,2}\s*[-=/.]\s*YEAR[^?\w]*$'` | `24/03/2011` | |
| **Annotated Dates** |
| `YYYY-MM-DD [Publication]` | `^\s*YEAR(\s*[-/.]\d{1,2}){0,2}\s*\[[Pp]ublication\][^?\w]*$` | `1908 [publication]` | |
| `YYYY-MM-DD (first performance)` | `^\s*YEAR(\s*[-/.]\d{1,2}){0,2}\s*\(first performance\)[^?\w]*$` | `1954.12.02 (first performance)` | |
| **Ranges** |
| `YYYY-YYYY` | `'^\s*YEAR\s*[-=/]\s*YEAR[^?\w]*$` | `1980-1990` | |
| | `^\s*YEAR[-/.]\d{1,2}\s*[-=/]\s*YEAR[-/.]\d{1,2}[^?\w]*$` | `2009-10/2009-11` | |
| | `^\s*YEAR[-/.]\d{1,2}[-/.]\d{1,2}\s*[-=/]\s*YEAR[-/.]\d{1,2}[-/.]\d{1,2}[^?\w]*$` | `1939-09-01 - 1939-09-01` | |
| **Timestamps** | 
| `YYYY-MM-DDThh:mm:ssZ` | `^\s*YEAR-\d{2}-\d{2}[T ]\d{2}:\d{2}:\d{2}Z?[^?\w]*$` | `2011-11-29T14:49:21Z` | |

## Resources

The XSLTs created for this experiment:
* [DateResults.xsl](../src/main/resources/etc/blackhole/DateResults.xsl): 
Calculates the number of records per year using a predefined set of patterns. Both the patterns and the year range can be configured using the "patterns" and "dates" parameters.
* [DatePatterns.xsl](../src/main/resources/etc/blackhole/DatePatterns.xsl): 
Clusters the date values per matching pattern using a predefined set of patterns within the XSLT which can be configured using the "patterns" parameter.

The two source files obtained using the Europeana API:
* [dctermsissued350000.xml]
(../src/test/resources/etc/blackhole/dctermsissued350000.xml): all dates from dcterms:issued.
* [dctermscreated350000.xml]
(../src/test/resources/etc/blackhole/dctermscreated350000.xml): all dates from dcterms:created.

The results obtained for each source file:
* See [maven test resources directory](../src/test/resources/etc/blackhole/).

## Usage
To run the software using as source files the ones on the maven test directory 
and using the predefined patterns in the XSLTs, just execute the java class 
[RunBlackHoleExperiment.java]
(../src/test/java/eu/europeana/rd/exp/blackhole/RunBlackHoleExperiment.java), 
like so:
```
java eu.europeana.rd.exp.blackhole.RunBlackHoleExperiment
```

* If you wish to run the experiment over different source data, you can either 
change the java file directly or use class [BlackHoleDateAnalyser.java]
(../src/main/java/eu/europeana/rd/exp/blackhole/BlackHoleDateAnalyser.java) 
in your code.

* If you wish to try different date patterns, you can either add them directly 
to the XSLT files or use the "patterns" parameter on both XSLTs.
