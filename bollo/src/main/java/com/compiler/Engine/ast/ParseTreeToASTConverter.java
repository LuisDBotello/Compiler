package com.compiler.Engine.ast;

import java.util.*;

import com.compiler.Engine.NodoParseTree;

// ============================================================================
// CONVERTIDOR: PARSE TREE → AST
// ============================================================================

public class ParseTreeToASTConverter {
    
    /**
     * Convierte un Parse Tree completo en un AST limpio
     * @param parseTree El nodo raíz del parse tree (debe ser "PROGRAMA")
     * @return ProgramaNode - La raíz del AST
     */
    public ProgramaNode convertir(NodoParseTree parseTree) {
        if (parseTree == null) {
            throw new IllegalArgumentException("El parse tree no puede ser null");
        }
        
        if (!parseTree.getTipo().equals("PROGRAMA")) {
            throw new IllegalArgumentException("El nodo raíz debe ser PROGRAMA, encontrado: " + parseTree.getTipo());
        }
        
        List<SentenciaNode> sentencias = new ArrayList<>();
        
        // Buscar el ESTATUTO principal dentro del PROGRAMA
        for (NodoParseTree hijo : parseTree.getHijos()) {
            if (hijo.getTipo().equals("ESTATUTO")) {
                convertirEstatutos(hijo, sentencias);
            }
        }
        
        return new ProgramaNode(sentencias, 1, 1);
    }
    
    // ========================================================================
    // CONVERTIR ESTATUTOS
    // ========================================================================
    
    /**
     * Convierte los estatutos del parse tree y los agrega a la lista de sentencias
     */
    private void convertirEstatutos(NodoParseTree nodo, List<SentenciaNode> sentencias) {
        List<NodoParseTree> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;
        
        // Identificar el tipo de estatuto
        NodoParseTree primerHijo = hijos.get(0);
        
        switch (primerHijo.getTipo()) {
            case "DECLARACION":
                convertirDeclaracion(primerHijo, sentencias);
                break;
                
            case "FOR":
                sentencias.add(convertirFor(primerHijo));
                break;
                
            case "IF":
                sentencias.add(convertirIf(primerHijo));
                break;
                
            case "WHILE":
                sentencias.add(convertirWhile(primerHijo));
                break;
                
            case "PRINT":
                sentencias.add(convertirPrint(primerHijo));
                break;
                
            case "ID":
                // Puede ser una asignación
                SentenciaNode asignacion = convertirAsignacion(nodo);
                if (asignacion != null) {
                    sentencias.add(asignacion);
                }
                break;
        }
        
        // Buscar siguiente estatuto (recursivo)
        for (NodoParseTree hijo : hijos) {
            if (hijo.getTipo().equals("ESTATUTO")) {
                convertirEstatutos(hijo, sentencias);
            }
        }
    }
    
    // ========================================================================
    // CONVERTIR DECLARACIÓN
    // ========================================================================
    
    /**
     * Convierte una declaración de variable del parse tree
     * Ejemplo: int x = 90;
     */
    private void convertirDeclaracion(NodoParseTree nodo, List<SentenciaNode> sentencias) {
        List<NodoParseTree> hijos = nodo.getHijos();
        if (hijos.isEmpty()) return;
        
        String tipo = null;
        String nombreVar = null;
        ExpresionNode inicializacion = null;
        int linea = 1;
        
        for (int i = 0; i < hijos.size(); i++) {
            NodoParseTree hijo = hijos.get(i);
            
            // Obtener tipo
            // Manejar tanto "TIPO_INT" con comillas como TIPO_INT sin comillas
            String tipoNodo = hijo.getTipo();
            if (tipoNodo.startsWith("TIPO_") || 
                tipoNodo.startsWith("\"TIPO_") ||
                tipoNodo.equals("\"TIPO_INT\"") ||
                tipoNodo.equals("\"TIPO_STRING\"") ||
                tipoNodo.equals("\"TIPO_FLOAT\"") ||
                tipoNodo.equals("\"TIPO_DOUBLE\"") ||
                tipoNodo.equals("\"TIPO_BOOLEAN\"")) {
                tipo = hijo.getLexema();
                linea = hijo.getLinea();
            }
            // Obtener identificador
            else if (hijo.getTipo().equals("ID") && nombreVar == null) {
                nombreVar = hijo.getLexema();
                linea = hijo.getLinea();
            }
            // Obtener inicialización
            else if (hijo.getTipo().equals("NUM")) {
                inicializacion = convertirLiteral(hijo);
            }
            else if (hijo.getTipo().equals("CADENA")) {
                inicializacion = convertirLiteral(hijo);
            }
            else if (hijo.getTipo().equals("CALCULO")) {
                inicializacion = convertirCalculo(hijo);
            }
        }
        
        // Crear nodo de declaración
        if (tipo != null && nombreVar != null) {
            List<String> identificadores = new ArrayList<>();
            identificadores.add(nombreVar);
            
            DeclaracionVariableNode declaracion = new DeclaracionVariableNode(
                tipo, identificadores, inicializacion, linea, 1
            );
            sentencias.add(declaracion);
        }
        
        // Procesar siguiente declaración (si existe)
        for (NodoParseTree hijo : hijos) {
            if (hijo.getTipo().equals("DECLARACION")) {
                convertirDeclaracion(hijo, sentencias);
            } else if (hijo.getTipo().equals("ESTATUTO")) {
                convertirEstatutos(hijo, sentencias);
            }
        }
    }
    
