import {
  useState
} from "react";

import api from "../api/axios";

export default function CheckoutModal({
  cartId
}: {
  cartId: number;
}) {
  const [method, setMethod] =
    useState("PIX");

  async function checkout() {
    await api.post(
      "/payment/charge",
      {
        orderId: cartId,
        paymentMethod:
          method,
        cardInformation:
          "MOCK"
      }
    );

    localStorage.removeItem(
      "cartId"
    );

    alert(
      "Pagamento realizado com sucesso!"
    );

    window.location.href = "/orders";
  }

  return (
    <div>
      <h2>
        Finalizar Compra
      </h2>

      <select
        value={method}
        onChange={(e) =>
          setMethod(
            e.target.value
          )
        }
      >
        <option value="PIX">
          Pix
        </option>

        <option value="CREDIT_CARD">
          Cartão de Crédito
        </option>

        <option value="DEBIT_CARD">
          Cartão de Débito
        </option>
      </select>

      <button
        onClick={checkout}
      >
        Confirmar Pagamento
      </button>
    </div>
  );
}
