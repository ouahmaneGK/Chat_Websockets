import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerChat  extends Thread{
	
	private boolean isActive = true;
	private int nbClients=0;
	private List<Conversation> clients = new ArrayList<Conversation>(); // list des clients connnect�s
	
	public static void main(String[] args) {
		
		new ServerChat().start();
	}
	
	@Override
	public void run() {
		try {
			ServerSocket serverSoket = new ServerSocket(8000);
			while(isActive) {
				Socket socket = serverSoket.accept();
				++nbClients;
				Conversation conv = new Conversation(socket, nbClients);
				// une conversation cr�e� cad un client est rajout� � la liste
				clients.add(conv);
				conv.start();
				
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	// class interne
	class Conversation extends Thread{
		protected Socket socketClient; 
		protected int numero;

		public Conversation(Socket socketClient, int numero) {
			this.socketClient = socketClient;
			this.numero = numero;
		}
		
		//******fonction qui envoie les requ�tes apres parcourir la liste des client connect�s
		public void broadcastMessage(String message, Socket source, int numClient) {
				try {
					for (Conversation client : clients) {
						if(client.socketClient !=source) {
							if(client.numero ==numClient || numClient == -1) {
								PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);//Le bool�en permet de pr�ciser si le tampon doit �tre automatiquement vid�
								printWriter.println(message);
							}
							
						}
					}
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
		}
		
		@Override
		public void run() {
	    try {
			InputStream is = socketClient.getInputStream(); // lire les caractere
			InputStreamReader isr = new InputStreamReader(is); // lire les chaine de caract�res
			BufferedReader br = new BufferedReader(isr);// lire des lignes de chaine de caract�res
		
			OutputStream os = socketClient.getOutputStream();
			PrintWriter pw = new PrintWriter(os, true); // true, ligne par ligne! (si false, on stock dans un buffer, et on envoie tout apres avec la methode flush)
			String ipClient = socketClient.getRemoteSocketAddress().toString();
			pw.println("bienvenue, vouts �tes le client num�ro "+numero);
			System.out.println("Connexion du client num�ro "+numero+","+ipClient);
	    
			while(true) {//� chaque fois qu'il ya un message...
				String req = br.readLine();//req envoy�
				if(req.contains("=>")) {
					String[] requestParams= req.split("=>");
					if(requestParams.length==2);
						String message = requestParams[1];
						int numeroClient = Integer.parseInt(requestParams[0]);
						broadcastMessage(message, socketClient, numeroClient);
					}
					else {
						broadcastMessage(req,socketClient,-1);
					}
				}
	
				
			
	    
	    } catch (IOException e) {
		
			e.printStackTrace();
		}
	    
		}
	}

}
