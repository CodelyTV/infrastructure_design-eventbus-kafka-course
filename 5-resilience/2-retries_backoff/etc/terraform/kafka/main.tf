terraform {
  required_providers {
    kafka = {
      source  = "Mongey/kafka"
      version = ">= 0.7"
    }
  }
}

provider "kafka" {
  bootstrap_servers = var.bootstrap_servers
  tls_enabled       = false
}
