//package group2_2;

import java.io.*;
import java.util.Scanner;

public class Player{
  String name;

  Player(String str){
    this.name=str;
  }

  Scanner sc = new Scanner(System.in);
  String myNumber;
  int ct=0; //ターン数
  boolean end=false; //ゲーム終了か？

  //入力情報の保存
  int size=100;
  String num[] = new String[size];
  int eat[] = new int[size];
  int bite[] = new int[size];

  String getInput(){
    while(true){
      System.out.print("4桁の数字を入力(同じ数字は使用不可) > ");
      String input = sc.next();
      if(isValidInput(input)) return input;
    }
  }

  void setMyNumber(){
    System.out.println("始めに数字を設定します。");
    myNumber = this.getInput();
  }

  boolean isValidInput(String input){
    int check[] = new int[10];

    if(input.length() != 4) {
      System.out.println("warning:: 数字が4桁ではありません。");
    }
    else{
      boolean usingOnlyNumber=true;
      for(int i=0; i<4; ++i){
        if('0'<=input.charAt(i) && input.charAt(i)<='9');
        else usingOnlyNumber=false;
      }

      if(usingOnlyNumber){

        for(int i=0; i<10; ++i) check[i]=0; //initialize
        for(int i=0; i<4; ++i) check[input.charAt(i)-'0']++;

        boolean doubling=false;
        for(int i=0; i<10; ++i){
          if(check[i]>=2) doubling=true;
        }

        if(doubling){
          System.out.println("warning:: 同じ数字が使用されている桁があります。");
        }
        else return true; //4桁で数字だけが使用され，ダブリがないときだけ抜けられる

      }
      else{
        System.out.println("warning:: 数字以外の文字が含まれています。");
      }
    }

    return false;
  }

  //答えとの一致具合を判定してリストを更新
  void countEatBite(String answer, String input){
    num[ct] = input;
    eat[ct]=0;
    bite[ct]=0;

    int check[] = new int[10];
    int ans[] = new int[10];

    for(int i=0; i<10; ++i){ //initialize
      ans[i]=0;
      check[i]=0;
    }
    for(int i=0; i<4; ++i){
      ans[answer.charAt(i)-'0']++;
      check[input.charAt(i)-'0']++;
    }

    for(int i=0; i<4; ++i){
      if(answer.charAt(i) == input.charAt(i)) eat[ct]++;
    }
    for(int i=0; i<10; ++i){
      if(ans[i]==1 && check[i]==1) bite[ct]++;
    }
    bite[ct]-=eat[ct];
    if(eat[ct]==4) end=true;

    ct++;

    System.out.println("今までの" + name + "の入力情報:::");
    for(int i=0; i<ct; ++i) System.out.println(num[i] + " : " + eat[i] + "EAT-" + bite[i] + "BITE");

  }

}
