export interface User {
  eliteId: string;
  name: string;
  email: string;
  username: string;
  role: string;
  profilePicture?: string;
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

export interface UserAuth {
  user: User;
  token: string;
  tokenExpiry?: string;
}

export interface LoginResponseDto {
  user: User;
  token: string;
  tokenExpiry?: string;
}

export interface UserResponseDto<T = any> {
  success: boolean;
  message?: string;
  data?: T;
}
