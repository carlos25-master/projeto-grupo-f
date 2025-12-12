import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;


public class programa {
  public static void main(String[] args) throws Exception{

    System.out.println("Número total de argumentos: " + args.length);
    for (int i = 0; i < args.length; i++) {
      if(args[i]!=""){
        System.out.println("Argumento [" + i + "]: " + args[i]);
      }
      else args[i]="0";
      
      }
      String argmento0="";
      String argmento1="";
      String argmento2="";
      String argmento3="";
     if(args.length > 0){argmento0 =  args[0];}
     if(args.length > 1){argmento1 =  args[1];}
     if(args.length > 2){argmento2 =  args[2];}
     if(args.length > 3){argmento3 =  args[3];}
    
     switch (argmento0) {
      case "help":  System.out.println(" C XX — considera apenas cidades de um continente. Ex.: C América do Sul \n P XX — considera apenas cidades de um país. Ex.: P Brasil \n + XX — considera apenas cidades com população mínima de XX. Ex.: + 10000000 \n - XX — considera apenas cidades com população máxima de XX. Ex.: - 500000");
        break;
      case "C": System.out.println("Selecionado Continentes");
      break;
      case "P": System.out.println("Selecionado Paises");
        break;
      case "+": System.out.println("Selecionado Paises");
        break;
      case "-":System.out.println("Selecionado Paises");
        break;
        case "": System.out.println("Sem argumentos - considera todas as cidades do mundo."); 
        break;
      default: System.out.println("sem parametros"); 
     }


 //primeiro bloco leitura do arquivo .csv  
try{
  FileReader reader = new FileReader("test.csv");
  BufferedReader buffer = new BufferedReader(reader);
  String line;
  while (true) {
    line = buffer.readLine();
    System.out.println(line);
    if (line==null)
      break;
  
    }
  } 
  catch(Exception e) {
  e.printStackTrace();
    }
  }
}
