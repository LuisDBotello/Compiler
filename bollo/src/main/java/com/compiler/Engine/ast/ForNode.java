package com.compiler.Engine.ast;


public class ForNode extends SentenciaNode {
    private DeclaracionVariableNode inicializacion;
    private ExpresionNode condicion;
    private ExpresionNode incremento;
    private BloqueNode cuerpo;
    
    public ForNode(DeclaracionVariableNode init, ExpresionNode cond, 
                   ExpresionNode inc, BloqueNode cuerpo, int linea, int columna) {
        super(linea, columna);
        this.inicializacion = init;
        this.condicion = cond;
        this.incremento = inc;
        this.cuerpo = cuerpo;
    }
    
    public DeclaracionVariableNode getInicializacion() { return inicializacion; }
    public ExpresionNode getCondicion() { return condicion; }
    public ExpresionNode getIncremento() { return incremento; }
    public BloqueNode getCuerpo() { return cuerpo; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("For\n");
        
        if (inicializacion != null) {
            sb.append(indent(nivel + 1)).append("Inicialización:\n");
            sb.append(inicializacion.toTreeString(nivel + 2));
        }
        
        if (condicion != null) {
            sb.append(indent(nivel + 1)).append("Condición:\n");
            sb.append(condicion.toTreeString(nivel + 2));
        }
        
        if (incremento != null) {
            sb.append(indent(nivel + 1)).append("Incremento:\n");
            sb.append(incremento.toTreeString(nivel + 2));
        }
        
        sb.append(indent(nivel + 1)).append("Cuerpo:\n");
        sb.append(cuerpo.toTreeString(nivel + 2));
        
        return sb.toString();
    }
}