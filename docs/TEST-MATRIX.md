# Full-stack verification matrix

**Status: Java module packaging + focused regression tests and frontend typecheck/build have passed in GitHub Actions. Live integration tests remain pending.**

Read-only live gateway / Eureka verification is supplied in `scripts/smoke-test.mjs`.
It must be run by the project owner with all services and test DBs running; it is
not part of CI and has not been executed against live dependencies.

| Layer | Unit/contract coverage | Environment-dependent verification |
|---|---|---|
| Eureka registry | Maven compile | Start server; verify registered services |
| API Gateway JWT/CORS | Code review | Expired/forged JWT, role enforcement, CORS preflight |
| User registration/login | Password match guard | MySQL, role validation, login and expiry |
| User OTP -> Notification | Feign request body and expiry fix | SMTP delivery, OTP brute force/rate limits |
| Product inventory | Overdraft/invalid quantity guard | MySQL, concurrent purchase race |
| Product images | Multipart interface | Cloudinary test account |
| Cart -> Product Feign | Quantity accumulation guard | Stock boundary and concurrency |
| Order -> Cart, Payment Feign | Empty cart guard | Checkout rollback, duplicate orders |
| Payment -> Order, Cart, Notification | Status update and signature checks | Razorpay test checkout, idempotent recovery |
| Notification -> User/Order/Product | Contract review | SMTP failure/retry/reconciliation |
| Frontend | Contract smoke tests and TypeScript build | Manual browser flows, mobile and payment popup |

## Required end-to-end scenarios
1. Start Eureka, check health and registration for each service.
2. Register CUSTOMER and SELLER. Test duplicate registration and invalid passwords.
3. Login; reject expired, forged and missing bearer tokens.
4. Seller uploads a product (image + details); customer sees it publicly.
5. Add product twice to cart; ensure merged quantity and correct total.
6. Reject zero, negative or out-of-stock quantity; verify stock never goes negative.
7. Checkout: cart -> order -> payment creation, verify order begins PENDING.
8. Complete Razorpay **test** payment; verify signature, stock deduction, cart cleared, order PAID, notification sent.
9. Replay same verification; confirm stock is not deducted again.
10. Simulate a failure after inventory update and before cart clearing; **must not ship** until state-recovery has been implemented.
11. Customer A must not access customer B's cart or orders via gateway.
12. Seller must not call customer cart or checkout; customer must not upload seller product.
13. OTP wrong, expired and correct-code flows; test replay and resend.
14. Verify frontend mobile breakpoints, failed API, empty lists, payment cancel and session expiry.

## Honest coverage statement
CI compiles Java and runs deterministic regression tests. It does not provide live MySQL,
Eureka, SMTP, Cloudinary and Razorpay test credentials. Passing CI does **not** mean
every synchronous service flow is verified or that the project is bug-free.
