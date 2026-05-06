create database dbempresa;

use dbempresa;

CREATE TABLE IF NOT EXISTS tbfuncionario(
ID int primary key auto_increment,
NOME varchar(50) not null,
DATA_NASCIMENTO date, 
SALARIO decimal(10,3) not null,
FUNCAO varchar(40) NOT NULL
);

insert into tbfuncionario(NOME, DATA_NASCIMENTO, SALARIO, FUNCAO) values(?,?,?,?);

select ID as id, NOME as Funcionario, date_format(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, SALARIO AS Salario, FUNCAO AS Função from tbfuncionario;

select * from tbfuncionario;

select ID as id, NOME as Funcionario, date_format(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, Concat( 
                            Replace  
                               (Replace  
                                   (Replace  
                                     (Format(SALARIO, 2), '.', '|'), ',', '.'), '|', ',')) AS Salario, FUNCAO AS Função from tbfuncionario;
                                     
                                     
   SELECT ID AS id, 
    NOME AS Funcionario, 
    DATE_FORMAT(DATA_NASCIMENTO,'%d/%m/%Y') AS Nascimento, 
    ROUND(SALARIO * 1.10, 2) AS Salario, 
    FUNCAO AS Função 
FROM tbfuncionario;

SELECT 
    ID as id, 
    NOME as Funcionario, 
    DATE_FORMAT(DATA_NASCIMENTO,'%d/%m/%Y') as Nascimento, 
    REPLACE(REPLACE(REPLACE(FORMAT(SALARIO, 2), '.', '|'), ',', '.'), '|', ',') AS Salario, 
    FUNCAO AS Função, 
    ROUND(SALARIO / 1212.00, 2) AS QtdSalariosMinimos 
FROM tbfuncionario;

SELECT NOME, TIMESTAMPDIFF(YEAR, DATA_NASCIMENTO, CURDATE()) AS IDADE FROM tbfuncionario ORDER BY DATA_NASCIMENTO ASC LIMIT 1;

SELECT SUM(SALARIO) AS TOTAL_SALARIO, COUNT(*) AS TOTAL_FUNCIONARIOS FROM tbfuncionario;
