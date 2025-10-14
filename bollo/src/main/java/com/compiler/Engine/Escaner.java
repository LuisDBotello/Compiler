package com.compiler.Engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.richtext.CodeArea;

public class Escaner {
    private final String[] PR = {"if", "print", "inputInt", "inputFloat", "inputString", "else"};
    private final String[] TIPO = {"int", "float", "String"};
    private final String FOR = "for"; //22 (2025-02-10)
    private final String INC = "++", DEC = "--"; //INC 23, DEC 24
    
    private int lineaActual = 1;
    private boolean hasError = false;    
    private String TokensString;
    private String CodigoFuente;
    public ArrayList<Integer> tokens = new ArrayList<>();
    public ArrayList<String> lexemas = new ArrayList<>(); // NUEVO: Lista de lexemas

    public Escaner(String codigoFuente) {
        this.CodigoFuente = codigoFuente;
        this.TokensString = "";
    }

    public String Scan() {
        // StringBuilder local para evitar acumulación
        StringBuilder Scanned = new StringBuilder();
        
        // Reiniciar estado
        this.lineaActual = 1;
        this.hasError = false;
        this.tokens.clear();
        this.lexemas.clear(); // NUEVO: Limpiar lexemas
        
        char[] chars = this.CodigoFuente.toCharArray();
        StringBuilder Token = new StringBuilder();
        int i = 0;

        while (i < chars.length) {
            char c = chars[i];
    
            if (Character.isWhitespace(c)) {
                if (c == '\n' || c == '\r') { 
                    this.lineaActual++;
                }
                i++;
                continue;
            }
            
            if (Character.isLetter(c)) {
                Token.setLength(0);
                while (i < chars.length && (Character.isLetter(chars[i]) || Character.isDigit(chars[i]))) {
                    Token.append(chars[i]);
                    i++;
                }
                String palabra = Token.toString();
    
                boolean esReservada = false;
                for (int k=0; k< PR.length; k++) {
                    if (palabra.equals(PR[k])) {
                        esReservada = true;
                        Scanned.append(palabra + "\n");
                        tokens.add(k);
                        lexemas.add(palabra); // NUEVO: Guardar lexema
                        break;
                    }
                }
                if (palabra.equals(FOR)){
                    esReservada = true;
                    Scanned.append(palabra + "\n");
                    tokens.add(22);
                    lexemas.add(palabra); // NUEVO: Guardar lexema
                }
                if (esReservada) continue;
    
                boolean esTipo = false;
                for (int k = 0; k < TIPO.length; k++) {
                    if (palabra.equals(TIPO[k])) {
                        esTipo = true;
                        Scanned.append(palabra + "\n");
                        tokens.add(6+k);
                        lexemas.add(palabra); // NUEVO: Guardar lexema
                        break;
                    }
                }
                if (esTipo) continue;
    
                Scanned.append(palabra + "\n");
                tokens.add(9);
                lexemas.add(palabra); // NUEVO: Guardar lexema (identificador)
                continue;
            }
    
            if (Character.isDigit(c)) {
                Token.setLength(0);
                boolean esFloat = false;
            
                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    if (chars[i] == '.') {
                        if (esFloat) {
                            Scanned.append("ERROR: Número inválido (" + Token + ".)\n");
                            i++;
                            break;
                        }
                        esFloat = true;
                    }
                    Token.append(chars[i]);
                    i++;
                }
            
                if (Token.toString().endsWith(".")) {
                    Scanned.append("ERROR: Número inválido (" + Token + ")\n");
                } else {
                    String numero = Token.toString();
                    if (esFloat) {
                        tokens.add(10);
                        lexemas.add(numero); // NUEVO: Guardar lexema
                        Scanned.append(numero + "\n");
                    } else {
                        tokens.add(11);
                        lexemas.add(numero); // NUEVO: Guardar lexema
                        Scanned.append(numero + "\n");
                    }
                }
                continue;
            }

            if (c == '=') {
                Token.setLength(0); 
                Token.append(c); 
                i++; 
                if (i < chars.length && chars[i] == '=') { 
                    Token.append('='); 
                    tokens.add(12); 
                    lexemas.add("=="); // NUEVO: Guardar lexema
                    Scanned.append(Token + "\n");
                    i++; 
                } else {
                    tokens.add(13); 
                    lexemas.add("="); // NUEVO: Guardar lexema
                    Scanned.append("=\n");
                }
                continue;
            }
            
            if (c == '!' || c == '<' || c == '>') {
                Token.setLength(0); 
                Token.append(c); 
                i++;
                if (i < chars.length && chars[i] == '=') { 
                    Token.append('='); 
                    i++; 
                }
                String comparador = Token.toString();
                tokens.add(12); 
                lexemas.add(comparador); // NUEVO: Guardar lexema
                Scanned.append(comparador + "\n");
                continue;
            }
            
            if (c == '+' && i + 1 < chars.length && chars[i+1] == '+') {
                tokens.add(23); 
                lexemas.add("++"); // NUEVO: Guardar lexema
                i+=2;
                Scanned.append(INC + "\n");
                continue;
            }
            
            if (c == '-' && i + 1 < chars.length && chars[i+1] == '-') {
                tokens.add(24); 
                lexemas.add("--"); // NUEVO: Guardar lexema
                i+=2;
                Scanned.append(DEC + "\n");
                continue;
            }            
            
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                tokens.add(14);
                lexemas.add(String.valueOf(c)); // NUEVO: Guardar lexema
                Scanned.append(c + "\n");
                i++;
                continue;
            }

