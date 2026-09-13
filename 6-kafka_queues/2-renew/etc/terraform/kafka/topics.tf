# Generated from the domain events by domain-events:kafka:terraform:generate. Do not edit by hand.

resource "kafka_topic" "codely_mooc_course_created" {
  name               = "codely.mooc.course.created"
  partitions         = var.partitions
  replication_factor = var.replication_factor

  config = {
    "cleanup.policy"      = "delete"
    "min.insync.replicas" = var.min_insync_replicas
    "retention.ms"        = "-1"
  }
}

resource "kafka_topic" "codely_mooc_course_renamed" {
  name               = "codely.mooc.course.renamed"
  partitions         = var.partitions
  replication_factor = var.replication_factor

  config = {
    "cleanup.policy"      = "delete"
    "min.insync.replicas" = var.min_insync_replicas
    "retention.ms"        = "-1"
  }
}

resource "kafka_topic" "codely_mooc_new_courses_newsletter_email_sent" {
  name               = "codely.mooc.new-courses-newsletter-email.sent"
  partitions         = var.partitions
  replication_factor = var.replication_factor

  config = {
    "cleanup.policy"      = "delete"
    "min.insync.replicas" = var.min_insync_replicas
    "retention.ms"        = "-1"
  }
}

resource "kafka_topic" "dlq_codely_backoffice" {
  name               = "dlq.codely.backoffice"
  partitions         = var.partitions
  replication_factor = var.replication_factor

  config = {
    "cleanup.policy"                      = "delete"
    "errors.deadletterqueue.group.enable" = "true"
    "min.insync.replicas"                 = var.min_insync_replicas
    "retention.ms"                        = "2592000000"
  }
}

resource "kafka_topic" "dlq_codely_mooc" {
  name               = "dlq.codely.mooc"
  partitions         = var.partitions
  replication_factor = var.replication_factor

  config = {
    "cleanup.policy"                      = "delete"
    "errors.deadletterqueue.group.enable" = "true"
    "min.insync.replicas"                 = var.min_insync_replicas
    "retention.ms"                        = "2592000000"
  }
}
