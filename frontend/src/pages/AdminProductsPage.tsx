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
  Product,
  ProductPageResponse
} from "../types/Product";
import {
  formatCurrency
} from "../utils/format";

const emptyProduct = {
  name: "",
  price: 0,
  quantity: 0,
  imageUrl: ""
};

export default function AdminProductsPage() {
  const [products, setProducts] =
    useState<Product[]>([]);

  const [form, setForm] =
    useState(emptyProduct);

  const [editingId, setEditingId] =
    useState<number | null>(null);

  const [message, setMessage] =
    useState("");

  useEffect(() => {
    loadProducts();
  }, []);

  async function loadProducts() {
    const response =
      await api.get<ProductPageResponse>(
        "/product",
        {
          params: {
            page: 0,
            size: 100
          }
        }
      );

    setProducts(
      response.data.content ?? []
    );
  }

  function updateField(
    field: keyof typeof emptyProduct,
    value: string
  ) {
    setForm((current) => ({
      ...current,
      [field]:
        field === "price" ||
        field === "quantity"
          ? Number(value)
          : value
    }));
  }

  async function saveProduct(
    event: FormEvent
  ) {
    event.preventDefault();

    try {
      if (editingId) {
        await api.put(
          `/product/update-product/${editingId}`,
          form
        );
        setMessage("Produto atualizado.");
      } else {
        await api.post(
          "/product/create-product",
          form
        );
        setMessage("Produto cadastrado.");
      }

      setForm(emptyProduct);
      setEditingId(null);
      loadProducts();
    } catch {
      setMessage(
        "Nao foi possivel salvar o produto."
      );
    }
  }

  function editProduct(product: Product) {
    setEditingId(product.id);
    setForm({
      name: product.name,
      price: product.price,
      quantity: product.quantity,
      imageUrl: product.imageUrl
    });
  }

  async function deleteProduct(product: Product) {
    const confirmed =
      window.confirm(
        `Excluir ${product.name}?`
      );

    if (!confirmed) {
      return;
    }

    try {
      await api.delete(
        `/product/delete-product/${product.id}`
      );
      setMessage("Produto excluido.");
      loadProducts();
    } catch {
      setMessage(
        "Nao foi possivel excluir o produto."
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
          <h1>
            {editingId
              ? "Editar Produto"
              : "Cadastrar Produto"}
          </h1>

          <form
            className="admin-form"
            onSubmit={saveProduct}
          >
            <label>
              Nome
              <input
                value={form.name}
                onChange={(event) =>
                  updateField(
                    "name",
                    event.target.value
                  )
                }
                required
              />
            </label>

            <label>
              Preco
              <input
                type="number"
                step="0.01"
                min="0"
                value={form.price}
                onChange={(event) =>
                  updateField(
                    "price",
                    event.target.value
                  )
                }
                required
              />
            </label>

            <label>
              Estoque
              <input
                type="number"
                min="0"
                value={form.quantity}
                onChange={(event) =>
                  updateField(
                    "quantity",
                    event.target.value
                  )
                }
                required
              />
            </label>

            <label>
              URL da imagem
              <input
                value={form.imageUrl}
                onChange={(event) =>
                  updateField(
                    "imageUrl",
                    event.target.value
                  )
                }
                required
              />
            </label>

            <div className="button-row">
              <button type="submit">
                {editingId
                  ? "Salvar Alteracoes"
                  : "Cadastrar"}
              </button>

              {editingId && (
                <button
                  type="button"
                  className="secondary-btn"
                  onClick={() => {
                    setEditingId(null);
                    setForm(emptyProduct);
                  }}
                >
                  Cancelar
                </button>
              )}
            </div>
          </form>

          {message && (
            <p className="form-message">
              {message}
            </p>
          )}
        </section>

        <section className="admin-panel wide">
          <h2>Produtos cadastrados</h2>

          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Produto</th>
                  <th>Preco</th>
                  <th>Estoque</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id}>
                    <td>{product.name}</td>
                    <td>
                      {formatCurrency(product.price)}
                    </td>
                    <td>{product.quantity}</td>
                    <td className="table-actions">
                      <button
                        className="table-action"
                        onClick={() =>
                          editProduct(product)
                        }
                      >
                        Editar
                      </button>
                      <button
                        className="table-action danger"
                        onClick={() =>
                          deleteProduct(product)
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
