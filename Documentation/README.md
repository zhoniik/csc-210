# OO Javadoc Demo (no Maven)

A minimal, object-oriented Java project with multiple packages and full Javadoc,
built using plain `javac`/`javadoc` scripts.

## Layout

```
oo-javadoc-demo/
├─ src/
│  └─ com/example/
│     ├─ geometry/        # Shape, Circle, Rectangle (+ package-info.java)
│     ├─ util/            # Strings (+ package-info.java)
│     └─ app/             # Main (+ package-info.java)
├─ out/                   # Compiled .class files (created by compile.sh)
├─ docs/                  # Generated Javadoc (created by javadoc.sh)
├─ compile.sh
├─ run.sh
└─ javadoc.sh
```

## Requirements

- JDK 17+ (`javac`, `java`, `javadoc` on PATH)

## Build, Run, and Generate Docs

```bash
./compile.sh
./run.sh
./javadoc.sh
```

Open `docs/index.html` in your browser to view the API docs.

## Notes

- `package-info.java` files provide package-level Javadoc.
- All public types/methods have Javadoc comments with tags.
- Scripts are POSIX-friendly and have `set -euo pipefail` for reliability.
```

