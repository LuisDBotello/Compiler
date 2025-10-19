package com.compiler.Engine.ast;

public interface ASTVisitor<T> {
    T visit(ProgramaNode node);
    T visit(DeclaracionVariableNode node);
    T visit(DeclaracionFuncionNode node);
    T visit(ParametroNode node);
    T visit(BloqueNode node);
    T visit(AsignacionNode node);
    T visit(IfNode node);
    T visit(WhileNode node);
    T visit(ForNode node);
    T visit(PrintNode node);
    T visit(ReturnNode node);
    T visit(ExpresionBinariaNode node);
    T visit(ExpresionUnariaNode node);
    T visit(LiteralNode node);
    T visit(IdentificadorNode node);
    T visit(LlamadaFuncionNode node);
}
