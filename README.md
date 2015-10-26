# WSO2 API Manager Demo

WSO2 API Manage demo is used to demonstrate API Manager's functionality. Demo is
dockerized so it is quite easy to setup. Demo consists of following components:

* WSO2 API Manager (AM)
* WSO2 Business Activity Monitor (BAM) for receiving events from AM and providing statistics
* MySQL for statistics storage
* Google Analytics for tracking statistics in addition to BAM
* Java Service which is exposed to consumer as an API using AM
* Java Client for invoking API
 
# Quickstart

1. Build docker image from Java service. This will build image named as tapantim/demo-service`.
```
cd java/demo-service
./mvnw package docker:build
```
2. Start dockerized demo application.
```
cd docker/demo
docker-compose up -d
```
3. Access API Manager
    * https://192.168.99.100:9443/publisher
    * https://192.168.99.100:9443/store


Note that if you remove docker-compose application you will lose all statistics accumulated in mysql database.

# Overview

```
+------+     api      +------+    service  +-------+
|      |    calls     |      |     calls   |       |
| Java +------------> | WSO2 +-----------> |  Java |
|Client|              |  AM  |             |Service|
|      |     +--------+      +---------+   |       |
+------+     |        +----+-+         |   +-------+
             |             |           |
             |             |     events|
             |events       |           |
             |             |        +--v------+
             |             |        |         |
          +--v---+         |        | Google  |
          |      |         |        |Analytics|
          | WSO2 |         |        |         |
          | BAM  |         |stats   +---------+
          |      |         |query
          +---+--+         |
              |        +---v---+
              |        |       |
              +------> | MySQL |
               stats   |       |
               update  +-------+
```


## Java Service

Java service is Spring Boot application which exposes following resources on docker host port 8080:

* Customer
    * GET /customers
    * GET /customers/{customerId}
* Product
    * GET /products
    * GET /products/{productId}

curl command example for getting customers:
```
curl -i -H "Accept: application/json" -X GET http://192.168.99.100:8080/customers
```



