package bank;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import account.Account;
import client.Client;



public class Bank {

    ArrayList<Account> accountList = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);

    private static final String FIXED_BRANCH = "8888";

    public void showMainMenu(Scanner scanner) {
        System.out.println("0 - Sair");
        System.out.println("1 - Cadastrar um cliente");
        System.out.println("2 - Remover um cliente");
        System.out.println("3 - Editar dados do cliente");
        System.out.println("4 - Realizar depósito para um cliente");
        System.out.println("5 - Realizar um saque na conta de um cliente");
        System.out.println("6 - Acessar conta do cliente");
        System.out.println("7 - Realizar depósito via Pix");
        System.out.println("Imprimri dados da conta e do cliente");
        System.out.println("Escolha:");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Clear the buffer
    
        switch (choice) {
            case 0 -> {
                System.out.println("Saindo...");
                System.exit(0);
            }
            case 1 -> createAccount();
            case 2 -> removeAccount(null);
            case 3 -> editClientData(null, null, null);
            case 4 -> deposit(null, null, choice);
            case 5 -> withdraw(null, null, choice);
            case 6 -> accessClientAccount(null);
            case 7 -> deposiPix(null);
            case 8 -> printAccount(null);
            default -> System.out.println("Opção inválida.");
        }
    }
    
    

public void createAccount() {
    System.out.println("Digite o nome do cliente:");
    String clientName = scanner.nextLine();
    
    System.out.println("Digite o CPF do cliente:");
    String documentId = scanner.nextLine();

    // Verifica se a conta já existe
    if (checkIfAccountExists(documentId)) {
        System.out.println("O cliente com CPF " + documentId + " já possui uma conta.");
        System.out.println("Deseja acessar a conta existente? (s/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("s")) {
            printAccountByDocumentId(documentId);
            return;
        } else {
            System.out.println("Retornando ao menu principal.");
            return;
        }
    }

    // Gerar número da conta único
    String numberAccount = generateUniqueAccountNumber();
    //saldo inical da conta 
    float initialBalance = 0.0f;
    //limite inicial da conta
    float limit = 100.0f; 

    Client client = new Client();
    client.setClientName(clientName);
    client.setDocumentId(documentId);

    Account newAccount = new Account();
    newAccount.client = client;
    newAccount.setNumberAccount(numberAccount);
    newAccount.setBranch(FIXED_BRANCH);
    newAccount.setBalance(initialBalance);
    newAccount.setLimit(limit);

    accountList.add(newAccount);
    System.out.println("Conta criada para: " + clientName + "\nO número da conta é: " + numberAccount);

    System.out.println("Deseja realizar um depósito inicial? (s/n)");
    String depositChoice = scanner.nextLine();
    if (depositChoice.equalsIgnoreCase("s")) {
        try {
            System.out.print("Digite o valor do depósito: ");
            float depositAmount = scanner.nextFloat();
            scanner.nextLine(); // Limpa o buffer
            deposit(numberAccount, documentId, depositAmount);
        } catch (InputMismatchException e) {
            System.out.println("Valor de depósito inválido. Tente novamente.");
            scanner.nextLine(); // Limpa o buffer em caso de erro
        }
    }

    updateLimit(newAccount);

    accessClientAccount(numberAccount);
}
//gerar numero da conta
private String generateUniqueAccountNumber() {
    Random random = new Random();
    String numberAccount;
    boolean unique;

    do {
        // Gerar número da conta com 8 dígitos
        numberAccount = String.format("%08d", random.nextInt(100000000));
        unique = true;

        // Verificar se o número da conta já existe
        for (Account account : accountList) {
            if (account.getNumberAccount().equals(numberAccount)) {
                unique = false;
                break;
            }
        }
    } while (!unique);

    return numberAccount;
}

    //pecorre a lista de contas criadas verificando se existe conta com o Id no caso o Cpf do cliente
    public boolean checkIfAccountExists(String documentId) { 
        for (Account account : accountList) {
            if (account.client.getDocumentId().equals(documentId)) {
                return true;
            }
        }
        return false;
    }
// para quando for fazer uma transferencia ele verifica a existencia da conta
    public void printAccountByDocumentId(String documentId) {
        for (Account account : accountList) {
            if (account.client.getDocumentId().equals(documentId)) {
                printAccount(account.getNumberAccount());
                return;
            }
        }
        System.out.println("Conta não encontrada para o CPF " + documentId);
    }


// remove a conta do client
    public void removeAccount(String numberAccount) {
        for (Account account : accountList) {
            if (account.getNumberAccount().equals(numberAccount)) {
                accountList.remove(account);
                System.out.println("Conta " + numberAccount + " removida");
                return;
            }
        }
        System.err.println("Conta não encontrada: " + numberAccount);
    }
