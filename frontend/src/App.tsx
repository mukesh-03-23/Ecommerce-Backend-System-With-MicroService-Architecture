import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import { ArrowRight, Check, ChevronDown, CircleUserRound, Heart, Leaf, LogOut, Menu, Package, Plus, Search, ShieldCheck, ShoppingBag, Sparkles, Truck, Upload, X } from 'lucide-react';
import { api, openRazorpay } from './api';
import { readSession, rupees, saveSession, type Cart, type Order, type Product, type Role, type Session } from './domain';

type Panel = 'none' | 'cart' | 'auth' | 'orders' | 'seller';
type AuthMode = 'login' | 'register' | 'forgot' | 'reset';
const categories = ['All finds', 'Electronics', 'Fashion', 'Home', 'Beauty', 'Lifestyle', 'Other'];
function getCategory(product: Product) { return product.categoryName?.trim() || 'Other'; }
function productImage(product: Product) { return product.imageUrl || ''; }

function App() {
  const [products, setProducts] = useState<Product[]>([]);
  const [busy, setBusy] = useState(true);
  const [loadingAction, setLoadingAction] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [session, setSession] = useState<Session | null>(() => readSession());
  const [panel, setPanel] = useState<Panel>('none');
  const [authMode, setAuthMode] = useState<AuthMode>('login');
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('All finds');
  const [sort, setSort] = useState('featured');
  const [cart, setCart] = useState<Cart | null>(null);
  const [orders, setOrders] = useState<Order[]>([]);
  const [favorites, setFavorites] = useState<number[]>([]);
  const [mobileMenu, setMobileMenu] = useState(false);

  const loadProducts = useCallback(async () => {
    try { setProducts(await api.products()); setError(''); }
    catch (e) { setError(getMessage(e)); }
    finally { setBusy(false); }
  }, []);
  useEffect(() => { void loadProducts(); }, [loadProducts]);
  const shown = useMemo(() => {
    const query = search.toLowerCase().trim();
    const result = products.filter(p => {
      const inCategory = category === 'All finds' || getCategory(p).toLowerCase() === category.toLowerCase();
      const inQuery = !query || [p.productName, p.productDescription, p.categoryName].some(v => v?.toLowerCase().includes(query));
      return inCategory && inQuery;
    });
    if (sort === 'low') result.sort((a,b) => a.productPrice - b.productPrice);
    if (sort === 'high') result.sort((a,b) => b.productPrice - a.productPrice);
    if (sort === 'name') result.sort((a,b) => a.productName.localeCompare(b.productName));
    return result;
  }, [products, search, category, sort]);

  const showPanel = (next: Panel) => { setPanel(next); setError(''); setSuccess(''); setMobileMenu(false); };
  const ensureCustomer = () => {
    if (!session) { setAuthMode('login'); showPanel('auth'); return false; }
    if (session.role !== 'CUSTOMER') { setError('This feature requires a customer account.'); return false; }
    return true;
  };
  const refreshCart = async (current = session) => {
    if (!current || current.role !== 'CUSTOMER') { setCart(null); return; }
    try { setCart(await api.cart(current)); }
    catch { setCart(null); }
  };
  const viewCart = async () => { if (!ensureCustomer()) return; showPanel('cart'); await refreshCart(); };
  const viewOrders = async () => {
    if (!ensureCustomer()) return;
    showPanel('orders');
    try { setOrders(await api.orders(session!)); }
    catch (e) { setError(getMessage(e)); }
  };
  const addToCart = async (product: Product) => {
    if (!ensureCustomer()) return;
    if (product.productQuantity <= 0) { setError('This product is currently out of stock.'); return; }
    setLoadingAction(true); setError('');
    try { await api.addCart(session!, product.productId, 1); await refreshCart(); setSuccess(product.productName + ' added to your bag'); showPanel('cart'); }
    catch (e) { setError(getMessage(e)); }
    finally { setLoadingAction(false); }
  };
  const checkout = async () => {
    if (!session || !cart?.cartItems.length) return;
    setError(''); setLoadingAction(true);
    try {
      const order = await api.checkout(session);
      const payment = await openRazorpay(import.meta.env.VITE_RAZORPAY_KEY_ID || '', order, session.email);
      await api.verifyPayment(session, payment.razorpay_order_id, payment.razorpay_payment_id, payment.razorpay_signature);
      setCart(null); setSuccess('Your payment was verified. Thank you for shopping with us!'); showPanel('orders');
      setOrders(await api.orders(session));
    } catch (e) { setError(getMessage(e)); }
    finally { setLoadingAction(false); }
  };
  const login = async (token: string) => {
    const signedIn = saveSession(token);
    setSession(signedIn); showPanel('none'); setSuccess('Welcome back!');
    if (signedIn.role === 'CUSTOMER') await refreshCart(signedIn);
  };
  const logout = () => { sessionStorage.removeItem('shopsphere-token'); setSession(null); setCart(null); setOrders([]); showPanel('none'); setSuccess('You have signed out.'); };
  const matchingCategories = useMemo(() => [...new Set([...categories, ...products.map(getCategory)])], [products]);
  const active = panel !== 'none';
  return (
    <div className="site">
      <div className="announcement"><Sparkles size={13} /> Discover thoughtfully chosen finds for every day <ArrowRight size={13} /></div>
      <header className="header">
        <div className="header-inner container">
          <button className="mobile-toggle icon-btn" aria-label="Open menu" onClick={() => setMobileMenu(!mobileMenu)}><Menu size={22}/></button>
          <a className="brand" href="#home" onClick={() => showPanel('none')}><span className="brand-mark"><Leaf size={22} strokeWidth={2.4}/></span><span>shopsphere<span className="brand-dot">.</span></span></a>
          <nav className={'nav ' + (mobileMenu ? 'nav-open' : '')}><a href="#shop" onClick={() => setMobileMenu(false)}>Shop all</a><a href="#collections" onClick={() => setMobileMenu(false)}>Collections</a><a href="#story" onClick={() => setMobileMenu(false)}>Our promise</a></nav>
          <div className="header-actions">
            <button className="icon-btn search-control" aria-label="Search products" onClick={() => { showPanel('none'); document.getElementById('product-search')?.focus(); document.getElementById('shop')?.scrollIntoView({behavior:'smooth'}); }}><Search size={21}/></button>
            <button className="account-link" onClick={() => session ? (session.role === 'SELLER' ? showPanel('seller') : viewOrders()) : (setAuthMode('login'), showPanel('auth'))}><CircleUserRound size={21}/><span>{session ? session.role === 'SELLER' ? 'Seller studio' : 'My orders' : 'Sign in'}</span></button>
            <button className="bag-link icon-btn" aria-label="Open shopping bag" onClick={() => void viewCart()}><ShoppingBag size={22}/>{cart?.cartItems?.length ? <span className="bag-count">{cart.cartItems.reduce((n,i)=>n+i.quantity,0)}</span>:null}</button>
          </div>
        </div>
      </header>
      <main id="home">
        <section className="hero">
          <div className="container hero-grid">
            <div className="hero-copy"><span className="eyebrow"><span className="eyebrow-line"/> THE EVERYDAY EDIT / 2026</span><h1>Find joy in the <em>little things.</em></h1><p>Good things, good living. Explore everyday essentials and delightful discoveries chosen with you in mind.</p>
              <div className="hero-buttons"><a href="#shop" className="btn btn-primary">Explore the collection <ArrowRight size={17}/></a><a href="#story" className="text-link">Why shop with us <ArrowRight size={16}/></a></div>
              <div className="hero-proof"><span className="proof-icon"><Check size={14}/></span> A little more thoughtful, a lot more lovely.</div>
            </div>
            <div className="hero-art"><div className="hero-photo"><div className="photo-sun"/><div className="photo-vase"><div className="vase-plant"><i/><i/><i/><i/><i/></div><div className="vase-neck"/><div className="vase-body"/></div><div className="photo-arch"/><div className="photo-table"/></div><div className="hero-label"><span>MADE FOR THE MOMENTS THAT MATTER</span><span className="label-number">01 / 03</span></div><div className="floating-note"><Sparkles size={18}/><span>Something lovely<br/>awaits inside</span></div></div>
          </div>
        </section>
        <section id="collections" className="collections container"><div className="collection-intro"><span className="eyebrow">DISCOVER YOUR STYLE</span><h2>Browse by mood, shop by moment.</h2></div><div className="category-list">{matchingCategories.slice(0,9).map(c => <button key={c} className={'category-pill ' + (category === c ? 'selected' : '')} onClick={() => {setCategory(c); document.getElementById('shop')?.scrollIntoView({behavior:'smooth'});}}>{c}<ArrowRight size={14}/></button>)}</div></section>
        <section className="products-section" id="shop"><div className="container"><div className="section-top"><div><span className="eyebrow">EXPLORE THE EDIT</span><h2>The good stuff, <em>all here.</em></h2><p>Find your next favorite among our everyday essentials.</p></div><span className="count-label">{shown.length} {shown.length === 1 ? 'FIND' : 'FINDS'}</span></div>
          <div className="filters"><label className="search-field"><Search size={18}/><input id="product-search" type="search" placeholder="Search for something lovely..." value={search} onChange={e=>setSearch(e.target.value)}/></label><label className="sort-field">Sort by <select value={sort} onChange={e=>setSort(e.target.value)}><option value="featured">Featured</option><option value="low">Price: low to high</option><option value="high">Price: high to low</option><option value="name">Name: A–Z</option></select><ChevronDown size={16}/></label></div>
          {busy ? <div className="empty-state"><div className="spinner"/>Loading the collection...</div> : shown.length === 0 ? <div className="empty-state"><Search size={36}/><h3>Nothing in this collection yet.</h3><p>Try another search or browse all products.</p><button className="btn btn-primary" onClick={()=>{setSearch('');setCategory('All finds');}}>Show all finds</button></div> : <div className="product-grid">{shown.map((product,index)=><article className="product-card" key={product.productId}><div className={'product-visual tone-' + index%4}>{productImage(product) ? <img src={productImage(product)} alt={product.productName} loading="lazy"/> : <div className="image-placeholder"><ShoppingBag size={36}/><span>SHOPSPHERE</span></div>}{index < 2 && <span className="product-tag">CURATED PICK</span>}<button className={'favorite-btn ' + (favorites.includes(product.productId)?'favorite-active':'')} aria-label="Favorite product" onClick={()=>setFavorites(v=>v.includes(product.productId)?v.filter(x=>x!==product.productId):[...v,product.productId])}><Heart size={18} fill={favorites.includes(product.productId)?'currentColor':'none'}/></button></div><div className="product-info"><span className="product-category">{getCategory(product)}</span><h3>{product.productName}</h3><p className="product-description">{product.productDescription}</p><div className="product-bottom"><span className="price">{rupees(product.productPrice)}</span><button disabled={product.productQuantity<=0 || loadingAction} className="add-button" onClick={()=>void addToCart(product)} aria-label={'Add '+product.productName+' to bag'}>{product.productQuantity>0?<><Plus size={18}/><span>Add</span></>:'Sold out'}</button></div></div></article>)}</div>}
        </div></section>
        <section className="promise" id="story"><div className="container promise-grid"><div className="promise-title"><span className="eyebrow">OUR LITTLE PROMISE</span><h2>Good shopping.<br/><em>Better feeling.</em></h2></div><div className="promise-feature"><ShieldCheck size={27}/><h3>Secure checkout</h3><p>Buy confidently with verified payments and a protected shopping experience.</p></div><div className="promise-feature"><Package size={27}/><h3>Fresh finds</h3><p>Discover practical pieces and charming details across every collection.</p></div><div className="promise-feature"><Truck size={27}/><h3>Easy experience</h3><p>Simple browsing, thoughtful design and a streamlined shopping bag.</p></div></div></section>
      </main>
      <footer className="footer"><div className="container footer-inner"><div><div className="brand footer-brand"><span className="brand-mark"><Leaf size={20}/></span>shopsphere<span className="brand-dot">.</span></div><p>Everyday things. A little more lovely.</p></div><div className="footer-right"><span>Thoughtfully made for better everyday moments.</span><span>© {new Date().getFullYear()} ShopSphere</span></div></div></footer>
      {success && <div role="status" className="toast toast-success"><Check size={17}/>{success}<button aria-label="Dismiss message" onClick={()=>setSuccess('')}><X size={15}/></button></div>}
      {error && <div role="alert" className="toast toast-error">{error}<button aria-label="Dismiss error" onClick={()=>setError('')}><X size={15}/></button></div>}
      {active && <div className="overlay" onClick={()=>showPanel('none')}/>}
      {active && <aside className="drawer" aria-label="Account panel"><div className="drawer-header"><div className="drawer-heading">{panel === 'cart'?'Your bag':panel==='auth'?'Your account':panel==='orders'?'Your orders':'Seller studio'}</div><button className="icon-btn" aria-label="Close panel" onClick={()=>showPanel('none')}><X size={23}/></button></div>
        <div className="drawer-content">{panel==='auth' && <Auth mode={authMode} setMode={setAuthMode} onLogin={login} onError={setError} onMessage={setSuccess} />}
          {panel==='cart' && <div className="cart-drawer">{!cart?.cartItems?.length ? <div className="drawer-empty"><ShoppingBag size={45}/><h3>Your bag is feeling light.</h3><p>Discover something lovely to take home.</p><button className="btn btn-primary" onClick={()=>showPanel('none')}>Continue shopping <ArrowRight size={17}/></button></div> : <><div className="cart-items">{cart.cartItems.map(item=>{const p=products.find(x=>x.productId===item.productId);return <div className="cart-item" key={item.productId}><div className="cart-thumb">{p?.imageUrl?<img src={p.imageUrl} alt=""/>:<Package size={25}/>}</div><div className="cart-item-copy"><strong>{p?.productName || 'Product #'+item.productId}</strong><span>Qty {item.quantity} · {rupees(item.price / item.quantity)} each</span><b>{rupees(item.price)}</b></div></div>})}</div><div className="cart-checkout"><div className="cart-total"><span>Subtotal</span><strong>{rupees(cart.total)}</strong></div><p>Payment is handled securely by Razorpay.</p><button className="btn btn-primary btn-full" disabled={loadingAction} onClick={()=>void checkout()}>{loadingAction?'Processing...':'Continue to payment'} <ArrowRight size={17}/></button></div></>}</div>}
          {panel==='orders' && <div>{session && <div className="signed-in"><CircleUserRound size={19}/> {session.email}<button onClick={logout}><LogOut size={15}/> Log out</button></div>}{orders.length===0 ? <div className="drawer-empty"><Package size={43}/><h3>No orders to show yet.</h3><p>Your order history will appear here once you place an order.</p><button className="btn btn-primary" onClick={()=>showPanel('none')}>Explore products <ArrowRight size={16}/></button></div>:orders.map(order=><div className="order-card" key={order.orderId}><div className="order-top"><strong>{order.orderId}</strong><span className={'order-status '+((order.paymentStatus||order.PaymentStatus)==='PAID'?'paid':'')}>{order.paymentStatus||order.PaymentStatus||'PENDING'}</span></div><span>{order.items?.length||0} items</span><div className="order-amount">{rupees(order.amount)}</div></div>)}</div>}
          {panel==='seller' && session?.role==='SELLER' && <Seller session={session} onCreated={loadProducts} onError={setError} onMessage={setSuccess} onLogout={logout}/>}
        </div></aside>}
    </div>
  );
}
function getMessage(error: unknown) { return error instanceof Error ? error.message : 'Something went wrong. Please try again.'; }
function Auth({mode,setMode,onLogin,onError,onMessage}:{mode:AuthMode;setMode:(v:AuthMode)=>void;onLogin:(token:string)=>Promise<void>;onError:(v:string)=>void;onMessage:(v:string)=>void}) {
  const [email,setEmail]=useState('');const [password,setPassword]=useState('');const [fullname,setFullname]=useState('');const [otp,setOtp]=useState('');const [role,setRole]=useState<Role>('CUSTOMER');const [working,setWorking]=useState(false);
  const submit=async(e:FormEvent)=>{e.preventDefault();onError('');setWorking(true);try{
    if(mode==='login'){await onLogin(await api.login(email,password));}
    else if(mode==='register'){await api.register(fullname,email,password,role);onMessage('Account created. You can now sign in.');setMode('login');}
    else if(mode==='forgot'){await api.forgotPassword(email,fullname);onMessage('If the account exists, the reset code has been requested.');setMode('reset');}
    else{await api.resetPassword(email,otp,password);onMessage('Password updated. Sign in with your new password.');setMode('login');}
  }catch(err){onError(getMessage(err));}finally{setWorking(false);}};
  return <div className="auth-form"><div className="panel-eyebrow">WELCOME TO SHOPSPHERE</div><h2>{mode==='login'?'Lovely to see you again.':mode==='register'?'Come on in.':mode==='forgot'?'Reset your password.':'Choose a new password.'}</h2><p>{mode==='login'?'Sign in to explore your account and shop your favorites.':mode==='register'?'Create an account in just a few steps.':mode==='forgot'?'We’ll email you a short verification code.':'Enter your code and your new password.'}</p><form onSubmit={e=>void submit(e)}>{(mode==='register'||mode==='forgot') && <label>Full name <input type="text" required value={fullname} onChange={e=>setFullname(e.target.value)} placeholder="Your name"/></label>}<label>Email address <input type="email" required value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com"/></label>{mode==='reset'&&<label>Verification code <input required value={otp} onChange={e=>setOtp(e.target.value)} placeholder="6-digit code"/></label>}{mode!=='forgot' && <label>Password <input type="password" minLength={6} required value={password} onChange={e=>setPassword(e.target.value)} placeholder="Your password"/></label>}{mode==='register' && <label>Account type <select value={role} onChange={e=>setRole(e.target.value as Role)}><option value="CUSTOMER">Customer — I want to shop</option><option value="SELLER">Seller — I want to list products</option></select></label>}{mode==='login'&&<button className="forgot-link" type="button" onClick={()=>setMode('forgot')}>Forgot password?</button>}<button className="btn btn-primary btn-full" type="submit" disabled={working}>{working?'Please wait...':mode==='login'?'Sign in':mode==='register'?'Create account':mode==='forgot'?'Send reset code':'Update password'}<ArrowRight size={17}/></button></form><div className="auth-alternative">{mode==='login'?<>New around here? <button onClick={()=>setMode('register')}>Create an account</button></>:<>Already have an account? <button onClick={()=>setMode('login')}>Sign in</button></>}</div><div className="auth-secure"><ShieldCheck size={16}/> Your account is protected by secure authentication.</div></div>;
}
function Seller({session,onCreated,onError,onMessage,onLogout}:{session:Session;onCreated:()=>Promise<void>;onError:(v:string)=>void;onMessage:(v:string)=>void;onLogout:()=>void}){
  const [name,setName]=useState('');const [description,setDescription]=useState('');const [price,setPrice]=useState('');const [quantity,setQuantity]=useState('');const [category,setCategory]=useState('Home');const [image,setImage]=useState<File|null>(null);const [working,setWorking]=useState(false);
  const submit=async(e:FormEvent)=>{e.preventDefault();onError('');if(!image){onError('Please upload a product image.');return;}setWorking(true);try{await api.addProduct(session,{productName:name,productDescription:description,productPrice:Number(price),productQuantity:Number(quantity),productCategory:category},image);await onCreated();setName('');setDescription('');setPrice('');setQuantity('');setImage(null);onMessage('Product published to the collection!');}catch(err){onError(getMessage(err));}finally{setWorking(false);}};
  return <div className="seller-form"><div className="signed-in"><CircleUserRound size={19}/>{session.email}<button onClick={onLogout}><LogOut size={15}/> Log out</button></div><div className="panel-eyebrow">YOUR CREATIVE SPACE</div><h2>Share something lovely.</h2><p>Upload a product and make it available in the ShopSphere collection.</p><form onSubmit={e=>void submit(e)}><label>Product name <input required value={name} onChange={e=>setName(e.target.value)} placeholder="The perfect everyday essential"/></label><label>Description <textarea required rows={4} value={description} onChange={e=>setDescription(e.target.value)} placeholder="What makes it special?"/></label><div className="form-columns"><label>Price (₹) <input required type="number" min="1" step="0.01" value={price} onChange={e=>setPrice(e.target.value)}/></label><label>Stock <input required type="number" min="0" step="1" value={quantity} onChange={e=>setQuantity(e.target.value)}/></label></div><label>Category <input required value={category} onChange={e=>setCategory(e.target.value)} placeholder="Home, Fashion, ..."/></label><label className="upload-area"><Upload size={25}/><strong>{image?.name || 'Choose a product image'}</strong><span>JPG, PNG or WebP</span><input required={!image} type="file" accept="image/png,image/jpeg,image/webp" onChange={e=>setImage(e.target.files?.[0]||null)}/></label><button disabled={working} className="btn btn-primary btn-full" type="submit">{working?'Publishing...':'Publish product'}<ArrowRight size={17}/></button></form></div>;
}
export default App;
