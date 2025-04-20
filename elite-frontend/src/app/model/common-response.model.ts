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

export class CommonResponse<T> implements CommonResponseDto<T> {
    success: boolean;
    message: string;
    data?: T;
    error?: ErrorDetails;

    constructor(success: boolean, message: string, data?: T, error?: ErrorDetails) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    static success<T>(message: string, data: T): CommonResponseDto<T> {
        return new CommonResponse<T>(true, message, data);
    }

    static error<T>(message: string, error: ErrorDetails): CommonResponseDto<T> {
        return new CommonResponse<T>(false, message, undefined, error);
    }
}
