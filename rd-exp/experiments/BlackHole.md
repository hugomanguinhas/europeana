# The 20th Century Black Hole

This project contains the source code that was developed to collect the 
necessary information to run the 20th Century Black Hole investigation. 

For additional information, see:
* [Europeana BlogPost on Pro] (http://pro.europeana.eu/blogpost/the-missing-decades-the-20th-century-black-hole-in-europeana)
* [Europeana Factsheet on the 20th Century Black Hole]
(http://pro.europeana.eu/files/Europeana_Professional/Advocacy/Twentieth%20Century%20Black%20Hole/copy-of-europeana-policy-illustrating-the-20th-century-black-hole-in-the-europeana-dataset.pdf)

Contact: Hugo Manguinhas (hugo.manguinhas@europeana.eu)

## Introduction

For this investigation, it was decided to use only the values from 
dcterms:issued. 

## Methodolody

The following major steps were performed for this experiment:

1. Obtain values for dcterms:issued from the Europeana API

In order to get the data we have queried the Europeana API. Since we wanted to
query only for the values of dcterms:issued, we chose to use the faceted search
with a limit of 350.000 items which significantly reduced the size of the output 
and time needed to get all the necessary values. This is the query that we have
used:
http://www.europeana.eu/api/v2/search.json?query=*:*&profile=facets&facet=proxy_dcterms_issued&rows=0&f.proxy_dcterms_issued.facet.limit=350000&wskey=api2demo

2. Analyse date patterns within dcterms:issued


## Contents

We need to generate some statistics per each year so that we could prove if
we could identify a possible Black Hole in the data. To do this, a XSLT file was
designed that given a list of patterns for dates, it would calculate for each 
year the total number of references for it by appying the patterns and checking their output.


To see if the patterns that were being used to obtain the counts for each year
were correct, a [second XSLT](DatePatterns.xsl) was designed. This XSLT searches the XML file
for a list of patterns and returns the dates that match each pattern, together
with its count. The list of patterns are provided through the "patterns"
parameter, but the current version already contains the parameters that have 
been identified so far.


## Resources

The XSLTs created for this experiment:
* [DateResults.xsl](../src/main/resources/etc/blackhole/DateResults.xsl): 
Performs the calculations for each date applying the patterns given as parameter.
* [DatePatterns.xsl](../src/main/resources/etc/blackhole/DatePatterns.xsl): 
Performs the calculations for each date applying the patterns given as parameter.

The two source files obtained using the Europeana API:

The results obtained for each source file:
* Results: []
* Patterns: []

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
to the XSLT files or define them using the [BlackHoleDateAnalyser.java]
(../src/main/java/eu/europeana/rd/exp/blackhole/BlackHoleDateAnalyser.java) 
specific constructor.
