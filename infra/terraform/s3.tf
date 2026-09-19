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

resource "aws_s3_bucket_server_side_encryption_configuration" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}