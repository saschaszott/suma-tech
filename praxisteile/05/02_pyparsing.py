from pyparsing import infix_notation, OpAssoc, Word, Keyword, alphas, ParseException

def parse_boolean_query(query):

    # Operanden sind Terme
    operand = Word(alphas)

    class BoolOperand:
        def __init__(self, tokens):
            self.tokens = tokens[0]

        def __repr__(self):
            return f"{self.tokens}"

    operand.setParseAction(BoolOperand)

    # Definition der Operatoren in richtiger Präzedenz (von stark nach schwach)
    bool_expr = infix_notation(operand,
        [
            (Keyword("NOT"), 1, OpAssoc.RIGHT),
            (Keyword("AND"), 2, OpAssoc.LEFT),
            (Keyword("OR"),  2, OpAssoc.LEFT),
        ]
    )

    try:
        parsed = bool_expr.parseString(query, parse_all=True)
        print(parsed)
    except ParseException as pe:
        print(f"Syntaxfehler in Suchanfrage:\n{pe}")

if __name__ == "__main__":
    query = input("Boolesche Suchanfrage: ")
    parse_boolean_query(query)