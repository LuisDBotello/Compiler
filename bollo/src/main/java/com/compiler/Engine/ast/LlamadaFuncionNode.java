package com.compiler.Engine.ast;

import java.util.ArrayList;
import java.util.List;

public class LlamadaFuncionNode extends ExpresionNode {
    private String nombreFuncion;
    private List<ExpresionNode> argumentos;
    
    public LlamadaFuncionNode(String nombre, List<ExpresionNode> args, 
                              int linea, int columna) {
        super(linea, columna);
        this.nombreFuncion = nombre;
        this.argumentos = args != null ? args : new ArrayList<>();
    }
    
    public String getNombreFuncion() { return nombreFuncion; }
    public List<ExpresionNode> getArgumentos() { return argumentos; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("LlamadaFuncion: ").append(nombreFuncion).append("\n");
        if (!argumentos.isEmpty()) {
            sb.append(indent(nivel + 1)).append("Argumentos:\n");
            for (ExpresionNode arg : argumentos) {
                sb.append(arg.toTreeString(nivel + 2));
            }
        }
        return sb.toString();
    }
}