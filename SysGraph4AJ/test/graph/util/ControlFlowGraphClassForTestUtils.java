package graph.util;

public class ControlFlowGraphClassForTestUtils {

	public String ifElseMethod(int numero) {
		int i = numero;
		return i == 1 ? "um" : "zero";
	}

	public void forMethod(String algumaCoisa){
		for(int i = 0; i < algumaCoisa.length(); i++){
			if(i == 0) {
				System.out.println(i);
			}
		}
	}

	public String switchMethod() {
		if(new Boolean(false)){
			System.out.println("false");
		}

		int i = 1;

		switch (i) {
			case 0: return "zero";
			case 1: return "um";
			case 2: return "dois";
			case 3: return "tres";
			case 4: return "quatro";
			case 5: return "cinco";
			case 6: return "seis";
			default: 
				return "sete";
		}
	}
	
	public void tryCatchMethod() {
		try {
			int i = 1;
			System.out.println(i);
			try {
				System.out.println("ndosa");
			} catch(Throwable t) {
				t.printStackTrace();
			}
		} catch(NullPointerException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("dnsoa");
		}
	}
	
	@SuppressWarnings("unused")
	public void aspectMethod() {
		int i = 0;
		i++;
		
		if(new Boolean("false")) {
			System.out.println("segunda linha");
		}
	}

}
