# Workshop: Kotlin DSL für Autorisierungsregeln

In diesem Repository findet ihr zum Workshop verschiedene Stände, mit denen wir gemeinsam arbeiten werden. Die unterschiedlichen Branches werden im Laufe des Tages sukzessive hinzugefügt.

Zur Vorbereitung möchte ich euch bitten das enthaltene Projekt in IntelliJ (Ultimate oder Community, 2022.2.2) zu **öffnen und tests auszuführen**, damit alle Dependencies und Indices geladen sind.

Stellt bitte sicher, dass ihr Java 17 oder neuer installiert habt.





# Agenda & Aufbau

Mehrere kurze, praktische Aufgaben.

ca. 1 Std. Blöcke mit kurzer Pause am Ende (5-10 min).

12:30 – 13:30: Mittagspause

Jeder Schritt hat einen eigenen git branch

Damit alles auch später noch nachvollziehbar ist, gibt es CHANGELOG.md und TASKS.md 




# Einleitung

Wir werden eine DSL für Autorisierungsregeln in Kotlin entwickeln!

Ziel:

```kotlin
forSubject<User> {
    grant permission "JANITOR" whenAccessing Floor::class where {
        User::id eq Floor::ownerId and User::isAdmin eq true
    }
    grant permission "OWNER" whenAccessing Floor::class where {
        User::id eq Floor::ownerId and User::isAdmin eq false
    }
    grant permission "USER" whenAccessing Floor::class where {
        User::id eq Floor::ownerId and User::isAdmin eq false
    }
}
```
