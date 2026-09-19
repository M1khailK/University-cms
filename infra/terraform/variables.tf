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