<p align="center">
  <a href="https://codely.com">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://codely.com/logo/codely_logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://codely.com/logo/codely_logo-light.svg">
      <img alt="Codely logo" src="https://codely.com/logo/codely_logo.svg">
    </picture>
  </a>
</p>

<h1 align="center">
  ☕🚀 Java DDD example: Save the boilerplate in your new projects
</h1>

<p align="center">
    <a href="https://github.com/CodelyTV"><img src="https://img.shields.io/badge/Codely-OS-green.svg?style=flat-square" alt="Codely Open Source projects"/></a>
    <a href="https://pro.codely.com"><img src="https://img.shields.io/badge/Codely-Pro-black.svg?style=flat-square" alt="Codely Pro courses"/></a>
    <a href="https://github.com/CodelyTV/java-ddd-example/actions"><img src="https://github.com/CodelyTV/java-ddd-example/workflows/CI/badge.svg" alt="CI pipeline status"></a>
</p>

> ⚡ Start your Java projects as fast as possible

## ℹ️ Introduction

This is a repository intended to serve as a starting point if you want to bootstrap a Java project with JUnit and Gradle.

Here you have the [course on CodelyTV Pro where we explain step by step all this](https://pro.codely.tv/library/ddd-en-java/about/?utm_source=github&utm_medium=social&utm_campaign=readme) (Spanish)

## 🏁 How To Start

1. Install Java 25: `brew install --cask corretto`
2. Set it as your default JVM: `export JAVA_HOME='/Library/Java/JavaVirtualMachines/amazon-corretto-25.jdk/Contents/Home'`
3. Clone this repository: `git clone https://github.com/CodelyTV/java-ddd-example`.
4. Bring up the Docker environment: `make start`.
5. Execute some [Gradle lifecycle tasks](https://docs.gradle.org/current/userguide/java_plugin.html#lifecycle_tasks) in order to check everything is OK:
    1. Create [the project JAR](https://docs.gradle.org/current/userguide/java_plugin.html#sec:jar): `make build`
    2. Run the tests and plugins verification tasks: `make test`
6. Start developing!

## ☝️ How to update dependencies

* Gradle ([releases](https://gradle.org/releases/)): `./gradlew wrapper --gradle-version=WANTED_VERSION --distribution-type=bin`

## 💡 Related repositories

### ☕ Java

* 📂 [Java Basic example](https://github.com/CodelyTV/java-basic-example)
* ⚛ [Java OOP Examples](https://github.com/CodelyTV/java-oop-examples)
* 🧱 [Java SOLID Examples](https://github.com/CodelyTV/java-solid-examples)
* 🥦 [Java DDD Example](https://github.com/CodelyTV/java-ddd-example)

### 🐘 PHP

* 📂 [PHP Basic example](https://github.com/CodelyTV/php-basic-example)
* 🎩 [PHP DDD example](https://github.com/CodelyTV/php-ddd-example)
* 🥦 [PHP DDD Example](https://github.com/CodelyTV/php-ddd-example)

### 🧬 Scala

* 📂 [Scala Basic example](https://github.com/CodelyTV/scala-basic-example)
* ⚡ [Scala Basic example (g8 template)](https://github.com/CodelyTV/scala-basic-example.g8)
* ⚛ [Scala Examples](https://github.com/CodelyTV/scala-examples)
* 🥦 [Scala DDD Example](https://github.com/CodelyTV/scala-ddd-example)
