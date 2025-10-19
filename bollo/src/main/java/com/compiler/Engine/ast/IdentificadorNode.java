package com.compiler.Engine.ast;

public class IdentificadorNode extends ExpresionNode {
    private String nombre;
    
    public IdentificadorNode(String nombre, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
    }
    
    public String getNombre() { return nombre; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        return indent(nivel) + "Identificador: " + nombre + "\n";
    }
}