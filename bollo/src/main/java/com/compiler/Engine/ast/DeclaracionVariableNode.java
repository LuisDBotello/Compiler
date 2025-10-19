package com.compiler.Engine.ast;

import java.util.List;

public class DeclaracionVariableNode extends SentenciaNode {
    private String tipo;
    private List<String> identificadores;
    private ExpresionNode inicializacion;
    
    public DeclaracionVariableNode(String tipo, List<String> ids, 
                                   ExpresionNode init, int linea, int columna) {
        super(linea, columna);
        this.tipo = tipo;
        this.identificadores = ids;
        this.inicializacion = init;
    }
    
    public String getTipo() { return tipo; }
    public List<String> getIdentificadores() { return identificadores; }
    public ExpresionNode getInicializacion() { return inicializacion; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("DeclaracionVariable\n");
        sb.append(indent(nivel + 1)).append("Tipo: ").append(tipo).append("\n");
        sb.append(indent(nivel + 1)).append("Identificadores: ")
          .append(String.join(", ", identificadores)).append("\n");
        if (inicializacion != null) {
            sb.append(indent(nivel + 1)).append("Inicialización:\n");
            sb.append(inicializacion.toTreeString(nivel + 2));
        }
        System.out.println(sb);
        return sb.toString();
    }
}