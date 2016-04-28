# Linking Europeana Collection to Wikidata

## Introduction

As a result of projects such as Europeana Sounds and Europeana Art 280, as well
as other initiatives such as the Wikidata project [Sum of All Paintings]
(https://www.wikidata.org/wiki/Wikidata:WikiProject_sum_of_all_paintings), data 
in Wikidata is referring more and more to objects in the Europeana collection.

This github project contains the source code that was developed to extract
data from Wikidata and generate annotations that will link Europeana objects to
Wikidata entries.

Contact: Hugo Manguinhas (hugo.manguinhas@europeana.eu)

## More on the projects

In [Europeana Sounds](http://www.europeanasounds.eu/), audio recordings are 
uploaded to Wikimedia Commons and where suitable the sounds will be referenced 
from Wikidata and used in Wikipedia articles.

In [Europeana 280](http://pro.europeana.eu/pressrelease/europeana-280-art-from-the-28-countries-of-europe), new Wikidata entities and existing entities representing artworks 
will be created and/or updated based on Europeana metadata. Corresponding 
Wikipedia articles will also be updated. The process has begun at small 
scale already, https://www.wikidata.org/wiki/User:Multichill/Europeana_280_list

## Methodology

The following major steps were performed for this experiment:

**1. Harvest wikidata links to CHOs from SPARQL endpoint**

The wikidata links to CHO resources were harvested from the Wikidata main
SPARQL endpoint available at ```http://query.wikidata.org/sparql```. This was
done by querying for all resources being linked through the 
[Europeana ID](http://www.wikidata.org/entity/P727) property using this SPARQL 
query 
```SELECT ?wdt ?cho WHERE { ?wdt wdt:P727 ?cho }```. All the links obtained
from Wikidata were stored in this 
[file](../src/test/resources/etc/chowdt/links.csv).

**2. Filter links referring to invalid resources**

After harvesting the links from Wikidata, we identified that there were links
referring to resources that had been deleted for which it would not make sense
to create an annotation. Given this, an intermediate step was introduced to
filter out all links which were referring to invalid resources. A resource was
found as invalid if a HTTP 404 was returned when dereferencing the URI. All 
invalid links identified in this step were stored in this 
[file](../src/test/resources/etc/chowdt/links_invalid.csv).

**3. Identifying duplicate links (or resources)**

We also found, after harvesting, that for some links either the CHO or the 
Wikidata resource was being referred twice, which can indicate that 
two of the resource (within the same dataset, i.e. Europeana collection or 
Wikidata) are potential duplicates (see Section on Duplicates).
 
 **4. Create annotations using the Annotation API**

Finally, an Annotation was created for all links that were found valid by 
calling the Annotation API and using the creation method. The semantic tagging
scenario was chosen for representing the annotation since the object linking
is not yet available. However, this should be changed once this method becomes
available. Below is an example of the generated annotation:
```

```

## Wikidata Links

This sections gives an overview of the sort of links that were obtained from
Wikidata. It shows only the ones for the Europeana 280 Project.

#?wkd_links?#

## List of references to potentially duplicate items

The list below shows the Wikidata resources which refer to more than one 
Europeana object. As this typically indicates that the two Europeana objects
are equivalent, a annotation was also generated linking them together.

#?wkd_dup?#

## Resources

The following resources were generated:
* [links.csv]
(../src/test/resources/etc/chowdt/links.csv): All links that were harvested.
* [links_invalid.csv]
(../src/test/resources/etc/chowdt/links_invalid.csv): All 
links that were no longer resolving and therefore considered as invalid.
* [links_dup.csv]
(../src/test/resources/etc/chowdt/links_dup.csv): All duplicate links.
* [links_anno.csv]
(../src/test/resources/etc/chowdt/links_anno.csv): All 
links for which an annotation was created.
* [links_sample.csv]
(../src/test/resources/etc/chowdt/links_sample.csv): A sample of the annotations 
that were created in particular for the Europeana 280 Project.

## Usage

