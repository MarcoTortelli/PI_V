import {
  useState
} from "react";
import type {
  FormEvent
} from "react";

import api from "../api/axios";
import Navbar from "../components/Navbar";
import type {
  CouponResponse
} from "../types/Cart";
import {
  formatDate
} from "../utils/format";

export default function AdminCouponsPage() {
  const [code, setCode] =
    useState("");

  const [discountPercentage, setDiscountPercentage] =
    useState(10);

  const [expirationDate, setExpirationDate] =
    useState("");

  const [lookupCode, setLookupCode] =
    useState("");

  const [coupon, setCoupon] =
    useState<CouponResponse | null>(null);

  const [message, setMessage] =
    useState("");

  async function createCoupon(
    event: FormEvent
  ) {
    event.preventDefault();

    try {
      const response =
        await api.post<CouponResponse>(
          "/coupon",
          null,
          {
            params: {
              code,
              discountPercentage,
              expirationDate:
                new Date(
                  expirationDate
                ).toISOString()
            }
          }
        );

      setCoupon(response.data);
      setMessage("Cupom criado.");
      setCode("");
      setExpirationDate("");
      setDiscountPercentage(10);
    } catch {
      setMessage(
        "Nao foi possivel criar o cupom."
      );
    }
  }

  async function findCoupon(
    event: FormEvent
  ) {
    event.preventDefault();

    try {
      const response =
        await api.get<CouponResponse>(
          `/coupon/${lookupCode.trim()}`
        );

      setCoupon(response.data);
      setMessage("");
    } catch {
      setCoupon(null);
      setMessage("Cupom nao encontrado.");
    }
  }

  async function deleteCoupon() {
    const target =
      coupon?.code || lookupCode;

    if (!target) {
      return;
    }

    const confirmed =
      window.confirm(
        `Excluir cupom ${target}?`
      );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/coupon/${target}`
      );
      setCoupon(null);
      setLookupCode("");
      setMessage("Cupom excluido.");
    } catch {
      setMessage(
        "Nao foi possivel excluir o cupom."
      );
    }
  }

  return (
    <>
      <Navbar />

      <main className="home-container admin-layout">
        <section className="admin-panel">
          <span className="eyebrow">
            Administracao
          </span>
          <h1>Criar Cupom</h1>

          <form
            className="admin-form"
            onSubmit={createCoupon}
          >
            <label>
              Codigo
              <input
                value={code}
                onChange={(event) =>
                  setCode(event.target.value)
                }
                required
              />
            </label>

            <label>
              Desconto (%)
              <input
                type="number"
                min="0"
                max="100"
                value={discountPercentage}
                onChange={(event) =>
                  setDiscountPercentage(
                    Number(event.target.value)
                  )
                }
                required
              />
            </label>

            <label>
              Data de expiracao
              <input
                type="datetime-local"
                value={expirationDate}
                onChange={(event) =>
                  setExpirationDate(
                    event.target.value
                  )
                }
                required
              />
            </label>

            <button type="submit">
              Criar Cupom
            </button>
          </form>

          {message && (
            <p className="form-message">
              {message}
            </p>
          )}
        </section>

        <section className="admin-panel">
          <h2>Consultar ou excluir</h2>

          <form
            className="inline-form"
            onSubmit={findCoupon}
          >
            <input
              value={lookupCode}
              onChange={(event) =>
                setLookupCode(event.target.value)
              }
              placeholder="Codigo do cupom"
            />
            <button type="submit">
              Buscar
            </button>
          </form>

          {coupon && (
            <div className="coupon-card">
              <span className="eyebrow">
                Cupom
              </span>
              <h3>{coupon.code}</h3>
              <p>
                {coupon.discountPercentage}% de desconto
              </p>
              <p>
                Expira em {formatDate(coupon.expirationDate)}
              </p>
              <button
                className="danger-btn"
                onClick={deleteCoupon}
              >
                Excluir Cupom
              </button>
            </div>
          )}
        </section>
      </main>
    </>
  );
}
