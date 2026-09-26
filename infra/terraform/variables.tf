variable "aws_region" {
  description = "AWS region for University-CMS infrastructure."
  type        = string
  default     = "eu-central-1"
}

variable "lesson_materials_bucket_name" {
  description = "Globally unique S3 bucket name for lesson materials."
  type        = string

  validation {
    condition = (
      length(var.lesson_materials_bucket_name) >= 3
      && length(var.lesson_materials_bucket_name) <= 63
    )

    error_message = "S3 bucket name must contain between 3 and 63 characters."
  }
}

variable "lesson_materials_cors_allowed_origins" {
  description = "Origins allowed to upload lesson materials directly to S3."
  type        = list(string)
  default     = ["http://localhost:4200"]
}

variable "terraform_operator_user_name" {
  description = "IAM user allowed to assume the local backend role."
  type        = string
  default     = "university-cms-admin"
}

variable "lesson_material_noncurrent_version_retention_days" {
  description = "Number of days to retain noncurrent lesson material object versions."
  type        = number
  default     = 1

  validation {
    condition = (
      var.lesson_material_noncurrent_version_retention_days >= 1
      && floor(
        var.lesson_material_noncurrent_version_retention_days
      ) == var.lesson_material_noncurrent_version_retention_days
    )

    error_message = "Noncurrent version retention must be a positive whole number of days."
  }
}