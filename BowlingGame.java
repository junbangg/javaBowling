import java.util.Scanner;
import java.util.Random;

/*
UserException
에러 처리 클라스 
 */
class UserException extends Exception
{
	private static final long serialVersionUID = 1L;
	private String character;
	
	public UserException(String s)
	{
		this.character = s;
	}
	
	@Override
	public String getMessage()
	{
		return String.valueOf(character);
	}
}

/*
출력 클라스

 */
class Print
{  
	//player 클라스 두개를 인자로 받는다 
    public static void print(Player player1, Player player2) 
    { 
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("-----------------Player 1-----------------");
        System.out.println("-------------------------------------------");
        System.out.println("| 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |  10 |");
        System.out.println("|"+player1.getStrPins().replace("0", "-"));
        System.out.println("|"+player1.getStrScores());
        System.out.println("-------------------------------------------");
        System.out.println("-----------------Player 2-----------------");
        System.out.println("-------------------------------------------");
        System.out.println("| 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 |  10 |");
        System.out.println("|"+player2.getStrPins().replace("0", "-"));
        System.out.println("|"+player2.getStrScores());
        System.out.println("-------------------------------------------");
    }
    //기타 
    public static void text(String text)
    {
    	System.out.println();
    	System.out.println(text);
    }
    //게임이 끝났을 경우 
    public static void end() throws UserException
    {  
        System.out.println();
        System.out.println();
        System.out.println(); 
        System.out.println("게임이 끝났습니다\n");
    	throw new UserException("GameOverException");
    }
}

/*
 * Game이 이루어지는 메인 클라스 
 */
public class BowlingGame 
{
	//누구의 차례인지 구별하기 위한 변수 
	boolean player1s_turn = true;
	boolean player2s_turn = false;
	//난이도 까지 적용된 핀 수 
	String finalStrPin = "1";
	//player1, player2 의 차례를 구별하기 위한 tracker 변수  
	int turnCounter;
	//난이도를 저장하기 위한 변수 
	int dif;
	//직전 점수 
	String previousRoll;
	public static void main(String[] args) throws UserException 
	{
		//게임, player 객체 생성 
        BowlingGame BG = new BowlingGame();
        Player Player1 = new Player();
        Player Player2 = new Player();
     // 사용자 입력 값을 점수로 계산하고, 콘솔에 출력한다.
        BG.game_run(Player1, Player2); 		
	}
	
