package com.compiler.Engine;

import java.util.ArrayList;
import javax.swing.JTextArea;

public class Parser {

    private final int IF = 0, PRINT = 1, INPUTINT = 2, INPUTFLOAT = 3, INPUTSTRING = 4, ELSE = 5, TYPEINT = 6, 
        TYPEFLOAT = 7, TYPESTRING = 8, ID = 9, FLOAT = 10, NUM = 11, COMP = 12, ASIG = 13, OPER = 14, 
        LIM = 15, PAROPEN = 16, PARCLOSE = 17, LLAVEOPEN = 18, LLAVECLOSE = 19, EOL = 20, CADENA = 21, FOR = 22,
        INC = 23, DEC = 24;
    public String[] Words = {"if", "print", "inputInt", "inputFloat", "inputString", "else", "\"Typeint\"", 
    "\"Typefloat\"", "\"TypeString\"", "ID", "FLOAT", "NUM", "comparator", "=", "operator", "\"$$\"", "(", 
    ")", "{", "}", ";", "CADENA", "for", "++", "--"};
    int i = 0; // Apuntador de token
    int Tok;
    public boolean ParserError = false;
    byte llavebloque = 0; // Indica cuántas llaves se han abierto
    ArrayList<String> Calculos = new ArrayList<>();

    public Parser(Escaner escaneado, JTextArea jta) {
        Escaneado = escaneado;
    }

    public boolean P() {
        System.out.println("ESCANER:");
        System.out.println("Tokens: " + Escaneado.tokens);
        System.out.println("\nPARSER:");
        this.Tok = (int) Escaneado.tokens.get(i);
        eat(LIM); 
        DECLARACION(); 
        ESTATUTO(); 
        return !this.ParserError; // Devuelve true si no hay errores, false si hay errores
    }    
       
    public void DECLARACION() {
        switch (this.Tok) {
            case TYPEINT: eat(TYPEINT); eat(ID); 
                switch (this.Tok) {
                    case ASIG: 
                        eat(ASIG); 
                        switch (this.Tok) {
                            case NUM: eat(NUM); eat(EOL); DECLARACION(); break;
                            case INPUTINT: eat(INPUTINT); eat(EOL); DECLARACION(); break;
                        } break;
                    case EOL: eat(EOL); DECLARACION(); break;
                } break;
            case TYPEFLOAT: eat(TYPEFLOAT); eat(ID); 
                switch (this.Tok) {
                    case ASIG: eat(ASIG); 
                        switch (this.Tok) {
                            case FLOAT: eat(FLOAT); eat(EOL); DECLARACION(); break;
                            case INPUTFLOAT: eat(INPUTFLOAT); eat(EOL); DECLARACION(); break;
                        } break;
                    case EOL: eat(EOL); DECLARACION(); break;
                } break;
            case TYPESTRING: eat(TYPESTRING); eat(ID); 
                switch (this.Tok) {
                    case ASIG: eat(ASIG); 
                        switch (this.Tok) {
                            case CADENA: eat(CADENA); eat(EOL); DECLARACION(); break;
                            case INPUTSTRING: eat(INPUTSTRING); eat(EOL); DECLARACION(); break;
                            default:
                                Error();
                                break;
                        } break;
                    case EOL: eat(EOL); DECLARACION(); break;
                } break;
            default: 
                ESTATUTO();
                break;
        }
    }

