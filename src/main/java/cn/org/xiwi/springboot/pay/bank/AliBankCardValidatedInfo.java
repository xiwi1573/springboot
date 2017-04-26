package cn.org.xiwi.springboot.pay.bank;

import java.util.List;

public class AliBankCardValidatedInfo {
	private String bank;
	private String stat;
	private String cardType;
	private String key;
	private boolean validated;

	private List<ErrorInfo> messages;

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public List<ErrorInfo> getMessages() {
		return messages;
	}

	public void setMessages(List<ErrorInfo> messages) {
		this.messages = messages;
	}

	public class ErrorInfo {
		private String errorCodes;
		private String name;

		public String getErrorCodes() {
			return errorCodes;
		}

		public void setErrorCodes(String errorCodes) {
			this.errorCodes = errorCodes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "ErrorInfo [errorCodes=" + errorCodes + ", name=" + name + "]";
		}

	}

	@Override
	public String toString() {
		return "AliBankCardValidatedInfo [bank=" + bank + ", stat=" + stat + ", cardType=" + cardType + ", key=" + key
				+ ", validated=" + validated + ", messages=" + messages + "]";
	}
	
	
}
