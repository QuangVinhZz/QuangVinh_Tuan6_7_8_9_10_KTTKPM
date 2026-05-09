import { useEffect, useState } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [userId] = useState(1)
  const [products, setProducts] = useState([])
  const [cart, setCart] = useState([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    loadProducts()
    loadCart()
  }, [])

  const loadProducts = async () => {
    const { data } = await axios.get('http://172.16.64.208:8081/products')
    setProducts(data)
  }

  const loadCart = async () => {
    const { data } = await axios.get('http://172.16.64.208:8082/cart', {
      params: { userId },
    })
    setCart(Array.isArray(data) ? data : [])
  }

  const addToCart = async (productId) => {
    await axios.post('http://172.16.64.208:8082/cart/add', { userId, productId })
    await loadCart()
  }

  const checkout = async () => {
    setLoading(true)
    try {
      const { data } = await axios.post('http://172.16.64.208:8083/checkout', null, {
        params: { userId },
      })
      alert(data)
      await loadCart()
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="app-shell">
      <section className="topbar">
        <div>
          <p className="eyebrow">Flash Sale</p>
          <h1>Minimal Cart</h1>
        </div>
        <div className="user-chip">User {userId}</div>
      </section>

      <section className="grid">
        <div className="panel">
          <div className="panel-head">
            <h2>Sản phẩm</h2>
            <span>{products.length} items</span>
          </div>
          <div className="list">
            {products.map((product) => (
              <article className="card" key={product.id}>
                <div>
                  <h3>{product.name}</h3>
                  <p>{Number(product.price).toLocaleString('vi-VN')} đ</p>
                </div>
                <button className="action-btn" onClick={() => addToCart(product.id)}>
                  <svg viewBox="0 0 24 24" aria-hidden="true">
                    <path d="M12 5v14M5 12h14" />
                  </svg>
                  Thêm vào giỏ
                </button>
              </article>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-head">
            <h2>Giỏ hàng</h2>
            <span>{cart.length} items</span>
          </div>
          <div className="cart-box">
            {cart.length === 0 ? <p className="empty">Chưa có sản phẩm</p> : cart.map((id) => <div className="cart-item" key={id}>Product {id}</div>)}
          </div>
          <button className="checkout-btn" onClick={checkout} disabled={loading}>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M7 7h13l-2 8H8L7 7ZM7 7 6 4H3" />
              <circle cx="9" cy="19" r="1.5" />
              <circle cx="18" cy="19" r="1.5" />
            </svg>
            Thanh toán
          </button>
        </div>
      </section>
    </main>
  )
}

export default App
