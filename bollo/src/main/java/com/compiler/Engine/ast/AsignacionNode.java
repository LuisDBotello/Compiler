package com.compiler.Engine.ast;

public class AsignacionNode extends SentenciaNode {
    private String identificador;
    private ExpresionNode expresion;
    
    public AsignacionNode(String id, ExpresionNode expr, int linea, int columna) {
        super(linea, columna);
        this.identificador = id;
        this.expresion = expr;
    }
    
    public String getIdentificador() { return identificador; }
    public ExpresionNode getExpresion() { return expresion; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("Asignacion\n");
        sb.append(indent(nivel + 1)).append("Variable: ").append(identificador).append("\n");
        sb.append(indent(nivel + 1)).append("Valor:\n");
        sb.append(expresion.toTreeString(nivel + 2));
        return sb.toString();
    }
}
