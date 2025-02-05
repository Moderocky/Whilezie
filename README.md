Whilezie
=====

A compiler for the basic <em>WHILE</em> language to VM bytecode.

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

I also included the program 'macro' for convenience.
```antlr
expression:
    | < <identifier> > <expression>
```

There seem to be some discrepancies about what is considered 'core' to the <em>WHILE</em> language.

### Text & Print-out
Some <em>WHILE</em> implementations include text literals and a `print` keyword.<sup>4</sup>
I chose not to include these.
However, my tokeniser has quoted text support, so an extender would only need to handle the text to binary tree conversion.


### Numbers & Maths
Some grammars and implementations include number literals.<sup>1</sup> <sup>4</sup>
I also chose not to include these.
My reasoning was that part of the excitement of <em>WHILE</em> is assembling everything from binary trees.
My tokeniser has support for number literals, and the built-in _Java_ operation methods have tree to number conversion.

### If, Else & Switch
Some grammars include language-level `if` and `switch` statements.
I found this to be a little antithetical to the original purpose of <em>WHILE</em>:
I think the idea of being able to construct every other flow control statement from while-loops is betrayed slightly by also including every other flow control statement.

I included `if` and `if-else` as optional content in the model parser.

### Skip
A no-operation code `skip` is included in some BNF grammars for <em>WHILE</em>, due to its presence in Hoare logic.
Since I already had the block `{}` as an empty statement, I did not include this.

## Bonus Grammar






## References

1. Jonathan Aldrich, "The <em>WHILE</em> Language and <em>WHILE3ADDR</em> Representation", [cs.cmu.edu](https://www.cs.cmu.edu/~aldrich/courses/15-819O-13sp/resources/while-language.pdf).
2. Giulio Guerrieri, "Limits of Computation (4): WHILE-Semantics", Lecture, University of Sussex, Feb. 2025.
3. Aho, Sethi, Ullman, "Compilers: Principles, Techniques, and Tools", Addison-Wesley, 1986.
4. Leonardo Lucena, "While language", [whilelang](https://lrlucena.github.io/whilelang/#grammar), Federal Institute of Education, Science & Technology, Brazil.