    // ========================================================================
    // CONVERTIR ASIGNACIÓN
    // ========================================================================
    
    /**
     * Convierte una asignación: x = 10;
     */
    private SentenciaNode convertirAsignacion(NodoParseTree nodo) {
        List<NodoParseTree> hijos = nodo.getHijos();
        if (hijos.size() < 3) return null;
        
        String nombreVar = null;
        ExpresionNode expresion = null;
        int linea = 1;
        boolean esAsignacion = false;
        
        for (int i = 0; i < hijos.size(); i++) {
            NodoParseTree hijo = hijos.get(i);
            
            if (hijo.getTipo().equals("ID") && nombreVar == null) {
                nombreVar = hijo.getLexema();
                linea = hijo.getLinea();
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals("=")) {
                esAsignacion = true;
            }
            else if (esAsignacion && hijo.getTipo().equals("CALCULO")) {
                expresion = convertirCalculo(hijo);
            }
            else if (esAsignacion && hijo.getTipo().equals("NUM")) {
                expresion = convertirLiteral(hijo);
            }
            else if (esAsignacion && hijo.getTipo().equals("CADENA")) {
                expresion = convertirLiteral(hijo);
            }
        }
        
        if (nombreVar != null && expresion != null && esAsignacion) {
            return new AsignacionNode(nombreVar, expresion, linea, 1);
        }
        
        return null;
    }
    
    // ========================================================================
    // CONVERTIR FOR
    // ========================================================================
    
    /**
     * Convierte un ciclo for: for(int i = 0; i < 10; i++)
     */
    private ForNode convertirFor(NodoParseTree nodo) {
        List<NodoParseTree> hijos = nodo.getHijos();
        
        String tipoContador = null;
        String nombreContador = null;
        ExpresionNode inicializacion = null;
        ExpresionNode condicion = null;
        BloqueNode cuerpo = null;
        int linea = 1;
        
        boolean enInicializacion = false;
        boolean enCondicion = false;
        boolean enIncremento = false;
        boolean enCuerpo = false;
        int contadorPuntoyComa = 0;
        
        for (int i = 0; i < hijos.size(); i++) {
            NodoParseTree hijo = hijos.get(i);
            
            // Detectar secciones del for
            if (hijo.getLexema() != null && hijo.getLexema().equals("(")) {
                enInicializacion = true;
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals(";")) {
                contadorPuntoyComa++;
                if (contadorPuntoyComa == 1) {
                    enInicializacion = false;
                    enCondicion = true;
                } else if (contadorPuntoyComa == 2) {
                    enCondicion = false;
                    enIncremento = true;
                }
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals(")")) {
                enIncremento = false;
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals("{")) {
                enCuerpo = true;
            }
            
            // Procesar inicialización: int i = 0
            if (enInicializacion) {
                String tipoNodo = hijo.getTipo();
                if (tipoNodo.startsWith("TIPO_") || 
                    tipoNodo.startsWith("\"TIPO_") ||
                    tipoNodo.contains("TIPO_")) {
                    tipoContador = hijo.getLexema();
                    linea = hijo.getLinea();
                }
                else if (hijo.getTipo().equals("ID") && nombreContador == null) {
                    nombreContador = hijo.getLexema();
                }
                else if (hijo.getTipo().equals("NUM")) {
                    inicializacion = convertirLiteral(hijo);
                }
            }
            
            // Procesar condición: i < 10
            else if (enCondicion && condicion == null) {
                condicion = construirCondicionFor(hijos, i);
            }
            
            // Procesar cuerpo
            else if (hijo.getTipo().equals("ESTATUTO") && enCuerpo) {
                List<SentenciaNode> sentenciasCuerpo = new ArrayList<>();
                convertirEstatutos(hijo, sentenciasCuerpo);
                cuerpo = new BloqueNode(sentenciasCuerpo, linea, 1);
            }
        }
        
        // Crear la declaración del contador como parte de la inicialización
        DeclaracionVariableNode declContador = null;
        if (tipoContador != null && nombreContador != null) {
            List<String> ids = new ArrayList<>();
            ids.add(nombreContador);
            declContador = new DeclaracionVariableNode(
                tipoContador, ids, inicializacion, linea, 1
            );
        }
        
        // Crear incremento (i++)
        ExpresionNode incremento = null;
        if (nombreContador != null) {
            // i++ se representa como i = i + 1
            ExpresionNode varI = new IdentificadorNode(nombreContador, linea, 1);
            ExpresionNode uno = new LiteralNode(1, "int", linea, 1);
            incremento = new ExpresionBinariaNode(varI, "+", uno, linea, 1);
        }
        
        if (cuerpo == null) {
            cuerpo = new BloqueNode(new ArrayList<>(), linea, 1);
        }
        
        return new ForNode(declContador, condicion, incremento, cuerpo, linea, 1);
    }
    
