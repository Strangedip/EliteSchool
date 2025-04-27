export interface User {
  id: string;
  name: string;
  email: string;
  username: string;
  role: string;
  profilePicture?: string;
  mobileNumber?: string;
  gender?: string;
  age?: number;
//   createdAt?: string;
//   updatedAt?: string;
//   lastLogin?: string;
//   isActive: boolean;
//   permissions?: string[];
  additionalInfo?: Record<string, any>;
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