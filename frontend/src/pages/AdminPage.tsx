import {
  Link
} from "react-router-dom";

import Navbar from "../components/Navbar";

export default function AdminPage() {
  return (
    <>
      <Navbar />

      <main className="home-container">
        <section className="section-heading">
          <div>
            <span className="eyebrow">
              Administracao
            </span>
            <h1>Painel do Administrador</h1>
          </div>
        </section>

        <div className="admin-cards">
          <Link
            className="admin-card-link"
            to="/admin/products"
          >
            <span className="eyebrow">
              Catalogo
            </span>
            <h2>Produtos</h2>
            <p>
              Cadastre, edite e exclua produtos da loja.
            </p>
          </Link>

          <Link
            className="admin-card-link"
            to="/admin/coupons"
          >
            <span className="eyebrow">
              Promocoes
            </span>
            <h2>Cupons</h2>
            <p>
              Crie, consulte e remova cupons de desconto.
            </p>
          </Link>
        </div>
      </main>
    </>
  );
}
