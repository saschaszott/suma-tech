from lark import Lark, tree

grammar = """
    ?start: expr
    ?expr: expr "OR" term   -> or_expr
         | term
    ?term: term "AND" factor -> and_expr
         | factor
    ?factor: "NOT" factor    -> not_expr
           | "(" expr ")"
           | VAR

    %import common.CNAME -> VAR
    %import common.WS
    %ignore WS
"""

def show_parse_tree(query, png_filename):
    """
    Gibt den Parsebaum als PNG-Grafik aus.
    """
    parser = Lark(grammar, parser="lalr")
    tree.pydot__tree_to_png(parser.parse(query), png_filename, rankdir="BT", name="Parsebaum")

if __name__ == "__main__":
    query = input("Boolesche Suchanfrage: ")
    result = show_parse_tree(query, "parsetree.png")
