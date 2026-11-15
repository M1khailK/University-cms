export interface GradeResponse {
  id: number;
  value: number;

  studentId: number;
  studentFirstName: string;
  studentLastName: string;
  studentEmail: string;

  lessonId: number;
  lessonName: string;
  lessonDate: string;
  lessonStartTime: string;
  lessonEndTime: string;
}

export interface GradeCreateRequest {
  studentId: number;
  lessonId: number;
  value: number;
}
