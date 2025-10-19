package com.compiler.Engine.ast;


public abstract class ExpresionNode extends ASTNode {
    protected String tipoResultado;
    
    public ExpresionNode(int linea, int columna) {
        super(linea, columna);
    }
    
    public String getTipoResultado() { return tipoResultado; }
    public void setTipoResultado(String tipo) { this.tipoResultado = tipo; }
}