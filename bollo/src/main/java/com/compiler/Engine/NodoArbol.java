package com.compiler.Engine;

import java.util.ArrayList;
import java.util.List;

public class NodoArbol {
    private String nombre;
    private String valor;
    private List<NodoArbol> hijos;
    
    public NodoArbol(String nombre) {
        this.nombre = nombre;
        this.valor = "";
        this.hijos = new ArrayList<>();
    }
    
    public NodoArbol(String nombre, String valor) {
        this.nombre = nombre;
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }
    
    public void agregarHijo(NodoArbol hijo) {
        if (hijo != null) {
            this.hijos.add(hijo);
        }
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getValor() {
        return valor;
    }
    
    public List<NodoArbol> getHijos() {
        return hijos;
    }
    
    public String imprimirArbol() {
        return imprimirArbol("", true);
    }
    
    private String imprimirArbol(String prefijo, boolean esUltimo) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(prefijo);
        sb.append(esUltimo ? "└── " : "├── ");
        sb.append(nombre);
        if (!valor.isEmpty()) {
            sb.append(" [").append(valor).append("]");
        }
        sb.append("\n");
        
        for (int i = 0; i < hijos.size(); i++) {
            boolean ultimoHijo = (i == hijos.size() - 1);
            String nuevoPrefijo = prefijo + (esUltimo ? "    " : "│   ");
            sb.append(hijos.get(i).imprimirArbol(nuevoPrefijo, ultimoHijo));
        }
        
        return sb.toString();
    }
}