package com.compiler.Engine;

import java.util.ArrayList;
import org.fxmisc.richtext.CodeArea;

public class Parser {

    private final int IF = 0, PRINT = 1, INPUTINT = 2, INPUTFLOAT = 3, INPUTSTRING = 4, ELSE = 5, TYPEINT = 6, 
        TYPEFLOAT = 7, TYPESTRING = 8, ID = 9, FLOAT = 10, NUM = 11, COMP = 12, ASIG = 13, OPER = 14, 
        LIM = 15, PAROPEN = 16, PARCLOSE = 17, LLAVEOPEN = 18, LLAVECLOSE = 19, EOL = 20, CADENA = 21, FOR = 22,
        INC = 23, DEC = 24;

    public String[] Words = {"if", "print", "inputInt", "inputFloat", "inputString", "else", "\"Typeint\"", 
    "\"Typefloat\"", "\"TypeString\"", "ID", "FLOAT", "NUM", "comparator", "=", "operator", "\"$$\"", "(", 
    ")", "{", "}", ";", "CADENA", "for", "++", "--"};
    private int i = 0;
    private int Tok;
    public boolean ParserError = false;
    private byte llavebloque = 0;
    ArrayList<String> Calculos = new ArrayList<>();
    private Escaner Escaneado;
    
    // Árbol sintáctico
    private NodoArbol arbolSintactico;

    public Parser(Escaner escaneado, CodeArea codeAreaParser) {
        Escaneado = escaneado;
    }

    public boolean P() {

        this.Tok = (int) Escaneado.tokens.get(i);
        
        // Crear nodo raíz del árbol
        arbolSintactico = new NodoArbol("PROGRAMA");
        
        NodoArbol nodoLim = eat(LIM);
        if (nodoLim != null) arbolSintactico.agregarHijo(nodoLim);
        
        NodoArbol nodoDeclaracion = DECLARACION();
        if (nodoDeclaracion != null) arbolSintactico.agregarHijo(nodoDeclaracion);
        
        NodoArbol nodoEstatuto = ESTATUTO();
        if (nodoEstatuto != null) arbolSintactico.agregarHijo(nodoEstatuto);
        
        return !this.ParserError;
    }    
    
    public NodoArbol getArbolSintactico() {
        return arbolSintactico;
    }
       
