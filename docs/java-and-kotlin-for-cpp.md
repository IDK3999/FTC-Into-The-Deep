# Java and Kotlin, for someone who knows olympiad C++ 

You already know how to program. This is a translation guide, not a tutorial: what maps straight
across, what is genuinely different, and which contest habits to drop.

This repo uses **both** languages. `PrimeTech` is Java; `PrimeTechV2` and `PrimeTechV3` are
Kotlin. They compile together and call each other freely, so you will read both.

## The short version

- Java is C++ with the sharp edges removed: no pointers, no manual memory management, no headers,
  no operator overloading, no templates-as-metaprogramming. Everything must live inside a class.
- Kotlin is Java with much less ceremony. Same libraries, same runtime, a fraction of the typing.
- Neither has anything like `#include <bits/stdc++.h>`, and you will not use 5% of what you know
  about fast I/O, because there is no input.

## Java, from C++

### No pointers, and `null` instead

There is no `*`, no `&`, no `new`/`delete` pairing to get right. Objects are always handled
through references, and a reference that points at nothing is `null`:

```java
DcMotorEx motor = null;              // like a null pointer
motor.setPower(0.5);                 // NullPointerException at runtime
```

`NullPointerException` is the single most common crash you will hit. In this codebase it almost
always means a component's `init()` was never called, so its motors were never looked up.

You never free anything: a garbage collector reclaims objects once nothing refers to them.

### Files, classes and packages

- One **public** class per file, and the file name must match the class name. `Claw.java` must
  contain `public class Claw`.
- No headers, no declaration/definition split. No `.h`, no forward declarations, no include
  guards, and the order you declare things in does not matter.
- `package PrimeTech.Components.Outtake;` at the top of a file says where it lives; it has to
  match the directory path.
- `import` is *not* `#include`. It does not paste in a file - it just lets you write `Servo`
  instead of `com.qualcomm.robotcore.hardware.Servo`. Importing costs nothing at runtime.
- There is no `using namespace`. There are no free functions at all: `Math.sqrt(x)` is a static
  method on the class `Math`.

### Types

| C++ | Java | Note |
|---|---|---|
| `int` | `int` | Always 32-bit signed. No `long long` surprises across machines. |
| `long long` | `long` | 64-bit. Write literals as `150L`. |
| `double` | `double` | Same. |
| `bool` | `boolean` | Not an int. `if (1)` does not compile - a real improvement. |
| `std::string` | `String` | Immutable. `+` concatenates. `==` compares *references*, so use `a.equals(b)`. |
| `std::vector<int>` | `ArrayList<Integer>` | Generics cannot hold primitives, hence `Integer`. |
| `int a[100]` | `int[] a = new int[100]` | Length known at runtime as `a.length`. Bounds are checked - out of range throws, it is not undefined behaviour. |
| `const` | `final` | `final` means "cannot be reassigned". It does **not** make the object immutable, so it is much weaker than `const`. |
| `struct` | just a class | No separate concept. |
| `enum class` | `enum` | Java enums are real objects: they can have fields and methods, and `switch` over one is exhaustive-ish. Used heavily here. |

Integer division truncates, `%` works, operator precedence is the same. Arithmetic will not
surprise you.

### Everything is in a class

There is no global scope. A "global" is a `static` field on some class - which is exactly what
`PrimeTech.Global.Global` is, and its own documentation explains why that was a mistake.

```java
public class Claw {
    public static final double GRIP_OPEN = 0.9;   // a constant: Claw.GRIP_OPEN
    private Servo gripServo;                      // one per instance
    public void open() { ... }                    // a method
}
```

`public` / `private` mean what you expect. Omitting them gives package-private: visible to other
classes in the same folder. You will see that in this codebase, usually by accident rather than
design.

### Passing arguments

Java is **always** pass-by-value, but for objects the value passed is the reference. So:

```java
void f(int x)      { x = 5; }        // caller's int is unchanged
void g(Claw c)     { c.open(); }     // caller's Claw IS affected
void h(Claw c)     { c = null; }     // caller's variable is unchanged
```

There is no `&` parameter, so a method cannot reassign the caller's variable. To return two
things, return an object.

### Things that simply do not exist

No operator overloading. No `friend`. No multiple inheritance (interfaces instead). No default
arguments - you write two methods with the same name and different parameters, which is called
overloading, and `Pedro.followPath` is an example. No destructors or RAII. No macros. No
templates as a compile-time computation tool, only generics, which are much weaker.

### Exceptions replace error codes

When something goes wrong at runtime you get an exception with a **stack trace** naming the exact
file and line. Read it - it is far more informative than a segfault. On the robot, exceptions show
up on the Driver Hub screen.

## Kotlin, from Java

Kotlin is where `PrimeTechV3` lives, so this is the part you will actually write. It runs on the
same runtime and uses the same libraries.

### Declarations are backwards, and semicolons are gone

```kotlin
val maxSpeed: Double = 1.0    // type AFTER the name, after a colon
val maxSpeed = 1.0            // and usually inferred, so you omit it
var counter = 0
```

`val` cannot be reassigned (like `final`); `var` can. Prefer `val`. No semicolons.

