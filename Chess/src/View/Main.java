package View;

import java.awt.EventQueue;

//import CommunicationModel.Server;

public class Main {

	public static void main(String[] args) {
		//TODO w celach testowych wewnątrz eclipse odkomentować natomiast zakomentować przy przenoszeniu aplikacji do .jar
	//	new Thread(new Server(4448)).start();
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				new LoginFrame();
			};
		});
	}

}
