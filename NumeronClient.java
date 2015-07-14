//package group2_2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class NumeronClient{
	public static void main(String[] args) throws IOException{
    Scanner sc = new Scanner(System.in);
		Player me = new Player("相手");
		Player enemy = new Player("あなた");

		InetAddress addr = InetAddress.getByName("localhost"); //サーバーのアドレスを指定する
		System.out.println("addr= " + addr);

		Socket socket = new Socket(addr, NumeronServer.PORT);

  	try{
      //接続が確認されたら前準備スタート
      System.out.println("socket= " + socket);
  		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
  		PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

      //まずはじめに自分の数字をセット
      me.setMyNumber();
      out.println(me.myNumber); //サーバーにセットした数を送信

      //順序を決める(2数の和の偶奇で順序決定)
      System.out.print("先攻・後攻を決めます。1桁の数字を入力してください > ");
      String orderNum;
      while(true){
        orderNum = sc.next();
        if(orderNum.length()==1 && '0'<=orderNum.charAt(0) && orderNum.charAt(0)<='9') break;
        else System.out.print("入力が正しくありません。1桁の数字を入力してください。");
      }
      out.println(orderNum);

			//先攻後攻が決定
      String order = in.readLine();
			int firstAttacker;
      if(order.equals("FIRST")){
        System.out.println("あなたは先攻です。");
				firstAttacker = 1;
      }
      else{
        System.out.println("あなたは後攻です。");
				firstAttacker = 0;
      }

			enemy.myNumber = in.readLine();

			//ゲーム開始
			System.out.println("ゲームを開始します。");
			int turncount=1;

			while( !me.end && !enemy.end ){

				if(turncount%2==firstAttacker){
					System.out.println();
					System.out.println("！！あなたの番です。");
					out.println("ATTACKER");

					String call = me.getInput();
					out.println(call);

					enemy.countEatBite(enemy.myNumber, call);
				}
				else{
					System.out.println();
					System.out.println("！相手の番です。");
					out.println("OBSERVER");

					String returnCall = in.readLine(); //相手がこのターンでcallした値を読み込む
					me.countEatBite(me.myNumber, returnCall);
				}

				turncount++;
			}

			out.println("GAMESET");

			if(me.end) System.out.println("！！！あなたの「負け」です！！！");
			else if(enemy.end) System.out.println("！！！あなたの「勝ち」です！！！");
			else System.out.println("エラーです...");

			System.out.println("あなたの設定した数字は " + me.myNumber + " , 相手の設定した数字は " + enemy.myNumber + " でした。");

  	} finally{
  		System.out.println("GAME SET");
  		socket.close();
  	}

  }
}
