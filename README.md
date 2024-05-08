# grpcdemo
---
## Features
1. generate code from gRPC proto files
2. generate code from openapi specifications
3. uber-jar packaging support , where jar genarated would include all the necessary dependencies 
    so that it could be launched/executed on any platform without worrying about any dependency
> both the features work within single module, no conflict between protobuf or quarkus plugin 
4. Unit, Component, Integration Testing

### Prerequisite for Unit Testing
as part of testing following dependencies is must to have
`quarkus-junit5`

`quarkus-junit5` is required for testing, as it provides the `@QuarkusTest` annotation that controls the testing framework.

> ### what do you mean by "@QuarkusTest controls the testing framework" ?
> 1. **Configures Test Environment:** It sets up the necessary configuration, including any test-specific properties that you've defined.
> 2. **Starts the Application Context:** It starts up a lightweight version of your Quarkus application's runtime environment, including dependency injection and configuration.
> 3. **Injects Required Resources:** It injects resources such as the EntityManager, RestAssured, and others if specified, into your test class.
> 4. **Starts Embedded Services:** It can start embedded services like an in-memory database (when using @QuarkusTestResource) or a web server to handle REST API requests.
> 5. **Executes Test Methods:** Once the test environment is set up, it executes the test methods in your test class.

Because we are using JUnit 5, the version of the `Surefire Maven Plugin` must be set, as the default version does not support Junit 5
```xml
<plugin>
<artifactId>maven-surefire-plugin</artifactId>
<version>${surefire-plugin.version}</version>
<configuration>
<systemPropertyVariables>
<java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
<maven.home>${maven.home}</maven.home>
</systemPropertyVariables>
</configuration>
</plugin>
```
`java.util.logging.manager` system property to make sure tests will use the correct logmanager and maven.home to ensure that custom configuration from `${maven.home}/conf/settings.xml` is applied (if any).

### On which design pattern the ResAssured is built? What features it comes for Integration Testing?
> RestAssured doesn't strictly adhere to a particular design pattern, but its design is heavily influenced by the **builder pattern** and the **fluent interface pattern**.

1. **Builder Pattern:** RestAssured uses the builder pattern to construct HTTP requests and responses. 

For example:

```java
RestAssured
        .given()
            .baseUri("http://api.example.com")
            .basePath("/users")
            .param("id", 123)
            .header("Authorization", "Bearer token")
        .when()
            .get("/profile")
        .then()
            .statusCode(200);
```

In this code snippet, each method call `(given(), baseUri(), basePath(), param(), header(), when(), etc.)` returns an instance of the `RequestSpecification` or `ResponseSpecification`, allowing for the fluent chaining of method calls to construct complex requests and assertions.

2. **Fluent Interface Pattern:**
The fluent interface pattern aims to provide a more readable and expressive API by allowing method chaining. RestAssured's fluent API makes it easy to write integration tests that read like natural language. 
For example:
```java
RestAssured.
    given()
        .baseUri("http://api.example.com")
        .basePath("/users")
    .when()
        .get("/profile")
    .then()
        .statusCode(200);
```

This code reads like English: 
>**Given** the base URI is 'http://api.example.com' and the base path is '/users', 
**when** I make a GET request to '/profile', 
**then** the response status code should be 200.

**Features for Integration Testing provided by RestAssured:**
> 1. **Easy Request Configuration:** easily configure various aspects of the HTTP request, including headers, query parameters, request body, etc., using a fluent API.
> 2. **Powerful Response Validation:** validate various aspects of the HTTP response, such as status code, response body content, response headers, response time, etc., using intuitive and expressive assertions.
> 3. **Support for Different Content Types:** supports different content types (JSON, XML, etc.) out of the box, making it easy to work with APIs that produce or consume different types of data.
> 4. **Mocking and Stubbing:** integrates seamlessly with tools like WireMock and Mockito, allowing you to mock external dependencies or simulate different scenarios for more comprehensive integration testing.
> 5. **Logging and Debugging:** provides logging capabilities that help in debugging integration tests by logging the details of HTTP requests and responses, making it easier to diagnose issues.
> 6. **Integration with Testing Frameworks:** integrates with popular testing frameworks like JUnit and TestNG, allowing you to incorporate API tests into your existing test suites.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/grpcdemo-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.
