# ShopSphere React frontend

React + TypeScript + Vite storefront for the existing Spring Boot microservices.
This project deliberately does not replace Eureka, OpenFeign, the API gateway, or any backend service.

## Setup
1. Run Eureka, all backend services and API Gateway (port 8080).
2. Configure backend MySQL, Razorpay **test** key, SMTP and Cloudinary environment variables.
3. In this directory run `npm install` and `npm run dev`.
4. Optionally create `.env.local` with `VITE_RAZORPAY_KEY_ID=rzp_test_...` (the public key only).
5. Browse `http://localhost:5173`.

Development requests use the Vite `/api` proxy to `http://localhost:8080`.
For deployments, set `VITE_API_URL` to the public gateway base URL and configure gateway CORS.
Never put MySQL passwords, JWT signing secrets or Razorpay private secrets into frontend variables.

## User journeys
- All visitors: browse, filter, search and sort products.
- Customers: register, sign in, add to cart, view cart, pay via Razorpay and view their orders.
- Sellers: register, sign in and upload products with images.
- Users: request/reset password with emailed one-time code.

## Known gaps
The backend currently does not offer cart remove/change-quantity or customer-side invoice APIs, so the UI
does not invent endpoints. Live Razorpay testing requires a valid test key and running backend.
The checkout creates an order before invoking the Razorpay popup; dismissal may leave a PENDING order.
See `../docs/KNOWN-LIMITATIONS.md` for cross-service reconciliation requirements.
