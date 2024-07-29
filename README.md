# Wezuwiusz VULCAN UONET+ SDK

[![GitHub Workflow status](https://img.shields.io/github/actions/workflow/status/wulkanowy/sdk/test.yml?branch=master&style=flat-square)](https://github.com/wulkanowy/sdk/actions)
[![JitPack](https://img.shields.io/jitpack/v/wulkanowy/sdk.svg?style=flat-square)](https://jitpack.io/#wulkanowy/sdk)
[![License](https://img.shields.io/github/license/wulkanowy/sdk.svg?style=flat-square)](https://github.com/wulkanowy/sdk)
[![Discord](https://img.shields.io/discord/390889354199040011.svg?style=flat-square)](https://discord.gg/vccAQBr)

> Unofficial unified way of retrieving data from the UONET+ register through mobile api and scraping api

## Features

Check it out [full public api](https://github.com/wezuwiusz/neowulkanowy-sdk/blob/master/sdk/src/main/kotlin/io/github/wulkanowy/sdk/Sdk.kt)
and [test examples](https://github.com/wezuwiusz/neowulkanowy-sdk/sdk/blob/master/sdk/src/test/kotlin/io/github/wulkanowy/sdk/SdkRemoteTest.kt).

## Documentation

Check [wiki page](https://sdk.wezuwiusz.eu/).

## Download

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.wezuwiusz:neowulkanowy-sdk:-SNAPSHOT' // lub inna wersja
}
```
