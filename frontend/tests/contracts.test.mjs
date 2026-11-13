import test from 'node:test';
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
const api = readFileSync(new URL('../src/api.ts', import.meta.url),'utf8');
const domain = readFileSync(new URL('../src/domain.ts', import.meta.url),'utf8');
test('all browser APIs use the gateway or dev proxy, not direct microservice ports', () => {
  assert.match(api, /VITE_API_URL \|\| '\/api'/);
  assert.doesNotMatch(api, /localhost:808[1-6]/);
});
test('customer cart endpoint is JWT scoped and no longer takes arbitrary email', () => {
  assert.match(api, /cart: \(session: Session\) => request<Cart>\('\/cart\/getcart', \{ session \}\)/);
  assert.doesNotMatch(api, /getcart\/\$\{/);
});
test('payment verification sends Razorpay signature to backend', () => {
  assert.match(api, /razorpaySignature/);
  assert.match(api, /\/payments\/verify/);
});
test('JWT is stored in sessionStorage instead of persistent localStorage', () => {
  assert.match(domain, /sessionStorage/);
  assert.doesNotMatch(domain, /localStorage/);
});
test('product upload sends multipart JSON requestDto and image', () => {
  assert.match(api, /data\.append\('requestDto'/);
  assert.match(api, /data\.append\('image', image\)/);
});
