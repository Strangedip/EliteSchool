export interface ErrorDetails {
    errorCode: string;
    errorDescription: string;
    timestamp: string;
}

export interface CommonResponseDto<T> {
    success: boolean;
    message: string;
    data?: T;
    error?: ErrorDetails;
}