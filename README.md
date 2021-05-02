# VULCAN UONET+ SDK

[![GitHub Workflow status](https://img.shields.io/github/workflow/status/wulkanowy/sdk/Tests/master?style=flat-square)](https://github.com/wulkanowy/sdk/actions)
[![Codecov branch](https://img.shields.io/codecov/c/github/wulkanowy/sdk/master.svg?style=flat-square)](https://codecov.io/gh/wulkanowy/sdk)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.wulkanowy/sdk?style=flat-square)](https://search.maven.org/artifact/io.github.wulkanowy/sdk)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.wulkanowy/sdk?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)
[![JitPack](https://img.shields.io/jitpack/v/wulkanowy/sdk.svg?style=flat-square)](https://jitpack.io/#wulkanowy/sdk)
[![License](https://img.shields.io/github/license/wulkanowy/sdk.svg?style=flat-square)](https://github.com/wulkanowy/sdk)
[![Discord](https://img.shields.io/discord/390889354199040011.svg?style=flat-square)](https://discord.gg/vccAQBr)

> Unified way of retrieving data from the UONET+ register through mobile api and scraping api

## Features

Check it out [full public api](https://github.com/wulkanowy/sdk/blob/master/sdk/src/main/kotlin/io/github/wulkanowy/sdk/Sdk.kt)
and [test examples](https://github.com/wulkanowy/sdk/blob/master/sdk/src/test/kotlin/io/github/wulkanowy/sdk/SdkRemoteTest.kt).

## Documentation

Check [wiki page](https://github.com/wulkanowy/sdk/wiki).

## Download

```gradle
allprojects {
    repositories {
        // for stable releases
        mavenCentral()

        // for snapshots
        maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots/" }

        // for everything
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'io.github.wulkanowy:sdk:<version>'
}
```
