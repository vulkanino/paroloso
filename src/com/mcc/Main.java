package com.mcc;

import java.io.*;
import java.util.*;

public class Main
{
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";

    private static final String FILE = "resources/ridotto.txt";

    public static void main(String[] args)
    {
        Main m = new Main();
        // m.stats();
        String s = m.scegli();
        m.indovina(s);
    }

    void stats()
    {
        //  Per ogni dimensione di parola conta quante sono
        SortedMap<Integer, Integer> contatore = new TreeMap<>();

        //  Massima lunghezza
        int max = 0;
        String maxString="";

        int totale = 0;

        InputStream is = ClassLoader.getSystemResourceAsStream(FILE);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is)))
        {
            String line;
            while ( (line = br.readLine()) != null )
            {
                final int dimensione = line.length();
                Integer quanti = contatore.get(dimensione);
                contatore.put(dimensione, quanti == null ? 1 : quanti+1);

                if ( dimensione >= max )
                {
                    max = dimensione;
                    maxString = line;
                }

                ++totale;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return;
        }

        contatore
                .keySet()
                .forEach(k-> System.out.printf("Lunghezza parola da %d, quantità parole: %d%n", k, contatore.get(k)));

        System.out.printf("\nParola più lunga: %s (%d)%n", maxString, max);
        System.out.println("Totale parole: " + totale);
    }

    public String scegli()
    {
        final Calendar c = Calendar.getInstance();
        final int seed = c.get(Calendar.YEAR) + c.get(Calendar.MONTH) + c.get(Calendar.DAY_OF_MONTH);

        final int linea = new Random(seed).nextInt(28874); // deve corrispondere al numero di parole nel file, vedi stats
        InputStream is = ClassLoader.getSystemResourceAsStream(FILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.lines().skip(linea-1).findFirst().get();
    }

    public void indovina(String daIndovinare)
    {
        final int quanti = daIndovinare.length();
        System.out.println("Lunghezza parola da indovinare: " + quanti);
        System.out.println(new String(new char[quanti]).replace("\0", "*"));

        Scanner sc = new Scanner(System.in);
        int tentativi=0;
        while ( true )
        {
            String input = sc.next();

            if ( input.isBlank() || input.isEmpty() || input.equals("\n") )
                break;

            if ( input.length() != quanti )
            {
                System.out.printf("Numero caratteri non corretto, devono essere %d ma sono %d.%n", quanti, input.length());
                continue;
            }

            if ( ! parolaEsisteNelFile(input) )
            {
                System.out.println("Questa parola non è nel dizionario.");
                continue;
            }

            ++tentativi;
            int contaGiusti=0;
            for(int i=0; i<quanti; ++i)
            {
                final char suo = input.charAt(i);
                if ( suo == daIndovinare.charAt(i) )
                {
                    System.out.printf("%s%s%s%s ", suo, ANSI_GREEN, "◼︎", ANSI_RESET);
                    ++contaGiusti;
                }
                else if ( daIndovinare.contains(String.valueOf(suo)) )
                {
                    System.out.printf("%s%s%s%s ", suo, ANSI_YELLOW, "◼︎", ANSI_RESET);
                    //System.out.print(suo + ' ' + ANSI_YELLOW + "◼︎" + ANSI_RESET);
                }
                else
                {
                    System.out.printf("%s%s%s%s ", suo, ANSI_BLACK, "◼︎", ANSI_RESET);

                    // System.out.print(suo + ' ' + ANSI_BLACK + "◼︎" + ANSI_RESET);
                }
            }
            System.out.println();

            if ( contaGiusti == quanti )
            {
                System.out.printf("BRAV* - ci sei riuscito in %d tentativi%n", tentativi);
                if ( tentativi < 3 )
                    System.out.println("(barando)");

                return;
            }
        }

    }

    private boolean parolaEsisteNelFile(final String input)
    {
        InputStream is = ClassLoader.getSystemResourceAsStream(FILE);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;

        try
        {
            while ((line = br.readLine()) != null)
            {
                if (line.equals(input))
                    return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return false;
    }
}
