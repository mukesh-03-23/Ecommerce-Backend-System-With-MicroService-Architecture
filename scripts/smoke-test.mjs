// Read-only gateway and Eureka integration smoke checks against a running stack.
// Usage: GATEWAY_URL=http://localhost:8080 EUREKA_URL=http://localhost:8761 \
//   CUSTOMER_EMAIL=buyer@example.com CUSTOMER_PASSWORD=... node scripts/smoke-test.mjs
const gateway = process.env.GATEWAY_URL || 'http://localhost:8080';
const eureka = process.env.EUREKA_URL || 'http://localhost:8761';
const customer = process.env.CUSTOMER_EMAIL;
const password = process.env.CUSTOMER_PASSWORD;
let failures = 0;
async function check(name, run) {
  try { await run(); console.log('PASS', name); }
  catch (e) { console.error('FAIL', name, String(e)); failures++; }
}
async function http(url, init) {
  const response = await fetch(url, init);
  if (!response.ok) throw new Error(response.status + ' ' + (await response.text()).slice(0,200));
  return response;
}
await check('Eureka discovery server responds', async () => {
  const response = await http(eureka + '/eureka/apps', {headers:{Accept:'application/json'}});
  const data = await response.json();
  const applications = data.applications?.application || [];
  const registered = applications.map(app => app.name?.toUpperCase());
  const required = ['USER-SERVICE','PRODUCT-SERVICE','CART-SERVICE','ORDER-SERVICE','PAYMENT-SERVICE','NOTIFICATION-SERVICE','API-GATEWAY'];
  const absent = required.filter(x => !registered.includes(x));
  if (absent.length) throw new Error('Not registered: '+absent.join(', '));
});
await check('Public gateway product list responds with JSON', async () => {
  const data = await (await http(gateway + '/product/getproducts')).json();
  if (!Array.isArray(data)) throw new Error('Expected product array');
});
await check('Unauthenticated cart access is rejected', async () => {
  const response = await fetch(gateway + '/cart/getcart');
  if (response.status !== 401 && response.status !== 403) throw new Error('Expected 401/403, got ' + response.status);
});
await check('Internal arbitrary-email cart lookup blocked at gateway', async () => {
  const response = await fetch(gateway + '/cart/getcart/another@example.com');
  if (response.status !== 401 && response.status !== 403) throw new Error('Expected 401/403, got ' + response.status);
});
if (customer && password) {
  await check('Customer login -> cart/orders via API gateway', async () => {
    const response = await http(gateway + '/user/login', {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({email:customer,password})});
    const token = (await response.text()).replace(/^"|"$/g, '');
    if (token.split('.').length !== 3) throw new Error('Invalid JWT');
    const headers = {Authorization:'Bearer '+token};
    const cart = await fetch(gateway + '/cart/getcart', {headers});
    if (cart.status !== 200 && cart.status !== 404) throw new Error('Cart '+cart.status);
    const orders = await http(gateway + '/order/getorders',{headers});
    if (!Array.isArray(await orders.json())) throw new Error('Expected order history array');
    const arbitrary = await fetch(gateway + '/cart/getcart/another@example.com',{headers});
    if (arbitrary.status !== 403) throw new Error('Expected forbidden internal cart path; got '+arbitrary.status);
  });
} else console.log('SKIP authenticated user/Feign smoke checks; set CUSTOMER_EMAIL and CUSTOMER_PASSWORD');
if (failures) process.exitCode = 1;
else console.log('Read-only smoke checks passed. Payment, email, image upload and writes require separate controlled integration tests.');
