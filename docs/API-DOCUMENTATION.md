# API Documentation

This document contains the main APIs available in the Ecommerce Backend System.

The application is developed using Spring Boot and Microservices. Requests from the client are mainly handled through the API Gateway. The gateway validates the JWT token for protected APIs and forwards the request to the required microservice.

## Services

| Service              | Port | Purpose                                   |
| -------------------- | ---: | ----------------------------------------- |
| API Gateway          | 8080 | Entry point for client requests           |
| Eureka Server        | 8761 | Service discovery                         |
| User Service         | 8083 | User registration, login and user details |
| Product Service      | 8081 | Product and inventory management          |
| Cart Service         | 8082 | Shopping cart management                  |
| Order Service        | 8084 | Order creation and order management       |
| Payment Service      | 8085 | Razorpay payment processing               |
| Notification Service | 8086 | Email notifications                       |

---

# 1. API Gateway

The API Gateway is the main entry point for the client application.

The gateway is responsible for:

- Routing requests to the required microservice.
- Validating JWT tokens.
- Extracting user information from the JWT.
- Passing user information to downstream services using request headers.
- Handling CORS configuration.

### Base URL

```text
http://localhost:8080
```

### Authentication

Protected APIs require a JWT token.

```http
Authorization: Bearer <JWT_TOKEN>
```

The gateway validates the token before forwarding the request.

---

# 2. User Service

The User Service is responsible for user registration, authentication and user-related operations.

### Register User

```http
POST /user/register
```

Creates a new user account.

Example request:

```json
{
  "fullname": "Mukesh",
  "email": "mukesh@example.com",
  "password": "password",
  "confirmPassword": "password",
  "role": "CUSTOMER/SELLER"
}
```

### Login

````http
POST /user/login
```Example Request:
...json
 {
    "email":"user@gmail.com",
    "password":"password"
 }

Authenticates the user and generates a JWT token.

The generated token is used for accessing protected APIs.

### Get User By Email

```http
GET /user/getuser?email={email}
````

Returns the user information for the given email.

Example:

```text
GET /user/getuser?email=mukesh@example.com
```

---

# 3. Product Service

The Product Service manages products and inventory.

### Add Product

```http
POST /product/addproduct
```

Adds a new product to the system.

This Only accesible to seller

The product information is stored in the product database.

### Get Products

```http
GET /product/getproducts
```

Returns the available products.

### Get Product

```http
GET /product/{id}
```

Returns the details of a particular product.

### Update Product

```http
PUT /product/{id}
```

Updates the product information.

### Delete Product

```http
DELETE /product/{id}
```

Deletes a product.

### Update Inventory

```http
PUT /product/inventory
```

Updates the available inventory after an order is successfully paid.

---

# 4. Cart Service

The Cart Service manages the shopping cart of the user.

The user information is obtained from the request headers added by the API Gateway.

### Add Product To Cart

```http
POST /cart/addcart
```

Adds a product to the user's cart.

Example request:

```json
{
  "productId": 1,
  "quantity": 2
}
```

### Get Cart

```http
GET /cart/getcart
```

Returns the current user's cart.

### Update Cart

```http
PUT /cart/updatecart
```

Updates the quantity of a product in the cart.

### Remove Product From Cart

```http
DELETE /cart/remove
```

Removes a product from the cart.

### Clear Cart

```http
DELETE /cart/clearcart
```

Clears the user's cart after a successful order/payment.

---

# 5. Order Service

The Order Service is responsible for creating and managing orders.

It communicates with the Cart Service, Product Service and Payment Service.

### Place Order

```http
POST /order/placeorder
```

Creates an order using the products available in the user's cart.

The order service:

1. Gets the user's cart.
2. Creates the order.
3. Creates order items.
4. Calculates the order amount.
5. Calls the Payment Service.
6. Returns the Razorpay order details.

### Get Order

```http
GET /order/{id}
```

