package com.compiler.Engine.ast;

import java.util.ArrayList;
import java.util.List;

public class DeclaracionFuncionNode extends SentenciaNode {
    private String tipoRetorno;
    private String nombre;
    private List<ParametroNode> parametros;
    private BloqueNode cuerpo;
    
    public DeclaracionFuncionNode(String tipoRetorno, String nombre,
                                  List<ParametroNode> params, BloqueNode cuerpo,
                                  int linea, int columna) {
        super(linea, columna);
        this.tipoRetorno = tipoRetorno;
        this.nombre = nombre;
        this.parametros = params != null ? params : new ArrayList<>();
        this.cuerpo = cuerpo;
    }
    
    public String getTipoRetorno() { return tipoRetorno; }
    public String getNombre() { return nombre; }
    public List<ParametroNode> getParametros() { return parametros; }
    public BloqueNode getCuerpo() { return cuerpo; }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toTreeString(int nivel) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent(nivel)).append("DeclaracionFuncion\n");
        sb.append(indent(nivel + 1)).append("Nombre: ").append(nombre).append("\n");
        sb.append(indent(nivel + 1)).append("Retorno: ").append(tipoRetorno).append("\n");
        if (!parametros.isEmpty()) {
            sb.append(indent(nivel + 1)).append("Parametros:\n");
            for (ParametroNode p : parametros) {
                sb.append(p.toTreeString(nivel + 2));
            }
        }
        sb.append(indent(nivel + 1)).append("Cuerpo:\n");
        sb.append(cuerpo.toTreeString(nivel + 2));
        return sb.toString();
    }
}
