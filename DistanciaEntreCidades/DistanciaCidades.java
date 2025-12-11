import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

// =================================================================
// CLASSE AUXILIAR PARA REPRESENTAR CADA CIDADE
// =================================================================
class Cidade {
    public String continente;
    public String pais;
    public String nome;
    public double latitude;
    public double longitude;
    public long populacao;

    public Cidade(String cont, String p, String n, double lat, double lon, long pop) {
        this.continente = cont;
        this.pais = p;
        this.nome = n;
        this.latitude = lat;
        this.longitude = lon;
        this.populacao = pop;
    }
}

public class DistanciaCidades {

    // Raio da Terra em km, conforme especificado no desafio (6.378,13 km)
    private static final double RAIO_TERRA_KM = 6378.13; 

    /**
     * Calcula a distância de grande círculo entre dois pontos na Terra
     * usando a fórmula de Haversine.
     * @param lat1 Latitude do Ponto 1 em graus.
     * @param lon1 Longitude do Ponto 1 em graus.
     * @param lat2 Latitude do Ponto 2 em graus.
     * @param lon2 Longitude do Ponto 2 em graus.
     * @return A distância em quilômetros (km).
     */
    public static double calcularDistanciaHaversine(
            double lat1, double lon1, double lat2, double lon2) {
        
        // 1. Converter graus para radianos
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);
        
        // 2. Aplicar a fórmula de Haversine (parte interna do arcsen)
        // a = sin²(Δφ/2) + cos(φ1) · cos(φ2) · sin²(Δλ/2)
        
        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                   Math.cos(phi1) * Math.cos(phi2) *
                   Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