	/*
	 * 게임이 실제로 돌아가는 함수 
	 *
	 */
	public void game_run(Player player1, Player player2) throws UserException
	{ 
		player1.setStrPins("");				    // 핀수.
		player1.setStrScores("");               // 문자점수.
        player1.setScore(0);                    // 숫자점수.
        player1.setStrike(0);                   // strike횟수.
        player1.setSpare(0);                    // spare횟수.
        
        player2.setStrPins("");				    // 핀수.
		player2.setStrScores("");               // 문자점수.
        player2.setScore(0);                    // 숫자점수.
        player2.setStrike(0);                   // strike횟수.
        player2.setSpare(0); 					//spare 횟
        //사용자로부터 난이도 입력 받기 
        System.out.println("난이도를 고르세요");
        System.out.println("1 <= 쉬움");
        System.out.println("2 <= 중간");
        System.out.println("3 <= 어려움");
        Scanner scanner = new Scanner(System.in);
        dif = scanner.nextInt();
        //player 1, 2의 프레임 수 
        int i,j;

        //turncounter가 짝수면 player1 차례, 홀수면 player2 차례
        //i,j 로 각 플레이어 프레임 수는 따로 저장한다 
        for(turnCounter=0,i=0,j=0;turnCounter<24;turnCounter++)
        {
        	if(turnCounter%2==0) {//player 1
        		//player1의 게임이 끝났을 때 player2에게 차례를 넘긴다 
        		if(player1.gameEnded == true) {
        			turnCounter++;
        		}
        		else {
        			//boolean 으로 player2의 차례가 끝났고 player1의 차례로 바꾼다 
        			player2s_turn = false;
            		player1s_turn = true;
            		//첫 번 째 핀을 0으로 설정 
            		player1.Pin[i] = 0;
            		// 사용자 입력을 위한 안내 문구 표시.
                	interact(i);            
                	//입력 받은 값을 변수에 저장 
                	String roll = player1.input.next().toUpperCase();
                	//입력 받은 값에 난이도 정해줌 
                	applyDifficulty(player1,dif,roll,i);
                	player1.Pin[i] = first_roll(finalStrPin);
                    score_display(player1, player2,i); // 점수표기.
                    Print.print(player1, player2);
                    i+=2;
        		}
        		
        	}
        	else if(player1.gameEnded == true && player2.gameEnded == true) {
        		Print.end();
        	}
        	else {
        		if(player2.gameEnded == true) {
        			turnCounter++;
        		}
        		else {
        			player1s_turn = false;
            		player2s_turn = true;
            		player2.Pin[j] = 0;
                	
                	interact(j);            // 사용자 입력을 위한 안내 문구 표시.
                	
                	String roll2 = player2.input.next().toUpperCase();
                	applyDifficulty(player2,dif,roll2,j);
                	player2.Pin[j] = first_roll(finalStrPin);
                    score_display(player2,player1, j); // 점수표기.
                    Print.print(player1, player2);
                    j+=2;
        		}
        		
        	}
        	
        }
	}
	//난이도를 적용하는 함수 
	public void applyDifficulty(Player player,int difficulty, String strPin, int i) {
		//무작위로 나오게 될 결과를 담은 배열 
		String tempStrPins[] = {"F","G","0","1","2","3","4","5","6","7","8","9"};
	
		//무작위 숫자와 무작위 인덱스 
		int randomNumber;
		int randomIndex;
		
		//난이도 별로 확률을 바꿔준다 
		
		//난이도가 1이면 원하는 roll 결과가 나올 확률이 80퍼센트
		if(difficulty == 1) { 
			Random random = new Random();
			randomNumber = random.nextInt(100);
			if(randomNumber>80) {
				Random index = new Random();
				randomIndex = index.nextInt(12);
				finalStrPin = tempStrPins[randomIndex];
			}
			else {
				finalStrPin = strPin;
			}
			
		}
		//난이도가 2일 때는 원하는 roll 결과가 나올 확률이 50 퍼센트 
		else if(difficulty == 2) {
			Random random = new Random();
			randomNumber = random.nextInt(100);
			if(randomNumber>50) {
				Random index = new Random();
				randomIndex = index.nextInt(12);
				finalStrPin = tempStrPins[randomIndex];
			}
			else {
				finalStrPin = strPin;
			}
		}
		//난이도가 3일 때는 원하는 roll 결과가 나올 확률이 15퍼센트 
		else if(difficulty == 3) {
			Random random = new Random();
			randomNumber = random.nextInt(100);
			if(randomNumber>15) {
				Random index = new Random();
				randomIndex = index.nextInt(12);
				finalStrPin = tempStrPins[randomIndex];
			}
			else {
				finalStrPin = strPin;
			}
			
		}
		else {
			System.out.println("Error:난이도를 올바르게 입력해주세요");
		}
		previousRoll = finalStrPin;
		player.strPin[i] = finalStrPin;
		
	}

	/*
	 * 사용자 입력을 위한 안내 문구 표시.
	 *
	 */
	public void interact(int i)
	{
		String player;
        if(i < 20)
        { 
        	if(player1s_turn == true) {
        		player = "Player 1";
        	}
        	else {
        		player = "Player 2";
        	}
        	System.out.println(player+" 핀 몇 개를 쓰러뜨릴까요?");
        	Print.text((i/2+1)+"프레임 1구 ('X','0~9') 의 값 중 하나를 입력 후 엔터를 눌러 주세요 : "); 
        }

        if(i >= 20)
        { 
        	Print.text("10프레임 보너스 구:");  
        }
	}

