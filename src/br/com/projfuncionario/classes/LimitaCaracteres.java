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
package br.com.projfuncionario.classes;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 *
 * @author Luciano Albuquerque Lima
 */
public class LimitaCaracteres extends PlainDocument {

    public enum TipoEntrada {
        NUMEROINTEIRO, NUMERODECIMAL, NOME, EMAIL, DATA;
    }

    private int qtdCaracteres;
    private TipoEntrada tbEntrada;

    public LimitaCaracteres(int qtdCaracteres, TipoEntrada tbEntrada) {
        this.qtdCaracteres = qtdCaracteres;
        this.tbEntrada = tbEntrada;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null || getLength() == qtdCaracteres) {
            return;
        }

        int totalCarac = getLength() + str.length();

        String regex = "";

        switch (tbEntrada) {
            case NUMEROINTEIRO:
                regex = "[^0-9]";
                break;
            case NUMERODECIMAL:
                regex = "[^0-9,]";
                break;
            case NOME:
                regex = "[^\\p{IsLatin} ]";
                break;
            case EMAIL:
                regex = "[^\\p{IsLatin}@.\\-_][^0-9]";
                break;
            case DATA:
                regex = "[^0-9/]";
                break;
        }
        //Fazendo a substituição 
        str = str.replaceAll(regex, "");

        if (totalCarac <= qtdCaracteres) {
            super.insertString(offs, str, a); //To change body of generated methods, choose Tools | Templates.
        } else {
            String nova = str.substring(0, qtdCaracteres);
             super.insertString(offs, nova, a);
        }

    }

}
