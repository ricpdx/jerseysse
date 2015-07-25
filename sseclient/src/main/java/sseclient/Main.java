package sseclient;

public class Main {
	public static void main(String[] args) {
		if (args.length == 3) {
			String serverName = null;
			String clientId = null;
			Integer duration = null;
			serverName = args[0];
			clientId = args[1];
			try {
				duration = Integer.parseInt(args[2]);
			} catch (Exception ex) {
				System.out.println("duration must be an integer.");
			}
			if (clientId != null && duration != null) {
				Processor processor = new Processor();
				processor.run(serverName, clientId, duration);
			}
		} else {
			System.out.println("usage: sseclient servername clientid duration");
		}
	}
}
