package com.compiler.Engine.ast;

public class ParametroNode extends ASTNode {
    private String tipo;
    private String nombre;
    
    public ParametroNode(String tipo, String nombre, int linea, int columna) {
        super(linea, columna);
        this.tipo = tipo;
        this.nombre = nombre;
    }
    
    public String getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        return indent(nivel) + "Parametro: " + tipo + " " + nombre + "\n";
    }
}