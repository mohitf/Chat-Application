import javax.swing.JFrame;

public class ServerTest {
	public static void main(String[] args){
		Server sr=new Server(9789);
		sr.startRunning();
	}
}
