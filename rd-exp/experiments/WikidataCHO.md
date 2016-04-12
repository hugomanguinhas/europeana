# Linking Europeana Collection to Wikidata

## Introduction

As a result of projects such as Europeana Sounds and Europeana Art 280, as well
as other and initiatives such as the Wikidata project Sum of All Paintings, data 
in Wikidata is refering more and more to objects in the Europeana collection.

This github project contains the source code that was developed to extract
data from Wikidata and generate annotations that will link Europeana objects to
Wikidata entries. 

Contact: Hugo Manguinhas (hugo.manguinhas@europeana.eu)

## More on the projects

In Europeana Sounds, audio recordings are uploaded to Wikimedia Commons and 
where suitable the sounds will be referenced from Wikidata and used in Wikipedia articles.

In Art 280 new Wikidata entities and existing entities representing artworks 
will be created and/or updated based on Europeana metadata. Corresponding 
Wikipedia articles will also be updated. The process has been begun at small 
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


| Europeana Object | Wikidata Entry | Annotation |
| :--- | :--- | :--- |
[](http://data.europeana.eu/item/2063602/SWE_280_001) | [](http://www.wikidata.org/entity/Q21257918) | [184](http://test-annotations.europeana.eu/annotation/webanno/184?wskey=apidemo) |


## Duplicates

This section gives an overview of the resources for which we found duplicate
references:


| Wikidata Entry | Europeana Object 1 | Europeana Object 2 |
| :--- | :--- | :--- |
[](http://www.wikidata.org/entity/Q11721791) | [](http://data.europeana.eu/item/2020718/DR_31756) | [](http://data.europeana.eu/item/2020718/DR_508960) |


## Resources

The following resource were generated:
* [links.csv]
(../src/test/resources/etc/chowdt/links.csv): A CSV file containing all links
that were harvested.
* [links_invalid.csv]
(../src/test/resources/etc/chowdt/links_invalid.csv): A CSV file containing all 
links that were no longer resolving and therefore considered as invalid.
* [links_dup.csv]
(../src/test/resources/etc/chowdt/links_dup.csv): A CSV file containing all 
duplicate links.
* [links_anno.csv]
(../src/test/resources/etc/chowdt/links_anno.csv): A CSV file containing all 
links for which an annotation was created.
* [links_sample.csv]
(../src/test/resources/etc/chowdt/links_sample.csv): A CSV file containing just 
a sample of the annotations that were created in particular for the Europeana
280 Project.

## Usage