    /**
     * Construye la condición del for a partir de los nodos
     */
    private ExpresionNode construirCondicionFor(List<NodoParseTree> hijos, int inicio) {
        String nombreVar = null;
        String operador = null;
        String valorLimite = null;
        int linea = 1;
        
        for (int i = inicio; i < hijos.size(); i++) {
            NodoParseTree hijo = hijos.get(i);
            
            if (hijo.getTipo().equals("ID") && nombreVar == null) {
                nombreVar = hijo.getLexema();
                linea = hijo.getLinea();
            }
            else if (hijo.getTipo().equals("COMPARADOR")) {
                operador = hijo.getLexema();
            }
            else if (hijo.getTipo().equals("NUM") && valorLimite == null) {
                valorLimite = hijo.getLexema();
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals(";")) {
                break;
            }
        }
        
        if (nombreVar != null && operador != null && valorLimite != null) {
            ExpresionNode izq = new IdentificadorNode(nombreVar, linea, 1);
            ExpresionNode der = new LiteralNode(Integer.parseInt(valorLimite), "int", linea, 1);
            return new ExpresionBinariaNode(izq, operador, der, linea, 1);
        }
        
        return new LiteralNode(true, "boolean", linea, 1);
    }
    
    // ========================================================================
    // CONVERTIR IF
    // ========================================================================
    
    /**
     * Convierte un if-else
     */
    private IfNode convertirIf(NodoParseTree nodo) {
        List<NodoParseTree> hijos = nodo.getHijos();
        
        ExpresionNode condicion = null;
        BloqueNode bloqueIf = null;
        BloqueNode bloqueElse = null;
        int linea = 1;
        boolean dentroCondicion = false;
        boolean dentroIf = false;
        int nivelParentesis = 0;
        
        for (int i = 0; i < hijos.size(); i++) {
            NodoParseTree hijo = hijos.get(i);
            
            if (hijo.getTipo().equals("if")) {
                linea = hijo.getLinea();
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals("(")) {
                nivelParentesis++;
                if (condicion == null) {
                    dentroCondicion = true;
                }
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals(")")) {
                nivelParentesis--;
                if (nivelParentesis == 0 && dentroCondicion) {
                    dentroCondicion = false;
                    dentroIf = true;
                }
            }
            else if (dentroCondicion && hijo.getTipo().equals("CALCULO")) {
                condicion = convertirCalculo(hijo);
            }
            else if (dentroIf && hijo.getTipo().equals("ESTATUTO")) {
                List<SentenciaNode> sentencias = new ArrayList<>();
                convertirEstatutos(hijo, sentencias);
                bloqueIf = new BloqueNode(sentencias, linea, 1);
                dentroIf = false;
            }
            else if (hijo.getTipo().equals("ELSE")) {
                bloqueElse = convertirElse(hijo);
            }
        }
        
        if (condicion == null) {
            condicion = new LiteralNode(true, "boolean", linea, 1);
        }
        if (bloqueIf == null) {
            bloqueIf = new BloqueNode(new ArrayList<>(), linea, 1);
        }
        
        return new IfNode(condicion, bloqueIf, bloqueElse, linea, 1);
    }
    
    /**
     * Convierte el bloque else
     */
    private BloqueNode convertirElse(NodoParseTree nodo) {
        List<SentenciaNode> sentencias = new ArrayList<>();
        
        for (NodoParseTree hijo : nodo.getHijos()) {
            if (hijo.getTipo().equals("ESTATUTO")) {
                convertirEstatutos(hijo, sentencias);
            }
        }
        
        return new BloqueNode(sentencias, 1, 1);
    }
    
