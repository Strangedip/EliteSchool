export interface User {
  eliteId: string;
  name: string;
  email: string;
  username: string;
  role: string;
  mobileNumber?: string;
  gender?: string;
  age?: number;
  address?: string;
  emergencyContact?: string;
  createdAt?: string;
  updatedAt?: string;
  active: boolean;
  emailVerified?: boolean;
}

export interface LoginResponseDto {
  user: User;
  token: string;
  tokenExpiry?: string;
}
