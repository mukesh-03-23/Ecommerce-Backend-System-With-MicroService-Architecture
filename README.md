# Ecommerce Backend System

This is an Ecommerce backend project that I developed using **Spring Boot and Microservices Architecture**.

The main purpose of this project is to understand how an Ecommerce application can be divided into different microservices and how those services communicate with each other.

I used **OpenFeign** for synchronous communication between services and **Netflix Eureka** for service discovery. I also implemented JWT authentication and integrated Razorpay for payment processing.

## Technologies Used

- Java
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- OpenFeign
- Netflix Eureka
- Spring Cloud Gateway
- Razorpay
- Cloudinary
- Maven

## Project Architecture

The project contains the following services:

- API Gateway
- Eureka Server
- User Service
- Product Service
- Cart Service
- Order Service
- Payment Service
- Notification Service

The basic flow of the application is:

```text
Client
   |
   v
API Gateway
   |
   v
Eureka Server
   |
   +------------------+
   |                  |
   v                  v
User Service      Product Service
   |
   |
   +------ Cart Service
              |
              v
         Order Service
              |
              v
        Payment Service
              |
              v
           Razorpay
              |
              v
     Notification Service
```

## Services

### API Gateway

The API Gateway is the entry point for the client requests.

I used it to:

- Route requests to different services
- Validate JWT tokens
- Get user information from the JWT
- Send user information to the required service

Port: `8080`

### Eureka Server

I used Netflix Eureka for service discovery.

Instead of directly depending on the IP address of another service, services can find each other using their registered service name.

Port: `8761`

### User Service

This service handles user-related operations.

Main functionalities:

- User registration
- User login
- JWT generation
- User details
- Password reset
- Forgot password
- User roles

Port: `8083`

### Product Service

This service manages products and inventory.

Main functionalities:

- Add product
- Get products
- Update product
- Delete product
- Update inventory
- Product image upload

I used Cloudinary for storing product images.

Port: `8081`

### Cart Service

This service manages the user's cart.

Main functionalities:

- Add product to cart
- Increase quantity
- Reduce quantity
- Remove product
- Get cart
- Clear cart

Port: `8082`

### Order Service

This service handles order creation.

When the user places an order, the Order Service communicates with other services to get cart and product information and creates the order.

It also communicates with the Payment Service for payment processing.

Port: `8084`

### Payment Service

I integrated Razorpay for payment processing.

The payment flow contains:

1. Create Razorpay order
2. Open the payment process
3. Receive payment details
4. Verify the Razorpay signature
5. Update payment status
6. Clear the cart
7. Update inventory
8. Update order payment status
9. Send order confirmation notification

Port: `8085`

### Notification Service

This service is responsible for sending notifications such as order confirmation and password-related emails.

Port: `8086`

## Communication Between Services

I used **OpenFeign** for synchronous communication.

For example:

```text
Order Service
      |
      +----> Cart Service
      |
      +----> Product Service
      |
      +----> Payment Service
```

Payment Service communicates with:

```text
Payment Service
      |
      +----> Order Service
      |
      +----> Cart Service
      |
      +----> Notification Service
```

The services are discovered using Eureka.

## Authentication

I implemented JWT based authentication.

The basic flow is:

```text
Login
  |
  v
User Service
  |
  v
JWT Token
  |
  v
Client
  |
  v
API Gateway
  |
  v
JWT Validation
  |
  v
Requested Microservice
```

The API Gateway validates the token before allowing requests to protected endpoints.

## Payment Flow

The payment flow implemented in the project is:

```text
User
 |
 | Place Order
 v
Order Service
 |
 | Create Payment Order
 v
Payment Service
 |
 v
Razorpay
 |
 | Payment
 v
Payment Verification
 |
 v
Payment Service
 |
 +---- Clear Cart
 |
 +---- Update Inventory
 |
 +---- Update Order
 |
 +---- Send Notification
```

Razorpay is currently used in test mode.

## Service Ports

| Service              | Port |
| -------------------- | ---: |
| API Gateway          | 8080 |
| Product Service      | 8081 |
| Cart Service         | 8082 |
| User Service         | 8083 |
| Order Service        | 8084 |
| Payment Service      | 8085 |
| Notification Service | 8086 |
| Eureka Server        | 8761 |

## Project Structure

```text
Micro-Services/
│
├── README.md
├── .gitignore
│
├── api-gateway/
├── eureka-service/
├── user-service/
├── product-service/
├── cart-service/
├── order-service/
├── PaymentService/
└── Notification-service/
```

## Configuration

I used environment variables for sensitive information.

For example:

```text
Database username
Database password
JWT secret
Razorpay key
Razorpay secret
Email credentials
Cloudinary credentials
```

The `.env` file is added to `.gitignore`, so the actual secrets are not pushed to GitHub.

## How to Run

First start the Eureka Server.

Then start the remaining services:

```text
Eureka Server
User Service
Product Service
Cart Service
Order Service
Payment Service
Notification Service
API Gateway
```

After starting the services, they can be checked from the Eureka dashboard:

```text
http://localhost:8761
```

The API Gateway runs on:

```text
http://localhost:8080
```

Requests can be sent through the API Gateway.

## API Documentation

API documentation will be available in:

```text
docs/API-DOCUMENTATION.md
```

It will contain the endpoints, request details and response examples for the different services.

## What I Learned From This Project

While developing this project, I learned how different microservices communicate with each other and how service discovery works.

I also got practical experience with:

- Spring Boot
- Spring Security
- JWT
- REST APIs
- JPA and Hibernate
- MySQL
- OpenFeign
- Eureka
- API Gateway
- Payment integration
- Handling communication between multiple services

## Future Improvements

Some features I plan to work on later:

## These are future Improvements not implemented in This project

- Docker and Docker Compose
- Redis
- Kafka
- Inventory reservation
- Better error handling between services
- Testing
- CI/CD

## Author

**Mukesh**

B.Tech Information Technology | Java Backend Developer
