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
    String TokensString;
    String CodigoFuente;
    ArrayList<Integer> tokens = new ArrayList<>();

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
                        break;
                    }
                }
                if (palabra.equals(FOR)){
                    esReservada = true;
                    Scanned.append(palabra + "\n");
                    tokens.add(22);
                }
                if (esReservada) continue;
    
                boolean esTipo = false;
                for (int k = 0; k < TIPO.length; k++) {
                    if (palabra.equals(TIPO[k])) {
                        esTipo = true;
                        Scanned.append(palabra + "\n");
                        tokens.add(6+k);
                        break;
                    }
                }
                if (esTipo) continue;
    
                Scanned.append(palabra + "\n");
                tokens.add(9);
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
                    if (esFloat) {
                        tokens.add(10);
                        Scanned.append(Token + "\n");
                    } else {
                        tokens.add(11);
                        Scanned.append(Token + "\n");
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
                    Scanned.append(Token + "\n");
                    i++; 
                } else {
                    tokens.add(13); 
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
                tokens.add(12); 
                Scanned.append(Token + "\n");
                continue;
            }
            
            if (c == '+' && i + 1 < chars.length && chars[i+1] == '+') {
                tokens.add(23); 
                i+=2;
                Scanned.append(INC + "\n");
                continue;
            }
            
            if (c == '-' && i + 1 < chars.length && chars[i+1] == '-') {
                tokens.add(24); 
                i+=2;
                Scanned.append(DEC + "\n");
                continue;
            }            
            
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                i++;
                tokens.add(14);
                Scanned.append(c + "\n");
                continue;
            }

            if (c == '$' && i + 1 < chars.length && chars[i + 1] == '$') {
                i += 2;
                tokens.add(15);
                Scanned.append("$$\n");
                continue;
            }
    
            if (c == '(') {
                i++;
                tokens.add(16);
                Scanned.append(c + "\n");
                continue;
            }
            
            if (c == ')') {
                i++;
                tokens.add(17);
                Scanned.append(c + "\n");
                continue;
            }
    
            if (c == '{') {
                i++;
                tokens.add(18);
                Scanned.append(c + "\n");
                continue;
            }
            
            if (c == '}') {
                i++;
                tokens.add(19);
                Scanned.append(c + "\n");
                continue;
            }
            
            if (c == ';') {
                i++;
                tokens.add(20);
                Scanned.append(";\n");
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
                    i++;
                    tokens.add(21);
                    Scanned.append(Token + "\n");
                }
                continue;
            }
            
            // Token inválido
            this.hasError = true;
            tokens.add(-1);                
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
}