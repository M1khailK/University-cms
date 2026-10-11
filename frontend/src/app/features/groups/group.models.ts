export interface GroupResponse {
  id: number;
  name: string;
}

export interface GroupRequest {
  name: string;
}

export interface GroupStudentResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}
