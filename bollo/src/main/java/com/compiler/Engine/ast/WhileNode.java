package com.compiler.Engine.ast;


public class WhileNode extends SentenciaNode {
    private ExpresionNode condicion;
    private SentenciaNode cuerpo;
    
    public WhileNode(ExpresionNode cond, SentenciaNode cuerpo, int linea, int columna) {
        super(linea, columna);
        this.condicion = cond;
        this.cuerpo = cuerpo;
    }
    
    public ExpresionNode getCondicion() { return condicion; }
    public SentenciaNode getCuerpo() { return cuerpo; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("While\n");
        sb.append(indent(nivel + 1)).append("Condicion:\n");
        sb.append(condicion.toTreeString(nivel + 2));
        sb.append(indent(nivel + 1)).append("Cuerpo:\n");
        sb.append(cuerpo.toTreeString(nivel + 2));
        return sb.toString();
    }
}