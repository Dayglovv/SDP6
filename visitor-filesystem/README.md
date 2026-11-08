# visitor-filesystem

Java 17 / Maven project (`org.example:visitor-filesystem`): an in-memory file system (files and
directories) with three independent operations implemented with the **Visitor pattern**.

## Visitor pattern

Visitor lets us add new operations to a class hierarchy without changing the classes.
The element structure (`FileNode`, `DirectoryNode`) stays the same; each operation lives in its own visitor.

**Double Dispatch** (two steps):
1. `element.accept(visitor)` - the call is dispatched on the runtime type of the *element*.
2. inside `accept`, `visitor.visit(this)` - the overload `visit(FileNode)` or `visit(DirectoryNode)`
   is chosen by the type of `this`, and the concrete visitor's code runs.

## Project structure

```
src/main/java/org/example/
  Element.java            interface: accept, getName, getParent, attachTo
  Visitor.java            interface: visit(FileNode), visit(DirectoryNode)
  AbstractNode.java       name validation + parent link
  FileNode.java           name, size (>= 0)
  DirectoryNode.java      name, children, tree integrity checks
  TraversingVisitor.java  base class that owns the traversal
  TotalSizeVisitor.java   sum of file sizes
  SearchVisitor.java      name criterion + minimum size -> sorted relative paths
  StatisticsVisitor.java  files, directories, max depth
  Main.java               deterministic demo
src/test/java/org/example/
  VisitorTest.java        T1-T7 (+ extra cases)
  FileSystemTest.java     T8 (tree integrity) + extra cases
```

## Visitors

* **TotalSizeVisitor** - adds the size of every file exactly once; directories add nothing. `getTotalSize()`.
* **SearchVisitor(nameCriterion, minSize)** - file matches if its name *contains* the criterion
  (case-insensitive) **and** `size >= minSize` (inclusive). Results are paths relative to the chosen
  root, separated by `/`, sorted alphabetically. `getResults()`.
* **StatisticsVisitor** - number of files, number of directories (the root counts), maximum depth
  (root = 0, its sub-directories = 1, ...). Empty directories are counted.
  Depth is measured over directories, which matches the assignment example (Files 3, Directories 3, Depth 2).

Every visitor is reusable: each new top-level `accept` resets the previous results.

## Traversal (variant B: visitors own the traversal)

The recursion is written **once** in `TraversingVisitor`: `visit(DirectoryNode)` calls
`child.accept(this)` for every child. No `instanceof`, `getClass()`, reflection or casts are used;
the correct behaviour is chosen by Double Dispatch. Concrete visitors only override small hooks
(`onStart`, `onFile`, `onEnterDirectory`, `onLeaveDirectory`).

## Tree integrity

| Situation | Result |
|---|---|
| negative file size, null/blank name, name with `/` or `\` | `IllegalArgumentException` |
| duplicate name in one directory | `IllegalArgumentException` |
| adding a directory to itself / creating a cycle | `IllegalArgumentException` |
| element that already has a parent | `IllegalStateException` |
| `getChildren()` | unmodifiable copy |

Policy: invalid *values* -> `IllegalArgumentException`; invalid *state* (already owned) -> `IllegalStateException`.

## Run

```
mvn test
mvn compile exec:java
```

Expected demo output is deterministic (see `Main`).

## Trade-offs

Adding a new operation = one new visitor class. Adding a new element type = a new `visit(...)`
method in `Visitor` and changes in all existing visitors.
