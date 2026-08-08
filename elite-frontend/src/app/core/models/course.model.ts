export interface Course {
  id?: string;
  courseCode?: string;
  name: string;
  description?: string;
  subject: string;
  grade: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
}
