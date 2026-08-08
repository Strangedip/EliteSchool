export interface NewUserPayload {
  name: string;
  username: string;
  email: string;
  password: string;
  role: string;
  gender: string;
  age?: number | null;
  mobileNumber?: string;
  address?: string;
}
