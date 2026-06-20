import {
  useEffect,
  useState
} from "react";
import type {
  FormEvent
} from "react";

import api from "../api/axios";
import Navbar from "../components/Navbar";
import type {
  CartResponse,
  PaymentDto
} from "../types/Cart";
import {
  formatCurrency,
  formatDate
} from "../utils/format";

export default function OrdersPage() {
  const [orders, setOrders] =
    useState<CartResponse[]>([]);

  const [paymentId, setPaymentId] =
    useState("");

  const [payment, setPayment] =
    useState<PaymentDto | null>(null);

  const [message, setMessage] =
    useState("");

  useEffect(() => {
    loadOrders();
  }, []);

  async function loadOrders() {
    try {
      const response =
        await api.get<CartResponse[]>(
          "/cart/me"
        );

      setOrders(response.data);
    } catch {
      setMessage(
        "Nao foi possivel carregar seus pedidos."
      );
    }
  }

  async function deleteOrder(cartId: number) {
    const confirmed =
      window.confirm(
        `Excluir o carrinho #${cartId}?`
      );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/cart/delete-cart/${cartId}`
      );
      setOrders((current) =>
        current.filter(
          order => order.id !== cartId
        )
      );
      setMessage("Carrinho excluido.");
    } catch {
      setMessage(
        "Nao foi possivel excluir o carrinho."
      );
    }
  }

  async function findPayment(
    event: FormEvent
  ) {
    event.preventDefault();

    if (!paymentId.trim()) {
      return;
    }

    try {
      const response =
        await api.get<PaymentDto>(
          `/payment/${paymentId.trim()}`
        );
      setPayment(response.data);
      setMessage("");
    } catch {
      setPayment(null);
      setMessage(
        "Pagamento nao encontrado."
      );
    }
  }

  return (
    <>
      <Navbar />

      <main className="home-container">
        <div className="section-heading">
          <div>
            <span className="eyebrow">
              Conta
            </span>
            <h1>Pedidos e Pagamentos</h1>
          </div>
        </div>

        {message && (
          <p className="form-message">
            {message}
          </p>
        )}

        <section className="admin-panel">
          <h2>Consultar pagamento</h2>

          <form
            className="inline-form"
            onSubmit={findPayment}
          >
            <input
              value={paymentId}
              onChange={(event) =>
                setPaymentId(event.target.value)
              }
              placeholder="ID do pagamento"
            />
            <button type="submit">
              Consultar
            </button>
          </form>

          {payment && (
            <div className="summary-grid">
              <div>
                <span>ID</span>
                <strong>{payment.id}</strong>
              </div>
              <div>
                <span>Pedido</span>
                <strong>{payment.orderId}</strong>
              </div>
              <div>
                <span>Metodo</span>
                <strong>{payment.paymentMethod}</strong>
              </div>
              <div>
                <span>Valor</span>
                <strong>
                  {formatCurrency(payment.amountPaid)}
                </strong>
              </div>
            </div>
          )}
        </section>

        <section className="admin-panel">
          <h2>Meus carrinhos</h2>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Criado em</th>
                  <th>Itens</th>
                  <th>Cupom</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order) => (
                  <tr key={order.id}>
                    <td>#{order.id}</td>
                    <td>
                      {formatDate(order.createdAt)}
                    </td>
                    <td>
                      {order.cartItems?.length ?? 0}
                    </td>
                    <td>
                      {order.couponCode || "-"}
                    </td>
                    <td>
                      {formatCurrency(order.total)}
                    </td>
                    <td>
                      <span
                        className={`status-pill ${
                          order.paid ? "paid" : ""
                        }`}
                      >
                        {order.paid
                          ? "Pago"
                          : "Pendente"}
                      </span>
                    </td>
                    <td>
                      <button
                        className="table-action danger"
                        onClick={() =>
                          deleteOrder(order.id)
                        }
                      >
                        Excluir
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </main>
    </>
  );
}