            if (c == '$' && i + 1 < chars.length && chars[i + 1] == '$') {
                tokens.add(15);
                lexemas.add("$$"); // NUEVO: Guardar lexema
                Scanned.append("$$\n");
                i += 2;
                continue;
            }
    
            if (c == '(') {
                tokens.add(16);
                lexemas.add("("); // NUEVO: Guardar lexema
                Scanned.append(c + "\n");
                i++;
                continue;
            }
            
            if (c == ')') {
                tokens.add(17);
                lexemas.add(")"); // NUEVO: Guardar lexema
                Scanned.append(c + "\n");
                i++;
                continue;
            }
    
            if (c == '{') {
                tokens.add(18);
                lexemas.add("{"); // NUEVO: Guardar lexema
                Scanned.append(c + "\n");
                i++;
                continue;
            }
            
            if (c == '}') {
                tokens.add(19);
                lexemas.add("}"); // NUEVO: Guardar lexema
                Scanned.append(c + "\n");
                i++;
                continue;
            }
            
            if (c == ';') {
                tokens.add(20);
                lexemas.add(";"); // NUEVO: Guardar lexema
                Scanned.append(";\n");
                i++;
                continue;
            }
    
            if (c == '"') {
                Token.setLength(0);
                Token.append(c);
                i++;
                while (i < chars.length && chars[i] != '"') {
                    Token.append(chars[i]);
                    i++;
                }
                if (i < chars.length && chars[i] == '"') {
                    Token.append(chars[i]);
                    String cadena = Token.toString();
                    tokens.add(21);
                    lexemas.add(cadena); // NUEVO: Guardar lexema
                    Scanned.append(cadena + "\n");
                    i++;
                }
                continue;
            }
            
            // Token inválido
            this.hasError = true;
            tokens.add(-1);
            lexemas.add(String.valueOf(c)); // NUEVO: Guardar el carácter inválido
            Scanned.append("ERROR: Token inválido ('" + c + "') en línea " + this.lineaActual + "\n");
            i++;
        }
        
        return Scanned.toString();
    }
    
    public void WriteRun(CodeArea codeAreaLexico) {
        String scannedResult = this.Scan();
        formatLexerOutput(scannedResult);
        codeAreaLexico.replaceText(this.TokensString);
    }

    private void formatLexerOutput(String scanned) {
        String[] lines = scanned.split("\n");
        StringBuilder formattedTokens = new StringBuilder();
        
        // Mapa para almacenar errores únicos: clave = "carácter_línea"
        Map<String, String> uniqueErrors = new LinkedHashMap<>();
        int tokenCount = 0;

        for (String line : lines) {
            if (line.startsWith("ERROR:")) {
                // Extraer el carácter inválido y número de línea
                Pattern pattern = Pattern.compile("Token inválido \\('(.)'\\) en línea (\\d+)");
                Matcher matcher = pattern.matcher(line);
                
                if (matcher.find()) {
                    String invalidChar = matcher.group(1);
                    String lineNumber = matcher.group(2);
                    
                    // Crear clave única: carácter + línea
                    String key = invalidChar + "_" + lineNumber;
                    
                    // Solo agregar si no existe (evita duplicados)
                    if (!uniqueErrors.containsKey(key)) {
                        uniqueErrors.put(key, String.format("Token inválido '%s' en línea %s", 
                            invalidChar, lineNumber));
                    }
                }
            } else if (!line.trim().isEmpty()) {
                // Línea válida (token reconocido)
                formattedTokens.append("TOKEN: ").append(line).append("\n");
                tokenCount++;
            }
        }

        // Decidir el contenido final de TokensString
        if (!uniqueErrors.isEmpty()) {
            StringBuilder errorOutput = new StringBuilder();
            errorOutput.append(String.format(
                "ERRORES LÉXICOS ENCONTRADOS: %d\n" +
                "═══════════════════════════════════\n\n",
                uniqueErrors.size()
            ));
            
            // Mostrar errores únicos
            for (String errorMsg : uniqueErrors.values()) {
                errorOutput.append("  • ").append(errorMsg).append("\n");
            }
            
            errorOutput.append(String.format(
                "\n═══════════════════════════════════\n" +
                "✓ Tokens válidos: %d\n" +
                "✗ Errores únicos: %d",
                tokenCount, uniqueErrors.size()
            ));
            
            this.TokensString = errorOutput.toString();
        } else {
            this.TokensString = String.format(
                "✓ ANÁLISIS LÉXICO COMPLETADO\n" +
                "═══════════════════════════════════\n" +
                "Total de tokens: %d\n\n%s",
                tokenCount, formattedTokens.toString()
            );
        }
    }

    public String getTokensString() {
        return TokensString;
    }

    public void setTokensString(String tokensString) {
        TokensString = tokensString;
    }
    
    public boolean gethasError() {
        return this.hasError;
    }
    
    public void sethasError(boolean error){
        this.hasError = error;
    }
    
    public ArrayList<Integer> getTokens() {
        return tokens;
    }
    
    public ArrayList<String> getLexemas() {
        return lexemas;
    }
}