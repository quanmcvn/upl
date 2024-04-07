<h2> UPL </h2>
A compiler for UPL (Uet Programming Language), a custom-made programming language

Has scanner and parser (to AST) currently

<h2>Usage</h2>

To run with the usage of JFlex
```sh
$ ant run
```

Or run with manual made scanner
```sh
$ ant run-manual 
```

These runs will take input as file `input.upl` by default.
You can change it by adding `-Dinput=<yourfile>`, eg `ant run -Dinput=input2.upl`

Output: Parsed AST in prefix notation

<h2>Grammar</h2>

The (current) grammar is in [context-free-grammar.pdf](./context-free-grammar.pdf)

<h2>Group member</h2>

- Nguyễn Hồng Quân (msv 22021122)

- Vũ Huy Hoàng (msv 22021108)
