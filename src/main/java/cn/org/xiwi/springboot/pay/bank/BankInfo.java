package cn.org.xiwi.springboot.pay.bank;

public class BankInfo {
	private String bank;
	private String bankName;
	public BankInfo(String bank, String bankName) {
		super();
		this.bank = bank;
		this.bankName = bankName;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	@Override
	public String toString() {
		return "BankInfo [bank=" + bank + ", bankName=" + bankName + "]";
	}
}
