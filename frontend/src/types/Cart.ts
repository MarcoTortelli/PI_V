export interface CartItemRequest {
  productId: number;
  quantity: number;
}

export interface CreateCartDto {
  cartItems: CartItemRequest[];
}

export interface CartResponse {
  id: number;
  createdAt: string;
  total: number;
  couponCode: string | null;
  paid: boolean;
  cartItems: CartItemResponse[];
}

export interface CartItemResponse {
  productId: number;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
  total: number;
}

export type PaymentMethod = "PIX" | "CREDIT_CARD" | "DEBIT_CARD";

export interface CreatePaymentDto {
  orderId: number;
  paymentMethod: PaymentMethod;
  cardInformation: string;
}

export interface PaymentDto {
  id: number;
  orderId: number;
  paymentMethod: PaymentMethod;
  amountPaid: number;
}

export interface CouponResponse {
  code: string;
  discountPercentage: number;
  expirationDate: string;
}