	/*
	 * 1구에 처리 된 문자 핀수를 정수로 리턴한다.
	 *
	 */
	public int first_roll(String pin)
	{ 
        if(pin.toUpperCase().equalsIgnoreCase("/")) // Spare 는 2구에서만 입력가능
        { 
          Print.text("GameException");   
    	  throw new RuntimeException(); 
        }
        
        if(pin.toUpperCase().equalsIgnoreCase("X")) // Strike
        {
        	pin = "10";
        }

        if(pin.equalsIgnoreCase("-")) // Open
        {
        	pin = "0"; // 하나도 넘어지지 않은 경우 
        }
        
        if(pin.toUpperCase().equalsIgnoreCase("F")) // Foul
        {
        	pin = "0";  
        }

        if(pin.toUpperCase().equalsIgnoreCase("G")) // Gutter
        {
        	pin = "0"; // 또랑에 빠진경우. 
        }
//        previousRoll = pin;
        return Integer.parseInt(pin); 
	}

	/*
	 * 콘솔에 게임점수표시.
	 *
	 */
	public void score_display(Player player, Player otherPlayer, int i) throws UserException
	{  
        if(player.Pin[i] == 10)  //Strike
        { 
        	strike(player,otherPlayer, i); 
        	turnCounter-=1;
        }
        
        if(player.Pin[i] < 10)   // Strike 이외.
        {
        	notStrike_first(player,otherPlayer, i); // 프레임1구.
        	
        	notStrike_second(player,otherPlayer, i); // 프레임2구.
        }

        if(i >= 22)        // 10프레임게임종료 퍼펙트게임종료.
        { 
        	player.gameEnded = true;
        	
//        	Print.end();
        }
	}

	/*
	 * strike 스코어 계산.
	 *
	 */
	public void strike(Player player, Player otherPlayer, int i) throws UserException
	{
		player.addStrPins("X|");
         
        if(i < 18)               //10프레임 1칸 줄이기.
        { 
        	player.addStrPins(" |");
        }
         
        player.Pin[i+1] = 0;
          
        if(player.getStrike() < 3)
        	player.addStrike(1);;
          
        if(player.getStrike() == 3)
        {
        	player.addScore(30); 
            player.addStrScores(String.format("%3d|",player.getScore())); 
             
        }
        
        if(i>=20 && player.getStrike() != 3) // 10프레임마지막.strike
        {
        	player.addScore(10 + player.Pin[i]); 
        	player.addStrScores(String.format("%5d|",player.getScore()));  
        }
        
        
        if(i>=20 && player.getStrike() != 3) // 10프레임마지막.strike
        { 
//        	Print.end();
        	player.gameEnded = true;
        } 
	}

	/*
	 * 1구 스코어 계산.
	 * 
	 */
	public void notStrike_first(Player player,Player otherPlayer, int i) throws UserException
	{
    	if(player.getSpare() == 1)	//Spare
        {
    		player.addScore(10 + player.Pin[i]); 
    		player.addStrScores(String.format("%3d|",player.getScore())); 
        }
          
    	if(player.getStrike() == 3 || player.getStrike() == 2)
        {
    		player.addScore(20 + player.Pin[i]); 
        	player.addStrScores(String.format("%3d|",player.getScore())); 

        }
        
    	player.addStrPins(player.strPin[i]+"|");
         
        player.setSpare(0);
 
        if(i >= 20)
        { 
            Print.text("10프레임 보너스 구:");    
        }
        
        if(i < 20)
        { 
        	System.out.println("\n"+previousRoll+"개 를 쓰러뜨렸습니다\n");
        	System.out.println("이번 차례에는 핀 몇 개를 쓰러뜨릴까요?");
            Print.text((i/2+1)+"프레임 2구 ('0~9') 의 값 중 하나를 입력 후 엔터를 눌러 주세요 : ");  
        } 
	}

