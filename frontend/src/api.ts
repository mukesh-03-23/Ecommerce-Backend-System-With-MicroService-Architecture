import type { Cart, CheckoutResponse, Order, Product, Session } from './domain';
const BASE_URL = import.meta.env.VITE_API_URL || '/api';
type Method = 'GET' | 'POST';
async function request<T>(path: string, options: { method?: Method; data?: unknown; session?: Session | null; multipart?: boolean } = {}): Promise<T> {
  const headers = new Headers();
  if (options.session) headers.set('Authorization', 'Bearer ' + options.session.token);
  if (options.data !== undefined && !options.multipart) headers.set('Content-Type', 'application/json');
  const response = await fetch(BASE_URL + path, {
    method: options.method || 'GET', headers,
    body: options.data === undefined ? undefined : options.multipart ? options.data as FormData : JSON.stringify(options.data)
  });
  const body = await response.text();
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) throw new Error('Your session is invalid or you do not have permission.');
    throw new Error(body || 'Request failed (' + response.status + ')');
  }
  if (!body) return undefined as T;
  try { return JSON.parse(body) as T; } catch { return body as T; }
}
export const api = {
  products: () => request<Product[]>('/product/getproducts'),
  product: (id: number) => request<Product>('/product/getproduct/' + id),
  login: (email: string, password: string) => request<string>('/user/login', { method: 'POST', data: { email, password } }),
  register: (fullname: string, email: string, password: string, role: string) =>
    request<string>('/user/register', { method: 'POST', data: { fullname, email, password, confirmPassword: password, role } }),
  forgotPassword: (email: string, username: string) =>
    request<void>('/user/forgotpassword', { method: 'POST', data: { email, username } }),
  resetPassword: (email: string, otp: string, password: string) =>
    request<void>('/user/resetpassword', { method: 'POST', data: { email, otp, password, confirmPassword: password } }),
  cart: (session: Session) => request<Cart>('/cart/getcart', { session }),
  addCart: (session: Session, productId: number, quantity: number) =>
    request<string>('/cart/addcart', { method: 'POST', session, data: { productId, quantity } }),
  orders: (session: Session) => request<Order[]>('/order/getorders', { session }),
  checkout: (session: Session) => request<CheckoutResponse>('/order/placeorder', { method: 'POST', session }),
  verifyPayment: (session: Session, razorpayOrderId: string, razorpayPaymentId: string, razorpaySignature: string) =>
    request<string>('/payments/verify', { method: 'POST', session, data: { razorpayOrderId, razorpayPaymentId, razorpaySignature } }),
  addProduct: (session: Session, values: { productName: string; productDescription: string; productPrice: number; productQuantity: number; productCategory: string }, image: File) => {
    const data = new FormData();
    data.append('requestDto', new Blob([JSON.stringify(values)], { type: 'application/json' }));
    data.append('image', image);
    return request<string>('/product/addproduct', { method: 'POST', session, data, multipart: true });
  }
};
export async function openRazorpay(key: string, details: CheckoutResponse, email: string): Promise<{ razorpay_order_id: string; razorpay_payment_id: string; razorpay_signature: string }> {
  if (!key) throw new Error('Set VITE_RAZORPAY_KEY_ID to your Razorpay test public key before checkout.');
  if (!(window as RazorpayWindow).Razorpay) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Unable to load Razorpay checkout'));
      document.head.appendChild(script);
    });
  }
  return new Promise((resolve, reject) => {
    const gateway = (window as RazorpayWindow).Razorpay;
    if (!gateway) { reject(new Error('Razorpay checkout unavailable')); return; }
    const order = details.paymentorderresponse;
    const checkout = new gateway({
      key, order_id: order.razorpayorderId, amount: Math.round(order.amount * 100),
      currency: order.currency, name: 'ShopSphere', description: 'Secure checkout',
      prefill: { email },
      theme: { color: '#203f35' },
      handler: resolve,
      modal: { ondismiss: () => reject(new Error('Payment window was closed. You can retry from your orders.')) }
    });
    checkout.open();
  });
}
interface RazorpayWindow extends Window {
  Razorpay?: new (config: {
    key: string; order_id: string; amount: number; currency: string; name: string; description: string;
    prefill: { email: string }; theme: { color: string };
    handler: (result: { razorpay_order_id: string; razorpay_payment_id: string; razorpay_signature: string }) => void;
    modal: { ondismiss: () => void }
  }) => { open: () => void };
}
