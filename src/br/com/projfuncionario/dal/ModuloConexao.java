/*
 * The MIT License
 *
 * Copyright 2024 Luciano Albuquerque Lima.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.projfuncionario.dal;
import java.sql.*;

/**
 *Conexão com banco de dados
 * @author Luciano Albuquerque Lima
 * @version 1.1
 */
public class ModuloConexao {
    
     public static Connection conector(){
        
         Connection conexao = null;
        
        //a linha abaixo chama o drive que foi importado para a biblioteca 
        String driver = "com.mysql.cj.jdbc.Driver";
        //Armazenando informações referente ao banco
         //Banco teste
         String url = "jdbc:mysql://127.0.0.1:3306/dbempresa?characterEncoding=utf-8";
         
         
        //Banco oficial e limpo 
        //String url = "jdbc:mysql://127.0.0.1:3306/asmdb?characterEncoding=utf-8";
          String user = "dba";
        
        //Senha do banco da minha maquina 
         String password = "Sist@123456";
        
        //Acesso Banco ASM
       //String password = "LPSist@!2517";
        
        //Estabelecendo conexao com o banco
        try {
            Class.forName(driver);
            conexao = DriverManager.getConnection(url, user, password);
            return conexao;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
}
