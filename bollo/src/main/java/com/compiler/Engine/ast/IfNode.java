package com.compiler.Engine.ast;


public class IfNode extends SentenciaNode {
    private ExpresionNode condicion;
    private SentenciaNode bloqueIf;
    private SentenciaNode bloqueElse;
    
    public IfNode(ExpresionNode cond, SentenciaNode ifBlock, 
                  SentenciaNode elseBlock, int linea, int columna) {
        super(linea, columna);
        this.condicion = cond;
        this.bloqueIf = ifBlock;
        this.bloqueElse = elseBlock;
    }
    
    public ExpresionNode getCondicion() { return condicion; }
    public SentenciaNode getBloqueIf() { return bloqueIf; }
    public SentenciaNode getBloqueElse() { return bloqueElse; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("If\n");
        sb.append(indent(nivel + 1)).append("Condicion:\n");
        sb.append(condicion.toTreeString(nivel + 2));
        sb.append(indent(nivel + 1)).append("Bloque Then:\n");
        sb.append(bloqueIf.toTreeString(nivel + 2));
        if (bloqueElse != null) {
            sb.append(indent(nivel + 1)).append("Bloque Else:\n");
            sb.append(bloqueElse.toTreeString(nivel + 2));
        }
        return sb.toString();
    }
}

