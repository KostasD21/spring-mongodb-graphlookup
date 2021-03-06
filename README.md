![Java CI with Maven](https://github.com/kmandalas/spring-mongodb-graphlookup/workflows/Java%20CI%20with%20Maven/badge.svg)

# Description
This a simplistic Dockerized Spring Boot app for testing hierarchical data retrieval (and other operations) with MongoDB. 
Created for the purposes of the DZone article https://dzone.com/articles/manage-hierarchical-data-in-mongodb-with-spring.

# References
In order to be able to follow-up better the reasoning, check the following stuff:
- https://docs.mongodb.com/manual/applications/data-models-tree-structures/
- https://www.slideshare.net/mongodb/webinar-working-with-graph-data-in-mongodb
- https://docs.mongodb.com/manual/reference/operator/aggregation/graphLookup/

# Prerequisites
In order to execute the app:
- Install Docker and Docker Compose :whale:

In order to build, test etc:
- Apache Maven 3.5.x and above
- Java 11 and above

# Usage
Sample data is provided (see [nodes.json](https://github.com/kmandalas/spring-mongodb-graphlookup/blob/master/mongo-init/data-import/nodes.json)).
The data are like a "forest of trees" i.e. multiple trees under a "virtual root" node with id (`nodeId`) having the value "-1".
Each tree is identified by its `treeId` and each node by is `nodeId`.

## Endpoints 

Method	| Path	| Description
------------- | ------------------------- | ------------- |
GET	| /app/{treeId}	| retrieve a whole hierarchical structure by treeId
GET	| /app/{treeId}/set/{nodeId}	| retrieve a sub-tree of a treeId, starting from the node with a given nodeId
POST | under development | add a new node to a given tree (arbitrary depth)
PUT | under development | update an existing node
DELETE | under development | delete a node
GET | under development | compare subtrees with [Javers](https://javers.org/)

You can have a view of a whole tree from the imported tree-structure by performing an HTTP-GET operation:
- http://localhost:8080/app/1001

Then you may retrieve sub-trees by performing am HTTP-GET operation on the following URL:
- http://localhost:8080/app/1001/st/100

## How to run
You can try this app with the following 2 ways:

- by spinning a MongoDB cluster with `docker-compose.yml` and run the Spring-boot app standalone (non-dockerized)
- by using `docker-compose-all.yml` and bring up both the MongoDB cluster and the app all-together (dockerized)

## Allow cluster connectivity
This step is necessary if you want to run the Spring-boot app standalone and/or to be able to execute Integration tests.

Modify your **/etc/hosts** file and add the following 3 entries:

- 127.0.0.1 mongo1
- 127.0.0.1 mongo2
- 127.0.0.1 mongo3

### Build/Test
First of all you need to build the app with:
```    
mvn clean build
```
If you want to execute Integration tests, go with:
```    
mvn verify
```
With the help of `docker-compose-maven-plugin` this will start a MongoDB replica set and then the integration tests will be executed.

### Run the Spring-boot app non-dockerized
Bring up the MongoDB cluster with:
```
docker-compose up
```
Run the application with:
```
mvn spring-boot:run
```

### Run the Spring-boot app dockerized
Alternatively, if you want to start everything with docker-compose, execute the following:
```
mvn clean package
docker-compose -f docker-compose-all.yml build
docker-compose -f docker-compose-all.yml up
```

### Load sample data
Once the cluster is up, load the sample data, with:
```
docker-compose exec mongo1 mongoimport --host mongo1 --db test --collection nodes --type json --file /tmp/nodes.json --jsonArray
```

###
To bring down all the containers, execute:
```
docker-compose down -v --remove-orphans
```
This way the containers are disposed and cleanup is performed.
   
# Additional information

## $graphLookup example
You can try this via Mongo Shell:
```
db.nodes.aggregate([ 
{ $match: { treeId: 1001, $and: [ { nodeId: 100 } ] } },
{
 $graphLookup: {
    from: "nodes",
    startWith: "$nodeId",
    connectFromField: "nodeId",
    connectToField: "parentId",
    restrictSearchWithMatch: {"treeId": 1001},
    as: "descendants"
 }
}
]);
```

## Schema indexes
Indexes on the following fields (based on the sample data) are necessary for achieving performance:
- nodeId
- treeId
- parentId
