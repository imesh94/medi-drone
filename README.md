# Medi-Drone 1.0.0

### Introduction

Welcome to Medi-Drone-1.0.0. This project is intended to manage a fleet of drones 
that are used to transport medication. You can find instructions to build and run the project below.

### Build

The project was tested in the following environment.

- Java Version - openjdk-17.0.10
- Maven Version - 3.9.6
- Operating System - MacOS Sonoma 14.3
- Database - H2

To build the project, run the following command in the current directory

> mvn clean install

Test coverage reports can be found at `drone/target/site/jacoco/index.html`

Dockerfile is available if you want to create a docker image. Use the following 
command to create the image and start the container.

> docker build -t medi-drone:latest .
> 
> docker run -p 8080:8080 medi-drone:latest

### Run Locally

To start the application, run the following command in the current directory

> mvn spring-boot:run

### Testing

At the start, data for 10 drones and 5 medications are available in 
the database. To test the functionality, send GET requests to the 
following 2 endpoints.

> http://localhost:8080/drones
> 
> http://localhost:8080/medications

Postman collection with all the requests that can be used to manage the 
system is available in `Medi Drone - Imesh.postman_collection.json`. Please import the collection to postman 
to test the system.
Details of all 16 endpoints are available in the Open API file 
`Medi-DroneAPI-1.0.0.yaml`. Please use https://editor.swagger.io/ to view the Open API file.

### Assumptions

Following are the assumptions made when making design decisions of the system.

- The fleet starts with 10 drones and 5 medications. But new drones and medications can be added as required.
- API security was considered as out of scope for this task.
- New drones must be registered in IDLE state, and battery capacity and weight limit must be > 0.
- Drone state changes follow a hierarchy (eg: LOADED state must come after LOADING state).
- For the state change from DELIVERING -> DELIVERED, drone must be unloaded first.
- State changes are done explicitly through state change endpoint (Not changed automatically with loading/unloading).
- Data is not persisted through restarts of the system.

Please contact me through imeshuperera@gmail.com if you have any trouble in building or running the project. Thank you!
***