### Functions

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun double(x: Int) = x * 2    // single-expression form, return type inferred
fun update() { }              // returns Unit, i.e. void
```

Parameters can be passed by name, which is why the autonomous OpModes read the way they do:

```kotlin
runStep(
    begin = { Pedro.followPath(path) },
    isFinished = { Pedro.isDone() }
)
```

### `when` instead of `switch`

```kotlin
when (step) {
    0 -> { }
    1 -> doSomething()
    2, 3 -> step++            // several values at once
    else -> step = 0
}
```

No `break`, and no fallthrough. It is also an expression, so it can produce a value:

```kotlin
val position = when (target) {
    ClawPivotState.FRONT -> CLAW_PIVOT_FRONT
    ClawPivotState.BACK -> CLAW_PIVOT_BACK
}
```

When the subject is an enum and you use it as an expression, the compiler makes you cover every
case - a genuinely useful safety net when someone adds a new enum value later.

### `object` = singleton

This one matters, because `PrimeTechV3` is built out of them:

```kotlin
object Claw {
    private var gripState = GripState.CLOSED
    fun openGrip() { ... }
}
```

That declares a class **and** its single instance in one go. You never write `new Claw()`; you
just call `Claw.openGrip()`. Effectively a class where everything is static, created lazily on
first use. Convenient, but it means there is exactly one - `Delay` in this repo is a shared timer
for that reason, which its docs warn about.

### Null safety

The type system tracks nullability, which is Kotlin's headline feature:

```kotlin
var a: String = "hi"      // cannot ever be null; a = null will not compile
var b: String? = null     // the ? means it may be null
b?.length                 // safe call: null if b is null, instead of crashing
```

So `NullPointerException` largely stops being a thing. But hardware cannot be looked up until
`init()` runs, which leaves the awkward case of a field that is not nullable yet has no value
yet. Hence:

```kotlin
private lateinit var gripServo: Servo
```

`lateinit` means "I will assign this before anything reads it, trust me". Read it too early and
you get `UninitializedPropertyAccessException` - which in this codebase means `init()` was not
called, or `build()` was not called on a paths object.

### Properties, not getters

```kotlin
var targetTicks = 0.0
    private set           // anyone can read it, only this object can write it
```

Callers write `Lift.targetTicks`, not `Lift.getTargetTicks()`. Kotlin also sees Java getters and
setters as properties, which is why you see `motor.power = 0.5` calling Java's `setPower`, and
`motor.currentPosition` calling `getCurrentPosition()`.

### Lambdas

A block in braces is a function you can pass around:

```kotlin
private fun runStep(begin: () -> Unit, isFinished: () -> Boolean) { ... }
```

`() -> Unit` is "takes nothing, returns nothing". The autonomous step helper uses this so each
step can be written as two small blocks instead of eight lines of boilerplate.

### Small things you will notice

- String templates: `"step $step of $total"`, and `"${a + b}"` for expressions.
- `0.5.coerceIn(-1.0, 1.0)` clamps - the equivalent of `std::clamp`.
- `mapOf(A to 1.0, B to 2.0)` builds a read-only map; `map[key]` gives a nullable result, so you
  see `?: 0.0` supplying a default.
- `const val` is a true compile-time constant; plain `val` in an `object` is computed at runtime.
  That is why `HEADING = Math.toRadians(180.0)` cannot be `const`.

## Contest habits to drop

Robot code is read by your teammates for years and runs against physical hardware. Different
priorities:

| Olympiad habit | Here |
|---|---|
| `int n, m, k; double a, b;` | **Name things.** This was the main problem with the code in this repo before it was cleaned up: `p`, `i`, `d`, `f`, `ff`, `rx`, `score2f`, `get1`. You cannot review what you cannot read. |
| Comments are a waste of time | Write down *why*, especially where a number came from. `900.0` is meaningless; "lift ticks for the high basket, measured on the robot" is not. |
| Shortest code wins | Clearest code wins. It will be debugged at a competition, by someone else, under time pressure. |
| `#include <bits/stdc++.h>` | Import exactly what you use. Android Studio does it for you. |
| Read input, print answer, exit | No input, no output, never exits. It loops until someone presses stop. |
| Correct means "passes the tests" | There are no tests. Correct means it works on the robot, repeatedly, on a low battery, when something has been knocked out of alignment. |
| Globals are fine, the program is 200 lines | Globals bite hard once several components share state. See `PrimeTech.Global.Global` for a cautionary example that is still in this repo. |
| Undefined behaviour is a real danger | Not really a thing here. Out-of-bounds throws, null throws, and you get a stack trace. |

One habit that *does* transfer: thinking about state machines. An olympiad DP over states is not
far off what every OpMode in this repo is doing - `step` is the state, and each loop is one
transition.

## Reading order in this repo

1. `PrimeTechV3/Components/Claw.kt` - short, and the simplest possible component.
2. `PrimeTechV3/Components/Lift.kt` - the same shape, plus a PID controller.
3. `PrimeTechV3/Actions/Actions.kt` - how components combine into jobs. The core idea.
4. `PrimeTechV3/OpModes/Auto/Specimen/SpecimenAuto.kt` - a whole autonomous run.
5. `PrimeTech/OpModes/Tele/TeleSimple.java` - the Java side, and the TeleOp we drove.
