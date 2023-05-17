# BookStoreBackend

This is a backend using Java Spring Boot and MongoDB for database.

## Running the server

Open the BookStoreBackend in IntelliJ or other IDE of choice.

### Starting MongoDB

Open the `docker-compose.yaml` file. Run the `services` section of the file. It will
automatically run the following command:

```bash
docker-compose -f docker-compose.yaml -p bookstorebackend up -d
```

You should be able to see the MongoDB server running at `http://localhost:8081/`.

### Starting the Java application server

Open the `BookStoreApplication.java` file and run it. It should start the server on port `8080`.

---

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.11/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.11/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.11/reference/htmlsingle/#web)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/2.7.11/reference/htmlsingle/#data.nosql.mongodb)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

