import {
  Link,
  NavLink
} from "react-router-dom";

import {
  useCart
} from "../context/CartContext";
import {
  isAdminUser
} from "../utils/auth";

export default function Navbar() {
  const {
    items,
    openCart
  } = useCart();

  const itemCount =
    items.reduce(
      (total, item) =>
        total + item.quantity,
      0
    );

  const isAdmin =
    isAdminUser();

  function logout() {
    localStorage.removeItem("token");
    window.location.href = "/login";
  }

  return (
    <nav className="navbar">
      <Link
        to="/"
        className="logo"
      >
        Atlas
      </Link>

      <div className="nav-links">
        <NavLink to="/">
          Catalogo
        </NavLink>
        <NavLink to="/orders">
          Pedidos
        </NavLink>
        {isAdmin && (
          <NavLink to="/admin">
            Administracao
          </NavLink>
        )}
      </div>

      <div className="nav-actions">
        <button
          className="cart-btn"
          onClick={openCart}
        >
          Carrinho
          <span className="cart-badge">
            {itemCount}
          </span>
        </button>

        <button
          className="logout-btn"
          onClick={logout}
        >
          Sair
        </button>
      </div>
    </nav>
  );
}
