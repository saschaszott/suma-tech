# Demo zur Phrasensuche mit einem Positional Index

### Zwei Einträge aus dem Positional Index

```
bar: [12: { 18 20 44 86 109 114 }, 32: { 12 }]
foo: [7: { 11 }, 12: { 10 19 39 87 112 }]
```

### Resultate verschiedener Suchanfragen (Phrase Queries, Proximity Queries)

```
q = foo/1 bar
12: 19 18
12: 19 20
12: 87 86
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
q = "foo bar"
12: 19 20
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
q = bar/2 foo
12: 18 19
12: 20 19
12: 86 87
12: 114 112
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
q = bar/4 foo
12: 18 19
12: 20 19
12: 86 87
12: 109 112
12: 114 112
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
q = "bar foo"
12: 18 19
12: 86 87
```
