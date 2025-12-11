/**
 * User model representing a user in the system
 * Uses eliteId as the unique identifier (matches backend)
 */
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

/**
 * User authentication response
 */
export interface UserAuth {
  user: User;
  token: string;
  tokenExpiry?: string;
}

/**
 * Login response from the API
 */
export interface LoginResponseDto {
  user: User;
  token: string;
  tokenExpiry?: string;
}

/**
 * Common response format for user-related API calls
 */
export interface UserResponseDto<T = any> {
  success: boolean;
  message?: string;
  data?: T;
} 