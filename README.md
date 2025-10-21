# Booking Service
>Booking Service is responsible for managing service appointments in Rezervero. It handles creating, updating, canceling, and retrieving bookings for clients, and provides  
> mechanics with information about scheduled slots. All sensitive data is encrypted, and events are published to RabbitMQ for integration with other microservices.

Key features:
- Manage Bookings: Create, update, cancel, and retrieve service appointments.
- Slot Association: Link bookings to specific mechanic slots.
- Event Publishing: Notify other microservices of booking changes via RabbitMQ.

## Table of Contents
* [Technologies Used](#technologies-used)
* [Setup](#setup)
* [Contact](#contact)

## Technologies Used
- Spring Boot 3
- Spring Security 6 
- RabbitMQ
- JWT Token Authentication
- Spring Data JPA
- JSR-303 and Spring Validation
- OpenAPI and Swagger UI Documentation
- Docker

## Setup
0. **Before starting:** You must have docker installed and running

1. Clone the repository:
```bash
git clone https://github.com/jakubknap/Rezerveo-Booking.git
```

2. Go to the project directory and start the containers with the command:
```bash
docker compose up -d
```

3. Run the application
```bash
mvn spring-boot:run
```

4. **[Optional]** Run the Spring Boot application using the java -jar command after building through maven<br>
   Go to the project directory and run app with the command:
```bash
java -jar .\target\rezerveo-booking-1.0.0.jar
```

## API
- You can check the functionality of the backend itself using swagger: http://localhost:8080/swagger-ui.html  Important! Remember about authentication

## Contact
Created by [Jakub Knap](https://www.linkedin.com/in/jakub-knap/) - feel free to contact me!