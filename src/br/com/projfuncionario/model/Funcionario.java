/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projfuncionario.model;

import java.math.BigDecimal;

/**
 *
 * @author Luciano & Paty
 */
public class Funcionario extends Pessoa {

    private BigDecimal salario;
    private String funcao;

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSalarioFormatado() {
        if (salario == null) {
            return "0,00";
        }
        return String.format("%,.2f", salario).replace(".", ",");
    }
}
