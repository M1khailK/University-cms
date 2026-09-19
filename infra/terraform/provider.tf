provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project   = "university-cms"
      ManagedBy = "terraform"
    }
  }
}