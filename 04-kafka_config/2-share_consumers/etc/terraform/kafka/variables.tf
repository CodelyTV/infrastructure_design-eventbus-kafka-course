variable "bootstrap_servers" {
  type = list(string)
}

variable "partitions" {
  type    = number
  default = 6
}

variable "replication_factor" {
  type    = number
  default = 3
}

variable "min_insync_replicas" {
  type    = string
  default = "2"
}
