import {
  useEffect,
  useState
} from "react";
import {
  Link,
  useParams
} from "react-router-dom";

import api from "../api/axios";
import Navbar from "../components/Navbar";
import {
  useCart
} from "../context/CartContext";
import type {
  Product
} from "../types/Product";
import {
  formatCurrency
} from "../utils/format";

export default function ProductPage() {
  const { id } =
    useParams();

  const [product, setProduct] =
    useState<Product>();

  const [quantity, setQuantity] =
    useState(1);

  const [loading, setLoading] =
    useState(true);

  const { addItem } =
    useCart();

  useEffect(() => {
    loadProduct();
  }, [id]);

  async function loadProduct() {
    try {
      setLoading(true);
      const response =
        await api.get<Product>(
          `/product/${id}`
        );

      setProduct(response.data);
    } finally {
      setLoading(false);
    }
  }

  function changeQuantity(next: number) {
    const max =
      product?.quantity || 1;

    setQuantity(
      Math.min(
        max,
        Math.max(1, next)
      )
    );
  }

  if (loading) {
    return (
      <>
        <Navbar />
        <p className="loading-grid">
          Carregando produto...
        </p>
      </>
    );
  }

  if (!product) {
    return (
      <>
        <Navbar />
        <main className="home-container">
          <p className="error-box">
            Produto nao encontrado.
          </p>
          <Link
            className="product-button inline-button"
            to="/"
          >
            Voltar ao catalogo
          </Link>
        </main>
      </>
    );
  }

  const outOfStock =
    product.quantity <= 0;

  return (
    <>
      <Navbar />

      <main className="product-page">
        <img
          src={
            product.imageUrl ||
            "https://placehold.co/700x700"
          }
          alt={product.name}
        />

        <section className="product-details">
          <Link
            className="back-link"
            to="/"
          >
            Voltar ao catalogo
          </Link>

          <span className="eyebrow">
            Produto
          </span>

          <h1>
            {product.name}
          </h1>

          <h2>
            {formatCurrency(product.price)}
          </h2>

          <p className="stock-line">
            {outOfStock
              ? "Produto sem estoque"
              : `${product.quantity} unidades disponiveis`}
          </p>

          <div className="quantity-selector">
            <button
              disabled={outOfStock}
              onClick={() =>
                changeQuantity(
                  quantity - 1
                )
              }
            >
              -
            </button>

            <span>
              {quantity}
            </span>

            <button
              disabled={outOfStock}
              onClick={() =>
                changeQuantity(
                  quantity + 1
                )
              }
            >
              +
            </button>
          </div>

          <button
            className="add-cart-btn"
            disabled={outOfStock}
            onClick={() =>
              addItem({
                id: product.id,
                name: product.name,
                price: product.price,
                quantity,
                maxQuantity: product.quantity
              })
            }
          >
            Adicionar ao Carrinho
          </button>
        </section>
      </main>
    </>
  );
}
