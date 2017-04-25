package cn.org.xiwi.springboot.pay.bank;

public class BankInfo {
	private String bank;
	private String bankName;
	private String bankImg;
	public BankInfo(String bank, String bankName) {
		super();
		this.bank = bank;
		this.bankName = bankName;
		this.bankImg = "https://apimg.alipay.com/combo.png?d=cashier&t="+bank;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
		this.bankImg = "https://apimg.alipay.com/combo.png?d=cashier&t="+bank;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	
	public String getBankImg() {
		
		return bankImg;
	}
	@Override
	public String toString() {
		return "BankInfo [bank=" + bank + ", bankName=" + bankName + ", bankImg=" + bankImg + "]";
	}
}
