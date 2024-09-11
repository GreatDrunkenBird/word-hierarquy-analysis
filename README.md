# Hierarchy Analyzer CLI

Este projeto é uma aplicação em Java que analisa uma frase em relação a uma árvore de hierarquia JSON, buscando correspondências em nós e folhas da árvore em níveis de profundidade específicos.

## Funcionalidades

- Busca por palavras em uma árvore JSON hierárquica.
- Suporte para definir o nível de profundidade da busca.
- Exibe métricas de desempenho (tempo de carregamento e tempo de análise).
- Análise tanto nos nós quanto nas folhas da árvore.

## Requisitos

- Java 8 ou superior.
- A biblioteca `Gson` para manipulação de JSON (inclusa no classpath).

## Estrutura de Diretórios

* project-root/ 
* │ 
* ├─ dicts/ # Diretório onde deve estar o arquivo JSON com a árvore hierárquica. 
* │ 
* └── tree.json # Arquivo JSON a ser analisado. 
* │ 
* ├─ src/ 
* │ 
* └── Main.java # Arquivo principal da aplicação. 
* │ 
* └─ README.md # Instruções de uso e documentação.


## Como usar

### Compilação

1. Compile o código Java:

```bash
javac -cp gson.jar Main.java
```

2. Crie um arquivo .jar para rodar o programa:

```bash
jar cfe cli.jar Main Main.class
```

## Execução

Para executar o analisador de hierarquia, utilize o seguinte comando:

```bash
java -jar cli.jar analyze --depth <n> [--verbose] "{frase}"
```

* --depth <n>: Especifica o nível de profundidade a ser analisado na árvore.
* --verbose: (Opcional) Exibe as métricas de tempo.
* {frase}: Frase a ser analisada em relação à hierarquia.

## Exemplo

```bash
java -jar cli.jar analyze --depth 3 --verbose "palavra1 palavra2 palavra3"
```

Neste exemplo, o programa irá buscar por palavra1, palavra2, e palavra3 no nível 3 da árvore e exibir métricas detalhadas de desempenho.

## Mensagem de Saída

* Caso as palavras sejam encontradas no nível especificado ou em seus subníveis, será exibida a contagem de ocorrências.
* Se nenhuma correspondência for encontrada, a mensagem "Na frase não existe nenhum filho do nível X e nem o nível X possui os termos especificados." será exibida.

### Métricas de Desempenho

* Se o parâmetro --verbose for utilizado, uma tabela de métricas de tempo será exibida, mostrando o tempo de carregamento dos parâmetros e o tempo de análise da frase.

## Estrutura do arquivo JSON
O arquivo JSON deve estar localizado no diretório dicts/ e conter uma estrutura de árvore. Um exemplo de arquivo JSON:

```json
{
    "root": {
        "level1": {
            "level2": {
                "level3": ["word1", "word2", "word3"]
            }
        }
    }
}

```
## Observações

* Certifique-se de que o arquivo JSON esteja corretamente formatado e localizado no diretório dicts/.
* O programa busca tanto nos nós quanto nas folhas da árvore, comparando as palavras da frase com o conteúdo do JSON.
* Já existe uma CLI pré compilada nos arquivos do programa
