export interface AuthenticationDto {
  login: string;
  password: string;
}

export interface UserRegisterRequest {
  login: string;
  password: string;
}

export interface TokenResponse {
  token: string;
}