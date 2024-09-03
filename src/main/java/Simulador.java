

import java.util.Scanner;

import bank.Bank;


public class Simulador {
	public static void main(String[] args) {
		
		Bank bank = new Bank();
		
            try (Scanner scanner = new Scanner(System.in)) {
                int choice = -1;
                
                do {
                    bank.showMainMenu(scanner);
                    choice = scanner.nextInt();
                    
                }while(choice != 0);
            }
	}
}
