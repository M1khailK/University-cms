export type LessonMaterialStatus =
  'PENDING_UPLOAD' | 'UPLOADED' | 'PROCESSING' | 'READY' | 'FAILED';

export interface LessonMaterialUploadIntentResponse {
  materialId: number;
  uploadUrl: string;
  uploadMethod: string;
  expiresAt: string;
  contentType: string;
  formFields: Record<string, string>;
}

export interface LessonMaterialStatusResponse {
  materialId: number;
  lessonId: number;
  originalFilename: string;
  contentType: string;
  status: LessonMaterialStatus;
  expectedSizeBytes: number;
  actualSizeBytes: number | null;
  createdAt: string;
  uploadedAt: string | null;
  processingStartedAt: string | null;
  processedAt: string | null;
  failureReason: string | null;
}
