from pyparsing import infixNotation, opAssoc, Word, Keyword, alphas, ParseException

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
    bool_expr = infixNotation(operand,
        [
            (Keyword("NOT"), 1, opAssoc.RIGHT),
            (Keyword("AND"), 2, opAssoc.LEFT),
            (Keyword("OR"),  2, opAssoc.LEFT),
        ]
    )

    try:
        parsed = bool_expr.parseString(query, parse_all=True)
        print(parsed)
    except ParseException as pe:
        print(f"Syntaxfehler in Suchanfrage:\n{pe}")

if __name__ == "__main__":
    query = input("Boolesche Suchanfrage: ")
    result = parse_boolean_query(query)