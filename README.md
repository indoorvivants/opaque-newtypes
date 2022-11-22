## Opaque newtypes

Internal library to define convenient newtypes.

Incubated in various versions in the following projects:

- [sn-bindgen](https://github.com/indoorvivants/sn-bindgen)
- [langoustine](https://github.com/neandertech/langoustine/)
- [sn-roguelike](https://github.com/indoorvivants/sn-roguelike)

|                | JVM  | Scala.js (1.x) | Scala Native (0.4.x)  |
| -------------- | ---  | -------------- | --------------------- |
| Scala 3.2.x    | ✅   | ✅             | ✅                    |

See [tests](modules/core/src/test/scala/Tests) for usage.

Coordinates:

```scala
"com.indoorvivants" %%% "opaque-newtypes" % "<version>" // SBT
"com.indoorvivants::opaque-newtypes::<version>" // Mill, Scala CLI
```

**What does "internal" mean?**

It means that version compatibility is not enforced, the usage patterns are 
tailored for how I see things, and in general I would alter things at will with 
little to know consideration for downstream users - it being internal, I assume 
myself to be the target audience.

That said, I welcome (and will gladly give credit) all cool ideas and contributions.
To balance those two seemingly opposite intentions, I strive to keep the licence as 
permissive as possible and the code structure as conducive to copypasta as possible.

The reason it's public is because I'm already paying more than I want for private Github Actions minutes 
and it's eating rapidly into my family's food budget (joke).
