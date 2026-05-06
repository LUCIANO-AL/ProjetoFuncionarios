/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.projfuncionario.telas;

import br.com.projfuncionario.dal.ModuloConexao;
import br.com.projfuncionario.model.Funcionario;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLSyntaxErrorException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author Luciano & Paty
 */
public class TelaFuncionarios extends javax.swing.JFrame {

    //Usando a variavel de conexao do DAL
    Connection conexao = null;
    //Criando variaveis especiais para conexao com o banco 
    //Prepared Stetament e ResultSet são Frameworks do pacote java.sql
    // e servem para preparar e executar as instruções SQL
    PreparedStatement pst = null;
    ResultSet rs = null;

    private MaskFormatter ValorSalario;
    private MaskFormatter DataNascimento;
    int dia;
    int mes;
    int ano;
    // Objeto Funcionario para manipular os dados
    private Funcionario funcionario = new Funcionario();

    /**
     * Creates new form TelaFuncionarios
     */
    public TelaFuncionarios() {
        initComponents();

        conexao = ModuloConexao.conector();

        try {
            ValorSalario = new MaskFormatter("#####");
            DataNascimento = new MaskFormatter("##/##/####");

        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        txtSalario.setFormatterFactory(new DefaultFormatterFactory(ValorSalario));
        txtDataNascimento.setFormatterFactory(new DefaultFormatterFactory(DataNascimento));

        DecimalFormat decimal = new DecimalFormat("###,###,###.00");
        NumberFormatter numFormatter = new NumberFormatter(decimal);
        numFormatter.setFormat(decimal);
        numFormatter.setAllowsInvalid(false);
        DefaultFormatterFactory dfFactory = new DefaultFormatterFactory(numFormatter);

        txtSalario.setFormatterFactory(dfFactory);

        imprimir_todos_func();

    }

    private void adicionar() {

        String dia = txtDataNascimento.getText().substring(0, 2);
        String mes = txtDataNascimento.getText().substring(3, 5);
        String ano = txtDataNascimento.getText().substring(6);

        String datamysql = ano + "-" + mes + "-" + dia;

        // Preenche o objeto Funcionario
        funcionario.setNome(txtNomeFuncionario.getText());
        funcionario.setFuncao(txtFuncao.getText());

        // Converte e seta o salário
        String salarioTexto = txtSalario.getText().replace(".", "").replace(",", ".");
        BigDecimal salario = new BigDecimal(salarioTexto);
        funcionario.setSalario(salario);

        String sql = "insert into tbfuncionario(NOME, DATA_NASCIMENTO, SALARIO, FUNCAO) values(?,?,?,?)";
        try {
            pst = conexao.prepareStatement(sql);

            pst.setString(1, funcionario.getNome());
            pst.setString(2, datamysql);
            pst.setString(3, funcionario.getSalario().toString());
            pst.setString(4, funcionario.getFuncao());
            
            //Validação dos campos obrigatorios
            if ((txtNomeFuncionario.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Campo Nome, obrigatório.");

            } else if ((txtDataNascimento.getText().equals("  /  /    ")) || (txtDataNascimento.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Vencimento, obrigatório.");
            } else {
                int adicionado = pst.executeUpdate();

                buscaUltimoId();

                //System.out.println(adicionado);
                if (adicionado > 0) {

                    JOptionPane.showMessageDialog(null, "Funcionario adicionado.");

                    btnAdd.setEnabled(false);

                    btnExcluir.setEnabled(true);

                    btnLimpar.setEnabled(true);

                    limpar();

                    imprimir_todos_func();

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void remover() {
        int confirma = JOptionPane.showConfirmDialog(null, "Tem Certeza que deseja excluir este funcionario", "Atenção", JOptionPane.YES_NO_OPTION);

        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from tbfuncionario where ID=?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, txtId.getText());
                int apagado = pst.executeUpdate();

                if (apagado > 0) {
                    JOptionPane.showMessageDialog(null, "Funcionario removido com sucesso");

                    limpar();
                    imprimir_todos_func();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    //Imprimir todos funcioanrios
    private void imprimir_todos_func() {

        String sql = "select ID as id, NOME as Funcionario, date_format(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, Concat(   \n"
                + "               Replace  \n"
                + "                 (Replace  \n"
                + "                   (Replace  \n"
                + "                     (Format(SALARIO, 2), '.', '|'), ',', '.'), '|', ',')) AS Salario, FUNCAO AS Função from tbfuncionario;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Imprimir todos funcioanrios com salario com almento de 10%
    private void imprimir_todos_func_10porc_sal() {

        String sql = "SELECT ID AS id, \n"
                + "    NOME AS Funcionario, \n"
                + "    DATE_FORMAT(DATA_NASCIMENTO,'%d/%m/%Y') AS Nascimento, \n"
                + "    ROUND(SALARIO * 1.10, 2) AS Salario, \n"
                + "    FUNCAO AS Função \n"
                + "FROM tbfuncionario;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Imprimir todos funcioanrios por função
    private void imprimir_todos_por_funcao() {

        String sql = "select ID as id, NOME as Funcionario, date_format(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, Concat(   \n"
                + "               Replace  \n"
                + "                 (Replace  \n"
                + "                   (Replace  \n"
                + "                     (Format(SALARIO, 2), '.', '|'), ',', '.'), '|', ',')) AS Salario, FUNCAO AS Função from tbfuncionario order by FUNCAO;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Imprimir os funcioanrios que fazem aniversario no mes 10 e 12
    private void imprimir_funcinonarios_niver_10_12() {

        String sql = "SELECT \n"
                + "    ID AS id, \n"
                + "    NOME AS Funcionario, \n"
                + "    DATE_FORMAT(DATA_NASCIMENTO,'%d/%m/%Y') AS Nascimento,\n"
                + "    REPLACE(FORMAT(SALARIO, 2), '.', ',') AS Salario,\n"
                + "    FUNCAO AS Função \n"
                + "FROM tbfuncionario\n"
                + "WHERE MONTH(DATA_NASCIMENTO) IN (10, 12)\n"
                + "ORDER BY MONTH(DATA_NASCIMENTO), DAY(DATA_NASCIMENTO);";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void imprimir_funcionario_velho() {
       
        String sql = "SELECT NOME, DATA_NASCIMENTO, TIMESTAMPDIFF(YEAR, DATA_NASCIMENTO, CURDATE()) AS IDADE FROM tbfuncionario ORDER BY DATA_NASCIMENTO ASC LIMIT 1;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();

            // 3. Exibir no JOptionPane
            if (rs.next()) {
                String nome = rs.getString("NOME");
                int idade = rs.getInt("IDADE");              

                // Criando o objeto Funcionario
                Funcionario func = new Funcionario();
                func.setNome(nome);

                // Converte a data do banco para LocalDate
                java.sql.Date dataSQL = rs.getDate("DATA_NASCIMENTO");
                if (dataSQL != null) {
                    LocalDate dataNasc = dataSQL.toLocalDate();
                    func.setDataNascimento(dataNasc);
                }

                String mensagem = "Funcionário com maior idade:\n\n"
                        + "Nome: " + func.getNome() + "\n"
                        + "Idade: " + idade + " anos\n"
                        + "Data Nascimento: " + func.getDataNascimentoFormatada();
                
                JOptionPane.showMessageDialog(null, mensagem, "Funcionário Mais Velho", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum funcionário encontrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    private void imprimir_total_salario() {
    
    String sql = "SELECT SUM(SALARIO) AS TOTAL_SALARIO, COUNT(*) AS TOTAL_FUNCIONARIOS FROM tbfuncionario;";

    try {
        pst = conexao.prepareStatement(sql);
        rs = pst.executeQuery();

        if (rs.next()) {
            double totalSalario = rs.getDouble("TOTAL_SALARIO");
            int totalFuncionarios = rs.getInt("TOTAL_FUNCIONARIOS");
            
            // Formata o valor total do salário
            String totalFormatado = String.format("R$ %,.2f", totalSalario).replace(".", ",");
            
            String mensagem = "RELATÓRIO DE SALÁRIOS\n\n"
                    + "Total de Funcionários: " + totalFuncionarios + "\n"
                    + "Total de Salários: " + totalFormatado + "\n\n"
                    + "Média Salarial: R$ " + String.format("%,.2f", totalSalario / totalFuncionarios).replace(".", ",");
            
            JOptionPane.showMessageDialog(null, mensagem, "Total de Salários", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Nenhum funcionário encontrado!", "Aviso", JOptionPane.WARNING_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Erro ao calcular total: " + e.getMessage());
        e.printStackTrace();
    }
}

    //Imprimir todos funcioanrios por ordem alfabetica
    private void imprimir_todos_func_a_z() {

        String sql = "select ID as id, NOME as Funcionario, date_format(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, Concat(   \n"
                + "               Replace  \n"
                + "                 (Replace  \n"
                + "                   (Replace  \n"
                + "                     (Format(SALARIO, 2), '.', '|'), ',', '.'), '|', ',')) AS Salario, FUNCAO AS Função from tbfuncionario ORDER BY NOME ASC;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Imprimir todos funcioanrios com a quandtidade de salario que cada um tem
    private void imprimir_func_quant_salarios() {

        String sql = "SELECT \n"
                + "    ID as id, \n"
                + "    NOME as Funcionario, \n"
                + "    DATE_FORMAT(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, \n"
                + "    REPLACE(REPLACE(REPLACE(FORMAT(SALARIO, 2), '.', '|'), ',', '.'), '|', ',') AS Salario, \n"
                + "    FUNCAO AS Função, \n"
                + "    ROUND(SALARIO / 1212.00, 2) AS QtdSalariosMinimos \n"
                + "FROM tbfuncionario;";

        try {
            pst = conexao.prepareStatement(sql);
            //Passando o conteudo da caixa de pesquisa para o ?
            //atenção ao "%" - continuação da pesquisa sql
            rs = pst.executeQuery();
            // a linha abaixo usa a biblioteca rs2xml.jar para preencher a tabela 
            tblFuncionariosTodos.setModel(DbUtils.resultSetToTableModel(rs));
            tblFuncionariosTodos.getColumnModel().getColumn(0).setMaxWidth(50);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Metodo com todas opções de impressão com o JComoBox
    public void opcaoImpressoes() {
        int posicaoSelecionada = cboImpressoes.getSelectedIndex();

        if (posicaoSelecionada == 0) {
            imprimir_todos_func();
        } else if (posicaoSelecionada == 1) {
            imprimir_todos_func_10porc_sal();
        } else if (posicaoSelecionada == 2) {
            imprimir_todos_por_funcao();
        } else if (posicaoSelecionada == 3) {
            imprimir_funcinonarios_niver_10_12();
        } else if (posicaoSelecionada == 4) {
            imprimir_funcionario_velho();
        } else if (posicaoSelecionada == 5) {
            imprimir_todos_func_a_z();
        } else if (posicaoSelecionada == 6) {
            imprimir_func_quant_salarios();
        } else if (posicaoSelecionada == 7) {
            imprimir_total_salario();
        }
    }

    //Metodo para setar os campos do formulario com o conteudo da tabela    
    public void setar_campos() {

        int setar = tblFuncionariosTodos.getSelectedRow();

        // Verifica se há uma linha selecionada
        if (setar < 0) {
            JOptionPane.showMessageDialog(null, "Selecione um funcionário na tabela!");
            return;
        }

        try {
            // Pega os valores diretamente da tabela
            txtId.setText(tblFuncionariosTodos.getModel().getValueAt(setar, 0).toString());
            txtNomeFuncionario.setText(tblFuncionariosTodos.getModel().getValueAt(setar, 1).toString());
            txtDataNascimento.setText(tblFuncionariosTodos.getModel().getValueAt(setar, 2).toString());
            txtSalario.setText(tblFuncionariosTodos.getModel().getValueAt(setar, 3).toString());
            txtFuncao.setText(tblFuncionariosTodos.getModel().getValueAt(setar, 4).toString());

            // Também preenche o objeto Funcionario
            funcionario.setNome(txtNomeFuncionario.getText());
            funcionario.setFuncao(txtFuncao.getText());

            // Converte o salário da tabela para BigDecimal
            String salarioTexto = txtSalario.getText().replace(".", "").replace(",", ".");
            BigDecimal salario = new BigDecimal(salarioTexto);
            funcionario.setSalario(salario);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar dados: " + e.getMessage());
            e.printStackTrace();
        }

        // Desabilita o botão adicionar
        btnAdd.setEnabled(false);
        btnExcluir.setEnabled(true);
        btnLimpar.setEnabled(true);

    }

    public void limpar() {

        txtId.setText(null);
        txtNomeFuncionario.setText(null);
        txtFuncao.setText(null);
        txtSalario.setValue(null);
        btnExcluir.setEnabled(false);
        btnLimpar.setEnabled(false);
        txtDataNascimento.setText(null);
        btnAdd.setEnabled(true);
    }

    private void buscaUltimoId() {

        String sql = "select ID from tbfuncionario order by ID desc limit 1;";

        try {
            pst = conexao.prepareStatement(sql);
            //pst.setString(1, txtUsuId.getText());
            rs = pst.executeQuery();
            if (rs.next()) {
                txtId.setText(rs.getString(1));

            }
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnLimpar = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtSalario = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtNomeFuncionario = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtFuncao = new javax.swing.JTextField();
        txtDataNascimento = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFuncionariosTodos = new javax.swing.JTable();
        cboImpressoes = new javax.swing.JComboBox<>();
        btnExcluir = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnLimpar.setText("Limpar Campos");
        btnLimpar.setEnabled(false);
        btnLimpar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimparActionPerformed(evt);
            }
        });

        jLabel19.setText("Data De Nascimento:");

        jLabel20.setText("Salario");

        txtSalario.setText("0,00");
        txtSalario.setToolTipText("");
        txtSalario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSalarioActionPerformed(evt);
            }
        });
        txtSalario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSalarioKeyReleased(evt);
            }
        });

        jLabel1.setText("Nome");

        txtNomeFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeFuncionarioActionPerformed(evt);
            }
        });

        jLabel4.setText("Função");

        txtFuncao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFuncaoActionPerformed(evt);
            }
        });

        jLabel7.setText("Id");

        txtId.setEnabled(false);
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Associados Cadastrados"));

        tblFuncionariosTodos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex){
                return false;
            }
        };
        tblFuncionariosTodos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nome", "Nascimento", "Salario", "Funcão", "NSal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblFuncionariosTodos.getTableHeader().setReorderingAllowed(false);
        tblFuncionariosTodos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblFuncionariosTodosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblFuncionariosTodos);

        cboImpressoes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Imprimir todos funcionarios", "Imprimir todos com salario de 10%", "Imprimir funcionarios agrupados por função", "Imprimir os funcionários que fazem aniversário no mês 10 e 12", "Imprimir o funcionário com a maior idade", "Imprimir a lista de funcionários por ordem alfabética", "Imprimir quantos salários mínimos ganha cada funcionário", "Imprimir total dos salarios", " " }));
        cboImpressoes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cboImpressoesMouseClicked(evt);
            }
        });
        cboImpressoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboImpressoesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 724, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cboImpressoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cboImpressoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnExcluir.setText("Excluir");
        btnExcluir.setEnabled(false);
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        btnAdd.setText("ADD");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnLimpar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(73, 73, 73)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtNomeFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel19)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(txtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(txtFuncao, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnExcluir)
                                .addGap(18, 18, 18)
                                .addComponent(btnAdd)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtId))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(txtDataNascimento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtNomeFuncionario)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtSalario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtFuncao))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExcluir)
                    .addComponent(btnAdd))
                .addGap(18, 18, 18)
                .addComponent(btnLimpar)
                .addGap(40, 40, 40))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLimparActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimparActionPerformed
        limpar();

    }//GEN-LAST:event_btnLimparActionPerformed

    private void txtSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSalarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSalarioActionPerformed

    private void txtSalarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalarioKeyReleased
        String valor = txtSalario.getText();

        if (valor.length() == 0) {
            DecimalFormat decimal = new DecimalFormat("###,###,###.00");
            NumberFormatter numFormatter = new NumberFormatter(decimal);
            numFormatter.setFormat(decimal);
            numFormatter.setAllowsInvalid(false);
            DefaultFormatterFactory dfFactory = new DefaultFormatterFactory(numFormatter);

            txtSalario.setFormatterFactory(dfFactory);

        }
    }//GEN-LAST:event_txtSalarioKeyReleased

    private void txtNomeFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeFuncionarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNomeFuncionarioActionPerformed

    private void txtFuncaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFuncaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFuncaoActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void tblFuncionariosTodosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblFuncionariosTodosMouseClicked
        // Chamando o metodo para setar os campos
        setar_campos();
    }//GEN-LAST:event_tblFuncionariosTodosMouseClicked

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        remover();
    }//GEN-LAST:event_btnExcluirActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        adicionar();
    }//GEN-LAST:event_btnAddActionPerformed

    private void cboImpressoesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cboImpressoesMouseClicked
        opcaoImpressoes();
    }//GEN-LAST:event_cboImpressoesMouseClicked

    private void cboImpressoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboImpressoesActionPerformed
        opcaoImpressoes();
    }//GEN-LAST:event_cboImpressoesActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaFuncionarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaFuncionarios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JComboBox<String> cboImpressoes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblFuncionariosTodos;
    private javax.swing.JFormattedTextField txtDataNascimento;
    private javax.swing.JTextField txtFuncao;
    public static javax.swing.JTextField txtId;
    public static javax.swing.JTextField txtNomeFuncionario;
    private javax.swing.JFormattedTextField txtSalario;
    // End of variables declaration//GEN-END:variables
}
