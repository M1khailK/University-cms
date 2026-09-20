data "aws_caller_identity" "current" {}

data "aws_iam_policy_document" "backend_local_assume_role" {
  statement {
    effect = "Allow"

    actions = [
      "sts:AssumeRole"
    ]

    principals {
      type = "AWS"

      identifiers = [
        "arn:aws:iam::${data.aws_caller_identity.current.account_id}:user/${var.terraform_operator_user_name}"
      ]
    }
  }
}

resource "aws_iam_role" "backend_local" {
  name = "university-cms-backend-local"

  assume_role_policy = data.aws_iam_policy_document.backend_local_assume_role.json
}

data "aws_iam_policy_document" "backend_s3_upload" {
  statement {
    sid    = "UploadLessonMaterials"
    effect = "Allow"

    actions = [
      "s3:PutObject"
    ]

    resources = [
      "${aws_s3_bucket.lesson_materials.arn}/lesson-materials/*"
    ]
  }
}

resource "aws_iam_role_policy" "backend_s3_upload" {
  name = "lesson-materials-upload"

  role   = aws_iam_role.backend_local.id
  policy = data.aws_iam_policy_document.backend_s3_upload.json
}