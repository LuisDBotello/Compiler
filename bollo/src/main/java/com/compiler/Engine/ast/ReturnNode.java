package com.compiler.Engine.ast;

public class ReturnNode extends SentenciaNode {
    private ExpresionNode expresion;
    
    public ReturnNode(ExpresionNode expr, int linea, int columna) {
        super(linea, columna);
        this.expresion = expr;
    }
    
    public ExpresionNode getExpresion() { return expresion; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("Return\n");
        if (expresion != null) {
            sb.append(expresion.toTreeString(nivel + 1));
        }
        return sb.toString();
    }
}