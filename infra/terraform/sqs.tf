resource "aws_sqs_queue" "lesson_material_ingestion_dlq" {
  name = "university-cms-lesson-material-ingestion-dlq"

  message_retention_seconds = 1209600
  sqs_managed_sse_enabled   = true
}

resource "aws_sqs_queue" "lesson_material_ingestion" {
  name = "university-cms-lesson-material-ingestion"

  visibility_timeout_seconds = 300
  message_retention_seconds  = 345600
  receive_wait_time_seconds  = 20
  sqs_managed_sse_enabled    = true
}

resource "aws_sqs_queue_redrive_policy" "lesson_material_ingestion" {
  queue_url = aws_sqs_queue.lesson_material_ingestion.id

  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.lesson_material_ingestion_dlq.arn
    maxReceiveCount     = 5
  })
}

resource "aws_sqs_queue_redrive_allow_policy" "lesson_material_ingestion_dlq" {
  queue_url = aws_sqs_queue.lesson_material_ingestion_dlq.id

  redrive_allow_policy = jsonencode({
    redrivePermission = "byQueue"
    sourceQueueArns = [
      aws_sqs_queue.lesson_material_ingestion.arn
    ]
  })
}

data "aws_iam_policy_document" "lesson_material_ingestion_from_s3" {
  statement {
    sid    = "AllowS3LessonMaterialEvents"
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["s3.amazonaws.com"]
    }

    actions = [
      "sqs:SendMessage"
    ]

    resources = [
      aws_sqs_queue.lesson_material_ingestion.arn
    ]

    condition {
      test     = "ArnEquals"
      variable = "aws:SourceArn"
      values = [
        aws_s3_bucket.lesson_materials.arn
      ]
    }

    condition {
      test     = "StringEquals"
      variable = "aws:SourceAccount"
      values = [
        data.aws_caller_identity.current.account_id
      ]
    }
  }
}

resource "aws_sqs_queue_policy" "lesson_material_ingestion" {
  queue_url = aws_sqs_queue.lesson_material_ingestion.id
  policy    = data.aws_iam_policy_document.lesson_material_ingestion_from_s3.json
}

resource "aws_s3_bucket_notification" "lesson_materials" {
  bucket = aws_s3_bucket.lesson_materials.id

  queue {
    id            = "lesson-material-created"
    queue_arn     = aws_sqs_queue.lesson_material_ingestion.arn
    events        = ["s3:ObjectCreated:*"]
    filter_prefix = "lesson-materials/"
  }

  depends_on = [
    aws_sqs_queue_policy.lesson_material_ingestion
  ]
}