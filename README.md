# UONET+ Scraping API

> The UONET+ client using web scraping

[![CircleCI](https://img.shields.io/circleci/project/github/wulkanowy/api/master.svg?style=flat-square)](https://circleci.com/gh/wulkanowy/api)
[![Codecov](https://img.shields.io/codecov/c/github/wulkanowy/api/master.svg?style=flat-square)](https://codecov.io/gh/wulkanowy/api)
[![Bintray](https://img.shields.io/bintray/v/wulkanowy/wulkanowy/api.svg?style=flat-square)](https://bintray.com/wulkanowy/wulkanowy/api)
[![JitPack](https://img.shields.io/jitpack/v/wulkanowy/api.svg?style=flat-square)](https://jitpack.io/#wulkanowy/api)
[![License](https://img.shields.io/github/license/wulkanowy/api.svg?style=flat-square)](https://github.com/wulkanowy/api)
[![Trello](https://img.shields.io/badge/trello-api-blue.svg?style=flat-square)](https://trello.com/b/h9mKsEjb/api)
[![Discord](https://img.shields.io/discord/390889354199040011.svg?style=flat-square)](https://discord.gg/vccAQBr)


## Features

- attendance
- exams
- grades
- homework
- notes
- timetable
- messages

... and more. Check it out [full public api](https://github.com/wulkanowy/api/blob/0.6.4/src/main/kotlin/io/github/wulkanowy/api/Api.kt).


## Download

```gradle
allprojects {
    repositories {
		...
        jcenter()
    }
}

dependencies {
    implementation 'io.github.wulkanowy:api:0.6.4'
}
```

Or you can get last development version with [jitpack](https://jitpack.io/#wulkanowy/api).