    public NodoArbol DECLARACION() {
        if (ParserError) return null;

        NodoArbol nodo = new NodoArbol("DECLARACION");

        switch (this.Tok) {
            case TYPEINT: 
                nodo.agregarHijo(eat(TYPEINT)); 
                nodo.agregarHijo(eat(ID)); 
                switch (this.Tok) {
                    case ASIG: 
                        nodo.agregarHijo(eat(ASIG)); 
                        switch (this.Tok) {
                            case NUM: 
                                nodo.agregarHijo(eat(NUM)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                            case INPUTINT: 
                                nodo.agregarHijo(eat(INPUTINT)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                        } break;
                    case EOL: 
                        nodo.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(DECLARACION()); 
                        break;
                } break;
            case TYPEFLOAT: 
                nodo.agregarHijo(eat(TYPEFLOAT)); 
                nodo.agregarHijo(eat(ID)); 
                switch (this.Tok) {
                    case ASIG: 
                        nodo.agregarHijo(eat(ASIG)); 
                        switch (this.Tok) {
                            case FLOAT: 
                                nodo.agregarHijo(eat(FLOAT)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                            case INPUTFLOAT: 
                                nodo.agregarHijo(eat(INPUTFLOAT)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                        } break;
                    case EOL: 
                        nodo.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(DECLARACION()); 
                        break;
                } break;
            case TYPESTRING: 
                nodo.agregarHijo(eat(TYPESTRING)); 
                nodo.agregarHijo(eat(ID)); 
                switch (this.Tok) {
                    case ASIG: 
                        nodo.agregarHijo(eat(ASIG)); 
                        switch (this.Tok) {
                            case CADENA: 
                                nodo.agregarHijo(eat(CADENA)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                            case INPUTSTRING: 
                                nodo.agregarHijo(eat(INPUTSTRING)); 
                                nodo.agregarHijo(eat(EOL)); 
                                nodo.agregarHijo(DECLARACION()); 
                                break;
                            default:
                                Error();
                                break;
                        } break;
                    case EOL: 
                        nodo.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(DECLARACION()); 
                        break;
                } break;
            default: 
                return ESTATUTO();
        }
        return nodo;
    }

    public NodoArbol ESTATUTO() {
        NodoArbol nodo = new NodoArbol("ESTATUTO");
        
        switch (this.Tok) {
            case TYPEINT:
            case TYPEFLOAT: 
            case TYPESTRING:
                nodo.agregarHijo(DECLARACION()); 
                nodo.agregarHijo(ESTATUTO()); 
                break;
            case ID:
                nodo.agregarHijo(eat(ID)); 
                nodo.agregarHijo(eat(ASIG)); 
                nodo.agregarHijo(CALCULO()); 
                nodo.agregarHijo(eat(EOL)); 
                nodo.agregarHijo(ESTATUTO()); 
                break;
            case IF:
                NodoArbol nodoIf = new NodoArbol("IF");
                nodoIf.agregarHijo(eat(IF)); 
                nodoIf.agregarHijo(eat(PAROPEN));
                switch (this.Tok) {
                    case ID:
                    case FLOAT:
                    case NUM:
                        nodoIf.agregarHijo(eat(this.Tok)); 
                        nodoIf.agregarHijo(eat(COMP));
                        switch (this.Tok) {
                            case ID:
                            case FLOAT:
                            case NUM:
                                nodoIf.agregarHijo(eat(this.Tok));  
                                nodoIf.agregarHijo(eat(PARCLOSE));
                                System.out.println("ME aaaa");
                                nodoIf.agregarHijo(eat(LLAVEOPEN));
                                nodoIf.agregarHijo(BLOQUE());
                                nodoIf.agregarHijo(handleElse());
                                nodo.agregarHijo(nodoIf);
                                nodo.agregarHijo(ESTATUTO());
                                break;
                            case PAROPEN:
                                nodoIf.agregarHijo(eat(PAROPEN)); 
                                nodoIf.agregarHijo(CALCULO()); 
                                nodoIf.agregarHijo(eat(PARCLOSE));
                                nodoIf.agregarHijo(eat(LLAVEOPEN));
                                nodoIf.agregarHijo(BLOQUE());
                                nodoIf.agregarHijo(handleElse());
                                nodo.agregarHijo(nodoIf);
                                nodo.agregarHijo(ESTATUTO());
                                break;
                        } 
                        break;
                    case PAROPEN:
                        nodoIf.agregarHijo(eat(PAROPEN));
                        nodoIf.agregarHijo(CALCULO());
                        nodoIf.agregarHijo(eat(PARCLOSE));
                        nodoIf.agregarHijo(eat(COMP));
                        switch (this.Tok) {
                            case ID:
                            case FLOAT:
                            case NUM:
                                nodoIf.agregarHijo(eat(this.Tok));  
                                nodoIf.agregarHijo(eat(PARCLOSE));
                                nodoIf.agregarHijo(eat(LLAVEOPEN));
                                nodoIf.agregarHijo(BLOQUE());
                                nodoIf.agregarHijo(handleElse());
                                nodo.agregarHijo(nodoIf);
                                nodo.agregarHijo(ESTATUTO());
                                break;
                            case PAROPEN:
                                nodoIf.agregarHijo(eat(PAROPEN)); 
                                nodoIf.agregarHijo(CALCULO());
                                nodoIf.agregarHijo(eat(PARCLOSE));
                                nodoIf.agregarHijo(eat(PARCLOSE));
                                nodoIf.agregarHijo(eat(LLAVEOPEN));
                                nodoIf.agregarHijo(BLOQUE());
                                nodoIf.agregarHijo(handleElse());
                                nodo.agregarHijo(nodoIf);
                                nodo.agregarHijo(ESTATUTO());
                                break;
                        } 
                        break;
                }       
                break;
            case PRINT:
                NodoArbol nodoPrint = new NodoArbol("PRINT");
                nodoPrint.agregarHijo(eat(PRINT)); 
                nodoPrint.agregarHijo(eat(PAROPEN));
                switch (this.Tok) {
                    case ID:
                        nodoPrint.agregarHijo(eat(ID)); 
                        nodoPrint.agregarHijo(eat(PARCLOSE)); 
                        nodoPrint.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(nodoPrint);
                        nodo.agregarHijo(ESTATUTO());
                        break;
                    case CADENA:
                        nodoPrint.agregarHijo(eat(CADENA)); 
                        nodoPrint.agregarHijo(eat(PARCLOSE)); 
                        nodoPrint.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(nodoPrint);
                        nodo.agregarHijo(ESTATUTO());
                        break;
                    case PAROPEN:
                        nodoPrint.agregarHijo(CALCULO()); 
                        nodoPrint.agregarHijo(eat(PARCLOSE)); 
                        nodoPrint.agregarHijo(eat(EOL)); 
                        nodo.agregarHijo(nodoPrint);
                        nodo.agregarHijo(ESTATUTO());
                        break;
                }  
                break;
            case FOR:
                NodoArbol nodoFor = new NodoArbol("FOR");
                nodoFor.agregarHijo(eat(FOR));
                nodoFor.agregarHijo(eat(PAROPEN));
                if(this.Tok == TYPEINT) {
                    nodoFor.agregarHijo(eat(TYPEINT)); 
                    nodoFor.agregarHijo(eat(ID));   
                } else if(this.Tok == ID)    
                    nodoFor.agregarHijo(eat(ID));                     

                nodoFor.agregarHijo(eat(ASIG));
                switch (this.Tok) {
                    case PAROPEN:
                        nodoFor.agregarHijo(eat(PAROPEN)); 
                        nodoFor.agregarHijo(CALCULO()); 
                        nodoFor.agregarHijo(eat(PARCLOSE));
                        break;
                    case NUM:
                        nodoFor.agregarHijo(eat(NUM));
                        break;
                    case ID:
                        nodoFor.agregarHijo(eat(ID));
                        break;
                }
                nodoFor.agregarHijo(eat(EOL)); 
                switch (this.Tok) {
                    case PAROPEN:
                        nodoFor.agregarHijo(eat(PAROPEN)); 
                        nodoFor.agregarHijo(CALCULO()); 
                        nodoFor.agregarHijo(eat(PARCLOSE)); 
                        break;
                    case NUM:
                        nodoFor.agregarHijo(eat(NUM));
                        break;
                    case ID:
                        nodoFor.agregarHijo(eat(ID));
                        break;
                }
                nodoFor.agregarHijo(eat(COMP));
                switch (this.Tok) {
                    case PAROPEN:
                        nodoFor.agregarHijo(eat(PAROPEN)); 
                        nodoFor.agregarHijo(CALCULO()); 
                        nodoFor.agregarHijo(eat(PARCLOSE)); 
                        break;
                    case NUM:
                        nodoFor.agregarHijo(eat(NUM));
                        break;
                    case ID:
                        nodoFor.agregarHijo(eat(ID));
                        break;
                }
                nodoFor.agregarHijo(eat(EOL));
                nodoFor.agregarHijo(eat(ID));
                if(this.Tok == INC) nodoFor.agregarHijo(eat(INC));
                if(this.Tok == DEC) nodoFor.agregarHijo(eat(DEC));
                nodoFor.agregarHijo(eat(PARCLOSE));
                nodoFor.agregarHijo(eat(LLAVEOPEN));
                nodoFor.agregarHijo(BLOQUE());
                nodo.agregarHijo(nodoFor);
                break;
            
            case LLAVECLOSE:
                if (llavebloque == 0) {
                    break;
                } else {
                    llavebloque--;
                    nodo.agregarHijo(eat(LLAVECLOSE));
                    return nodo;
                }
            default:
                nodo.agregarHijo(eat(LIM));
                break;    
        }
        return nodo;
    }
    
    private NodoArbol handleElse() {
        if (this.Tok == ELSE) {
            NodoArbol nodoElse = new NodoArbol("ELSE");
            nodoElse.agregarHijo(eat(ELSE));
            nodoElse.agregarHijo(eat(LLAVEOPEN));
            nodoElse.agregarHijo(BLOQUE());
            return nodoElse;
        }
        return null;
    }
    
    
    public NodoArbol BLOQUE() {
        llavebloque++;
        NodoArbol nodo = new NodoArbol("BLOQUE");
        nodo.agregarHijo(DECLARACION());
        return nodo;
    }

    public NodoArbol CALCULO() {
        System.out.println("Entrando a CALCULO con token: " + this.Tok + " " + Words[this.Tok]);
        NodoArbol nodo = new NodoArbol("CALCULO");
        
        switch (this.Tok) {
            case ID:
            case FLOAT:
            case NUM:
                nodo.agregarHijo(eat(this.Tok)); 
                break;
            case PAROPEN:
                nodo.agregarHijo(eat(PAROPEN)); 
                nodo.agregarHijo(CALCULO()); 
                nodo.agregarHijo(eat(PARCLOSE)); 
                if (this.Tok == OPER) {
                    nodo.agregarHijo(eat(OPER));
                    if (this.Tok == PAROPEN) {
                        System.out.println("Entrando a CALCULO despues de parentesis");
                        nodo.agregarHijo(eat(PAROPEN)); 
                        nodo.agregarHijo(CALCULO()); 
                        nodo.agregarHijo(eat(PARCLOSE)); 
                    } else if (this.Tok == ID || this.Tok == FLOAT || this.Tok == NUM) {
                        nodo.agregarHijo(eat(this.Tok));
                    }
                }
                break;
        }
        while (this.Tok == OPER) {
            nodo.agregarHijo(eat(OPER));
            switch (this.Tok) {
                case ID:
                case FLOAT:
                case NUM:
                    nodo.agregarHijo(eat(this.Tok)); 
                    break;
                case PAROPEN:
                    nodo.agregarHijo(eat(PAROPEN)); 
                    nodo.agregarHijo(CALCULO());
                    nodo.agregarHijo(eat(PARCLOSE)); 
                    break;
                default:
                    return nodo;
            }
        }
        return nodo;
    }

    public NodoArbol eat(int tok) {
        if (this.ParserError) return null;
    
        if (i >= Escaneado.tokens.size() || i < 0) {
            return null;
        }
        if (this.Tok == tok) {
            String lexema = "";
            if (i < Escaneado.lexemas.size()) {
                lexema = Escaneado.lexemas.get(i);
            }
            System.out.println("Token reconocido: " + this.Tok + " " + Words[this.Tok]);
            NodoArbol nodo = new NodoArbol(Words[this.Tok], lexema);
            Avanzar();
            return nodo;
        } else {
            Error();
            return null;
        }
    }

    private void Avanzar() {
        i++;    
        if (i < Escaneado.tokens.size()) {
            this.Tok = (int) Escaneado.tokens.get(i);
        } else {
            System.out.println("Sin errores de Parser");
        }
    }
    
    private void Error() {
        setParserError(true);        
        System.out.println("Token inesperado: " + this.Tok + " " + Words[this.Tok]);
    }

    public boolean isParserError() {
        return ParserError;
    }

    public void setParserError(boolean parserError) {
        this.ParserError = parserError;
    }

    public ArrayList<String> getCalculos() {
        return Calculos;
    }

    public void setCalculos(ArrayList<String> calculos) {
        Calculos = calculos;
    }

    public void setCalcTok(int Tok) {
        Escaneado.Scan();
    }
}