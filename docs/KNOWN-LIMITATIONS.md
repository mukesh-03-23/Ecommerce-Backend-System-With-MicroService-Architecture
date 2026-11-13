# Audit notes and outstanding verification

This repository preserves the original synchronous REST/OpenFeign + Eureka architecture.

## Reviewed and corrected
- The API gateway allows Vite development origin and exposes a JWT-scoped customer cart lookup.
- Browsers cannot request arbitrary customers' carts or individual order IDs through the gateway.
- Gateway validates JWT and forwards the trusted email on authenticated calls.
- Cart rejects non-positive quantities and correctly merges repeated product additions.
- Inventory rejects negative and excessive deductions.
- Order status is marked PAID on successful payment, instead of PENDING.
- OTP reset rejects incorrect and expired codes; registration checks password confirmation.
- User -> Notification Feign OTP call explicitly uses @RequestBody.
- Cart compiler target is Java 17.

## Important limitations — not yet production-ready
Synchronous payment fan-out (payment -> order -> inventory/cart/notification) has no distributed
transaction, inventory reservation or durable outbox. Stock deductions now use a DB-backed per-order/per-product ledger and a product row lock,
making repeated inventory adjustment calls idempotent. However a crash midway through the
fan-out can still leave a temporarily partially completed order; a durable
payment reconciliation and retry worker remains required before production.
A successful unit test cannot prove the complete system bug-free. Live end-to-end tests also
need DBs, Eureka, SMTP, Cloudinary and Razorpay test credentials.

Never expose internal microservice ports to the public network. API gateway is the public
entry point; service-to-service URLs must be reachable only on a trusted private network.
