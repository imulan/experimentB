//package group2_2;

import java.io.*;
import java.net.*;

public class NumeronServer{
	public static final int PORT=8081;
	public static void main(String[] args) throws IOException{
		ServerSocket s = new ServerSocket(PORT);
		System.out.println("ServerStarted: " + s);

		NumeronGame game = new NumeronGame();

		int serverNums=0;
		PlayerThread th[] = new PlayerThread[2];
		while(serverNums<2){
			Socket socket = s.accept();
			th[serverNums] = new PlayerThread(socket, game);
			th[serverNums].start();
			serverNums++;
		}

	}
}

//ゲームのデータを保管する
class NumeronGame{

	static String answer[] = new String[2];
	static int answerCount = 0;

	static int order = 0;
	static int orderCount = 0;

	static String call[][] = new String[2][100];
	static int callCount[] = new int[2];

	/*
	callCount[0] = 0;
	callCount[1] = 0;
	*/

	synchronized void setAnswer(Thread t, String s){
		System.out.println(Thread.currentThread().getName());
		String cp=Thread.currentThread().getName();

		int a;
		if(cp.equals("Thread-0")) a=0;
		else a=1;

		answer[a] = s;
		answerCount++;

		if(answerCount==2){
			for(int i=0; i<2; ++i){
				System.out.println("Player" + i + " :: setNumber = " + answer[i]);
			}
		}
	}

	public String getEnemyAnswer(Thread t){
		String cp=Thread.currentThread().getName();
		if(cp.equals("Thread-0")) return answer[1];
		else return answer[0];
	}

	synchronized void setOrder(int a){
		order += a;
		orderCount++;
		try{
			if(orderCount==1) wait();
			else notifyAll();
		} catch(InterruptedException ie){}
	}

	public int getOrder(Thread t){ //0が先攻，1が後攻
		String cp=Thread.currentThread().getName();
		if(cp.equals("Thread-0")) return order%2;
		else return (order+1)%2;
	}

	synchronized void setCall(String s){
		String cp=Thread.currentThread().getName();

		int a;
		if(cp.equals("Thread-0")) a=0;
		else a=1;

		call[a][callCount[a]++] = s;

		System.out.println("call end, turn change");
		notifyAll();
	}

	synchronized String getCall(){
		int a=0;
		try {
			wait();
			String cp=Thread.currentThread().getName();

			//相手のcallを受け取ってClientへ送信
			if(cp.equals("Thread-0")) a=1;
			else a=0;

		} catch (InterruptedException ie){}
		finally{
			return call[a][callCount[a]-1];
		}
	}


}

class PlayerThread extends Thread{
	private Socket ps;
	private NumeronGame ng;

	public PlayerThread(Socket socket, NumeronGame game){
		this.ps = socket;
		this.ng = game;
		System.out.println("Connected :: " + this.ps);
	}

	public void run(){

		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(ps.getOutputStream())), true);

			//まず答えを受け取り，保管場所へ
			System.out.println("waiting...");
			String answer = in.readLine();
			ng.setAnswer(this, answer);

			//先攻，後攻の決定
			String order = in.readLine();
			ng.setOrder(order.charAt(0)-'0');
			if(ng.getOrder(this) == 0) out.println("FIRST");
			else out.println("SECOND");

			//相手の答えを送っておく
			out.println(ng.getEnemyAnswer(this));

			//ゲーム開始
			while(true){
				String attacker = in.readLine(); //攻める番か，待つ番か
				if(attacker.equals("ATTACKER")){
					String call = in.readLine();
					ng.setCall(call);
				}
				else if(attacker.equals("OBSERVER")){
					String ret=ng.getCall();
					out.println(ret);
				}
				else break;

			}

		} catch(IOException ioe){}
		finally{
			try{
				if(ps != null){
					ps.close();
				}
			}catch(IOException ioe){
				System.out.println("切断されました");
			}
	}

	}

}
