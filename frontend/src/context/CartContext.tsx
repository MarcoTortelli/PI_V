import {
  createContext,
  useContext,
  useState
} from "react";

import type { ReactNode } from "react";

export interface CartItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  maxQuantity?: number;
}

interface CartContextType {
  items: CartItem[];
  isOpen: boolean;

  openCart: () => void;
  closeCart: () => void;

  addItem: (
    product: CartItem
  ) => void;

  removeItem: (
    productId: number
  ) => void;

  updateQuantity: (
    productId: number,
    quantity: number
  ) => void;

  replaceItems: (
    items: CartItem[]
  ) => void;

  clearCart: () => void;

  total: number;
}

const CartContext =
  createContext<CartContextType>(
    {} as CartContextType
  );

export function CartProvider({
  children
}: {
  children: ReactNode;
}) {
  const [items, setItems] =
    useState<CartItem[]>([]);

  const [isOpen, setIsOpen] =
    useState(false);

  function openCart() {
    setIsOpen(true);
  }

  function closeCart() {
    setIsOpen(false);
  }

  function addItem(
    product: CartItem
  ) {
    setItems((prev) => {
      const existing =
        prev.find(
          (p) => p.id === product.id
        );

      if (existing) {
        return prev.map((p) =>
          {
            if (p.id !== product.id) {
              return p;
            }

            const maxQuantity =
              product.maxQuantity ??
              p.maxQuantity;

            const nextQuantity =
              p.quantity +
              product.quantity;

            return {
              ...p,
              maxQuantity,
              quantity: maxQuantity
                ? Math.min(
                    maxQuantity,
                    nextQuantity
                  )
                : nextQuantity
            };
          }
        );
      }

      return [...prev, product];
    });

    setIsOpen(true);
  }

  function removeItem(
    productId: number
  ) {
    setItems((prev) =>
      prev.filter(
        (p) => p.id !== productId
      )
    );
  }

  function updateQuantity(
    productId: number,
    quantity: number
  ) {
    if (quantity <= 0) {
      removeItem(productId);
      return;
    }

    setItems((prev) =>
      prev.map((p) =>
        {
          if (p.id !== productId) {
            return p;
          }

          return {
            ...p,
            quantity: p.maxQuantity
              ? Math.min(
                  p.maxQuantity,
                  quantity
                )
              : quantity
          };
        }
      )
    );
  }

  function replaceItems(
    nextItems: CartItem[]
  ) {
    setItems(nextItems);
  }

  function clearCart() {
    setItems([]);
  }

  const total =
    items.reduce(
      (sum, item) =>
        sum +
        item.price *
          item.quantity,
      0
    );

  return (
    <CartContext.Provider
      value={{
        items,
        isOpen,
        openCart,
        closeCart,
        addItem,
        removeItem,
        updateQuantity,
        replaceItems,
        clearCart,
        total
      }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
