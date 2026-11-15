from lark import Lark, tree

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

def show_parse_tree(query, png_filename):
    """
    Gibt den Parsebaum als PNG-Grafik aus.
    """
    parser = Lark(grammar, parser="lalr")
    tree.pydot__tree_to_png(parser.parse(query), png_filename, rankdir="BT", name="Parsebaum")

if __name__ == "__main__":
    png_filename = "parsetree.png"
    query = input("Boolesche Suchanfrage: ")
    show_parse_tree(query, png_filename)
    print(f"Parsebaum als PNG-Grafik gespeichert unter {png_filename}")
