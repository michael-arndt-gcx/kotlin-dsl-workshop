# Type-safe builder 5

Damit wir uns einer natürlicheren sprache annähern und die kein Wissen über die Semantik der Parameterreihenfolge von `grant("JANITOR" whenAccessing Floor::class)` voraussetzen, haben wir eine [infix](https://kotlinlang.org/docs/functions.html#infix-notation) "operator" `whenAccessing` eingeführt.

Außerdem haben wir die `privilege`-Funktion umbenannt, um die Semantik in der Sprache deutlicher zu machen: es wird nicht länger nur ein `Privilege` erzeugt, sondern es wird ein `Privilege` *für* ein bestimmtes `Subject` erzeugt. Dass ein `Privilege` erzeugt wird ist außerdem implizit durch die Nutzung der DSL gegeben.

```kotlin
forSubject(User::class) {
    // TODO noch immer viele Klammern, Auto-Vervollständigung in grant(|) wenig hilfreich, nutze stattdessen nur noch Infix-Operatoren
    grant("JANITOR" whenAccessing Floor::class) {
        Conjunction(
            Equals(User::id, Floor::ownerId),
            Equals(User::isAdmin, true)
        )
    }
}
```
# Type-safe builder 5

Da nur noch die condition innerhalb von grant {…} gesetzt wurde, geben wir diese direkt zurück.

Der ursprüngliche Builder wird nun nicht mehr als receiver verwendet, stattdessen haben wir mit `GrantBuilderDsl` ein `object` eingeführt, dass wir nur verwenden, um die Sichtbarkeit innerhalb der DSL zu steuern.

```kotlin
privilege(User::class) {
    grant("JANITOR", Floor::class) {
        Conjunction(
            Equals(User::id, Floor::ownerId),
            Equals(User::isAdmin, true)
        )
    }
}
```

# Type-safe builder 4

Wir haben auch für den `GrantBuilder` eine Facade eingeführt.

# Type-safe builder 3

Damit die `subject`-Property und die `addGrant` Funktion nicht länger sichtbar sind, haben wir eine `Facade` eingeführt.

Der Receiver von `privilege` ist jetzt eine `PrivilegeBuilderDslFacade`. Diese kapselt den ursprünglichen Builder und delegiert alle sichtbaren Properties an diesen. Unerwünschte Properties sind so nicht länger sichtbar.

```kotlin
fun privilege(subject: KClass<User>, block: PrivilegeBuilderDslFacade.() -> Unit)

class PrivilegeBuilderDslFacade(private val privilegeBuilder: PrivilegeBuilder) {
    private fun addGrant(grant: Grant) = privilegeBuilder.addGrant(grant)
}
```

```kotlin
privilege(User::class) {
    grant {
        permission = "JANITOR"
        target = Floor::class
        condition = Conjunction(
            Equals(User::id, Floor::ownerId),
            Equals(User::isAdmin, true)
        )
    }
    // nicht mehr sichtbar
    //subject = User::class
    //addGrant(grant)
}
```

# Type-safe builder 2

Wir haben das `Subject` als Parameter ermöglicht, zusätzlich kann `grant {…}` nur noch innerhalb von `privilege {…}` aufgerufen werden. Dazu haben wir aus `grant` eine [Extension Function](https://kotlinlang.org/docs/extensions.html#extension-functions) von `PrivilegeBuilder` gemacht.

Außerdem ist der Aufruf von `addGrant` nun nicht mehr möglich: innerhalb der Extension Function können wir auf den `PrivilegeBuilder` mittels `this` zugreifen. 

Obwohl `Subject` als Parameter verfügbar ist, kann subject im `privilege {…}`-Block erneut zugewiesen werden, ebenso ist `addGrant` noch sichtbar.

```kotlin
privilege(User::class) {
    grant {
        permission = "JANITOR"
        target = Floor::class
        condition = Conjunction(
            Equals(User::id, Floor::ownerId),
            Equals(User::isAdmin, true)
        )
    }
    subject = User::class
}
```
# Type-safe builder

Wir haben einen einfachen Typsicheren Builder gebaut, der die Hierarchie der Domäne abbildet.

Vorteil der Kotlin DSL: die Hierarchie der Domäne wird durch Einrückung visualisiert.

Allerdings gibt es noch einige "unangenehme" Artefakte: ein `grant`, der in der DSL erzeugt wird, muss zusätzlich noch zum umliegenden `Privilege` hinzugefügt werden und einzelne Werte können übersehen und nicht gesetzt werden. Wird dies vergessen, wird zur Laufzeit eine Exception geworfen.

```kotlin
privilege {
    subject = User::class

    val grant = grant {
        permission = "JANITOR"
        target = Floor::class
        condition = Conjunction(
            Equals(User::id, Floor::ownerId),
            Equals(User::isAdmin, true)
        )
    }
    
    addGrant(grant)
}
```