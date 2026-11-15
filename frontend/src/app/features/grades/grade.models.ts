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
export interface GradeStudentOptionResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface GradeLessonOptionResponse {
  id: number;
  name: string;
  date: string;
  startTime: string;
  endTime: string;
  groupId: number;
  groupName: string;
  students: GradeStudentOptionResponse[];
}

export interface GradeOptionsResponse {
  lessons: GradeLessonOptionResponse[];
}
