import {
  useEffect,
  useRef,
  useState
} from "react";
import {
  useLocation,
  useNavigate
} from "react-router-dom";

import api from "../api/axios";
import {
  useCart
} from "../context/CartContext";
import type {
  CartItem
} from "../context/CartContext";
import type {
  CartResponse,
  PaymentMethod
} from "../types/Cart";
import {
  formatCurrency
} from "../utils/format";

export default function CartDrawer() {
  const location =
    useLocation();

  const navigate =
    useNavigate();

  const [cart, setCart] =
    useState<CartResponse | null>(null);

  const [couponCode, setCouponCode] =
    useState("");

  const [paymentMethod, setPaymentMethod] =
    useState<PaymentMethod>("PIX");

  const [cardInformation, setCardInformation] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  const [message, setMessage] =
    useState("");

  const [successMessage, setSuccessMessage] =
    useState("");

  const syncTimer =
    useRef<number | null>(null);

  const {
    items,
    isOpen,
    closeCart,
    removeItem,
    updateQuantity,
    replaceItems,
    clearCart,
    total
  } = useCart();

  const canCheckout =
    items.length > 0;

  const apiTotal =
    cart?.total ?? total;

  function toCartItems(
    serverCart: CartResponse
  ): CartItem[] {
    return serverCart.cartItems.map(
      item => ({
        id: item.productId,
        name: item.productName,
        price: item.priceAtPurchase,
        quantity: item.quantity
      })
    );
  }

  async function loadActiveCart(options?: {
    hydrateItems?: boolean;
  }) {
    const response =
      await api.get<CartResponse[]>(
        "/cart/me"
      );

    const activeCart =
      response.data.find(
        userCart => !userCart.paid
      ) ?? null;

    if (!activeCart) {
      return null;
    }

    setCart(activeCart);
    localStorage.setItem(
      "cartId",
      String(activeCart.id)
    );

    if (options?.hydrateItems) {
      replaceItems(
        toCartItems(activeCart)
      );
    }

    return activeCart;
  }

  async function syncCart(options?: {
    silent?: boolean;
  }) {
    if (!canCheckout) {
      if (!options?.silent) {
        setMessage(
          "Adicione pelo menos um produto ao carrinho."
        );
      }
      return null;
    }

    try {
      if (!options?.silent) {
        setLoading(true);
        setMessage("");
      }

      const payload = {
        cartItems: items.map(
          item => ({
            productId: item.id,
            quantity: item.quantity
          })
        )
      };

      const activeCart =
        cart ??
        await loadActiveCart();

      const response =
        activeCart
          ? await api.put(
              `/cart/edit-cart/${activeCart.id}`,
              payload
            )
          : await api.post(
              "/cart/create-cart",
              payload
            );

      setCart(response.data);

      localStorage.setItem(
        "cartId",
        String(response.data.id)
      );

      if (!options?.silent) {
        setMessage(
          `Carrinho #${response.data.id} atualizado.`
        );
      }
      return response.data as CartResponse;
    } catch {
      if (!options?.silent) {
        setMessage(
          "Nao foi possivel atualizar o carrinho."
        );
      }
      return null;
    } finally {
      if (!options?.silent) {
        setLoading(false);
      }
    }
  }

  useEffect(() => {
    const token =
      localStorage.getItem("token");

    if (!token || cart) {
      return;
    }

    loadActiveCart({
      hydrateItems: items.length === 0
    }).catch(() => {
      localStorage.removeItem("cartId");
    });
  }, [location.pathname]);

  useEffect(() => {
    if (!cart || cart.paid) {
      return;
    }

    if (items.length === 0) {
      setCart(null);
      localStorage.removeItem("cartId");
      return;
    }

    if (syncTimer.current) {
      window.clearTimeout(syncTimer.current);
    }

    syncTimer.current = window.setTimeout(
      () => {
        syncCart({
          silent: true
        });
      },
      450
    );

    return () => {
      if (syncTimer.current) {
        window.clearTimeout(syncTimer.current);
      }
    };
  }, [items, cart?.id, cart?.paid]);

  async function applyCoupon() {
    let activeCart = cart;

    if (!couponCode.trim()) {
      setMessage("Informe um cupom.");
      return;
    }

    activeCart = await syncCart();

    if (!activeCart) {
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      const response = await api.patch(
        `/cart/${activeCart.id}/coupons`,
        null,
        {
          params: {
            code: couponCode.trim()
          }
        }
      );

      setCart(response.data);
      setMessage("Cupom aplicado ao pedido.");
    } catch {
      setMessage("Cupom invalido ou expirado.");
    } finally {
      setLoading(false);
    }
  }

  async function pay() {
    const activeCart =
      await syncCart();

    if (!activeCart) {
      return;
    }

    try {
      setLoading(true);
      setMessage("");
      setSuccessMessage("");

      await api.post(
        "/payment/charge",
        {
          orderId: activeCart.id,
          paymentMethod,
          cardInformation:
            paymentMethod === "PIX"
              ? "PIX"
              : cardInformation || "MOCK"
        }
      );

      setCart(null);
      clearCart();
      setCouponCode("");
      localStorage.removeItem("cartId");
      closeCart();
      navigate("/orders");
    } catch {
      setMessage(
        "Nao foi possivel concluir o pagamento."
      );
    } finally {
      setLoading(false);
    }
  }

  function resetAndClose() {
    closeCart();
    setMessage("");
    setSuccessMessage("");
  }

  function resetLocalCart() {
    clearCart();
    setCart(null);
    setCouponCode("");
    localStorage.removeItem("cartId");
    setSuccessMessage("");
  }

  async function clearCurrentCart() {
    const activeCart =
      cart ??
      await loadActiveCart();

    if (!activeCart) {
      resetLocalCart();
      setMessage("Carrinho limpo.");
      return;
    }

    try {
      setLoading(true);
      setMessage("");

      await api.delete(
        `/cart/delete-cart/${activeCart.id}`
      );

      resetLocalCart();
      setMessage("Carrinho excluido.");
    } catch {
      setMessage(
        "Nao foi possivel excluir o carrinho."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div
      className={`cart-overlay ${
        isOpen ? "show" : ""
      }`}
      onClick={resetAndClose}
    >
      <div
        className="cart-drawer"
        onClick={(event) =>
          event.stopPropagation()
        }
      >
        <div className="cart-header">
          <div>
            <span className="eyebrow">
              Checkout
            </span>
            <h2>Meu Carrinho</h2>
          </div>

          <button
            aria-label="Fechar carrinho"
            onClick={resetAndClose}
          >
            x
          </button>
        </div>

        <div className="cart-items">
          {items.length === 0 && (
            <p className="empty-state">
              Seu carrinho esta vazio.
            </p>
          )}

          {items.map(item => (
            <div
              key={item.id}
              className="cart-item"
            >
              <div>
                <h4>{item.name}</h4>
                <p>
                  {formatCurrency(item.price)}
                </p>
              </div>

              <div className="qty-controls">
                <button
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity - 1
                    )
                  }
                >
                  -
                </button>

                <span>
                  {item.quantity}
                </span>

                <button
                  disabled={
                    Boolean(item.maxQuantity) &&
                    item.quantity >= item.maxQuantity!
                  }
                  onClick={() =>
                    updateQuantity(
                      item.id,
                      item.quantity + 1
                    )
                  }
                >
                  +
                </button>
              </div>

              {item.maxQuantity !== undefined && (
                <p className="cart-note">
                  Estoque disponivel: {item.maxQuantity}
                </p>
              )}

              <button
                className="remove-btn"
                onClick={() =>
                  removeItem(item.id)
                }
              >
                Remover
              </button>
            </div>
          ))}
        </div>

        <div className="cart-footer">
          {cart && (
            <div className="checkout-summary">
              <span>
                Pedido #{cart.id}
              </span>
              <strong>
                {cart.paid
                  ? "Pago"
                  : "Aguardando pagamento"}
              </strong>
            </div>
          )}

          <div className="coupon-row">
            <input
              value={couponCode}
              onChange={(event) =>
                setCouponCode(event.target.value)
              }
              placeholder="Cupom de desconto"
            />

            <button
              type="button"
              onClick={applyCoupon}
              disabled={loading || !canCheckout}
            >
              Aplicar
            </button>
          </div>

          {cart?.couponCode && (
            <p className="cart-note">
              Cupom aplicado: {cart.couponCode}
            </p>
          )}

          <h3>
            Total: {formatCurrency(apiTotal)}
          </h3>

          <div className="payment-box">
            <select
              value={paymentMethod}
              onChange={(event) =>
                setPaymentMethod(
                  event.target.value as PaymentMethod
                )
              }
            >
              <option value="PIX">PIX</option>
              <option value="CREDIT_CARD">
                Cartao de Credito
              </option>
              <option value="DEBIT_CARD">
                Cartao de Debito
              </option>
            </select>

            {paymentMethod !== "PIX" && (
              <input
                value={cardInformation}
                onChange={(event) =>
                  setCardInformation(
                    event.target.value
                  )
                }
                placeholder="Informacoes do cartao"
              />
            )}

            <button
              type="button"
              className="pay-btn"
              onClick={pay}
              disabled={loading || !canCheckout || cart?.paid}
            >
              Finalizar Pagamento
            </button>
          </div>

          {message && (
            <p className="form-message">
              {message}
            </p>
          )}

          {successMessage && (
            <p className="success-message">
              {successMessage}
            </p>
          )}

          <button
            className="clear-cart-btn"
            type="button"
            onClick={clearCurrentCart}
            disabled={loading}
          >
            Excluir Carrinho
          </button>
        </div>
      </div>
    </div>
  );
}
