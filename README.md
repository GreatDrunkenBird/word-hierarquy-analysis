# Analisador de Hierarquia de Palavras

Aplicação de linha de comando que analisa uma árvore hierárquica buscando correlação com palavras de uma frase.

A árvore é carregada a partir de um arquivo JSON dentro da pasta /dicts.

## Exeução da aplicação

java -jar cli.jar analyze --depth <n> --verbose “{phrase}”

Onde:
--depth <n> é a profundidade da árvore sendo <n> declarado por um número.
Exemplo: -depth 2

--verbose é um parâmetro opicional que exibe o tempo de carregamento dos parâmetros e o tempo de análise da frase.
