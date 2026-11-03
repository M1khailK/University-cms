export interface TeacherResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface TeacherCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
}

export interface TeacherUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
}
