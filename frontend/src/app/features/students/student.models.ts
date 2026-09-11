export interface StudentResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  groupId: number;
  groupName: string;
}

export interface StudentsPageResponse {
  content: StudentResponse[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface StudentCreateRequest {
  firstName: string;
  lastName: string;
  email: string;
  groupId: number;
}