    // ========================================================================
    // CONVERTIR WHILE
    // ========================================================================
    
    /**
     * Convierte un while
     */
    private WhileNode convertirWhile(NodoParseTree nodo) {
        List<NodoParseTree> hijos = nodo.getHijos();
        
        ExpresionNode condicion = null;
        BloqueNode cuerpo = null;
        int linea = 1;
        boolean dentroCondicion = false;
        
        for (NodoParseTree hijo : hijos) {
            if (hijo.getTipo().equals("while")) {
                linea = hijo.getLinea();
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals("(")) {
                dentroCondicion = true;
            }
            else if (hijo.getLexema() != null && hijo.getLexema().equals(")")) {
                dentroCondicion = false;
            }
            else if (dentroCondicion && hijo.getTipo().equals("CALCULO")) {
                condicion = convertirCalculo(hijo);
            }
            else if (hijo.getTipo().equals("ESTATUTO")) {
                List<SentenciaNode> sentencias = new ArrayList<>();
                convertirEstatutos(hijo, sentencias);
                cuerpo = new BloqueNode(sentencias, linea, 1);
            }
        }
        
        if (condicion == null) {
            condicion = new LiteralNode(true, "boolean", linea, 1);
        }
        if (cuerpo == null) {
            cuerpo = new BloqueNode(new ArrayList<>(), linea, 1);
        }
        
        return new WhileNode(condicion, cuerpo, linea, 1);
    }
    
    // ========================================================================
    // CONVERTIR PRINT
    // ========================================================================
    
    /**
     * Convierte un print
     */
    private PrintNode convertirPrint(NodoParseTree nodo) {
        ExpresionNode expresion = null;
        int linea = 1;
        
        for (NodoParseTree hijo : nodo.getHijos()) {
            if (hijo.getTipo().equals("print")) {
                linea = hijo.getLinea();
            }
            else if (hijo.getTipo().equals("ID")) {
                expresion = new IdentificadorNode(hijo.getLexema(), hijo.getLinea(), 1);
            }
            else if (hijo.getTipo().equals("NUM") || hijo.getTipo().equals("CADENA")) {
                expresion = convertirLiteral(hijo);
            }
            else if (hijo.getTipo().equals("CALCULO")) {
                expresion = convertirCalculo(hijo);
            }
        }
        
        return new PrintNode(expresion, linea, 1);
    }
    
    // ========================================================================
    // CONVERTIR EXPRESIONES (CALCULO)
    // ========================================================================
    
    /**
     * Convierte un nodo CALCULO a una ExpresionNode
     * Ejemplo: (f + i) * a
     */
    private ExpresionNode convertirCalculo(NodoParseTree nodo) {
        List<NodoParseTree> hijos = nodo.getHijos();
        if (hijos.isEmpty()) {
            return new LiteralNode(0, "int", 1, 1);
        }
        
        // Aplanar la estructura eliminando paréntesis y CALCULOs anidados innecesarios
        List<NodoParseTree> elementos = aplanarCalculo(nodo);
        
        // Si solo hay un elemento, convertirlo directamente
        if (elementos.size() == 1) {
            return convertirNodoAExpresion(elementos.get(0));
        }
        
        // Construir expresión binaria con precedencia correcta
        ExpresionNode resultado = construirExpresionBinaria(elementos);
        
        return resultado != null ? resultado : new LiteralNode(0, "int", 1, 1);
    }
    
    /**
     * Aplana la estructura de CALCULO eliminando paréntesis y nodos intermedios
     */
    private List<NodoParseTree> aplanarCalculo(NodoParseTree nodo) {
        List<NodoParseTree> resultado = new ArrayList<>();
        int nivelParentesis = 0;
        List<NodoParseTree> dentroParentesis = new ArrayList<>();
        
        for (NodoParseTree hijo : nodo.getHijos()) {
            String lexema = hijo.getLexema();
            
            if (lexema != null && lexema.equals("(")) {
                nivelParentesis++;
                if (nivelParentesis == 1) {
                    dentroParentesis.clear();
                } else {
                    dentroParentesis.add(hijo);
                }
            } else if (lexema != null && lexema.equals(")")) {
                nivelParentesis--;
                if (nivelParentesis == 0 && !dentroParentesis.isEmpty()) {
                    // Crear un nodo CALCULO con el contenido de los paréntesis
                    NodoParseTree calculoParentesis = new NodoParseTree("CALCULO");
                    for (NodoParseTree n : dentroParentesis) {
                        calculoParentesis.agregarHijo(n);
                    }
                    resultado.add(calculoParentesis);
                    dentroParentesis.clear();
                } else if (nivelParentesis > 0) {
                    dentroParentesis.add(hijo);
                }
            } else {
                if (nivelParentesis > 0) {
                    dentroParentesis.add(hijo);
                } else {
                    // Expandir CALCULOs anidados
                    if (hijo.getTipo().equals("CALCULO")) {
                        List<NodoParseTree> subCalculo = aplanarCalculo(hijo);
                        resultado.addAll(subCalculo);
                    } else {
                        resultado.add(hijo);
                    }
                }
            }
        }
        
        return resultado;
    }
    
