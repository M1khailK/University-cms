output "lesson_materials_bucket_name" {
  description = "S3 bucket used for lesson materials."
  value       = aws_s3_bucket.lesson_materials.id
}

output "lesson_materials_bucket_arn" {
  description = "ARN of the lesson materials S3 bucket."
  value       = aws_s3_bucket.lesson_materials.arn
}