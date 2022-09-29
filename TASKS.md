# Type-safe builder

Sieh dir `kt.dslworkshop.JavaBuilderStyleKt.javaBuilderStyle` an und baue ein Kotlin-Pendant.

Benutze dafür das Muster:

```kotlin
val fooBuilder = FooBuilder()
fooBuilder.bar = "…"
fooBuilder.baz = "…"
val foo = fooBuilder.build()
```

wird zu

```kotlin
val fooBuilder = FooBuilder()
fooBuilder.bar = "…"
fooBuilder.baz = "…"
val foo = foo {
    bar = "…"
    baz = "…"
}
```
