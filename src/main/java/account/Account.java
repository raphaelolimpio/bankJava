package account;

import client.Client;

public class Account {
    public Client client = new Client();
	private String pixKey = "";
	private String numberAccount = "";
	private String branch = "";
	private float balance = 0.0f;
	private float limit = 0.0f;
	
	
	public String getPixKey() {
		return pixKey;
	}
	public void setPixKey(String pixKey) {
		this.pixKey = pixKey;
	}
	public String getNumberAccount() {
		return numberAccount;
	}
	public void setNumberAccount(String numberAccount) {
		this.numberAccount = numberAccount;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public float getLimit() {
		return limit;
	}
	public void setLimit(float limit) {
		this.limit = limit;
	}

    
}
