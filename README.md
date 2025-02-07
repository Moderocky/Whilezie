Whilezie
=====

A compiler for the basic <em>WHILE</em> language to VM bytecode.

## Preface: Notes on Implementation

I built this in my 'model' language style:
a source code document is turned into a string of tokens, parsed into a complete 'model' of the document, then compiled.
This is not the most efficient way to write a compiler -- especially for something like <em>WHILE</em>,
which needs to know very little about its surrounding code.

The 'model' stage is a rigorous in-memory structure of the code.
This is a fantastic way to verify and test the program, resolve forward references, collect insights,
or build new adaptations (e.g. 'transpilation' to another language).
If I were writing an optimal compiler, the parsing should be done at the same time as the tokenising
and the model step could be skipped entirely.
(In fact, <em>WHILE</em> is so basic it could be done in a single stream without a backtracking buffer.)

The bytecode assembler is my [_Foundation 3_](https://github.com/Moderocky/Foundation).
Aside from being a lot more fun to use, it allowed me to do a lot of the resolution of values at compile-time.

## Grammar

There seemed to be some disagreement over what is considered the 'essential' WHILE grammar.
I chose to eliminate everything other than the bare essentials.

```antlr
identifier: [A-Za-z_][A-Za-z0-9_]*

program:
    | <identifier> read <identifier> <statement> write <identifier>

statements:
    | <statement> <statements>
    | ∅
 
statement:
    | <identifier> := <expression>
    | while <expression> <statement>
    | { <statements> }

expression:
    | nil
    | cons <expression> <expression>
    | hd <expression>
    | tl <expression>
    | <variable>
```

### Grammar Extensions

I also included the following as optional extensions in the parser.
Most extensions resolve to their real code.

#### Macros

```antlr
expression:
    | < <identifier> > <expression>
```

#### Macros

```antlr
statement:
    | if <expression> <statement> else <statement>
    | if <expression> <statement>
```

#### Literals

```antlr
expressions:
    | <expression>
    | <expressions> , <expression>
expression:
    | < <expression> . <expression >
    | [ <expressions> ]
    | (true|false)
    | [0-9]+
```

There seem to be some discrepancies about what is considered 'core' to the <em>WHILE</em> language.

### Text & Print-out

Some <em>WHILE</em> implementations include text literals and a `print` keyword.<sup>4</sup>
I chose not to include these.
However, my tokeniser has quoted text support, so an extender would only need to handle the text to binary tree
conversion.

### Numbers & Maths

Some grammars and implementations include number literals.<sup>1</sup> <sup>4</sup>
I also chose not to include these.
My reasoning was that part of the excitement of <em>WHILE</em> is assembling everything from binary trees.
My tokeniser has support for number literals, and the built-in _Java_ operation methods have tree to number conversion.

### If, Else & Switch

Some grammars include language-level `if` and `switch` statements.
I found this to be a little antithetical to the original purpose of <em>WHILE</em>:
I think the idea of being able to construct every other flow control statement from while-loops is betrayed slightly by
also including every other flow control statement.

I included `if` and `if-else` as optional content in the model parser.

### Skip

A no-operation code `skip` is included in some BNF grammars for <em>WHILE</em>, due to its presence in Hoare logic.
Since I already had the block `{}` as an empty statement, I did not include this.

## Example Macros

Several programs (macros) are included as examples:

1. Logic (not, and, or, xor, implication)
2. (Positive) addition, subtraction, multiplication, division
3. Deep-tree equality, number-kind test

## While-in-While

I wanted to create <em>WHILE</em>-evaluation in <em>WHILE</em>.

### Instruction Set

I stuck to the simplest possible program representation.

1. A program is a list of instructions.
2. Each instruction is a three-address list.
3. The first element in an instruction is a numerical operation code
   corresponding to the instruction.
4. The interpretation of the following elements depends on the instruction.

Theoretically, it is not very difficult to represent any <em>WHILE</em> program as three-address code.
This is essentially what the model stage of my compiler does, and doing it in <em>WHILE</em> itself is no different.
The only minor difference between _Java_ bytecode and three-address code is that bytecodes take up a variable number
of slots, whereas the three-address code is a fixed number.
I have cheated slightly in that, rather than jumping to subroutines within the instruction set,
I simply call the evaluator with a sub-instruction.

The operation codes are displayed below.
1. while <expr> <stmt>
2. read <index> nil
3. write <index> <expr>
4. cons <expr> <expr>
5. hd <expr> nil
6. tl <expr> nil
7. (tuple) <stmt> <stmt>

### Evaluation

1. Variables are indexed numerically in a **register** list
2. A two-element stack is used to hold values.


## References

1. Jonathan Aldrich, "The <em>WHILE</em> Language and <em>WHILE3ADDR</em>
   Representation", [cs.cmu.edu](https://www.cs.cmu.edu/~aldrich/courses/15-819O-13sp/resources/while-language.pdf).
2. Giulio Guerrieri, "Limits of Computation (4): WHILE-Semantics", Lecture, University of Sussex, Feb. 2025.
3. Aho, Sethi, Ullman, "Compilers: Principles, Techniques, and Tools", Addison-Wesley, 1986.
4. Leonardo Lucena, "While language", [whilelang](https://lrlucena.github.io/whilelang/#grammar), Federal Institute of
   Education, Science & Technology, Brazil.