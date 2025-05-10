/**
 * Error details for API responses
 */
export interface ErrorDetails {
    errorCode: string;
    errorDescription: string;
    timestamp: string;
}

/**
 * Standard API response format
 */
export interface CommonResponseDto<T> {
    success: boolean;
    message: string;
    data?: T;
    error?: ErrorDetails;
} 