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

1. Build docker image from Java service. This will build an image named as tapantim/demo-service.

        cd java/demo-service
        ./mvnw package docker:build
        
2. Start dockerized demo application.

        cd docker/demo
        docker-compose up -d

3. Access API Manager
    * https://192.168.99.100:9443/publisher
    * https://192.168.99.100:9443/store


Note that if you remove docker-compose application you will lose all statistics accumulated in mysql database.

# Components

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

Java service is Spring Boot application which exposes following resources on docker host on port 8080:

* Customer
    * GET /customers
    * GET /customers/{customerId}
* Product
    * GET /products
    * GET /products/{productId}

curl command example for getting all customers and single customer (valid customer ids: 1, 2):
```
curl -i -H "Accept: application/json"-X GET http://192.168.99.100:8080/customers

curl -i -H "Accept: application/json" -X GET http://192.168.99.100:8080/customers/1
```

## Java Client

Java client is used to generate API calls. Perform following steps in order to use demo client:

1. Create API in API publisher with following specs:
    * Context path: /api
    * Version: v1
    * At least one resource having: method = GET, path = /customers    
2. In API store create application, associate the application with above API and generate keys in subscription page.
3. Copy consumer id and secret key from AM's subscription page and paste them into demo-client/src/resources/aplication.properties
4. Run demo-client 

        cd demo-clien
        ./mvnw spring-boot:run  
        
5. Open http://localhost:8080
6. Login with API Store account such as admin/admin. Client uses OAuth to get access token.
7. Specify call count and the client starts to call API sequentially.
8. Go to GA to view real-time statistics

## Google Analytics

Demo setup is configured so that AM publishes statistics to Google Analytics. Contact Timo Tapanainen to get access
into GA account.



