export interface LoginRequest {
  email: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
}
export type UserAuthority = 'ROLE_ADMIN' | 'ROLE_TEACHER' | 'ROLE_STUDENT';

export interface AuthenticatedUser {
  email: string;
  authorities: readonly UserAuthority[];
}
