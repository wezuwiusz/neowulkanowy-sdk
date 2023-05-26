# Instalacja

[![Maven Central](https://img.shields.io/maven-central/v/io.github.wulkanowy/sdk?style=flat-square)](https://search.maven.org/artifact/io.github.wulkanowy/sdk)

Ostatnią stabilną wersję SDK możesz łatwo pobrać z repozytorium Maven Central:

```groovy
allprojects {
    repositories {
        mavenCentral()
    }
}
dependencies {
    implementation 'io.github.wulkanowy:sdk:2.0.6' // lub nowsza wersja, patrz na badge wyżej
}
```

---

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.wulkanowy/sdk?server=https%3A%2F%2Fs01.oss.sonatype.org&style=flat-square)](https://s01.oss.sonatype.org/content/repositories/snapshots/io/github/wulkanowy/sdk/)

W razie potrzeby możesz też pobrać wersję SNAPSHOT zawierającą jeszcze niewydane zmiany, które znajdą się w następnej wersji:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'io.github.wulkanowy:sdk:2.0.7-SNAPSHOT' // lub nowsza wersja, patrz na badge powyżej
}
```
