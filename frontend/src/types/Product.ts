export interface Product {
  id: number;
  name: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

export interface ProductPageResponse {
  content: Product[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