        // 3. Aplicar a parte externa da fórmula d = 2R · arcsen(√a)
        // Usa atan2 para maior estabilidade numérica
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return RAIO_TERRA_KM * c;
    }

    public static void main(String[] args) {
        
        // =================================================================
        // PARTE 1: LEITURA DO ARQUIVO (VIA REDIRECIONAMENTO DE ENTRADA)
        // =================================================================
        ArrayList<Cidade> listaCidades = lerCidadesDeEntrada();
        
        if (listaCidades.isEmpty()) {
            System.err.println("Erro: Nenhuma cidade foi carregada. Verifique se o arquivo de entrada foi redirecionado corretamente e se o formato está correto (separador ';').");
            return;
        }

        // =================================================================
        // PARTE 2: APLICAÇÃO DO FILTRO (ARGUMENTOS DE LINHA DE COMANDO)
        // =================================================================
        ArrayList<Cidade> cidadesFiltradas = aplicarFiltro(listaCidades, args);
        
        if (cidadesFiltradas.size() < 2) {
            System.out.println("\n-------------------------------------------------------");
            System.out.println("Não há cidades suficientes (mínimo 2) após a aplicação do filtro para calcular a distância.");
            System.out.println("-------------------------------------------------------");
            return;
        }
        
        // =================================================================
        // PARTE 3: CÁLCULO DA MAIOR DISTÂNCIA
        // =================================================================
        calcularMaiorDistancia(cidadesFiltradas);
    }
    
    /**
     * LÊ CIDADES DO ARQUIVO VIA System.in (REDIRECIONAMENTO)
     */
    private static ArrayList<Cidade> lerCidadesDeEntrada() {
        ArrayList<Cidade> lista = new ArrayList<>();
        // Usa InputStreamReader para ler do System.in
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        String line;
        int contadorLinha = 0;
        final String SEPARADOR = ";"; // O enunciado indica ";" como separador
        
        try {
            while ((line = buffer.readLine()) != null) {
                contadorLinha++;
                
                // Ignora linhas vazias
                if (line.trim().isEmpty()) continue;
                
                // Trata a primeira linha como cabeçalho
                if (contadorLinha == 1 && line.toLowerCase().contains("continente")) {
                    System.out.println("--- Ignorando cabeçalho.");
                    continue; 
                }
               
                String[] dadosDaLinha = line.split(SEPARADOR); 
                
                if (dadosDaLinha.length >= 6) {
                    try {
                        String continente = dadosDaLinha[0].trim();
                        String pais = dadosDaLinha[1].trim();
                        String nome = dadosDaLinha[2].trim();
                        // Assume-se que Lat e Lon vêm em formato decimal (double)
                        double latitude = Double.parseDouble(dadosDaLinha[3].trim());
                        double longitude = Double.parseDouble(dadosDaLinha[4].trim());
                        // Assume-se que população é um número inteiro grande (long)
                        long populacao = Long.parseLong(dadosDaLinha[5].trim()); 

                        lista.add(new Cidade(continente, pais, nome, latitude, longitude, populacao));
                        
                    } catch (NumberFormatException e) {
                        System.err.println("!!! ERRO de número na Linha " + contadorLinha + " (" + e.getMessage() + "), pulando.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro de I/O durante a leitura de entrada: " + e.getMessage());
        } 
        
        System.out.println("Total de " + lista.size() + " cidades carregadas.");
        return lista;
    }

    /**
     * APLICA O FILTRO BASEADO NOS ARGUMENTOS DA LINHA DE COMANDO
     */
    private static ArrayList<Cidade> aplicarFiltro(ArrayList<Cidade> todasCidades, String[] args) {
        
        if (args.length == 0) {
            System.out.println("Filtro: Sem argumentos (Considerando todas as cidades).");
            return todasCidades; // Sem filtro
        }

        if (args.length < 2) {
             System.err.println("!!! ERRO: Argumento de filtro incompleto. Use: C XX, P XX, + XX ou - XX. Usando todas as cidades.");
             return todasCidades;
        }

        String tipoFiltro = args[0].toUpperCase();
        // Concatena o restante dos argumentos para suportar nomes compostos (ex: "America do Sul")
        String valorFiltro = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        
        ArrayList<Cidade> filtradas = new ArrayList<>();
        
        System.out.println("Filtro Aplicado: " + tipoFiltro + " " + valorFiltro);

        for (Cidade c : todasCidades) {
            boolean incluir = false;
            
            try {
                switch (tipoFiltro) {
                    case "C": // Continente
                        incluir = c.continente.equalsIgnoreCase(valorFiltro);
                        break;
                    case "P": // País
                        incluir = c.pais.equalsIgnoreCase(valorFiltro);
                        break;
                    case "+": // População Mínima
                        long popMinima = Long.parseLong(valorFiltro);
                        incluir = c.populacao >= popMinima;
                        break;
                    case "-": // População Máxima
                        long popMaxima = Long.parseLong(valorFiltro);
                        incluir = c.populacao <= popMaxima;
                        break;
                    default:
                        System.err.println("!!! ERRO: Argumento de filtro de tipo inválido: " + tipoFiltro + ". Usando todas as cidades.");
                        return todasCidades;
                }
            } catch (NumberFormatException e) {
                System.err.println("!!! ERRO: O valor do filtro (+/-) deve ser um número inteiro. Usando todas as cidades.");
                return todasCidades;
            }
            
            if (incluir) {
                filtradas.add(c);
            }
        }
        
        System.out.println("Total de " + filtradas.size() + " cidades após a filtragem.");
        return filtradas;
    }

    /**
     * CALCULA E EXIBE A MAIOR DISTÂNCIA ENTRE O SUBSET DE CIDADES
     */
    private static void calcularMaiorDistancia(ArrayList<Cidade> cidades) {
        double maiorDistancia = 0.0;
        Cidade cidadeMaisDistante1 = null;
        Cidade cidadeMaisDistante2 = null;
        
        // Loop N² para comparar cada cidade com todas as outras
        for (int i = 0; i < cidades.size(); i++) {
            Cidade cidadeA = cidades.get(i);
            
            for (int j = i + 1; j < cidades.size(); j++) {
                Cidade cidadeB = cidades.get(j);
                
                double distanciaAtual = calcularDistanciaHaversine(
                    cidadeA.latitude, cidadeA.longitude, 
                    cidadeB.latitude, cidadeB.longitude
                );
                
                if (distanciaAtual > maiorDistancia) {
                    maiorDistancia = distanciaAtual;
                    cidadeMaisDistante1 = cidadeA;
                    cidadeMaisDistante2 = cidadeB;
                }
            }
        }
        
        // Exibição do Resultado
        System.out.println("\n\n=======================================================");
        System.out.println("RESULTADO: MAIOR DISTÂNCIA ENCONTRADA");
        System.out.println("-------------------------------------------------------");
        
        if (cidadeMaisDistante1 != null && cidadeMaisDistante2 != null) {
            System.out.printf("A maior distância de grande círculo é: %.2f km\n", maiorDistancia);
            System.out.println("-------------------------------------------------------");
            System.out.println("CIDADE 1: " + cidadeMaisDistante1.nome + " (" + cidadeMaisDistante1.pais + ")");
            System.out.printf("   Coordenadas: Lat %.4f, Lon %.4f\n", cidadeMaisDistante1.latitude, cidadeMaisDistante1.longitude);
            System.out.println("-------------------------------------------------------");
            System.out.println("CIDADE 2: " + cidadeMaisDistante2.nome + " (" + cidadeMaisDistante2.pais + ")");
            System.out.printf("   Coordenadas: Lat %.4f, Lon %.4f\n", cidadeMaisDistante2.latitude, cidadeMaisDistante2.longitude);
        }
        System.out.println("=======================================================");
    }
}