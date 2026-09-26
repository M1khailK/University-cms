resource "aws_s3_bucket" "lesson_materials" {
  bucket = var.lesson_materials_bucket_name
}

resource "aws_s3_bucket_public_access_block" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_ownership_controls" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  rule {
    object_ownership = "BucketOwnerEnforced"
  }
}

resource "aws_s3_bucket_versioning" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  depends_on = [
    aws_s3_bucket_versioning.lesson_materials
  ]

  rule {
    id     = "lesson-material-version-retention"
    status = "Enabled"

    filter {
      prefix = "lesson-materials/"
    }

    noncurrent_version_expiration {
      noncurrent_days = var.lesson_material_noncurrent_version_retention_days
    }

    abort_incomplete_multipart_upload {
      days_after_initiation = 1
    }
  }
}

resource "aws_s3_bucket_server_side_encryption_configuration" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

resource "aws_s3_bucket_cors_configuration" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  cors_rule {
    allowed_methods = ["POST", "HEAD"]
    allowed_origins = var.lesson_materials_cors_allowed_origins
    allowed_headers = ["Content-Type"]

    expose_headers = [
      "ETag",
      "x-amz-version-id"
    ]

    max_age_seconds = 300
  }
}