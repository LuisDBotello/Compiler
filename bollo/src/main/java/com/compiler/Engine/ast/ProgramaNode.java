package com.compiler.Engine.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramaNode extends ASTNode {
    private List<SentenciaNode> sentencias;
    
    public ProgramaNode(List<SentenciaNode> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias != null ? sentencias : new ArrayList<>();
    }
    
    public List<SentenciaNode> getSentencias() { return sentencias; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("Programa\n");
        for (SentenciaNode sent : sentencias) {
            sb.append(sent.toTreeString(nivel + 1));
        }
        return sb.toString();
    }
}
