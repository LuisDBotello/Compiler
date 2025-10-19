package com.compiler.Engine.ast;


public class LiteralNode extends ExpresionNode {
    private Object valor;
    private String tipo;
    
    public LiteralNode(Object valor, String tipo, int linea, int columna) {
        super(linea, columna);
        this.valor = valor;
        this.tipo = tipo;
        this.tipoResultado = tipo;
    }
    
    public Object getValor() { return valor; }
    public String getTipo() { return tipo; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        return indent(nivel) + "Literal: " + valor + " (tipo: " + tipo + ")\n";
    }
}