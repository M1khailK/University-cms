export interface ScheduleLessonResponse {
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
}