    /**
     * Construye una expresión binaria respetando precedencia de operadores
     */
    private ExpresionNode construirExpresionBinaria(List<NodoParseTree> nodos) {
        if (nodos.isEmpty()) return null;
        
        if (nodos.size() == 1) {
            return convertirNodoAExpresion(nodos.get(0));
        }
        
        // Buscar operador con menor prioridad (de derecha a izquierda)
        int posOperador = buscarOperadorMenorPrioridad(nodos);
        
        if (posOperador == -1) {
            // No hay operador, procesar primer elemento
            return convertirNodoAExpresion(nodos.get(0));
        }
        
        // Dividir en izquierda y derecha
        List<NodoParseTree> izq = nodos.subList(0, posOperador);
        List<NodoParseTree> der = nodos.subList(posOperador + 1, nodos.size());
        
        ExpresionNode expIzq = construirExpresionBinaria(izq);
        ExpresionNode expDer = construirExpresionBinaria(der);
        
        String operador = nodos.get(posOperador).getLexema();
        int linea = nodos.get(posOperador).getLinea();
        
        return new ExpresionBinariaNode(expIzq, operador, expDer, linea, 1);
    }
    
    /**
     * Busca el operador con menor prioridad en la lista
     * Precedencia: comparadores < +,- < *,/
     */
    private int buscarOperadorMenorPrioridad(List<NodoParseTree> nodos) {
        int nivelParentesis = 0;
        int posComparador = -1;
        int posSumaResta = -1;
        int posMulDiv = -1;
        
        for (int i = 0; i < nodos.size(); i++) {
            NodoParseTree nodo = nodos.get(i);
            
            // Contar paréntesis
            if (nodo.getLexema() != null) {
                if (nodo.getLexema().equals("(")) nivelParentesis++;
                if (nodo.getLexema().equals(")")) nivelParentesis--;
            }
            
            // Solo buscar operadores fuera de paréntesis
            if (nivelParentesis == 0) {
                if (nodo.getTipo().equals("COMPARADOR")) {
                    posComparador = i;
                }
                else if (nodo.getTipo().equals("OPERADOR")) {
                    String op = nodo.getLexema();
                    if (op.equals("+") || op.equals("-")) {
                        posSumaResta = i;
                    }
                    else if (op.equals("*") || op.equals("/")) {
                        posMulDiv = i;
                    }
                }
            }
        }
        
        // Retornar operador con menor prioridad
        if (posComparador != -1) return posComparador;
        if (posSumaResta != -1) return posSumaResta;
        if (posMulDiv != -1) return posMulDiv;
        
        return -1;
    }
    
    /**
     * Convierte un nodo simple a una ExpresionNode
     */
    private ExpresionNode convertirNodoAExpresion(NodoParseTree nodo) {
        if (nodo.getTipo().equals("CALCULO")) {
            return convertirCalculo(nodo);
        }
        else if (nodo.getTipo().equals("ID")) {
            return new IdentificadorNode(nodo.getLexema(), nodo.getLinea(), 1);
        }
        else if (nodo.getTipo().equals("NUM") || nodo.getTipo().equals("CADENA")) {
            return convertirLiteral(nodo);
        }
        
        return new LiteralNode(0, "int", 1, 1);
    }
    
    /**
     * Convierte un literal (número o cadena)
     */
    private ExpresionNode convertirLiteral(NodoParseTree nodo) {
        if (nodo.getTipo().equals("NUM")) {
            String valor = nodo.getLexema();
            if (valor.contains(".")) {
                return new LiteralNode(Double.parseDouble(valor), "float", nodo.getLinea(), 1);
            } else {
                return new LiteralNode(Integer.parseInt(valor), "int", nodo.getLinea(), 1);
            }
        }
        else if (nodo.getTipo().equals("CADENA")) {
            // Eliminar comillas
            String valor = nodo.getLexema().replace("\"", "");
            return new LiteralNode(valor, "String", nodo.getLinea(), 1);
        }
        
        return new LiteralNode(0, "int", 1, 1);
    }
}
