# catalog-service

## Schema

Two alternative data stores are used:
* relational data base (H2DB)
* graph data base (Neo4J)

## Category path

There are three alternative implementations for the category path:
* `CategoryService.loadCategoryPath` traverses the tree in Java code.
* `CategoryRepository.loadCategoryPath` traverses the tree with an recursive SQL-statement.
* `Neo4jCategoryRepository.loadCategoryPath` uses an Neo4J cypher query.

A further alternative would be to materialize the paths in the tree in a separate table
as described in [this paper](http://fungus.teststation.com/~jon/treehandling/TreeHandling.htm).

## Price conversion

The price conversion to Euro is done on the fly for every request. 
Performance and resilience could be improved by replicating exchange rate to the service data store.