Returns the details of a particular order.

### Update Payment Status

```http
PUT /order/updatepayment/{orderId}
```

Updates the payment status of the order after successful payment verification.

### Update Inventory

```http
PUT /order/updateinventory/{orderId}
```

Updates the product inventory after successful payment.

---

# 6. Payment Service

The Payment Service handles payment integration with Razorpay.

The payment process is divided into two main steps:

1. Create Razorpay order.
2. Verify the payment.

### Create Payment Order

```http
POST /payments/createorder
```

Creates a Razorpay order.

Example request:

```json
{
  "orderId": 1,
  "amount": 500,
  "currency": "INR",
  "email": "mukesh@example.com"
}
```

The response contains the Razorpay order ID which is required for completing the payment.

### Verify Payment

```http
POST /payments/verify
```

Verifies the payment received from Razorpay.

Example request:

```json
{
  "razorpayOrderId": "order_xxxxx",
  "razorpayPaymentId": "pay_xxxxx",
  "razorpaySignature": "signature"
}
```

After successful verification, the Payment Service:

1. Updates the payment status.
2. Calls the Notification Service.
3. Clears the user's cart.
4. Updates the inventory.
5. Updates the order payment status.

---

# 7. Notification Service

The Notification Service is responsible for sending email notifications.

### Send Notification

```http
POST /send-notification
```

Sends an email notification to the user.

The Notification Service can communicate with the User, Order and Product services to get the required information for the email.

For example, after successful payment, the Payment Service calls the Notification Service to send an order confirmation email.

---

# 8. Authentication Flow

The authentication flow is implemented using JWT.

```text
Client
   |
   | Login
   v
API Gateway
   |
   v
User Service
   |
   | Generate JWT
   v
Client
```

For a protected request:

```text
Client
   |
   | Authorization: Bearer JWT
   v
API Gateway
   |
   | Validate JWT
   |
   | Add user information
   v
Microservice
```

The API Gateway validates the JWT before forwarding the request to the required service.

---

# 9. Order and Payment Flow

The main order flow is:

```text
Client
   |
   v
API Gateway
   |
   v
Order Service
   |
   +----> Cart Service
   |
   +----> Product Service
   |
   +----> Payment Service
              |
              v
          Razorpay
              |
              v
        Payment Verification
              |
              +----> Notification Service
              |
              +----> Cart Service
              |
              +----> Order Service
              |
              +----> Product Service
```

After successful payment, the system updates the order status, updates inventory, clears the cart and sends an order confirmation notification.

---

# 10. Service Communication

The microservices communicate synchronously.

Feign Client is used for communication between services.

For example:

```text
Order Service
      |
      | Feign Client
      v
Cart Service
```

The services are registered with Eureka Server so that services can communicate using service names instead of hardcoded IP addresses.

Example:

```text
http://CART-SERVICE/...
http://PRODUCT-SERVICE/...
http://PAYMENT-SERVICE/...
```

---

# 11. Error Handling

Each service contains exception handling for common errors.

Examples include:

- User not found
- Product not found
- Cart not found
- Cart is empty
- Product out of stock
- Payment not found
- Wrong credentials

The services return appropriate HTTP status codes and error messages when an operation fails.

---

# 12. Environment Variables

Sensitive information is not stored directly in GitHub.

The project uses environment variables for values such as:

- Database credentials
- JWT secret
- Razorpay key
- Razorpay secret
- Email credentials
- Cloudinary credentials

The `.env` file is included in `.gitignore` and should not be committed to the repository.

For local setup, the required environment variables should be created before starting the services.

---

# 13. Important Notes

- Start Eureka Server before the other microservices.
- Start the required microservices and make sure they are registered with Eureka.
- Start the API Gateway on port `8080`.
- Use the API Gateway URL for client requests.
- JWT authentication is required for protected APIs.
- Razorpay is configured for testing/development.
- Secrets should never be committed to GitHub.
