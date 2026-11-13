export type Role = 'CUSTOMER' | 'SELLER';
export interface Product {
  productId: number;
  productName: string;
  productDescription: string;
  productPrice: number;
  productQuantity: number;
  categoryName: string;
  imageUrl: string;
}
export interface CartItem { productId: number; quantity: number; price: number }
export interface Cart { email: string; cartItems: CartItem[]; total: number }
export interface OrderItem { productId: number; quantity: number; productPrice: number }
export interface Order { orderId: string; email: string; paymentStatus?: string; PaymentStatus?: string; items: OrderItem[]; amount: number }
export interface PaymentOrder { order_Id: string; razorpayorderId: string; amount: number; currency: string; email: string }
export interface CheckoutResponse { orderResponse: Order; paymentorderresponse: PaymentOrder }
export interface Session { token: string; email: string; role: Role }
export const rupees = (value: number) => new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 }).format(value);
export function jwtPayload(token: string): { sub: string; role: Role; exp?: number } {
  const payload = token.split('.')[1];
  if (!payload) throw new Error('Invalid session token');
  const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
  const normalized = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
  const data = JSON.parse(atob(normalized)) as { sub: string; role: Role; exp?: number };
  if (!data.sub || !['CUSTOMER', 'SELLER'].includes(data.role)) throw new Error('Invalid session claims');
  if (data.exp && data.exp * 1000 <= Date.now()) throw new Error('Your session has expired. Please sign in again.');
  return data;
}
export function readSession(): Session | null {
  try {
    const token = sessionStorage.getItem('shopsphere-token');
    if (!token) return null;
    const payload = jwtPayload(token);
    return { token, email: payload.sub, role: payload.role };
  } catch {
    sessionStorage.removeItem('shopsphere-token');
    return null;
  }
}
export function saveSession(token: string): Session {
  const payload = jwtPayload(token);
  sessionStorage.setItem('shopsphere-token', token);
  return { token, email: payload.sub, role: payload.role };
}