// edita os dadso do client
    public void editClientData(String numberAccount, String newClientName, String newDocumentId) {
        for (Account account : accountList) {
            if (account.getNumberAccount().equals(numberAccount)) {
                account.client.setClientName(newClientName);
                account.client.setDocumentId(newDocumentId);
                System.out.println("Dados do cliente atualizados para " + newClientName);
                return;
            }
        }
        System.err.println("Conta não encontrada: " + numberAccount);
    }

    //area do client, onde ele vai fazer todas as operações
    public void accessClientAccount(String numberAccount) {
        Account account = findAccount(numberAccount);
    
        if (account != null) {
            while (true) {
                // Exibe os dados da conta
                System.out.println("Dados da Conta:");
                System.out.println("Número da Conta: " + account.getNumberAccount());
                System.out.println("Agência: " + account.getBranch());
                System.out.println("Nome: " + account.client.getClientName());
                System.out.println("CPF: " + account.client.getDocumentId());
                System.out.println("Chave Pix: " + (account.getPixKey().isEmpty() ? "Chave Pix não cadastrada" : account.getPixKey()));
                System.out.println("Saldo: " + account.getBalance());
                System.out.println("Limite: " + account.getLimit());
    
                // Menu de opções
                System.out.println("Escolha uma opção:");
                System.out.println("1 - Transferir");
                System.out.println("2 - Depositar");
                System.out.println("3 - Sacar");
                System.out.println("4 - Editar Dados do Cliente");
                System.out.println("5 - Remover Conta");
                System.out.println("6 - Área Pix");
                System.out.println("7 - Sair da conta e retornar ao menu inicial");
    
                int choice = scanner.nextInt();
                scanner.nextLine(); 
    
                switch (choice) {
                    case 1 -> transferViaPix(numberAccount);
                    case 2 -> {
                        System.out.print("Digite o valor do depósito: ");
                        float depositAmount = scanner.nextFloat();
                        scanner.nextLine(); 
                        deposit(numberAccount, account.client.getDocumentId(), depositAmount);
                    }
                    case 3 -> {
                        System.out.print("Digite o valor do saque: ");
                        float withdrawAmount = scanner.nextFloat();
                        scanner.nextLine(); 
                        withdraw(numberAccount, account.client.getDocumentId(), withdrawAmount);
                    }
                    case 4 -> editClientData(numberAccount, numberAccount, numberAccount);
                    case 5 -> {
                        System.out.println("Tem certeza que deseja remover a conta? (s/n)");
                        String confirm = scanner.nextLine();
                        if (confirm.equalsIgnoreCase("s")) {
                            removeAccount(numberAccount);
                            System.out.println("Conta removida com sucesso.");
                            return; // Volta ao menu principal após a remoção
                        } else {
                            System.out.println("Ação cancelada.");
                        }
                    }
                    case 6 -> pixArea(account);
                    case 7 -> {
                        System.out.println("Saindo da conta e retornando ao menu inicial...");
                        showMainMenu(scanner);
                        return; // retorna para o menu do do banco
                    }
                    default -> System.out.println("Opção inválida.");
                }
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
    
    
    //area pix onde ele tem as opções de modifcar/adicionar e saber qual sua chave pix
    public void pixArea(Account account) {
        while (true) {
            System.out.println("Área Pix:");
            System.out.println("Chave Pix atual: " + (account.getPixKey().isEmpty() ? "Chave Pix não cadastrada" : account.getPixKey()));
            System.out.println("Escolha uma opção:");
            System.out.println("1 - Alterar/Adicionar Chave Pix");
            System.out.println("2 - Voltar à conta");
    
            int choice = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer
    
            switch (choice) {
                case 1 -> {
                    System.out.print("Digite a nova Chave Pix: ");
                    String newPixKey = scanner.nextLine();
    
                    System.out.println("Tem certeza que deseja alterar a Chave Pix para: " + newPixKey + "? (s/n)");
                    String confirm = scanner.nextLine();
    
                    if (confirm.equalsIgnoreCase("s")) {
                        account.setPixKey(newPixKey);
                        System.out.println("Chave Pix alterada com sucesso!");
                        return; // Retorna ao menu principal da conta
                    } else {
                        System.out.println("Ação cancelada.");
                    }
                }
                case 2 -> {
                    return; // Volta ao menu principal da conta
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }
    
    
//deposito para o menu do usuario do banco
    public void deposit(String numberAccount, String documentId, float amount) {
        Account account = findAccount(numberAccount);
    
        if (account != null) {
            if (validateDocumentId(account, documentId)) {
                System.out.println("Deseja confirmar o depósito de " + amount + " na conta " + numberAccount + "? (s/n)");
                String confirmation = scanner.nextLine();
    
                if (confirmation.equalsIgnoreCase("s")) {
                    account.setBalance(account.getBalance() + amount);
                    System.out.println("Depósito de " + amount + " realizado na conta " + numberAccount);
                } else {
                    System.out.println("Depósito cancelado.");
                }
                accessClientAccount(numberAccount); // Retorna para o menu da conta
            } else {
                System.out.println("Número máximo de tentativas alcançado. Retornando ao menu principal.");
                accessClientAccount(numberAccount); // Retorna para o menu da conta
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
    //deposito para o menu do banoc... onde ele pode ser acessado facimente por meio de chave pix, sem precisar que o usuario tenha que saber sobre conta, agencia e cpf
    public void deposiPix(String numberAccount) {
        Account senderAccount = findAccount(numberAccount);

        if (senderAccount != null) {
            while (true) {
                System.out.println("Saldo atual: " + senderAccount.getBalance());
                System.out.print("Digite o valor do depósito: ");
                float amount = scanner.nextFloat();
                scanner.nextLine(); // Clear the buffer

                if (amount <= 0) {
                    System.out.println("Valor inválido. Tente novamente.");
                    continue;
                }

                System.out.print("Digite a chave Pix do destinatário: ");
                String recipientPixKey = scanner.nextLine();

                Account recipientAccount = findAccountByPixKey(recipientPixKey);

                if (recipientAccount != null) {
                    System.out.println("Detalhes do depósito:");
                    System.out.println("Nome do destinatário: " + recipientAccount.client.getClientName());
                    System.out.println("Chave Pix: " + recipientAccount.getPixKey());
                    System.out.println("Número da conta do destinatário: " + recipientAccount.getNumberAccount());
                    System.out.println("Valor do depósito: " + amount);

                    System.out.println("Confirme o depósito:");
                    System.out.println("1 - Confirmar");
                    System.out.println("2 - Cancelar");
                    System.out.println("3 - Editar dados do depósito");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer

                    switch (choice) {
                        case 1 -> {
                            senderAccount.setBalance(senderAccount.getBalance() - amount);
                            recipientAccount.setBalance(recipientAccount.getBalance() + amount);
                            System.out.println("Depósito de " + amount + " realizado com sucesso.");
                            updateLimit(senderAccount);
                            updateLimit(recipientAccount);
                            return;
                        }

                        case 2 -> {
                            System.out.println("Depósito cancelado.");
                            return;
                        }

                        case 3 -> {
                            // Editar dados
                            return; // Isso retornará e reiniciará o processo
                        }
                        default -> {
                            System.out.println("Opção inválida.");
                            return;
                        }
                    }
                } else {
                    System.out.println("Chave Pix do destinatário não encontrada.");
                    System.out.println("1 - Tentar novamente");
                    System.out.println("2 - Retornar ao menu principal");

                    int retryChoice = scanner.nextInt();
                    scanner.nextLine(); // Clear the buffer

                    if (retryChoice == 1) {
                    } else {
                        System.out.println("Retornando ao menu principal.");
                        return;
                    }
                }
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }


    //saque, para uso de acesso ao banco e para quem tem conta no menu dp client
    public void withdraw(String numberAccount, String documentId, float amount) {
        Account account = findAccount(numberAccount);
    
        if (account != null) {
            if (validateDocumentId(account, documentId)) {
                if (account.getBalance() + account.getLimit() >= amount) {
                    System.out.println("Deseja confirmar o saque de " + amount + " da conta " + numberAccount + "? (s/n)");
                    String confirmation = scanner.nextLine();
    
                    if (confirmation.equalsIgnoreCase("s")) {
                        account.setBalance(account.getBalance() - amount);
                        System.out.println("Saque de " + amount + " realizado da conta " + numberAccount);
                    } else {
                        System.out.println("Saque cancelado.");
                    }
                    accessClientAccount(numberAccount); // Retorna para o menu da conta
                } else {
                    System.out.println("Saldo insuficiente na conta " + numberAccount);
                    accessClientAccount(numberAccount); // Retorna para o menu da conta
                }
            } else {
                System.out.println("Número máximo de tentativas alcançado. Retornando ao menu principal.");
                accessClientAccount(numberAccount); // Retorna para o menu da conta
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
    

    private Account findAccount(String numberAccount) {
        for (Account account : accountList) {
            if (account.getNumberAccount().equals(numberAccount)) {
                return account;
            }
        }
        return null;
    }

    //limit de conta
    public void updateLimit(Account account) {
        float balance = account.getBalance();
        
        // Calcula o limite com base no saldo
        if (balance >= 500) {
            account.setLimit(200.0f);
        } else {
            account.setLimit(100.0f);
        }
        
        // Se o saldo for negativo, calcula juros
        if (balance < 0) {
            float debt = -balance;
            float interest = debt * 0.08f; // 8% juros
            System.out.println("Juros sobre o valor devedor: " + interest);
            account.setBalance(balance - interest);
        }
    }
    
    
//transferencia via pix
    public void transferViaPix(String numberAccount) {
        Account senderAccount = findAccount(numberAccount);
    
        if (senderAccount != null) {
            while (true) {
                System.out.println("Saldo atual: " + senderAccount.getBalance());
                System.out.print("Digite o valor da transferência: ");
                float amount = scanner.nextFloat();
                scanner.nextLine(); // Limpa o buffer
    
                if (amount > senderAccount.getBalance()) {
                    System.out.println("Valor maior que o saldo disponível. Tente novamente.");
                    continue;
                }
    
                System.out.print("Digite a chave Pix do destinatário: ");
                String recipientPixKey = scanner.nextLine();
    
                Account recipientAccount = findAccountByPixKey(recipientPixKey);
    
                if (recipientAccount != null) {
                    // Exibe os detalhes da transferência
                    System.out.println("Detalhes da transferência:");
                    System.out.println("Nome do destinatário: " + recipientAccount.client.getClientName());
                    System.out.println("Chave Pix: " + recipientAccount.getPixKey());
                    System.out.println("Número da conta do destinatário: " + recipientAccount.getNumberAccount());
                    System.out.println("Valor da transferência: " + amount);
    
                    while (true) {
                        System.out.println("Confirme a transferência:");
                        System.out.println("1 - Confirmar");
                        System.out.println("2 - Cancelar");
                        System.out.println("3 - Editar dados da transferência");
    
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // Limpa o buffer
    
                        switch (choice) {
                            case 1 -> {
                                // Confirma a transferência
                                senderAccount.setBalance(senderAccount.getBalance() - amount);
                                recipientAccount.setBalance(recipientAccount.getBalance() + amount);
                                System.out.println("Transferência de " + amount + " realizada com sucesso.");
                                accessClientAccount(numberAccount); // Retorna ao menu de conta após sucesso
                                return;
                            }
                            case 2 -> {
                                // Cancela a transferência
                                System.out.println("Transferência cancelada.");
                                accessClientAccount(numberAccount); // Retorna ao menu de conta após cancelamento
                                return;
                            }
                            case 3 -> {
                                // Edita os dados da transferência
                                break; // Sai do switch para repetir o processo de transferência
                            }
                            default -> System.out.println("Opção inválida.");
                        }
                    }
                } else {
                    System.out.println("Chave Pix do destinatário não encontrada.");
                    System.out.println("1 - Tentar novamente");
                    System.out.println("2 - Retornar à conta");
    
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Limpa o buffer
                    switch (choice) {
                        case 1 -> {
                            // Tenta novamente
                            continue;
                        }
                        case 2 -> {
                            // Retorna ao menu de conta
                            accessClientAccount(numberAccount);
                            return;
                        }
                        default -> {
                            System.out.println("Opção inválida. Retornando à conta.");
                            accessClientAccount(numberAccount);
                            return;
                        }
                    }
                }
            }
        } else {
            System.out.println("Conta não encontrada.");
        }
    }
    //verificação de id Cpf (poderia ser chave pix), em tres tentativas onde apos a terceira ele ira retorna ao meun do client e informara que as tentativas terminaram
    private boolean validateDocumentId(Account account, String documentId) {
        int attempts = 3;

        while (attempts > 0) {
            if (account.client.getDocumentId().equals(documentId)) {
                return true;
            } else {
                attempts--;
                if (attempts > 0) {
                    System.out.println("CPF inválido. Você tem mais " + attempts + " tentativa(s). Insira novamente:");
                    documentId = scanner.nextLine();
                }
            }
        }

        return false;
    }
    
    
    

    private Account findAccountByPixKey(String pixKey) {
        for (Account account : accountList) {
            if (account.getPixKey().equals(pixKey)) {
                return account;
            }
        }
        return null;
    }

//opção para mostrar os dados do client...
    public void printAccount(String numberAccount) {
        for (Account account : accountList) {
            if (account.getNumberAccount().equals(numberAccount)) {
                System.out.println("Dados da Conta:");
                System.out.println("Número da Conta: " + account.getNumberAccount());
                System.out.println("Agência: " + account.getBranch());
                System.out.println("Saldo: " + account.getBalance());
                System.out.println("Limite: " + account.getLimit());
                System.out.println("Chave Pix: " + account.getPixKey());
                System.out.println("Dados do Cliente:");
                System.out.println("Nome: " + account.client.getClientName());
                System.out.println("Documento: " + account.client.getDocumentId());
                return;
            }
        }
        System.err.println("Conta não encontrada: " + numberAccount);
    }
}