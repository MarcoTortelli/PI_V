import { BrowserRouter } from "react-router-dom";

import { AppRouter }
  from "./routes/AppRouter";

import { CartProvider }
  from "./context/CartContext";

import CartDrawer
  from "./context/CartDrawer";

export default function App() {
  return (
    <BrowserRouter>
      <CartProvider>

        <AppRouter />

        <CartDrawer />

      </CartProvider>
    </BrowserRouter>
  );
}