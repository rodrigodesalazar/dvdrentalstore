# DVD Rental Store

## Building
To build the executable jar just do:

> ./mvnw clean package

This will run the tests, build the jar and generate the documentation.

Of course, the database must be set up as expected, in the same host:

> ./mvnw generate-resources -Pdocker

> docker run -d -p 5432:5432 --name sakila-pg sakila-img

## Testing
To just run the application tests:

> ./mvnw test

## Running
To run the application:

> java -jar target/dvdrentalstore-1.0.0-SNAPSHOT.jar

## Documentation
Once built, the API usage documentation can be found at:

> target/generated-docs/index.html

## Notes
Logs are, by default, set at INFO and sent to the console.

Assumptions and considerations are commented in the code.

## **Enjoy!**