    public void ESTATUTO() {
        switch (this.Tok) {
            case ID:
                eat(ID); 
                eat(ASIG); 
                CALCULO(); 
                eat(EOL); 
                ESTATUTO(); 
                break;
            case IF:
                eat(IF); 
                eat(PAROPEN);
                switch (this.Tok) {
                    case ID:
                    case FLOAT:
                    case NUM:
                        eat(this.Tok); 
                        eat(COMP);
                        switch (this.Tok) {
                            case ID:
                            case FLOAT:
                            case NUM:
                                eat(this.Tok);  
                                eat(PARCLOSE);
                                System.out.println("ME aaaa");
                                eat(LLAVEOPEN);
                                BLOQUE();
                                handleElse();
                                ESTATUTO();
                                break;
                            case PAROPEN:
                                eat(PAROPEN); 
                                CALCULO(); 
                                eat(PARCLOSE);
                                eat(LLAVEOPEN);
                                BLOQUE();
                                handleElse();
                                ESTATUTO();
                                break;
                            default:
                                break;
                        } 
                        break;
                    case PAROPEN:
                        eat(PAROPEN);
                        CALCULO();
                        eat(PARCLOSE);
                        eat(COMP);
                        switch (this.Tok) {
                            case ID:
                            case FLOAT:
                            case NUM:
                                eat(this.Tok);  
                                eat(PARCLOSE);
                                eat(LLAVEOPEN);
                                BLOQUE();
                                handleElse();
                                ESTATUTO();
                                break;
                            case PAROPEN:
                                eat(PAROPEN); 
                                CALCULO(); //200+a)
                                eat(PARCLOSE);
                                eat(PARCLOSE);
                                eat(LLAVEOPEN);
                                BLOQUE();
                                handleElse();
                                ESTATUTO();
                                break;
                            default:
                                break;
                        } 
                        break;
                    default:
                        break;
                }       
                break;
            case PRINT:
                eat(PRINT); 
                eat(PAROPEN);
                switch (this.Tok) {
                    case ID:
                        eat(ID); 
                        eat(PARCLOSE); 
                        eat(EOL); 
                        ESTATUTO();
                        break;
                    case CADENA:
                        eat(CADENA); 
                        eat(PARCLOSE); 
                        eat(EOL); 
                        ESTATUTO();
                        break;
                    case PAROPEN:
                        CALCULO(); 
                        eat(PARCLOSE); 
                        eat(EOL); 
                        ESTATUTO();
                        break;
                    default:
                        break;
                }  
                break;
            case FOR:
                eat(FOR);
                eat(PAROPEN);
                if(this.Tok == TYPEINT) {
                    eat(TYPEINT); eat(ID);   //Ej. for(int i...)   
                } else if(this.Tok == ID)    //<-- Cuando for(i...) 'i' ya está declarada
                    eat(ID);                     

                eat(ASIG);
                switch (this.Tok) {
                    case PAROPEN:
                        eat(PAROPEN); CALCULO(); eat(PARCLOSE); //Ej. for(int i=0; (i-2)<10; i++){}
                        break;
                    case NUM:
                        eat(NUM);
                        break;
                    case ID:
                        eat(ID);
                        break;
                    default:
                        break;
                }
                eat(EOL); 
                switch (this.Tok) { //izquierda del comparador Ej. for(int i=0; i  <  [(a-i)]|10|id  ;)
                    case PAROPEN:
                        eat(PAROPEN); CALCULO(); eat(PARCLOSE); 
                        break;
                    case NUM:
                        eat(NUM);
                        break;
                    case ID:
                        eat(ID);
                        break;
                    default:
                        break;
                }
                eat(COMP); //Comparador que determina si el ciclo sigue
                switch (this.Tok) { //derecha del comparador Ej. for(int i=0; i <  [(a-i)]|10|id  ;)
                    case PAROPEN:
                        eat(PAROPEN); CALCULO(); eat(PARCLOSE); 
                        break;
                    case NUM:
                        eat(NUM);
                        break;
                    case ID:
                        eat(ID);
                        break;
                    default:
                        break;
                }
                eat(EOL); //Parte del step
                eat(ID);
                if(this.Tok == INC) eat(INC); //Ej. for(...i++)
                if(this.Tok == DEC) eat(DEC); //Ej. for(...i--)
                eat(PARCLOSE);
                eat(LLAVEOPEN);
                BLOQUE();
            
            case LLAVECLOSE:
                if (llavebloque == 0) {
                    break;
                } else {
                    llavebloque--;
                    eat(LLAVECLOSE);
                    return;
                }
            default:
                eat(LIM);
                break;    
        }
    }
    
    private void handleElse() {
        if (this.Tok == ELSE) {
            eat(ELSE);
            eat(LLAVEOPEN);
            BLOQUE();
        }
    }
    
    
    public void BLOQUE() {
        llavebloque++;
        DECLARACION();
    }

    public void CALCULO() {
        System.out.println("Entrando a CALCULO con token: " + this.Tok + " " + Words[this.Tok]);
        switch (this.Tok) {
            case ID:
            case FLOAT:
            case NUM:
                eat(this.Tok); 
                break;
            case PAROPEN:
                eat(PAROPEN); 
                CALCULO(); 
                eat(PARCLOSE); 
                if (this.Tok == OPER) {
                    eat(OPER);
                    if (this.Tok == PAROPEN) {
                        System.out.println("Entrando a CALCULO despues de parentesis");
                        eat(PAROPEN); 
                        CALCULO(); 
                        eat(PARCLOSE); 
                    } else if (this.Tok == ID || this.Tok == FLOAT || this.Tok == NUM) {
                        eat(this.Tok);
                    }
                }
                break;
            default:
                break;
        }
        while (this.Tok == OPER) {
            eat(OPER);
            switch (this.Tok) {
                case ID:
                case FLOAT:
                case NUM:
                    eat(this.Tok); 
                    break;
                case PAROPEN:
                    eat(PAROPEN); 
                    CALCULO();
                    eat(PARCLOSE); 
                    break;
                default:
                    return;
            }
        }
    }

    public void eat(int tok) {
        if (this.ParserError) return;
    
        if (i >= Escaneado.tokens.size() || i < 0) {
            return;
        }
        if (this.Tok == tok) {
            System.out.println("Token reconocido: " + this.Tok + " " + Words[this.Tok]);
            Avanzar();
        } else {
            Error();
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
        Escaneado.Scan(null);
    }
}