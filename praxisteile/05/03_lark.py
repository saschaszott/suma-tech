from lark import Lark, UnexpectedInput

# Formulierung der Grammatik für boolesche Suchanfragen in BNF
grammar = """
    ?start: expr
    ?expr: expr "OR" conj   -> or_expr
         | conj
    ?conj: conj "AND" factor -> and_expr
         | factor
    ?factor: "NOT" factor    -> not_expr
           | "(" expr ")"
           | VAR

    %import common.CNAME -> VAR
    %import common.WS
    %ignore WS
"""

def parse_boolean_query(query):
    try:
        parser = Lark(grammar, parser="lalr")
        tree = parser.parse(query)
        print(tree.pretty())
        print(tree)
    except UnexpectedInput as e:
        print(f"Syntaxfehler in Suchanfrage:\n{e}")

if __name__ == "__main__":
    query = input("Boolesche Suchanfrage: ")
    result = parse_boolean_query(query)