package scripts;

public class Poop {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		changeParam(20);
	}
	
	static void changeParam(int param)
	{
		System.out.println(param);
		param = -5;
		System.out.println(param);
	}

}
