import {
  useEffect,
  useState
} from "react";
import {
  Link
} from "react-router-dom";

import api from "../api/axios";
import Navbar from "../components/Navbar";
import type {
  Product,
  ProductPageResponse
} from "../types/Product";
import {
  formatCurrency
} from "../utils/format";

export default function HomePage() {
  const [products, setProducts] =
    useState<Product[]>([]);

  const [search, setSearch] =
    useState("");

  const [page, setPage] =
    useState(0);

  const [totalPages, setTotalPages] =
    useState(1);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    loadProducts();
  }, [page]);

  async function loadProducts() {
    try {
      setLoading(true);
      setError("");

      const response =
        await api.get<ProductPageResponse>(
          "/product",
          {
            params: {
              name:
                search.trim() ||
                undefined,
              page,
              size: 12
            }
          }
        );

      setProducts(
        response.data.content ?? []
      );
      setTotalPages(
        response.data.totalPages || 1
      );
    } catch {
      setError(
        "Nao foi possivel carregar os produtos."
      );
    } finally {
      setLoading(false);
    }
  }

  function handleSearch(
    event: React.FormEvent
  ) {
    event.preventDefault();
    setPage(0);
    loadProducts();
  }

  return (
    <>
      <Navbar />

      <main className="home-container">
        <section className="store-hero">
          <div>
            <span className="eyebrow">
              Atlas Ecommerce
            </span>
            <h1>
              Tenis selecionados para comprar sem enrolacao.
            </h1>
            <p>
              Consulte estoque, aplique cupons e finalize o pagamento em poucos cliques.
            </p>
          </div>

          <form
            className="search-bar"
            onSubmit={handleSearch}
          >
            <input
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
              placeholder="Buscar por nome"
            />
            <button type="submit">
              Buscar
            </button>
          </form>
        </section>

        <div className="section-heading">
          <div>
            <span className="eyebrow">
              Produtos
            </span>
            <h2>Catalogo</h2>
          </div>
        </div>

        {error && (
          <p className="error-box">
            {error}
          </p>
        )}

        {loading ? (
          <div className="loading-grid">
            Carregando produtos...
          </div>
        ) : (
          <div className="products-grid">
            {products.map((product) => (
              <article
                className="product-card"
                key={product.id}
              >
                <img
                  className="product-image"
                  src={
                    product.imageUrl ||
                    "https://placehold.co/600x420"
                  }
                  alt={product.name}
                />

                <div className="product-content">
                  <div className="product-meta">
                    <span>
                      Estoque: {product.quantity ?? 0}
                    </span>
                  </div>

                  <h3 className="product-name">
                    {product.name}
                  </h3>

                  <p className="product-price">
                    {formatCurrency(product.price)}
                  </p>

                  <Link
                    className="product-button"
                    to={`/product/${product.id}`}
                  >
                    Ver Produto
                  </Link>
                </div>
              </article>
            ))}
          </div>
        )}

        {!loading && products.length === 0 && (
          <p className="empty-state">
            Nenhum produto encontrado.
          </p>
        )}

        <div className="pagination">
          <button
            disabled={page === 0}
            onClick={() =>
              setPage((current) =>
                Math.max(0, current - 1)
              )
            }
          >
            Anterior
          </button>

          <span>
            Pagina {page + 1} de {totalPages}
          </span>

          <button
            disabled={page + 1 >= totalPages}
            onClick={() =>
              setPage((current) =>
                current + 1
              )
            }
          >
            Proxima
          </button>
        </div>
      </main>
    </>
  );
}
