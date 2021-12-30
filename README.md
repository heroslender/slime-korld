# Slime Korld
[![](https://jitpack.io/v/luiz-otavio/slime-korld.svg)](https://jitpack.io/#luiz-otavio/slime-korld)

Easily create many slime worlds with the Slime Korld.

## What is Slime Korld?
Slime Korld is a bukkit library written in Kotlin to make minecraft worlds based on Slime World Format from [Hypixel Blog 5#](https://hypixel.net/threads/dev-blog-5-storing-your-skyblock-island.2190753/)

## How to use it?
```kotlin
main() {
    val delegator = SlimeKorld.createDelegator()
    
    val slimeWorld = delegator.createWorld(
        File("world.slime"), 
        "World"
    )

    println("Slime world name: ${slimeWorld.name}")
}
```
Also, you can refresh, delete or save the slime world. (It include saving entities/tile-entities)

## Installation
Gradle DSL:
```groovy
repositories() {
    maven {
        name = "jitpack"
        url = 'https://jitpack.io'
    }
}   

dependencies() {
    implementation 'com.github.luiz-otavio:slime-korld:${PROJECT_VERSION}'
}
```

Kotlin DSL:
```kotlin

repositories() {
    maven("https://jitpack.io")
}

dependencies() {
    implementation("com.github.luiz-otavio:slime-korld:${PROJECT_VERSION}")
}
```

Maven:
```xml
<repositories>
    <repository>
        <id>jitpack</id>
        <name>jitpack</name>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.luiz-otavio</groupId>
        <artifactId>slime-korld</artifactId>
        <version>${PROJECT_VERSION}</version>
    </dependency>
</dependencies>
```

## License
This project is licensed under the GNU General Public License v3.0.

## Contributing
Please feel free to open an issue or create a pull request.