	/*
	 * 2구 스코어 계산.
	 * 
	 */
	public void notStrike_second(Player player,Player otherPlayer, int i) throws UserException
	{ 
		String roll = player.input.next().toUpperCase();
    	applyDifficulty(player,dif,roll,i+1);
        player.Pin[i+1] = second_roll(player.strPin[i+1],player.Pin[i]); 
         
        if(player.getStrike() == 3 || player.getStrike() == 2 || player.getStrike() == 1)
        {
        	player.addScore(10 + player.Pin[i] + player.Pin[i+1]); 
        	player.addStrScores(String.format("%3d|",player.getScore())); 
        }
         
        player.setStrike(0);
         
        //Spare인 경우
        if((player.Pin[i] + player.Pin[i+1]) == 10) 
        {
        	player.addStrPins("/|");
            player.addSpare(1);
        }
        // Spare 아닌경우
        if((player.Pin[i] + player.Pin[i+1]) != 10) 
        {
        	player.addStrPins(player.strPin[i+1]+"|");
        	  
        	player.addScore(player.Pin[i] + player.Pin[i+1]); 
            player.addStrScores(String.format("%3d|",player.getScore()));
        }
     // Spare 아닌경우 10프레임게임종료 조건.
        if(((player.Pin[i] + player.Pin[i+1]) != 10) && (i == 18)) 
        { 
        	player.gameEnded = true;
        }
     // Spare 경우 10프레임게임종료 조건.
        if(i == 20)        
        { 
        	player.gameEnded = true;
        }
	}
	
	/*
	 * 2구에 처리 된 문자 핀수를 정수로 리턴한다.
	 *
	 */
	public int second_roll(String in_str_next, int in_str)
	{ 
		// 2구에 처리 된 핀수
		int pin=0; 
		// Spare
        if(in_str_next.equalsIgnoreCase("/")) 
        {
        	pin = 10 - in_str; // 총 10개의 핀 중 1구에서 넘어지지 않은 핀 갯수가 2구의 Spare 핀수
        }
     // Open
        if(in_str_next.equalsIgnoreCase("-")) 
        {
        	in_str_next = "0"; // 하나도 넘어지지 않은 경우 
        }

        if(in_str_next.toUpperCase().equalsIgnoreCase("F")) // Foul
        {
        	in_str_next = "0";  
        }

        if(in_str_next.toUpperCase().equalsIgnoreCase("G")) // Gutter
        {
        	in_str_next = "0";
        }
    //  숫자 입력 값 처리
        if(pin == 0) 
        {
        	pin = Integer.parseInt(in_str_next);
        	
        } 
        return pin;  //2구처리핀수 
	}
   
}
/*
 * @class Player
 */
class Player
{
	
	int score;
	int spare;
	int strike;
	String StrPins;
	String StrScores;
	boolean gameEnded; //10프레임 끝냈을 때 
	int[] Pin = new int [24];	               			// 사용자 입력 값을 기록하는 숫자배열. 
    String[] strPin = new String [24];					// 사용자 입력 값을 기록하는 문자배열.
    Scanner input = new Scanner(System.in);	
    
    int getScore() {
    	return this.score;
    }
    
    void setScore(int v) {
    	this.score = v;
    }
    
    void addScore(int v) {
    	this.score += v;
    }
    
    int getSpare() {
    	return this.spare;
    }
    
    void setSpare(int v) {
    	this.spare = v;
    }
    
    void addSpare(int v) {
    	this.spare += v;
    }
    
    int getStrike() {
    	return this.strike;
    }
    
    void setStrike(int v) {
    	this.strike = v;
    }
    
    void addStrike(int v) {
    	this.strike += v;
    }
    
    String getStrPins() {
    	return this.StrPins;
    }
    
    void setStrPins(String v) {
    	this.StrPins = v;
    }
    
    void addStrPins(String v) {
    	this.StrPins += v;
    }
    
    String getStrScores() {
    	return this.StrScores;
    }
    
    void setStrScores(String v) {
    	this.StrScores = v;
    }
    
    void addStrScores(String v) {
    	this.StrScores += v;
    }
    
}
