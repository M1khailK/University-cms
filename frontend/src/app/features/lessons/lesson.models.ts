export interface LessonResponse {
  id: number;
  name: string;

  date: string;
  startTime: string;
  endTime: string;

  subjectId: number;
  subjectName: string;

  groupId: number;
  groupName: string;

  teacherId: number;
  teacherFirstName: string;
  teacherLastName: string;
  teacherEmail: string;
}

export interface LessonCreateRequest {
  name: string;
  date: string;
  startTime: string;
  endTime: string;
  subjectId: number;
  groupId: number;
  teacherId: number;
}

export interface LessonUpdateRequest {
  name: string;
  date: string;
  startTime: string;
  endTime: string;
  subjectId: number;
  groupId: number;
  teacherId: number;
}
