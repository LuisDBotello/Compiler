package com.compiler.Engine.ast;

public abstract class ASTNode {
    protected int linea;
    protected int columna;
    
    public ASTNode(int linea, int columna) {
        this.linea = linea;
        this.columna = columna;
    }
    
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }
    
    // Método para el patrón Visitor
    public abstract <T> T accept(ASTVisitor<T> visitor);
    
    // Método para imprimir el árbol
    public abstract String toTreeString(int nivel);
    
    protected String indent(int nivel) {
        return "  ".repeat(nivel);
    }
}