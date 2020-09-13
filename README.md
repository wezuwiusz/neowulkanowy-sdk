# VULCAN UONET+ SDK

[![Codecov branch](https://img.shields.io/codecov/c/github/wulkanowy/sdk/master.svg?style=flat-square)](https://codecov.io/gh/wulkanowy/sdk)
[![CircleCI branch](https://img.shields.io/circleci/project/github/wulkanowy/sdk/master.svg?style=flat-square)](https://circleci.com/gh/wulkanowy/sdk)
[![Bintray](https://img.shields.io/bintray/v/wulkanowy/wulkanowy/sdk.svg?style=flat-square)](https://bintray.com/wulkanowy/wulkanowy/sdk)
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
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'io.github.wulkanowy:sdk:0.20.4'
}
```
