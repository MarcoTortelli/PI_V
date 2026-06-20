import {
  useState
} from "react";
import type {
  FormEvent
} from "react";
import {
  Link,
  useNavigate
} from "react-router-dom";

import api from "../api/axios";

export default function RegisterPage() {
  const navigate =
    useNavigate();

  const [login, setLogin] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  async function handleSubmit(
    event: FormEvent
  ) {
    event.preventDefault();
    setError("");

    try {
      setLoading(true);

      const response = await api.post(
        "/auth/register",
        {
          login,
          password,
        }
      );

      localStorage.setItem(
        "token",
        response.data.token
      );

      navigate("/");
    } catch {
      setError(
        "Nao foi possivel cadastrar este usuario."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card">
        <span className="eyebrow">
          Atlas Ecommerce
        </span>

        <h1 className="auth-title">
          Criar Conta
        </h1>

        <p className="auth-subtitle">
          Cadastre login e senha para acessar as funcionalidades do ecommerce.
        </p>

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >
          <input
            className="auth-input"
            placeholder="Login"
            value={login}
            onChange={(event) =>
              setLogin(event.target.value)
            }
            required
          />

          <input
            className="auth-input"
            type="password"
            placeholder="Senha"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            required
          />

          {error && (
            <p className="form-message error">
              {error}
            </p>
          )}

          <button
            className="auth-button"
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Cadastrando..."
              : "Cadastrar"}
          </button>
        </form>

        <Link
          className="auth-link"
          to="/login"
        >
          Ja tenho conta
        </Link>
      </section>
    </main>
  );
}
