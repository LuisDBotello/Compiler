package com.compiler.Engine.ast;

public class ExpresionUnariaNode extends ExpresionNode {
    private String operador;
    private ExpresionNode expresion;
    
    public ExpresionUnariaNode(String op, ExpresionNode expr, int linea, int columna) {
        super(linea, columna);
        this.operador = op;
        this.expresion = expr;
    }
    
    public String getOperador() { return operador; }
    public ExpresionNode getExpresion() { return expresion; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("ExpresionUnaria: ").append(operador).append("\n");
        sb.append(expresion.toTreeString(nivel + 1));
        return sb.toString();
    }
}