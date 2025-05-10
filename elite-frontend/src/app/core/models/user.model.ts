/**
 * User model representing a user in the system
 */
export interface User {
  id: string;
  eliteId?: string; // Keeping for backward compatibility
  name: string;
  email: string;
  username: string;
  role: string;
  profilePicture?: string;
  mobileNumber?: string;
  gender?: string;
  age?: number;
  createdAt?: string;
  updatedAt?: string;
  isActive: boolean;
  additionalInfo?: Record<string, any>;
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