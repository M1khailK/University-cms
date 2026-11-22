export interface ProfileResponse {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  groupId: number | null;
  groupName: string | null;
}

export interface PasswordChangeRequest {
  oldPassword: string;
  newPassword: string;
}
