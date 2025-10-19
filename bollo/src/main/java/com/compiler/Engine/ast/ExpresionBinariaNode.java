package com.compiler.Engine.ast;


public class ExpresionBinariaNode extends ExpresionNode {
    private ExpresionNode izquierda;
    private String operador;
    private ExpresionNode derecha;
    
    public ExpresionBinariaNode(ExpresionNode izq, String op, ExpresionNode der,
                                int linea, int columna) {
        super(linea, columna);
        this.izquierda = izq;
        this.operador = op;
        this.derecha = der;
    }
    
    public ExpresionNode getIzquierda() { return izquierda; }
    public String getOperador() { return operador; }
    public ExpresionNode getDerecha() { return derecha; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("ExpresionBinaria: ").append(operador).append("\n");
        sb.append(indent(nivel + 1)).append("Izquierda:\n");
        sb.append(izquierda.toTreeString(nivel + 2));
        sb.append(indent(nivel + 1)).append("Derecha:\n");
        sb.append(derecha.toTreeString(nivel + 2));
        return sb.toString();
    }
}